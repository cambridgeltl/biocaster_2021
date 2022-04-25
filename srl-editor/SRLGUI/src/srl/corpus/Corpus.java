/* 
 * Copyright (c) 2008, National Institute of Informatics
 *
 * This file is part of SRL, and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://www.fsf.org/licensing/licenses/info/GPLv2.html.
 */
package srl.corpus;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import srl.tools.process.ProgressMonitor;
import srl.tools.process.StopSignal;
import srl.tools.strings.Strings;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.document.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.*;
import srl.rule.*;
import srl.wordlist.*;
import srl.tools.struct.*;

/**
 * This class wraps Lucene to provide all the tools useful for
 * access to the corpus.
 * 
 * The corpus stores documents in two forms
 * <ol><li> Head File</li>
 *   <ul>
 *      <li> Field "name": The document name as it appears in the document </li>
 *      <li> Field "originalContents": The raw text of the document </li>
 *      <li> Field "sentCount": The number of contexts this document is split into </li>
 *      <li>  Field "uid": A unique identifier (a long integer value) </li>
 *   </ul>
 * 
 *   <li>Context File</li>
 *   <ul>
 *      <li> Field "name": Of the form "name #" where name is the document name and # is the context number </li>
 *      <li> Field "contents": The tokenized contents of the context </li>
 *      <li> Field "taggedContents": The tokenized contents with the tags </li>
 *      <li> Field "extracted": The extracted templates </li>
 *      <li> Field "wordlists": The wordlists containing terms in this context </li>
 *      <li> Field "wordlistsets": The wordlist sets containing terms in this context </li>
 *      <li> Field "uid": A unique identifier (a long integer value) </li>
 *    </ul>
 * </ol>
 * @author John McCrae, National Institute of Informatics
 */
public class Corpus {

    IndexWriter indexWriter;
    IndexSearcher indexSearcher;
    Processor processor;
    HashSet<String> docNames;
    private File indexFile;
    private long lock = 0;
    // This is used to track changes that are being made to a corpus
    // but require querying the corpus to fulfill these changes. If
    // we didn't use this then this may lead to inconsistent results
    private Thread updateThread;

    private Corpus() {
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeCorpus();
    }


    ////////////////////////////////////////////////////////////////////////////
    // Primary Corpus actions 
  
    /**
     * Close the corpus
     * @throws IOException If a disk error occured 
     */
    public void closeCorpus() throws IOException {
        if (indexWriter != null) {
            indexWriter.optimize();
            indexWriter.close();
            indexWriter = null;
        }
        if (indexSearcher != null) {
            indexSearcher.close();
        }
    }

    /** Opens the corpus so that new documents can be added
     * @param indexFile The location of the indexFile
     * @param processor An instance of the processor used
     * @param newIndex If true any index on existing path will be removed
     */
    public static Corpus openCorpus(File indexFile, Processor processor, boolean newIndex) throws IOException {
        Corpus c = new Corpus();
        c.indexFile = indexFile;
        c.processor = processor;
        c.indexWriter = new IndexWriter(indexFile, processor.getAnalyzer(), newIndex);
        c.docNames = new HashSet<String>();
        if (!newIndex) {
            try {
                c.closeIndex(0);
                c.docNames.addAll(c.extractDocNames());
            } catch (CorpusConcurrencyException x) {
                x.printStackTrace();
                throw new RuntimeException("ERROR: Concurrency exception");
            }
            //c.reopenIndex(false);
        }
        return c;
    }
    
    private List<String> extractDocNames() throws IOException, IllegalStateException, CorpusConcurrencyException {
        if (indexSearcher == null) {
            closeIndex(0);
        }
        List<String> rv = new Vector<String>();
        for (int i = 0; i < indexSearcher.maxDoc(); i++) {
            String docName;
            try {
                docName = indexSearcher.doc(i).getField("name").stringValue();
            } catch (IllegalArgumentException x) {
                System.err.println("WARNING: Access to deleted document");
                continue;
            }
            String uid = indexSearcher.doc(i).getField("uid").stringValue();
            if (docName.matches("\\w+")) {
                rv.add(docName);
                uids.add(uid);
            }
        }
        return rv;
    }

       
    
