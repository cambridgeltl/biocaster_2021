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
import org.apache.lucene.analysis.*;
import java.util.*;

/**
 * This interface represents a sentence splitter and the functions it
 * is supposed to provide
 * @author john
 */
public interface Splitter {
    /** Split a document into sentences
     * @param doc The document
     * @param docName Documents like to have names, like dogs
     */
    public List<SrlDocument> split(Collection<Token> doc, String docName);
    /** Split a document into sentences
     * @param doc The document
     * @param docName Documents like to have names, like dogs
     */
    public List<SrlDocument> split(String doc, String docName);
}
