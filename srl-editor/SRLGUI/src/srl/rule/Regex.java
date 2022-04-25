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
 * Represents the regular expression matcher. This corresponds to <code>regex(...)</code>
 * in the SRL language
 * @author john
 */
public class Regex implements TypeExpr {

    final String expression;
    
    TypeExpr next;

    /**
     * Create a new instance.
     * @param expression The regular expression without double quotes (")
     */
    public Regex(String expression) {
        this.expression = expression;
    }

    /**
     * Build a query
     * @param query
     */
    public void getQuery(SrlQuery query) {
        query.query.append("\" \"");
    }

    /**
     * Does this match
     * @param token The token
     * @param no The token number (ignored)
     * @param stack The fork stack (ignored)
     * @param lookBackStack The reverse stack (ignored)
     */
    public TypeExpr matches(Token token, int no, Stack<MatchFork> stack, List<Token> lookBackStack) {
        if(token.termText().matches(expression)) {
            return next;
        } else {
            return null;
        }
    }

    /**
     * Set the next matcher
     */
    public void setNext(TypeExpr te) {
        next = te;
    }

    /** Reset the matcher
     *
     */
    public void reset() {
       
    }

    @Override
    public String toString() {
        return "regex(\"" + expression + "\")";
    }

    /** Can this end. Always no
     */
    public boolean canEnd() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Regex) {
            return ((Regex)obj).expression.equals(expression);
        }
        return false;
    }

    /**
     * Create an exact copy
     */
    public TypeExpr copy() {
        return new Regex(expression);
    }
}
