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
package srl.rule;

import srl.wordlist.*;
import java.util.*;
import srl.tools.struct.ListenableSet;
import org.apache.lucene.analysis.Token;
import srl.corpus.SrlQuery;

/**
 * Matcher to a list. This corresponds to <code>list(@list)</code> in the SRL
 * language.
 * @author John McCrae, National Institute of Informatics
 */
public class ListMatch implements TypeExpr {

    TypeExpr next;
    String wordListName;
    SortedSet<WordListEntry> matches = null;
    WordListEntry currentMatch = null;
    final boolean set;

    /**
     * Create an new instance
     * @param wordListName The word list name, must have an @ or % before it
     */
    public ListMatch(String wordListName) {
        if(wordListName.charAt(0) == '@')
            set = false;
        else if(wordListName.charAt(0) == '%')
            set = true;
        else
            throw new IllegalArgumentException("Word list name must start with @ or %");
        this.wordListName = wordListName.substring(1);
    }

    /**
     * Build the query
     * @param query
     */
    public void getQuery(SrlQuery query) {
        query.query.append("\" \"");
        if(set)
            query.wordListSets.add(wordListName);
        else
            query.wordLists.add(wordListName);
    }

    /**
     * Does this match?
     * @param token The current token
     * @param no The token number
     * @param stack The fork stack... may be used if there are partial matches
     * @param lookBackStack The reverse stack (ignored)
     */
    public TypeExpr matches(Token token, int no, Stack<MatchFork> stack, List<Token> lookBackStack) {
        if(matches == null) {
            if(set) {
                matches = new TreeSet<WordListEntry>();
                WordListSet wls = WordListSet.getWordListSetByName(wordListName);
                if(wls == null) {
                        throw new IllegalArgumentException("Cannot find word list set %" + wordListName);
                }
                for(Map.Entry<String,WordList> entry : wls.getWordListSets()) {
                    matches.addAll(WordListSet.getMatchSet(entry.getKey(), token.termText().toLowerCase()));
                }
                //currentMatch = wls.getEntry(token.termText().toLowerCase());
                currentMatch = new WordListEntry(new LinkedList<String>());
                currentMatch.addWord(token.term().toLowerCase());
            } else {
                matches = new TreeSet<WordListEntry>(WordListSet.getMatchSet(wordListName, token.termText().toLowerCase()));
                //currentMatch = WordListSet.getWordListSetByList(wordListName).getEntry(token.termText().toLowerCase());
                currentMatch = new WordListEntry(new LinkedList<String>());
                currentMatch.addWord(token.term().toLowerCase());
            }
        } else {
            currentMatch.addWord(token.termText().toLowerCase());
        }
        MatchFork mf = MatchFork.find(stack, no, this);
        if(mf != null && (mf.used == true || stack.peek() == mf)) {
            stack.peek().split(no, this);
            return this;
        }
        Iterator<WordListEntry> wleIter = matches.iterator();
        while(wleIter.hasNext()) {
            WordListEntry wle = wleIter.next();
            if(wle.equals(currentMatch)) {
                if(matches.size() > 1 && (stack.empty() || stack.peek().tokenNo < no))
                    stack.push(new MatchFork(no,this));
                return next;
            }
            if(!wle.matchable(currentMatch))
                wleIter.remove();
        }
        if(matches.isEmpty())
            return null;
        else
            return this;
    }

    /**
     * Set the next matcher
     * @param te
     */
    public void setNext(TypeExpr te) {
        next = te;
    }

    /**
     * Reset this matcher to the beginning
     */
    public void reset() {
        matches = null;
        currentMatch = null;
    }

    @Override
    public String toString() {
        return "list(" + (set ? "%" : "@") + wordListName + ")";
    }

    /**
     * Can this end. Always returns false, if it is possible for this to complete
     * this is handled by the fork stack (see "fire breathing dragons" example)
     */
    public boolean canEnd() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ListMatch) {
            return ((ListMatch)obj).wordListName.equals(wordListName) && ((ListMatch)obj).set == set;
        }
        return false;
    }

    /**
     * Create an exact copy of this matcher
     */
    public TypeExpr copy() {
        return new ListMatch((set ? "%" : "@") + wordListName);
    }
}
