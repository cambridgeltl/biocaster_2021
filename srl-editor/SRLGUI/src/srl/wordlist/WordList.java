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

import java.util.TreeSet;
import srl.tools.struct.ListenableSet;

/**
 * A word list. This file has no added functionality, it just provides a simple name
 * for word list objects.
 *
 * @author John McCrae, National Institute of Informatics
 */
public class WordList extends ListenableSet<WordListEntry> {

    /**
     * Create a word list.
     */
    public WordList() {
        super(new TreeSet<WordListEntry>());
    }
}
