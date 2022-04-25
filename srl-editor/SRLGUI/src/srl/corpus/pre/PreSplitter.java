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
package srl.corpus.pre;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.analysis.Token;
import srl.corpus.Splitter;
import srl.corpus.SrlDocument;

/**
 *
 * @author john
 */
public class PreSplitter implements Splitter {

    public List<SrlDocument> split(Collection<Token> doc, String docName) {
       int docNumber = 0;
       SrlDocument srlDoc = new SrlDocument(docName + " " + docNumber);
       List<SrlDocument> rv = new LinkedList<SrlDocument>();
       
       for(Token t : doc) {
           if(t.termLength() == 1 && (t.termBuffer()[0] == PreTokenizer.SPLITTER_CHAR1 || t.termBuffer()[0] == PreTokenizer.SPLITTER_CHAR2)) {
               if(srlDoc.size() != 0)
                    rv.add(srlDoc);
               srlDoc = new SrlDocument(docName + " " + ++docNumber);
           } else {
               srlDoc.add(t);
           }
       }
       if(!srlDoc.isEmpty())
           rv.add(srlDoc);
        
       return rv;
    }

    public List<SrlDocument> split(String doc, String docName) {
        int docNumber = 0;
       SrlDocument srlDoc = new SrlDocument(docName + " " + docNumber);
       List<SrlDocument> rv = new LinkedList<SrlDocument>();
       PreTokenizer tokenizer = new PreTokenizer(new StringReader(doc));
       try {
       Token t;
       while((t = tokenizer.next()) != null) {
           if(t.termLength() == 1 && t.termBuffer()[0] == PreTokenizer.SPLITTER) {
               rv.add(srlDoc);
               srlDoc = new SrlDocument(docName + " " + ++docNumber);
           } else {
               srlDoc.add(t);
           }
       }
       } catch(IOException x) {
           x.printStackTrace();
           return null;
       }
        
       return rv;
    }

}
