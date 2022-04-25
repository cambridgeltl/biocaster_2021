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
import srl.corpus.*;
import srl.tools.struct.*;
import java.util.*;

/**
 * This matcher represents a begin tag. This does not correspond to a element of
 * the SRL language but is part of the implementation of entities.
 * @author John McCrae, National Institute of Informatics
 */
public class BeginTag implements TypeExpr {
    final String entityType, entityValue;
    TypeExpr next;

    /**
     * Create an instance
     * @param entityType The type of the tag
     * @param entityValue The value (cl="...") of the tag
     */
    public BeginTag(String entityType, String entityValue) {
        this.entityType = entityType;
        this.entityValue = entityValue;
    } 
    
    /**
     * Build the query.
     * @param query The query to build onto.
     */
    public void getQuery(SrlQuery query) {
        query.entities.add(new Pair<String,String>(entityType,entityValue));
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
        if(token instanceof BeginTagToken) {
            BeginTagToken btt = (BeginTagToken)token;
            if(btt.type.equals(entityType) && btt.val.equals(entityValue))
                return next;
            else
                return null;
        } else {
            return null;
        }
    }

    /**
     * Set the next matcher.
     * @param te
     */
    public void setNext(TypeExpr te) {
        this.next = te;
    }

    /**
     * Reset the matcher. Does nothing
     */
    public void reset() {
        
    }

    /**
     * Are we at the end of a completed match. Answer is always no
     */
    public boolean canEnd() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BeginTag) {
            BeginTag bt = (BeginTag)obj;
            return entityType.equals(bt.entityType) && entityValue.equals(bt.entityValue);
        }
        return false;
    }

    /**
     * Create an exact copy of this matcher.
     */
    public TypeExpr copy() {
        return new BeginTag(entityType,entityValue);
    }
}