    /** Optimize the index. Call this only after significant changes to the corpus. It may take several
     * seconds, but will improve search speed afterwards (YMMV).
     */
    public synchronized void optimizeIndex(long lockID) throws IOException, CorpusConcurrencyException {
        //synchronized (this) {
            if (lock != lockID) {
                throw new CorpusConcurrencyException("Corpus is locked, this operation is not permitted until unlock");
            }
            lock = 0;
            if(indexWriter == null)
                indexWriter = new IndexWriter(dir, processor.getAnalyzer(), false);
            indexWriter.optimize();
            indexWriter.close();

            if (dir instanceof RAMDirectory) {
                dir = new RAMDirectory(indexFile);
            } else {
                dir = FSDirectory.getDirectory(indexFile);
            }

            indexSearcher = new IndexSearcher(dir);
            indexWriter = null;
            openThread = null;
            notifyAll();
        //}
    }

    /** Close the corpus, after which no more documents can be added. Also commits the corpus to disk */
    /*public void closeIndex() throws IOException, CorpusConcurrencyException {
        closeIndex(0);
    }*/

    // NOTE: The correctly to open the corpus for read/write should be as follows
    // long id = reopenIndex();
    //    .... edit corpus ...
    // if(id != 0)
    //     closeIndex(id);

    /** Close the corpus, after which no more documents can be added. Use this method if
     * you called reopenIndex(), as then only this method with the correct
     * lock value can be used to close the corpus.
     * @param lockID The lockID returned from reopenIndex()
     * @throws java.io.IOException
     * @throws srl.corpus.CorpusConcurrencyException
     */
    public synchronized void closeIndex(long lockID) throws IOException, CorpusConcurrencyException {
        //synchronized (this) {
            if(updateThread != null && updateThread != Thread.currentThread()) {
                try {
                    updateThread.join();
                } catch(Exception x) {
                    x.printStackTrace();
                }
            }
            if (lock != lockID) {
                throw new CorpusConcurrencyException("Corpus is locked, this operation is not permitted until unlock");
            }
            lock = 0;
            if(indexWriter == null)
                return;
            dir = indexWriter.getDirectory();
            indexWriter.close();


            if (dir instanceof RAMDirectory) {
                dir = new RAMDirectory(indexFile);
            } else {
                dir = FSDirectory.getDirectory(indexFile);
            }

            indexSearcher = new IndexSearcher(dir);
            indexWriter = null;
            openThread = null;
            this.notifyAll();
        //}
    }
    Directory dir;


    /** Reopen the index to add new documents. This is equivalent ot reopenIndex(false);
     *
     */
    /*public void reopenIndex() throws IOException {
        reopenIndex(false);
    }*/

    private Thread openThread;
    private Object openSignal = new Object();

    /**
     * Reopen the index to add new documents. Same as reopenIndex(true)
     * @return The Lock ID
     * @throws java.io.IOException
     * @throws srl.corpus.CorpusConcurrencyException
     */
    public long reopenIndex() throws IOException, CorpusConcurrencyException {
        return reopenIndex(true);
    }

    /**
     * Reopen the index to add new documents. If the second parameter is set the
     * current thread will wait until the corpus next becomes unlocked. If not the
     * function will throw a CorpusConcurrencyException if it can't obtain a lock.
     * @param wait If true wait until the index can be reopened.
     * @return The index lock ID
     * @throws java.io.IOException
     * @throws srl.corpus.CorpusConcurrencyException
     */
    public synchronized long reopenIndex(boolean wait) throws IOException, CorpusConcurrencyException {
       
        //synchronized (this) {
            if(updateThread != null && updateThread != Thread.currentThread()) {
                try {
                    updateThread.join();
                } catch(Exception x) {
                    x.printStackTrace();
                }
            }
            if (indexWriter != null) {
                if(this.lock == 0) {// Corpus is open but not locked so lock it
                    openThread = Thread.currentThread();
                    return this.lock = random.nextLong();
                }
                return 0;
            }
             while(wait && this.lock != 0) {
                if(waitOnCorpusUnlock() != 0)
                    lock = 0;
            }
            indexSearcher.close();
            indexWriter = new IndexWriter(dir, processor.getAnalyzer(), false);
            indexSearcher = null;
            openThread = Thread.currentThread();
            return this.lock = random.nextLong();
        //}
    }

    /**
     * Wait for the corpus to be unlocked.
     * @throws srl.corpus.CorpusConcurrencyException If this is called by thread that has the lock
     * @return The current lock value (if a thread dies while locking the corpus, the
     * lock value is 'given' to the next thread via this method)
     */
    public synchronized long waitOnCorpusUnlock() throws CorpusConcurrencyException {
        if(lock == 0)
            return 0;
        if(Thread.currentThread() == openThread) {
            throw new CorpusConcurrencyException("Thread is attempting to wait on itself");
        }
        if(!openThread.isAlive())
            return lock;
        try {
            this.wait();
        } catch(InterruptedException x) {
            x.printStackTrace();
        }
        return 0;
    }

