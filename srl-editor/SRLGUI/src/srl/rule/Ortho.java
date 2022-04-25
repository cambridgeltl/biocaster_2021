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
import java.util.regex.*;

/**
 * Represents the orthographic matcher. This corresponds <code>ortho(...)</code>
 * @author John McCrae, National Institute of Informatics
 */
public class Ortho implements TypeExpr {

    final String[][] expressions;
    final String baseExpr;
    
    TypeExpr next;

    /**
     * Create a new instance
     * @param expression The ortho expression, no double quotes (")
     */
    public Ortho(String expression) {
        this.expressions = readExpressions(expression);
        baseExpr = expression;
    }

    /**
     * Build the query
     * @param query
     */
    public void getQuery(SrlQuery query) {
        query.query.append("\" \"");
    }

    /**
     * Does this match?
     * @param token The current token
     * @param no The token number
     * @param stack The fork stack... may be used if there are partial matches
     * @param lookBackStack The reverse stack (ignored)
     */
    public TypeExpr matches(Token token, int no, Stack<MatchFork> stack, List<Token> lookBackStack) {
        LOOP: for(String[] expBlock : expressions) {
            for(String exp : expBlock) {
                if(!token.termText().matches(exp))
                    continue LOOP;
            }
            return next;
        }
        return null;
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
    
    boolean hasStartCheck = false;
    private String[][] readExpressions(String expression) {
        String[] blockExprs = expression.split("\\|");
        String[][] rval = new String[blockExprs.length][];
        int j = 0;
        for(String blockExpr : blockExprs) {
            String[] exprs = blockExpr.split("&");
            String[] rv = new String[exprs.length];
            for(int i = 0; i < exprs.length; i++) {
                Matcher m = Pattern.compile("(\\^)?(\\d*)(\\+)?(\\w+)").matcher(exprs[i]);
                if(!m.matches()) {
                    throw new IllegalArgumentException("Unrecognised ortho expression: " + exprs[i]);
                }
                boolean initial = m.group(1) != null && !m.group(1).equals("");
                int number;
                boolean fixed;
                if(m.group(2) != null && !m.group(2).equals("")) {
                    number = Integer.parseInt(m.group(2));
                    fixed = m.group(3) == null || m.group(3).equals("");
                } else {
                    fixed = false;
                    number = -1;
                }
                
                String type = "\\p{" + m.group(4) + "}";
                if(m.group(4).equals("Np")) {
                    if(number == -1 && !initial && !fixed)
                        rv[i] = "[0-9][0-9,.]*";
                    else if(initial && !fixed)
                        rv[i] = "[0-9].*" + (number > 1 ? "[0-9,.]{" + (number-1) + "}": "") + ".*";
                    else if(fixed) 
                        rv[i] = "[0-9]" + (number > 1 ?  "[0-9,.]{" + (number - 1) + "}": "");
                    else if(!initial)
                        rv[i] = ".*([0-9].*)" + (number > 1 ? "{" + number + ",}" : "");
                } else {
                    if(number == -1 && !initial && !fixed) {
                        rv[i] = type + "+";
                    } else if(initial && !fixed) {
                        rv[i] = type + ".*" + (number > 1 ? ("(" + type + ".*){" + (number-1) + ",}") : "") + ".*";
                    } else if(fixed) {
                        rv[i] = type + (number > 1 ? "{" + number + "}" : "");
                    } else if(!initial) {
                        rv[i] = ".*(" + type + ".*)" + (number > 1 ? "{" + number + ",}" : "");
                    }
                }
            }
            rval[j++] = rv;
        }
        return rval;
    }
    
    private String regexForOrtho(char ortho) {
        switch(ortho) {
            case 'u':
                return "\\p{Lu}";
            case 'l':
                return "\\p{Ll}";
            case 'd':
                return "\\p{Nd}";
            case 'p':
                return "\\p{P}";
            case 's':
                return "\\p{S}";
            case '(':
                return "\\p{Ps}";
            case ')':
                return "\\p{Pe}";
            case '-':
                return "-";
            default:
                throw new IllegalArgumentException("Unrecognised ortho type: " + ortho);
        }
    }
    

    @Override
    public String toString() {
        return "ortho(\"" + baseExpr + "\")";
    }

    /**
     * Can this end. Always no
     */
    public boolean canEnd() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Ortho) {
            return ((Ortho)obj).baseExpr.equals(baseExpr);
        }
        return false;
    }

    /**
     * Create an exact copy
     */
    public TypeExpr copy() {
        return new Ortho(baseExpr);
    }
}
