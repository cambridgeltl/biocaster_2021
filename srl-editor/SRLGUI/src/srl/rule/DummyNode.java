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
import srl.corpus.SrlQuery;
import org.apache.lucene.analysis.Token;
/**
 * A dummy node. This primarily means the end of this matcher
 *
 * @author John McCrae, National Institute of Informatics
 */
    
class DummyNode implements TypeExpr {
    TypeExpr next;

    /**
     * Create an instance
     */
    public DummyNode() { 
    }

    /**
     * Build the query. Should never be called throws IllegalStateException
     * @param query
     */
    public void getQuery(SrlQuery query) {
        throw new IllegalStateException();
    }

    /**
     * Does this match the token. Behaviour is to check the next if the next is set
     * or return itself otherwise.
     * @param token The token
     * @param no The token number (ignored)
     * @param stack The fork stack (ignored)
     * @param lookBackStack The reverse stack (ignored)
     * @return
     */
    public TypeExpr matches(Token token, int no, Stack<MatchFork> stack, List<Token> lookBackStack) {
        if(next != null) {
            TypeExpr te = next.matches(token,no,stack,lookBackStack);
            if(te == null)
                next.reset();
            return te;
        } else {
            return this;
        }
    }

    /**
     * Reset the matcher. Does nothing
     */
    public void reset() {
    }

    /**
     * Set the next matcher. Use this if there is something outside the current
     * matcher
     * @param te
     */
    public void setNext(TypeExpr te) {
        next = te;
    }

    /**
     *  Are we at the end of a completed match. Yes as this normally indicates the end.
     * @return
     */
    public boolean canEnd() {
        return true;
    }

    /**
     * Create a copy
     * @return
     */
    public TypeExpr copy() {
        return new DummyNode();
    }

    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof DummyNode) {
            return true;
        } else
            return false;
    }
}