    /** (expert) Switch corpus to RAM. If this option is set to true, the corpus will
     * be loaded into system memory, this can significantly improve performance but is
     * likely to cause out of memory exceptions.
     * @param value True for use RAM, false for use disk
     * @deprecated
     */
    public void setUseRAM(boolean value) throws IOException, CorpusConcurrencyException {
        if(indexSearcher == null)
            closeIndex(0);
        if (value && !(dir instanceof RAMDirectory)) {
            dir = new RAMDirectory(dir);
        } else {
            dir = FSDirectory.getDirectory(indexFile);
        }
    }

    /** (expert) See if the corpus is in RAM or disk.
     * @deprecated
     */
    public boolean getUseRAM() {
        return dir instanceof RAMDirectory;
    }

    
    
    ////////////////////////////////////////////////////////////////////////////
    // Document management

    /** Add a new document to corpus
     * @param name The name of the document
     * @param contents The text of the new document
     * @param wait If the corpus is being used by another thread, this parameter sets
     * whether the thread should wait or throw a CorpusConcurrencyException
     * @throws CorpusConcurrencyException If the corpus is locked and wait is false
     * @throws IOException The document couldn't be added
     * @throws IllegalArgumentException If the document name already exits
     */
    public void addDoc(String name, String contents, boolean wait) throws IOException, IllegalStateException, IllegalArgumentException, CorpusConcurrencyException {
        addDoc(name,contents,false, wait);
    }
    
    /** Add a new document to corpus
     * @param name The name of the document
     * @param contents The text of the new document
     * @param tagged Treat the document as pre-tagged
     * @param wait If the corpus is being used by another thread, this parameter sets
     * whether the thread should wait or throw a CorpusConcurrencyException
     * @throws CorpusConcurrencyException If the corpus is locked and wait is false
     * @throws IOException The document couldn't be added
     * @throws IllegalArgumentException If the document name already exits
     */
    public void addDoc(String name, String contents, boolean tagged, boolean wait) throws IOException, IllegalStateException, IllegalArgumentException, CorpusConcurrencyException {
        long id = reopenIndex(wait);
        name = name.toLowerCase();
        if (docNames.contains(name)) {
            throw new IllegalArgumentException(name + " already exists in corpus");
        }
        Document d = new Document();
        d.add(new Field("originalContents", tagged ? stripTags(contents) : contents, Field.Store.YES, Field.Index.TOKENIZED));
        d.add(new Field("name", name, Field.Store.YES, Field.Index.TOKENIZED));
        d.add(new Field("uid", generateUID(), Field.Store.YES, Field.Index.TOKENIZED));
        docNames.add(name);
        int i = 0;
        for (Collection<org.apache.lucene.analysis.Token> sentence : processor.getSplitter().split(new SrlDocument(name, contents, processor), name)) {
            StringBuffer sent = new StringBuffer();
            StringBuffer taggedSent = new StringBuffer();
            Iterator<org.apache.lucene.analysis.Token> tkIter = sentence.iterator();
            while (tkIter.hasNext()) {
                org.apache.lucene.analysis.Token tk = tkIter.next();
                sent.append(tk.termText());
                if(tk instanceof BeginTagToken) {
                    taggedSent.append(((BeginTagToken)tk).getTag());
                } else if(tk instanceof EndTagToken) {
                    taggedSent.append(((EndTagToken)tk).getTag());
                } else {
                    taggedSent.append(tk.termText());
                }
                if (tkIter.hasNext()) {
                    sent.append(" ");
                    taggedSent.append(" ");
                }
            }
            
            addContext(name + " " + i, sent.toString(), tagged ? taggedSent.toString() : sent.toString(),
                    tagged ? taggedSent.toString() : sent.toString(), null);
             
            i++;
        }
        d.add(new Field("sentCount", i + "", Field.Store.YES, Field.Index.NO));
        indexWriter.addDocument(d);
        if(id != 0)
            closeIndex(id);
    }
    private HashSet<String> uids = new HashSet<String>();
    private Random random = new Random();

