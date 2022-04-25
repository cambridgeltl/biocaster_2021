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
 * Represents the optional matcher. This corresponds to <code>optional</code> in
 * the SRL language.
 * @author John McCrae, National Institute of Informatics
 */
public class OptionalLiteral implements TypeExpr {
    public final String literal;
    public final ListMatch listMatcher;
    private TypeExpr next;
    private DummyNode dummy;
    private boolean first = true;

    /**
     * Create a new instance.
     * @param param If the parameter is a literal this must be a double quoted
     * string (e.g., <code>"literal"</code>) otherwise a list name with @ or %
     * (e.g., <code>@list</code>)
     */
    public OptionalLiteral(String param) {
        if(param.charAt(0) == '\"') {
            this.literal = param.toLowerCase().substring(1,param.length()-1);
            listMatcher = null;
        } else {
            this.literal = null;
            listMatcher = new ListMatch(param);
            dummy = new DummyNode();
            listMatcher.setNext(dummy);
        }
    }

    /**
     * Does this match?
     * @param token The current token
     * @param tokenNo The token number
     * @param stack The fork stack... may be used if there are partial matches
     * @param lookBackStack The reverse stack (ignored)
     */
    public TypeExpr matches(Token token, int tokenNo, Stack<MatchFork> stack, List<Token> lookBackStack) {
        if(literal != null) {
            MatchFork mf = MatchFork.find(stack, tokenNo, this);
            if(mf != null && (mf.used || stack.peek() == mf)) {
                mf.split(tokenNo, this);
                return next.matches(token, tokenNo, stack, lookBackStack);
            }
            if(token.termText().toLowerCase().equals(literal)) {
                if(next.matches(token, tokenNo, stack, lookBackStack) != null) {
                    if((stack.empty() || stack.peek().tokenNo < tokenNo) &&
                        !(next == Rule.successState))
                        stack.push(new MatchFork(tokenNo, this));
                }
                next.reset();
                return next;
            } else 
                return next.matches(token, tokenNo, stack,lookBackStack);
        } else {
            TypeExpr te = listMatcher.matches(token,tokenNo,stack,lookBackStack);
            if(te == null && first)
                return next.matches(token, tokenNo, stack,lookBackStack);
            if(te == null)
                return null;
            if(te == dummy)
                return next;
            if(te == listMatcher) {
                first = false;
                return this;
            }
            else
                throw new IllegalStateException();
        }
    }

    /**
     * Can this end. Depeneds on the next statements
     */
    public boolean canEnd() {
        return next.canEnd();
    }

    /**
     * Create an exact copy of this
     */
    public TypeExpr copy() {
        if(literal != null)
            return new OptionalLiteral("\"" + literal + "\"");
        else
            return new OptionalLiteral((listMatcher.set ? "%" : "@") + listMatcher.wordListName);
    }

    /**
     * Build the query
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
        first = true;
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
            return "optional(\"" + literal + "\")";
        else if(listMatcher.set) 
            return "optional(%" + listMatcher.wordListName + ")";
        else
            return "optional(@" + listMatcher.wordListName + ")";
    }

    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof OptionalLiteral) {
            OptionalLiteral arg = (OptionalLiteral)arg0;
            return (literal != null && arg.literal != null && literal.equals(arg.literal)) ||
                    (listMatcher != null && arg.listMatcher != null &&
                     listMatcher.wordListName.equals(arg.listMatcher.wordListName) &&
                     listMatcher.set == arg.listMatcher.set);
        } else
            return false;
    }
}
