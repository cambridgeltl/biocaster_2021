/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package srl.rule;

import org.apache.lucene.analysis.Token;
import srl.corpus.*;
import java.util.*;

/**
 * This matcher represents an end tag. This does not correspond to a element of
 * the SRL language but is part of the implementation of entities.
 *
 * @author John McCrae
 */
public class EndTag implements TypeExpr {
    final String entityType;
    TypeExpr next;

    /**
     * Create an instance
     * @param entityType The entity type
     */
    public EndTag(String entityType) {
        this.entityType = entityType;
    } 
    
    /**
     * Build the query. Does nothing.
     * @param query
     */
    public void getQuery(SrlQuery query) {
        query.query.append("\" \"");
    }

    /**
     * Does this match.
     * @param token The token
     * @param tokenNo The token number (ignored)
     * @param stack The fork stack (ignored)
     * @param lookBackStack The reverse stack (ignored)
     */
    public TypeExpr matches(Token token, int tokenNo, Stack<MatchFork> stack, List<Token> lookBackStack) {
        if(token instanceof EndTagToken) {
            EndTagToken ett = (EndTagToken)token;
            if(ett.type.equals(entityType))
                return next;
            else
                return null;
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
     * Reset the matcher. Does nothing
     */
    public void reset() {
        
    }

    /**
     *  Are we at the end of a completed match. Answer is always no.
     */
    public boolean canEnd() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof EndTag) {
            EndTag bt = (EndTag)obj;
            return entityType.equals(bt.entityType);
        }
        return false;
    }

    /**
     * Create an exact copy
     */
    public TypeExpr copy() {
        return new EndTag(entityType);
    }
}