    /**
     * Add a single context
     * @param name The name of the context
     * @param contents Its contents
     * @param taggedContents Its tagged contents (or null if not applicable)
     */
    protected void addContext(String name, String contents, String taggedContents, String pretaggedContents, String extracted) throws CorruptIndexException, IOException {
        Document d2 = new Document();
            d2.add(new Field("contents", contents/*.toLowerCase()*/, Field.Store.YES, Field.Index.TOKENIZED));
            d2.add(new Field("name", name, Field.Store.YES, Field.Index.TOKENIZED));
            d2.add(new Field("uid", generateUID(), Field.Store.YES, Field.Index.TOKENIZED));
            Set<Pair<String,String>> wls = wordListForDoc(contents);
            StringBuffer wlNames = new StringBuffer(), wlSetNames = new StringBuffer();
            for(Pair<String,String> wl : wls) {
                wlNames.append(wl.first + " ");
                WordListSet wls2 =WordListSet.getWordListSetByList(wl.first);
                if(wls2 == null) {
                    System.err.println("Could not locate " + wl.first);
                    continue;
                }
                wlSetNames.append(wls2.name + " ");
            }
            d2.add(new Field("wordlists", wlNames.toString(), Field.Store.YES, Field.Index.TOKENIZED));
            d2.add(new Field("wordlistsets", wlSetNames.toString(), Field.Store.YES, Field.Index.TOKENIZED));
            
            if(taggedContents != null)
                d2.add(new Field("taggedContents", taggedContents, Field.Store.YES, Field.Index.TOKENIZED));
            if(pretaggedContents != null)
                d2.add(new Field("pretaggedContents", pretaggedContents, Field.Store.YES, Field.Index.TOKENIZED));
            if(extracted != null)
                d2.add(new Field("extracted", extracted, Field.Store.YES, Field.Index.TOKENIZED));
            indexWriter.addDocument(d2);
    }
    
    private static String stripTags(String s) {
        return s.replaceAll("<[^>]*>", "");
    }
    
    private String generateUID() {
        String s;
        do {
            s = Math.abs(random.nextLong()) + "";
        } while (uids.contains(s));
        uids.add(s);
        return s;
    }
    

   /* public void clearTemplateExtractions() throws CorruptIndexException,IOException, CorpusConcurrencyException {
        System.err.println("Check efficiency");
        if(indexSearcher == null)
            closeIndex();
        for (int i = 0; i < indexSearcher.maxDoc(); i++) {
             try {
                if(!indexSearcher.doc(i).getField("name").stringValue().matches(".* .*"))
                        continue;
            } catch(IllegalArgumentException x) {
                System.err.println("Deleted document ignored");
                continue;
            }
            Document d = indexSearcher.doc(i);
            d.removeFields("extracted");
            if(indexSearcher == null)
                closeIndex();
           
        }
        return;
    }*/

    private Set<Pair<String, String>> wordListForDoc(String contents) {
        Set<Pair<String, String>> rval = new HashSet<Pair<String, String>>();
        for (String name : WordListSet.getAllWordListNames()) {
            if(WordListSet.getWordList(name) == null) {
                System.err.println("Could not find " + name);
                continue;
            }
            for (WordListEntry term : WordListSet.getWordList(name)) {
                if (contents.toLowerCase().contains(term.toString())) {
                    rval.add(new Pair(name, term.toString()));
                    break;
                }
            }
        }
        return rval;
    }
    
        /**
     * Does this corpus contain a particular document
     * @param docName The document name
     * @return True if the corpys contains a document by that name
     */
    public boolean containsDoc(String docName) {
        return docNames.contains(docName);
    }
    
    /** Get the names of all the documents in the corpus */
    public Set<String> getDocNames() {
        return new TreeSet<String>(docNames);
    }
    
    
    /**
     * Get a particular document
     * @param name Document name
     * @return The document, or null if the document is not in the index
     * @throws java.io.IOException If the corpus was not readable
     */
    protected Document getDoc(String name) throws IOException, CorpusConcurrencyException {
        if (indexSearcher == null) {
            closeIndex(0);
        }
        QueryParser qp = new QueryParser("name", processor.getAnalyzer());
        try {
            Query q = qp.parse("\"" + cleanQuery(name) + "\"");
            Hits hits = indexSearcher.search(q);
            for (int i = 0; i < hits.length(); i++) {
                if (hits.doc(i).getField("name").stringValue().equals(name)) {
                    return hits.doc(i);
                }
            }
            return null;
        } catch (org.apache.lucene.queryParser.ParseException x) {
            x.printStackTrace();
            return null;
        }

    }
    
    
    /**
     * Get a document by its UID
     * @param uid The unique identifier
     */
    protected Document getDocByUID(String uid) throws IOException, CorpusConcurrencyException {
        if(indexSearcher == null)
            closeIndex(0);
        QueryParser qp = new QueryParser("uid", processor.getAnalyzer());
        try {
            Query q = qp.parse(uid);
            Hits hits = indexSearcher.search(q);
            if(hits.length() == 0)
                return null;
            else if(hits.length() == 1) 
                return hits.doc(0);
            else
                throw new IllegalStateException("UID not unique!");
        } catch(ParseException x) {
            x.printStackTrace();
            return null;
        }
    }

