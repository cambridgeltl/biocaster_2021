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

import java.util.Collection;

/**
 * This object represents a decision point in the match that may need to be
 * back-tracked to
 * @author John McCrae, National Institute of Informatics
 */
public class MatchFork {
    /**
     * The token number where the fork occured
     */
    final public int tokenNo;
    /**
     * The matcher that was the source of the fork
     */
    final public TypeExpr typeExpr;
    /**
     * Has this fork been explored. When the fork is not the current fork, this indicates whether to take the
     * default branch (<code>false</false>) or the non-default branch (<code>true</code>)
     */
    public boolean used;

    /**
     * Create an instance
     * @param tokenNo The token number
     * @param typeExpr The source of the fork
     */
    public MatchFork(int tokenNo, TypeExpr typeExpr) {
        this.tokenNo = tokenNo;
        this.typeExpr = typeExpr;
        used = false;
    }

    /**
     * Call this before taking the other branch.
     * @param tokenNo The token number to split at (should match constructor parameters)
     * @param typeExpr The source (should match constructor parameters)
     * @return <code>true</code> if it is OK to split here. Otherwise <code>false</code>
     * indicating that this fork is taken or the parameters are not the current fork point
     */
    public boolean split(int tokenNo, TypeExpr typeExpr) {
        if(this.tokenNo == tokenNo && this.typeExpr == typeExpr) {
            used = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MatchFork) {
            MatchFork mf = (MatchFork)obj;
            return tokenNo == mf.tokenNo && typeExpr == mf.typeExpr;
        } else
            return false;
    }

    @Override
    public String toString() {
        return typeExpr + "@" + tokenNo + (used ? "*" : "");
    }

    /**
     * Find a fork at a given point
     * @param col The collection to search
     * @param tokenNo The token number
     * @param typeExpr The type expr
     * @return The appropriate Match Fork object, <code>null</code> if no match exists.
     */
    public static MatchFork find(Collection<MatchFork> col, int tokenNo, TypeExpr typeExpr) {
        for(MatchFork mf : col) {
            if(mf.tokenNo == tokenNo && mf.typeExpr == typeExpr)
                return mf;
        }
        return null;
    }
    
}
