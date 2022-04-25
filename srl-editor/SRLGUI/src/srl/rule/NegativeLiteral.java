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

import java.util.List;
import java.util.Stack;
import org.apache.lucene.analysis.Token;
import srl.corpus.SrlQuery;

/**
 * This represents the negative matcher. Corresponds to <code>not(...)</code> in
 * the SRL language. This matcher is a <it>zero-width, look-behind</it> assertion,
 * which means that it matches no term but instead checks that the reverse stack
 * does not have the appropriate literal or word-list element on top.
 * @author John McCrae, National Institute of Informatics
 */
public class NegativeLiteral implements TypeExpr {
    public final String literal;
    public final ListMatch listMatcher;
    private TypeExpr next;
    private DummyNode dummy;

    /**
     * Create a new instance
     * @param literal If the matcher is for a literal specify it with double quotes
     * (e.g., <code>"literal"</code>), for lists specify with % or @ (e.g.,
     * <code>@list</code>)
     */
    public NegativeLiteral(String literal) {
        if(literal.charAt(0) == '\"') {
            this.literal = literal.toLowerCase().substring(1,literal.length()-1);
            listMatcher = null;
        } else {
            this.literal = null;
            listMatcher = new ListMatch(literal);
            dummy = new DummyNode();
            listMatcher.setNext(dummy);
        }
    }

    /**
     * Does this match the current token. In fact this matcher does not check the
     * current token but instead the top element(s) of <code>lookBackStack</code>
     * @param token The current token (ignored)
     * @param tokenNo The token number (ignoed)
     * @param stack The fork stack (ignored)
     * @param lookBackStack The reverse stack
     */
    public TypeExpr matches(Token token, int tokenNo, Stack<MatchFork> stack, List<Token> lookBackStack) {
        if(literal != null) {
            if(lookBackStack.isEmpty()) 
                return next.matches(token,tokenNo,stack,lookBackStack);
            if(lookBackStack.get(lookBackStack.size()-1).termText().toLowerCase().equals(literal)) {
                return null;
            } else 
                return next.matches(token, tokenNo, stack,lookBackStack);
        } else {
            
            for(int i = lookBackStack.size()-1; i >= 0; i--) {
                TypeExpr te = listMatcher.matches(lookBackStack.get(i),tokenNo,stack,lookBackStack);
                if(te == null)
                    return next.matches(token, tokenNo, stack, lookBackStack);
                if(te == dummy)
                    return null;
                if(te == listMatcher)
                    continue;
                else
                    throw new IllegalStateException();
            }
            return next.matches(token, tokenNo, stack, lookBackStack);
        }
    }

    /**
     * Can this end. Answer depends on following matchers
     */
    public boolean canEnd() {
        return next.canEnd();
    }

    /**
     * Create an exact copy
     */
    public TypeExpr copy() {
        if(literal != null) 
            return new NegativeLiteral("\"" + literal + "\"");
        else
            return new NegativeLiteral((listMatcher.set ? "%" : "@") + listMatcher.wordListName);
    }

    /**
     * Build the query.
     * @param query
     */
    public void getQuery(SrlQuery query) {
        query.query.append("\" \"");
    }

    /**
     * Reset the matcher
     */
    public void reset() {
        if(listMatcher != null)
            listMatcher.reset();
    }

    /**
     * Set the next matcher
     * @param te
     */
    public void setNext(TypeExpr te) {
        next = te;
    }

    
    public String toString() {
        if(literal != null)
            return "not(\"" + literal + "\")";
        else if(listMatcher.set)
            return "not(%" + listMatcher.wordListName + ")";
        else
            return "not(@" + listMatcher.wordListName + ")";
    }

    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof NegativeLiteral) {
            NegativeLiteral nl = (NegativeLiteral)arg0;
            return (literal != null && nl.literal != null && literal.equals(nl.literal)) ||
                    (listMatcher != null && nl.listMatcher != null &&
                    listMatcher.wordListName.equals(nl.listMatcher.wordListName) &&
                    listMatcher.set == nl.listMatcher.set);
        } else
            return false;
    }


}
