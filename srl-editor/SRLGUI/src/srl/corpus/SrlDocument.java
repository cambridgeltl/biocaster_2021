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

import java.io.IOException;
import java.util.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.document.Document;

/**
 * This object is used to represent a tokenized document or sentence. As such
 * it can also be used with normal List methods from the Java API however it 
 * may be read-only and some methods don't like to be called until after 
 * the document has been iterated over once.
 * @author John McCrae, National Institute of Informatics
 */
public class SrlDocument extends AbstractList<Token> {

    TokenStream stream;
    List<Token> tokensRead;
    int readPoint;
    String name;

    /**
     * Create an empty SrlDocument. Use this constructor only if you wish
     * to add the contents one word at a time. (SRL documents created this
     * way are modifiable)
     * @param name The document name
     */
    public SrlDocument(String name) {
        this.name = name;
        tokensRead = new LinkedList<Token>();
        stream = null;
        readPoint = -1;
    }
    
    /**
     * Create an SRL Document from a Lucene object and a processor. The 
     * documents data should be in the field taggedContents if tagged is true
     * and contents if tagged is false. (SRL documents created this way are not
     * modifiable)
     */
    public SrlDocument(Document doc, Processor processor, boolean tagged) {
        try {
            init(doc.getField(tagged ? "taggedContents" : "contents").stringValue(), processor);
        } catch(NullPointerException x) {
            if(tagged) {
                init("",processor);
            } else {
                x.printStackTrace();
                throw new RuntimeException();
            }
        }
        name = doc.getField("name").stringValue();
    }

    /** Create an SRL document from raw text. (SRL documents created this way are not
     * modifiable) */
    public SrlDocument(String name, String contents, Processor processor) {
        init(contents, processor);
        this.name = name;
    }

    @Override
	/** Add a token to a modifiable document
	 * @throws IllegalStateException If the document is not modifiable */
    public boolean add(Token arg0) {
        if(stream != null)
            throw new IllegalStateException("Attempting to add to stream based document");
        readPoint++;         
        return tokensRead.add(arg0);   
    }

    /** Returns the representation of this document. Note this does not include tags and
     * may not preserve formatting information
     */
    @Override
    public String toString() {
        StringBuffer rval = new StringBuffer();
        Iterator<Token> tkIter = iterator();
        while(tkIter.hasNext()) {
            Token t = tkIter.next();
            rval.append(t.termBuffer(), 0, t.termLength());
            if(tkIter.hasNext())
                rval.append(" ");
        }
        return rval.toString();
    }

    /** Returns the representation of this document with tags. Note this does not preserve
     * formatting information
     */
    public String toStringTagged() {
        StringBuffer rval = new StringBuffer();
        Iterator<Token> tkIter = iterator();
        while(tkIter.hasNext()) {
            Token t = tkIter.next();
            if(t instanceof BeginTagToken) {
                rval.append(((BeginTagToken)t).getTag());
            } else if(t instanceof EndTagToken) {
                rval.append(((EndTagToken)t).getTag());
            } else {
                rval.append(t.termBuffer(), 0, t.termLength());
            }
            if(tkIter.hasNext())
                rval.append(" ");
        }
        return rval.toString();
    }


    /** Returns the name of the document */
    public String getName() {
        return name;
    }

    private void init(String contents, Processor processor) {
        stream = processor.getTokenStream(contents);
        tokensRead = new LinkedList<Token>();
        readPoint = -1;
    }
    public static HashMap<String, String> analyzerToTokenizerMap = new HashMap<String, String>();

    @Override
    public Iterator<Token> iterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<Token> listIterator(int index) {
        while (index > readPoint+1) {
            try {
                Token t = stream.next();
                if (t == null && index > readPoint) {
                    throw new IndexOutOfBoundsException();
                } else {
                    tokensRead.add((Token)t);
                    readPoint++;
                }
            } catch (IOException x) {
                throw new RuntimeException(x.getMessage());
            }
        }
        return new SrlDocumentIterator(index-1);
    }

    private class SrlDocumentIterator implements ListIterator<Token> {

        int pos;

        public SrlDocumentIterator(int pos) {
            this.pos = pos;
        }

        public void add(Token e) {
            if(stream != null)
                throw new IllegalStateException("Attempting to add to stream based document");
            tokensRead.add(pos,e);
            readPoint++;
        }

        public void set(Token e) {
            if(stream != null)
                throw new IllegalStateException("Attempting to add to stream based document");
            tokensRead.set(pos, e);
        }

        public void remove() {
            if(stream != null)
                throw new IllegalStateException("Attempting to remove from stream based document");
            tokensRead.remove(pos);
        }

        public boolean hasNext() {
            if (pos == readPoint) {
                try {
                    if(stream == null)
                        return false;
                    Token t = stream.next();
                    if (t == null) {
                        return false;
                    } else {
                        tokensRead.add((Token)t);
                        readPoint++;
                        return true;
                    }
                } catch (IOException x) {
                    x.printStackTrace();
                    return false;
                }
            } else {
                return true;
            }
        }

        public boolean hasPrevious() {
            return pos > 0;
        }

        public Token next() {
            if (pos == readPoint) {
                try {
                    if(stream == null)
                        throw new IllegalStateException();
                    Token t = stream.next();
                    if (t == null) {
                        throw new IllegalStateException();
                    }
                    readPoint++;
                    pos++;
                    tokensRead.add((Token)t);
                    return (Token)t;
                } catch (IOException x) {
                    x.printStackTrace();
                    throw new RuntimeException();
                }
            } else if (pos < readPoint) {
                return tokensRead.get(++pos);
            } else {
                throw new IllegalStateException();
            }
        }

        public int nextIndex() {
            return pos + 1;
        }

        public Token previous() {
            if (pos == 0) {
                throw new IllegalStateException();
            }
            return tokensRead.get(--pos);
        }

        public int previousIndex() {
            return pos - 1;
        }
    }

    @Override
	/**
	 * @throws IllegalStateException If we haven't got there yet on the token stream). If this is a problem  for(Token t : srlDoc) {} first fixes it
	 */
    public Token get(int index) {
        if(tokensRead.size() > index)
            return tokensRead.get(index);
        else if(stream == null)
            throw new ArrayIndexOutOfBoundsException();
        else
            throw new IllegalStateException("get() cannot be called until after the token stream has been completely read");
    }

    @Override
	/**
	 * @throws IllegalStateException If we haven't completed the stream yet.  If this is a problem  for(Token t : srlDoc) {} first fixes it
	 */
    public int size() {
        if(stream == null)
            return tokensRead.size();
        Token t;
        try {
            t = stream.next();
        } catch(IOException x) {
            x.printStackTrace();
            throw new RuntimeException(x.getMessage());
        }
        if(t == null)
            return tokensRead.size();
        tokensRead.add(t);
        readPoint++;
        throw new IllegalStateException("size() cannot be called until after the token stream has been completely read");
            
    }
}