    private String validateDocName(String name) {
        name = name.toLowerCase();
        if(!name.matches("[a-z0-9]+"))
            throw new IllegalArgumentException("Invalid document name:" + name);
        return name;
    }

        /**
     * Gets the original text for a document
     * @param name The document name
     * @return The plain text contents
     * @throws java.io.IOException If a disk error occurred
     */
    public String getPlainDocContents(String name) throws IOException, CorpusConcurrencyException {
        return getDoc(validateDocName(name)).getField("originalContents").stringValue();
    }

    private List<String> getDocFields(String docName, String fieldName) throws IOException, CorpusConcurrencyException {
        docName = validateDocName(docName);
           if(indexSearcher == null)
            closeIndex(0);
        QueryParser qp = new QueryParser("name", processor.getAnalyzer());
        Vector<String> rval = new Vector<String>();
        try {
            Query q = qp.parse("\"" + cleanQuery(docName) + "\"");
            Hits hits = indexSearcher.search(q);
            for (int i = 0; i < hits.length(); i++) {
                String docName2 = hits.doc(i).getField("name").stringValue();
                if (docName2.equals(docName)) { // Not necessary, but it's nice to set the vector to the correct size
                    rval.setSize(Integer.parseInt(hits.doc(i).getField("sentCount").stringValue()));
                } else {
                    Matcher m = Pattern.compile(".* (\\d+)").matcher(docName2);
                    if (!m.matches()) {
                        throw new RuntimeException("Invalid document name in corpus: " + docName2);
                    }
                    if (hits.doc(i).getField(fieldName) != null) {
                        rval.set(Integer.parseInt(m.group(1)),
                                hits.doc(i).getField(fieldName).stringValue());
                    } else {
                        rval.set(Integer.parseInt(m.group(1)), "");
                    }
                }
            }
        } catch (org.apache.lucene.queryParser.ParseException x) {
            x.printStackTrace();
            return null;
        }
        return rval;
    }
    
    /**
     * Get the document sentence by sentence
     * @param name The document name
     * @return A list of the sentences
     * @throws IOException If a disk error occurred
     */
    public List<String> getDocSentences(String name) throws IOException, CorpusConcurrencyException {
        return getDocFields(name, "contents");
    }

    /** Get the tagged contents (as stored) of the document.
     * @return The tagged contents of the document as a sentence-by-sentence list
     */
    public List<String> getDocTaggedContents(String name) throws IOException, CorpusConcurrencyException {
        return getDocFields(name, "taggedContents");
    }

    /**
     * The extracted templates as stored for a document.
     * @return The extracted templates of the document as a sentence-by-sentence list
     */
    public List<String> getDocTemplateExtractions(String name) throws IOException, CorpusConcurrencyException {
        return getDocFields(name, "extracted");
    }

    /**
     * Remove a document from the corpus
     * @param name The name of the document
     * @param wait If the corpus is being used by another thread, this parameter sets
     * whether the thread should wait or throw a CorpusConcurrencyException
     * @throws CorpusConcurrencyException If the corpus is locked and wait is false
     * @throws java.io.IOException
     */
    public void removeDoc(String name, boolean wait) throws IOException, CorpusConcurrencyException {
        name = validateDocName(name);
        long id = reopenIndex(wait);
        indexWriter.deleteDocuments(new Term("name", name));
        docNames.remove(name);
        if(id != 0)
            closeIndex(id);
    }

