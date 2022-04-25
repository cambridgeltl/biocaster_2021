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

import org.apache.lucene.analysis.Token;
import srl.corpus.SrlQuery;
import java.util.*;

/**
 * A case-sensitive literal. This corresponds to case("LiTeRaL") in the SRL language
 * and will match only if the case matches... i.e., the example will only match 
 * "LiTeRaL" not "literal"
 *
 * @author John McCrae, National Institute of Informatics
 */
public class CaseSensitiveLiteral implements TypeExpr {

    final String literal;
    
    TypeExpr next;

    /**
     * Create an instance. Parameter doesn't need double quotes (").
     *
     * @param literal
     */
    public CaseSensitiveLiteral(String literal) {
        this.literal = literal;
    }

    /**
     * Build the query. Adds the literal to the query
     *
     * @param query The query to be added to
     */
    public void getQuery(SrlQuery query) {
        if(query.query.charAt(query.query.length()-1) != '\"')
            query.query.append(" ");
        query.query.append(literal.replaceAll("([\\\"\\\'])", "\\$1"));
    }

    /**
     * Does this match the current token?
     * @param token The token
     * @param no The token number (ignored)
     * @param stack The fork stack (ignored)
     * @param lookBackStack The reverse stack (ignored)
     */
    public TypeExpr matches(Token token, int no, Stack<MatchFork> stack, List<Token> lookBackStack) {
        if(token.termText().equals(literal)) {
            return next;
        } else {
            return null;
        }
    }

    /**
     * Set the next matcher
     * @param te
     */
    public void setNext(TypeExpr te) {
        next = te;
    }

    /**
     * Reset  this matcher. (Does nothing)
     */
    public void reset() {
       
    }

    /**
     * Get the value of the literal.
     * @return The value of the literal
     */
    public String getVal() {
        return literal;
    }

    @Override
    public String toString() {
        return "case(\"" + literal + "\")";
    }

    /**
     * Are we at the end of a completed match. Answer is always no.
     */
    public boolean canEnd() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CaseSensitiveLiteral) {
            return literal.equals(((CaseSensitiveLiteral)obj).literal);
        }
        return false;
    }

    /**
     * Create an exact copy of this matcher
     * @return An exact copy of this matcher
     */
    public TypeExpr copy() {
        return new CaseSensitiveLiteral(literal);
    }
}
