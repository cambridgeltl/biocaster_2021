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
import srl.corpus.BeginTagToken;
import srl.corpus.EndTagToken;

/**
 * Represents the basic wildcard. This corresponds to <code>words(... , ...)</code>
 * in the SRL language
 * @author John McCrae, National Institute of Informatics
 */
public class SkipWords implements TypeExpr {
    /** The min number of tokens to match */
    public int min;
    /** The max number of tokens to match */        
    public int max;
    int i;
    TypeExpr next;
    int tagDepth = 0;

    /** Create a new instance
     * @param min Minimum number of tokens to match
     * @param max Maximum number of tokens to match
     */
    public SkipWords(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Build a query
     * @param query
     */
    public void getQuery(SrlQuery query) {
        query.query.append("\" \"");
    }

    private boolean skipTags = true;

    /**
     * If set to true, the matcher will ignore any tags
     * @param value
     */
    public void setSkipTags(boolean value) { skipTags = value; }

    /**
     * Does this match
     * @param token The current token
     * @param no The token number (ignored)
     * @param stack The fork stack. This will be added to if there is a need for more
     * complex stacks
     * @param lookBackStack The reverse stack (ignored)
     */
    public TypeExpr matches(Token token, int no, Stack<MatchFork> stack, List<Token> lookBackStack) {
        // Stack: Two options if next matches 1/ Return next.next 2/ Ignore, return this
        // If this number/expr pair is not on the stack do 1/ and add to stack
        // If this number/expr pair is on top of the stack do 2/ and mark as used
        // If this number/expr pair is on the stack and marked do 2/
        // If this number/expr pair is on the stack and not marked do 1/ (This will only happen if more than one 
        //  pair is added to the stack in a single run).
        // The last two cases are to cover the following case, say we find a branch point in the algorithm
        // at token 3, then another at token 7 then the first branch explored will be at token 7, this
        // requires that at token 3 we choose the default branch. However if having explored the other branch
        // from token 3 we meet another branch at token 8, then when exploring the branch from token 8, at token 3
        // we must choose the non-default action. This is controlled by the mark.
        if(token instanceof BeginTagToken) {
            if(!skipTags)
                return null;
            tagDepth++;
        }
       
        if(i < min) {
            //if(token instanceof BeginTagToken || token instanceof EndTagToken)
                i++;
	    return this;
        }
        MatchFork mf = MatchFork.find(stack, no, this);
        if(mf != null && (mf.used == true || stack.peek() == mf) && (!skipTags || !(token instanceof EndTagToken))) {
            stack.peek().split(no, this);
            if(i < max) {
                i++;
                return this;
            } else {
                return null;
            }
        }
        if(token instanceof EndTagToken) {
            if(!skipTags) {
                TypeExpr te = next.matches(token, no, stack, lookBackStack);
                if((stack.empty() || stack.peek().tokenNo < no) &&
                        !(te == Rule.successState))
                    stack.push(new MatchFork(no,this));
                return te;
            }
            tagDepth--;
            if(tagDepth > 0)
                return this;
        }
        TypeExpr te = next.matches(token,no,stack, lookBackStack);
        if(te != null) {
            if((stack.empty() || stack.peek().tokenNo < no) &&
                    !(te == Rule.successState) &&
                    !(next instanceof EndTag))
                stack.push(new MatchFork(no,this));
            // We have already matched to our next state, so we go straight on
            return te;
        } else if(i < max) {
            next.reset();
            //if(token instanceof BeginTagToken || token instanceof EndTagToken)
                i++;
            return this;
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
     * Reset the matcher to the beginning
     */
    public void reset() {
       i = 0;
       tagDepth = 0;
    }

    @Override
    public String toString() {
        return "words(" + (min == 0 ? "" : min) + "," + (max == Integer.MAX_VALUE ? "" : max) + ")";
    }

    /**
     * Can this matcher end. Depends on if the minimum number of tokens has been met and
     * the next elements.
     */
    public boolean canEnd() {
        if(i >= min)
            return next.canEnd();
        else
            return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SkipWords) {
            SkipWords l = (SkipWords)obj;
            return min == l.min && max == l.max;
        }
        return false;
    }

    /**
     * Create an exact copy
     */
    public TypeExpr copy() {
        return new SkipWords(min, max);
    }
}