    /**
     * Change the contents of document in the corpus. 
     * 
     * @param name The name of the document
     * @param contents The new contents
     * @param wait If the corpus is being used by another thread, this parameter sets
     * whether the thread should wait or throw a CorpusConcurrencyException
     * @throws CorpusConcurrencyException If the corpus is locked and wait is false
     * @throws java.io.IOException
     */
    public void updateDoc(String name, String contents, boolean wait) throws IOException, CorpusConcurrencyException {
        name = validateDocName(name);
        Document old = getDoc(name);
        if (old != null) {
            if (contents.equals(old.getField("originalContents").stringValue())) {
                return;
            }
        }
        long id = reopenIndex(wait);
        indexWriter.deleteDocuments(new Term("name", name));
        //support.removeDoc(name);
        docNames.remove(name);
        addDoc(name, contents,wait);
        if(id != 0)
            closeIndex(id);
    }
    
    
    /**
     * Change the contents of document in the corpus. 
     * 
     * @param old The document
     * @param contents The new contents
     * @param wait If the corpus is being used by another thread, this parameter sets
     * whether the thread should wait or throw a CorpusConcurrencyException
     * @param taggedContents The tagged contents, or null if no tagged contents exist
     * @throws CorpusConcurrencyException If the corpus is locked and wait is false
     * @throws java.io.IOException
     */
    public void updateContext(Document old, String contents, String taggedContents, boolean wait) throws IOException, CorpusConcurrencyException {
        String name = old.getField("name").stringValue();
        if (old != null) {
            if (!old.getField("name").stringValue().matches(".* .*") && 
                    contents.equals(old.getField("originalContents").stringValue())) {
                return;
            }
        }
        long id = reopenIndex(wait);
        indexWriter.deleteDocuments(new Term("uid", old.getField("uid").stringValue()));
        //support.removeDoc(name);
        try {
            addContext(name,
                    contents,
                taggedContents,
                old.getField("pretaggedContents") != null ? old.getField("pretaggedContents").stringValue() : null,
                old.getField("extracted") != null ? old.getField("extracted").stringValue() : null);
        } catch(NullPointerException x) {
            System.err.println(old.getField("name"));
            x.printStackTrace();
        }
        if(id != 0)
            closeIndex(id);
    }
    
