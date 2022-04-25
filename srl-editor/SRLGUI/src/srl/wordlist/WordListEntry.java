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
package srl.wordlist;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import srl.tools.strings.Strings;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import srl.corpus.Processor;

/** The class wraps a single entry in a word list. These can
 * be instantied by calling getEntry
 */
public class WordListEntry implements Comparable<WordListEntry> {

    private List<String> words;
    //private String originalVal;
    private final boolean modifiable;

    /**
     * Creates a word list. If this constructor is used the word list is fixed
     * @param val
     * @param processor
     */
    public WordListEntry(String val, Processor processor) {
        modifiable = false;
        words = new LinkedList<String>();
      //  originalVal = val;
        TokenStream ts = processor.getTokenStream(val.toLowerCase());
        try {
            for (Token s = ts.next(); s != null; s = ts.next()) {
                words.add(s.termText());
            }
        } catch (IOException x) {
            x.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Creates a word list. This is a simple model and can be manually changed.
     * @param words
     */
    public WordListEntry(List<String> words) {
        modifiable = true;
        this.words = words;
        //this.originalVal = "";
    }

    /** (expert) This function is used for constructing an Entry step by step
     * @param s The next token
     */
    public void addWord(String s) {
        if(!modifiable)
            throw new IllegalStateException("Cannot modify word list entry from tokenizer");
        words.add(s.toLowerCase());
    }

    /** Check if this entry could match the parameter dependent on following
     *   tokens. I.e., Is this entry as least as long and matching up until
     * current tokens */
    public boolean matchable(WordListEntry e) {
        if (e.words.size() > words.size()) {
            return false;
        }
        for (int i = 0; i < e.words.size(); i++) {
            if (!e.words.get(i).equals(words.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WordListEntry) {
            return compareTo((WordListEntry) obj) == 0;
        } else {
            return false;
        }
    }

    public int compareTo(WordListEntry o) {
        int n = Math.min(words.size(), o.words.size());
        for (int i = 0; i < n; i++) {
            int t = words.get(i).compareTo(o.words.get(i));
            if (t != 0) {
                return t;
            }
        }
        if (words.size() < o.words.size()) {
            return -1;
        } else if (words.size() > o.words.size()) {
            return 1;
        } else {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return Strings.join(" ", words);
    }
}
