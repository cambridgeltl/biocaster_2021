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

import java.util.HashSet;
import java.util.Set;
import srl.tools.strings.Strings;
import srl.tools.struct.Pair;

/**
 * This represents a query on the Corpus object. Corpus can be queried on 4
 * indices, the raw text, matches to any word list or word list set and any tagged entities
 * @author John McCrae, National Institute of Informatics
 */
public class SrlQuery {
    /** The string part of the query */
    public StringBuffer query;
    /** The word lists in this query */
    public Set<String> wordLists;
    /** The word list sets in this query */
    public Set<String> wordListSets;
    /** The entities in this query */
    public Set<Pair<String,String>> entities;
    
    public SrlQuery() {
        wordLists = new HashSet<String>();
        wordListSets = new HashSet<String>();
        query = new StringBuffer("\"");
        entities = new HashSet<Pair<String,String>>();
    }

    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof SrlQuery) {
            return query.toString().equals(((SrlQuery)arg0).query.toString()) &&
                    wordListSets.equals(((SrlQuery)arg0).wordListSets) &&
                    wordLists.equals(((SrlQuery)arg0).wordLists) &&
                    entities.equals(((SrlQuery)arg0).entities);
        } else
            return false;
    }

    @Override
    public String toString() {
        return "<query: " + query.toString() + " ||| wordLists: " + Strings.join(",", wordLists) +
                " ||| wordListSets: " + Strings.join(",", wordListSets) + " ||| entities " +
                Strings.join(",", entities) + ">";
    }
}
