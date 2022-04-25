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
 * This matches to a partial literal. This corresponds to <code>begins(...)</code>,
 * <code>ends(...)</code> and <code>contains(...)</code> in the SRL language
 *
 * @author John McCrae, National Institute of Informatics
 */
public class PartialLiteral implements TypeExpr {
    /**
     * The partial literal
     */
    public final String partLiteral;
    /**
     * Match begins, ends or contains
     */
    public final int part;
    private TypeExpr next;
    /**
     * Constant for begin match
     */
    public static final int BEGIN = 0;
    /**
     * Constant for end match
     */
    public static final int END = 1;
    /**
     * Constant for contains match
     */
    public static final int CONTAINS = 2;

    /**
     * Create a new instance
     * @param partLiteral The partial literal, no double quotes (")
     * @param part Either <code>PartialLiteral.BEGIN</code> or <code>PartialLiteral.END</code> or <code>PartialLiteral.CONTAINS</code>
     */
    public PartialLiteral(String partLiteral, int part) {
        this.partLiteral = partLiteral;
        this.part = part;
    }

    /**
     * Does this match?
     * @param token The current token
     * @param tokenNo The token number
     * @param stack The fork stack
     * @param lookBackStack The reverse stack (ignored)
     */
    public TypeExpr matches(Token token, int tokenNo, Stack<MatchFork> stack, List<Token> lookBackStack) {
        if(token.termLength() < partLiteral.length()) {
            return null;
        }
        if(part == BEGIN && token.termText().toLowerCase().substring(0, partLiteral.length()).equals(partLiteral) ||
                part == END && endString(token.termText().toLowerCase(),partLiteral.length()).equals(partLiteral) ||
                part == CONTAINS && token.termText().toLowerCase().contains(partLiteral))
            return next;
        else
            return null;
    }

    private static String endString(String string, int n) {
        if(n >= string.length())
            return string;
        else
            return string.substring(string.length()-n);
    }

    /**
     * Can this end? Always no
     */
    public boolean canEnd() {
        return false;
    }

    /**
     * Create an exact copy
     */
    public TypeExpr copy() {
        return new PartialLiteral(partLiteral, part);
    }

    /**
     * Build a query
     * @param query
     */
    public void getQuery(SrlQuery query) {
        query.query.append("\" \"");
    }

    /**
     * Reset the matcher
     */
    public void reset() {
    }

    /**
     * Set the next matcher
     * @param te
     */
    public void setNext(TypeExpr te) {
        next = te;
    }

    
    public String toString() {
        if(part == BEGIN) {
            return "begins(\"" + partLiteral + "\")";
        } else if(part == END) {
            return "ends(\"" + partLiteral + "\")";
        } else if(part == CONTAINS) {
            return "contains(\"" + partLiteral + "\")";
        } else {
            return "<<ERROR>>";
        }
    }

    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof PartialLiteral) {
            return partLiteral.equals(((PartialLiteral)arg0).partLiteral) &&
                    part == ((PartialLiteral)arg0).part;
        } else
            return false;
    }


}