    /**
     * Change the content of a single context in the corpus. (Used if wordlist or other things change)
     * 
     * @param uids The unique identifiers
     * @param wait If the corpus is being used by another thread, this parameter sets
     * whether the thread should wait or throw a CorpusConcurrencyException
     * @throws CorpusConcurrencyException If the corpus is locked and wait is false
     */
    public void updateContexts(List<String> uids, boolean wait)  throws IOException, CorpusConcurrencyException {
        LinkedList<Document> docs = new LinkedList<Document>();
        for(String uid : uids) {
            Document d = getDocByUID(uid);
            if(d == null)
                throw new IllegalArgumentException("UID " + uid + " not in corpus");
            docs.add(d);
        }
        long id = reopenIndex(wait);
        Iterator<Document> dIter = docs.iterator();
        for(String uid : uids) {
            Document old = dIter.next();
            indexWriter.deleteDocuments(new Term("uid",uid));
            try {
                addContext(old.getField("name").stringValue(),
                    old.getField("contents").stringValue(),
                    old.getField("taggedContents") != null ? old.getField("taggedContents").stringValue() : null,
                    old.getField("pretaggedContents") != null ? old.getField("pretaggedContents").stringValue() : null,
                    old.getField("extracted") != null ? old.getField("extracted").stringValue() : null);
            } catch(NullPointerException x) {
                System.err.println(uid);
                System.err.println(old.getField("name"));
                x.printStackTrace();
            }
        }
        if(id != 0)
            closeIndex(id);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Querying interface
    
    private class SrlHitCollector extends HitCollector {

        QueryHit qh;
        StopSignal signal;

        public SrlHitCollector(QueryHit qh, StopSignal signal) {
            this.qh = qh;
            this.signal = signal;
        }

        @Override
        public void collect(int doc, float arg1) {
            try {
                qh.hit(indexSearcher.doc(doc), signal);
            } catch (CorruptIndexException x) {
                x.printStackTrace();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }

    /** Derive an object from this class to provide a callback on
     * query success */
    public interface QueryHit {

        /** The callback function
         * @param d The document hit
         * @param signal Allows premature halt of the query. (If you don't
         * know what this is ignore it, it's really not that important) */
        public void hit(Document d, StopSignal signal);
    }

    /**
     * Query the corpus. This is the method normally used for query based on a 
     * SRL rule 
     * @param query The query normally returned from Rule.getQuery()
     * @param collector Every hit is passed to the hit(Document) method of the collector
     * @throws java.io.IOException There was an disk error with the corpus
     * @see Rule#getCorpusQuery()
     */
    public void query(SrlQuery query, QueryHit collector) throws IOException, CorpusConcurrencyException {
        query(query, collector, null);
    }

    /**
     * Query the corpus. This is the method normally used for query based on a 
     * SRL rule 
     * @param query The query normally returned from Rule.getQuery()
     * @param collector Every hit is passed to the hit(Document) method of the collector
     * @param signal An optional stop signal to abandon the query
     * @throws java.io.IOException There was an disk error with the corpus
     * @see Rule#getCorpusQuery()
     */
    public void query(SrlQuery query, QueryHit collector, StopSignal signal) throws IOException, CorpusConcurrencyException {
        if (indexSearcher == null) {
            closeIndex(0);
        }
        if (query.query.toString().matches("\\s*") &&
                query.entities.isEmpty() &&
                query.wordListSets.isEmpty() &&
                query.wordLists.isEmpty()) {
            nonLuceneQuery(query, collector, signal);
            return;
        }
        try {
            QueryParser qp = new QueryParser("contents", processor.getAnalyzer());
            qp.setDefaultOperator(QueryParser.Operator.AND);
            StringBuffer queryStr = new StringBuffer(cleanQuery(query.query.toString()));
            queryStr.append(" ");
            for (Pair<String, String> entity : query.entities) {
                queryStr.append("taggedContents:\"<" + entity.first + " cl=\\\"" + entity.second +
                        "\\\">\" ");
            }
            for (String wl : query.wordLists) {
                queryStr.append("wordlists:" + wl + " ");
            }
            for(String wls : query.wordListSets) {
                queryStr.append("wordlistsets:" + wls + " ");
            }
            Query q = qp.parse(queryStr.toString());
            if (q.toString().matches("\\s*")) {
                nonLuceneQuery(query, collector, signal);
                return;
            }
            indexSearcher.search(q, new SrlHitCollector(collector, signal));
         } catch (Exception x) {
            System.err.println(query.query.toString());
            x.printStackTrace();
        }
    }

    // If the SRLQuery is null we use this query method
    private void nonLuceneQuery(SrlQuery query, QueryHit collector, StopSignal signal) throws IOException, CorpusConcurrencyException {
        // Empty queries match everything (!)
        System.out.println("Empty Query! This may significantly affect performance");
        for (int i = 0; i < indexSearcher.maxDoc(); i++) {
            try {
                indexSearcher.doc(i);
            } catch(IllegalArgumentException x) {
                System.err.println("Deleted document ignored");
                continue;
            }
            if (indexSearcher.doc(i).getField("contents") != null) {
                collector.hit(indexSearcher.doc(i), signal);
            }
            if (signal != null && signal.isStopped()) {
                return;
            }
        }
        return;
    }

    /**
     * Query the corpus for a single string of plain text
     * @param query The string to query
     * @return The documents this matches
     * @throws java.io.IOException
     * @throws srl.corpus.CorpusConcurrencyException
    */
    public Hits query(String query) throws IOException, CorpusConcurrencyException {
        if (query.equals("")) {
            return null;
        }
        if (indexSearcher == null) {
            closeIndex(0);
        }
        try {
            QueryParser qp = new QueryParser("contents", processor.getAnalyzer());
            qp.setDefaultOperator(QueryParser.Operator.AND);
            Query q = qp.parse(QueryParser.escape(query.toLowerCase()));
            return indexSearcher.search(q);
        } catch (Exception x) {
            x.printStackTrace();
            return null;
        }
    }

    /**
     * Query the corpus. Unlike query(String) this function does not fix (escape) the query
     * string for Lucene, so calling this function requires the user to ensure the query is a valid
     * Lucene query
     * @param query The query string
     * @return The documents the query matches
     * @throws java.io.IOException
     * @throws srl.corpus.CorpusConcurrencyException
     * @see QueryParser#escape(String)
     */

    public Hits queryNoEscape(String query)throws IOException, CorpusConcurrencyException {
        if (query.equals("")) {
            return null;
        }
        if (indexSearcher == null) {
            closeIndex(0);
        }
        try {
            QueryParser qp = new QueryParser("contents", processor.getAnalyzer());
            qp.setDefaultOperator(QueryParser.Operator.AND);
            Query q = qp.parse(query.toLowerCase());
            return indexSearcher.search(q);
        } catch (Exception x) {
            x.printStackTrace();
            return null;
        }
    }

    /** Make a literal string not cause problems for the indexer, i.e., Put to lower case and bs all reserved terms */
    protected static String cleanQuery(String s) {
        s = s.toLowerCase();
        s = s.replaceAll("\\\\", "\\\\\\\\");
        return s;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Vigilance! (checking the corpus remains up to date with user changes)
    
    /** Reinitialize the corpus support. This is actually research for word 
     * list entry matches, sometimes they get out of sync, I don't know why,
     * hopefully they are fixed now and I just forgot to remove this comment.
     * @param wait If the corpus is being used by another thread, this parameter sets
     * whether the thread should wait or throw a CorpusConcurrencyException
     * @throws CorpusConcurrencyException If the corpus is locked and wait is false
     * @throws java.io.IOException
     * @throws CorpusConcurrencyException
     */
    public void resupport(boolean wait) throws IOException, CorpusConcurrencyException {
        
        try {
            if(indexSearcher == null)
                closeIndex(0);
            updateThread = Thread.currentThread();
            List<String> newDocs = new LinkedList<String>();
            for(int i = 0; i < indexSearcher.maxDoc(); i++) {
                Document d;
                try {
                    d = indexSearcher.doc(i);
                } catch(IllegalArgumentException x) {
                    System.err.println("Deleted document ignored");
                    continue;
                }
                if(!d.getField("name").stringValue().matches(".* .*"))
                    continue;
                newDocs.add(d.getField("uid").stringValue());
            }
            long id = reopenIndex(wait);
            updateThread = null;
            updateContexts(newDocs,wait);
            
            if(id != 0)
                optimizeIndex(id);
        } catch(Exception x) {
            x.printStackTrace();
        }
        
    }

    /** Is the corpus open for indexing */
    public boolean isIndexOpen() {
        return indexWriter != null;
    }

    /** Get the name of the class of the analyzer */
    public Processor getProcessor() {
        return processor;
    }

    /** Add this as a listener to list */
    public void listenToWordListSet(WordListSet list) {
        list.addChangeListener(new CollectionChangeListener<WordList>() {

            public void collectionChanged(CollectionChangeEvent<WordList> e) {
                // TODO: Should we be doing something here?
            }
        });
    }

    /** Add this as a listener to wordList */
    public void listenToWordList(String name, WordList wordList) {
        wordList.addCollectionChangeListener(new WLCCL(name));
    }

    // WLCCL = WordListCollectionChangeListener
    private class WLCCL implements CollectionChangeListener<WordListEntry> {

        String name;

        WLCCL(String name) {
            this.name = name;
        }

        public void collectionChanged(CollectionChangeEvent<WordListEntry> e) {
            Thread t = new Thread(new WLCCLRun(e.getOldVal(), e.getNewVal(), name), "wordListUpdate");
            updateThread = t;
            t.start();
        }
    }

    private class WLCCLRun implements Runnable {
        WordListEntry oldVal, newVal;
        String name;

        public WLCCLRun(WordListEntry oldVal, WordListEntry newVal, String name) {
            this.oldVal = oldVal;
            this.newVal = newVal;
            this.name = name;
        }

        public void run() {
            try {
                removeWordListElement(name, oldVal != null ? oldVal.toString() : null);
                addWordListElement(name, newVal != null ? newVal.toString() : null);
                updateThread = null;
            } catch(Exception x) {
                x.printStackTrace();
            }
        }

    }

    /**
     * Update all documents, that a word list element has been removed
     * @param name The word list name
     * @param oldVal The element removed
     */
    protected void removeWordListElement(String name, String oldVal) {
        try {
            if(indexSearcher == null)
                closeIndex(0);
            QueryParser qp = new QueryParser("contents", processor.getAnalyzer());
            qp.setDefaultOperator(QueryParser.Operator.AND);
            Query q = qp.parse("\"" + oldVal + "\"");
            Hits hits = indexSearcher.search(q);
            List<String> newDocs = new LinkedList<String>();
            for(int i = 0; i < hits.length(); i++) {
                Document d;
                try {
                    d = indexSearcher.doc(hits.id(i));
                } catch(IllegalArgumentException x) {
                    System.err.println("Deleted document ignored");
                    continue;
                }
                if(!d.getField("name").stringValue().matches(".* .*"))
                    continue;
                newDocs.add(d.getField("uid").stringValue());
            }
            updateContexts(newDocs, false);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

       /**
        * Update all document to a new word list element
        * @param name The word list name
        * @param newVal The new element
        */
    protected void addWordListElement(String name, String newVal) {
        try {
            if(indexSearcher == null)
                closeIndex(0);
            QueryParser qp = new QueryParser("contents", processor.getAnalyzer());
            qp.setDefaultOperator(QueryParser.Operator.AND);
            Query q = qp.parse("\"" + newVal + "\"");
            Hits hits = indexSearcher.search(q);
        
            String setName = WordListSet.getWordListSetByList(name).name;
            List<String> newDocs = new LinkedList<String>();
            for(int i = 0; i < hits.length(); i++) {
                Document d;
                try {
                    d = indexSearcher.doc(hits.id(i));
                } catch(IllegalArgumentException x) {
                    System.err.println("Deleted document ignored");
                    continue;
                }
                if(!d.getField("name").stringValue().matches(".* .*"))
                    continue;
                newDocs.add(d.getField("uid").stringValue());
            }
            updateContexts(newDocs, false);
        } catch(Exception x) {
            x.printStackTrace();
        }
    }

}
 