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
import srl.tools.struct.*;

/**
 * Matcher representing a single literal.
 * @author John McCrae, National Institute of Informatics
 */
public class Literal implements TypeExpr {

    final String literal;
    
    TypeExpr next;

    /**
     * Create an instance
     * @param literal The literal (no double quotes ("))
     */
    public Literal(String literal) {
        this.literal = literal;
    }

    /**
     * Build the query
     * @param query
     */
    public void getQuery(SrlQuery query) {
        if(query.query.charAt(query.query.length()-1) != '\"')
            query.query.append(" ");
        query.query.append(literal.replaceAll("([\\\"\\\'])", "\\$1"));
    }

    /**
     * Does it match
     * @param token The token
     * @param no The token number (ignored)
     * @param stack The fork stack (ignored)
     * @param lookBackStack The reverse stack (ignored)
     */
    public TypeExpr matches(Token token, int no, Stack<MatchFork> stack, List<Token> lookBackStack) {
        if(token.termText().toLowerCase().equals(literal.toLowerCase())) {
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


    /**
     * Reset the matcher. Does nothing
     */
    public void reset() {
       
    }

    /**
     * Get the literal.
     */
    public String getVal() {
        return literal;
    }

    @Override
    public String toString() {
        return "\"" + literal + "\"";
    }

    /**
     * Can this end? Always no
     * @return false
     */
    public boolean canEnd() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Literal) {
            return literal.equals(((Literal)obj).literal);
        }
        return false;
    }

    /**
     * Create an exact copy of this matcher
     */
    public TypeExpr copy() {
        return new Literal(literal);
    }
}
