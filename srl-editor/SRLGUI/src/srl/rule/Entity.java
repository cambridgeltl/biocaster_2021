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

import java.util.*;
import srl.tools.strings.Strings;
import srl.corpus.SrlQuery;
import org.apache.lucene.analysis.Token;
import srl.corpus.BeginTagToken;


/**
 * A matcher representing an entity expression.
 *
 * @author John McCrae, National Institute of Informatics
 */
public class Entity implements TypeExpr, Expr, Comparable<Entity> {

    /**
     * The body of the entity
     */
    public List<TypeExpr> body;

    /**
     * The entities values.
     */
    public final String entityType, entityValue, var;
    
    TypeExpr current;
    TypeExpr next;
    SrlMatchRegion match;
    /**
     * The rule type
     */
    public final int ruleType;
    

    /**
     * Create an instance. Note it <em> is necessary</em> to add a body to this
     * before using
     * @param entityType The type of the entity
     * @param entityValue The value of the entity (i.e., cl="...")
     * @param var The variable
     * @param ruleType The rule type
     */
    public Entity(String entityType, String entityValue, String var, int ruleType) {
        this.entityType = entityType;
        this.entityValue = entityValue;
        this.var = var;
        this.ruleType = ruleType;
        body = new LinkedList<TypeExpr>();
        if(ruleType == Rule.TEMPLATE_RULE) {
            EndTag endTag = new EndTag(entityType);
            BeginTag bt = new BeginTag(entityType, entityValue);
            bt.setNext(endTag);
            body.add(bt);
            body.add(endTag);
            current = bt;
        } 
        match = new SrlMatchRegion();
    }
    
    /**
     * Build the query
     * @param query
     */
    public void getQuery(SrlQuery query) {
        for(TypeExpr te : body) {
            te.getQuery(query);
        }
    }

    /**
     * Does this match the current token
     * @param token The current token
     * @param tokenNo The token number
     * @param stack The fork stack
     * @param lookBackStack The reverse stack
     * @return <code>this</code> if the matching is still in the entity,
     * <code>next</code> if the match is succesful, <code>null</code> is the
     * match fails.
     */
    public TypeExpr matches(Token token, int tokenNo, Stack<MatchFork> stack, List<Token> lookBackStack) {
        current = current.matches(token, tokenNo,stack, lookBackStack);
        if(current == null)
            return null;
        if(match.beginRegion < 0) {
            if(token instanceof BeginTagToken)
                match.beginRegion = tokenNo + 1;
            else 
                match.beginRegion = tokenNo;
        }
	
        if(!body.contains(current) && current == next) {
            if(token.termText().length() >= 1)
                match.value.append(token.termText());
            /*if(token instanceof EndTagToken)
                match.endRegion = tokenNo;
            else*/
                match.endRegion = tokenNo+1;
            if(match.value.toString().matches(".* "))
                match.value.deleteCharAt(match.value.length()-1);
	    return current;
        } else if(!body.contains(current)) { // Change this if nesting occurs
            match.endRegion = tokenNo;
            return current;
        } else {
            if(token.termText().length() >= 1)
                match.value.append(token.termText() + (current instanceof EndTag ? "" : " "));
        }
        return this;
    }

    /** Are we at the end of a completed match. Entity rules depend on the body,
     * however template rules require an end tag to be found
     */
    public boolean canEnd() {
        if(ruleType == Rule.ENTITY_RULE)
            return current.canEnd();
        else // Actually current.canEnd() always returns false in this case
            return false;
    }

    /**
     * Set the next matcher
     * @param te
     */
    public void setNext(TypeExpr te) {
        next = te;
        if(!body.isEmpty())
            body.get(body.size()-1).setNext(te);
    }

    /**
     * Reset the matcher. Returns the matching to the beginning of the entity
     * expression
     */
    public void reset() {
        current = body.get(0);
        match = new SrlMatchRegion();
        for(TypeExpr te : body)
            te.reset();
    }
    
    /**
     * Add an element to the body of this expression
     * @param typeExpr
     */
    public void addTypeExpr(TypeExpr typeExpr) {
        if(!body.isEmpty()) {
            if(ruleType == Rule.ENTITY_RULE)
                body.get(body.size()-1).setNext(typeExpr);
            else
                body.get(body.size()-2).setNext(typeExpr);
        } else {
            current = typeExpr;
        }
        if(ruleType == Rule.ENTITY_RULE) {
            typeExpr.setNext(next);
            body.add(typeExpr);
        } else {
            body.add(body.size()-1,typeExpr);
            typeExpr.setNext(body.get(body.size()-1));
            if(typeExpr instanceof SkipWords) {
                ((SkipWords)typeExpr).setSkipTags(false);
            }
        }
    }

    /**
     * Get the rule type.
     * @see Rule#ENTITY_RULE
     * @see Rule#TEMPLATE_RULE
     */
    public int getRuleType() {
        return ruleType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        if (this.body != other.body && (this.body == null || !this.body.equals(other.body))) {
            return false;
        }
        if (this.entityType != other.entityType && (this.entityType == null || !this.entityType.equals(other.entityType))) {
            return false;
        }
        if (this.entityValue != other.entityValue && (this.entityValue == null || !this.entityValue.equals(other.entityValue))) {
            return false;
        }
        if (this.var != other.var && (this.var == null || !this.var.equals(other.var))) {
            return false;
        }
        return true;
    }

    public int compareTo(Entity o) {
        int rv = var.compareTo(o.var);
        if(rv != 0) return rv;
        rv = entityType.compareTo(o.entityType);
        if(rv != 0) return rv;
        rv = entityValue.compareTo(o.entityValue);
        return rv;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.body != null ? this.body.hashCode() : 0);
        hash = 31 * hash + (this.entityType != null ? this.entityType.hashCode() : 0);
        hash = 31 * hash + (this.entityValue != null ? this.entityValue.hashCode() : 0);
        hash = 31 * hash + (this.var != null ? this.var.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        int skip = (ruleType == Rule.ENTITY_RULE ? 0 : 1); 
        String varForm = var.matches("EXPR.*") ? "" : "," + var;
        if(body.size() == 1 + skip + skip && body.get(skip) instanceof SkipWords && (((SkipWords)body.get(skip)).max == 1 && ruleType == Rule.ENTITY_RULE ||
                ((SkipWords)body.get(skip)).max == Integer.MAX_VALUE && ruleType == Rule.TEMPLATE_RULE)) {
            return entityType + "(" + entityValue + varForm + ")";
        } else if(body.get(skip) instanceof SkipWords && body.get(body.size() - skip - 1) instanceof SkipWords &&
                ((SkipWords)body.get(skip)).min == 0 && ((SkipWords)body.get(skip)).max == Integer.MAX_VALUE &&
                ((SkipWords)body.get(body.size() - skip - 1)).min == 0 && ((SkipWords)body.get(body.size() - skip - 1)).max == Integer.MAX_VALUE) {
                
            return entityType + "*(" + entityValue + varForm + ") {" + Strings.join(" ", body.subList(skip+1,body.size()-skip-1)) + "}";
        } else {
            return entityType + "(" + entityValue + varForm + ") { " + Strings.join(" ", body.subList(skip, body.size()-skip)) + " }";
        }
    }
    /**
     * Get the body
     */
    public List<TypeExpr> getBody() {
        return body;
    }

    /**
     * Copy this type expr. Note this implementation does not copy the body
     */
    public TypeExpr copy() {
        return new Entity(entityType, entityValue, var, ruleType);
    }
}

