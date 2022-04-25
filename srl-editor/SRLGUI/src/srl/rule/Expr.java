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

/**
 * This interface represents common elements between Entity and Rule.
 *
 * @author John McCrae, National Institute of Informatics
 */
public interface Expr {
    /**
     * Add a type expression to the body
     * @param typeExpr
     */
    public void addTypeExpr(TypeExpr typeExpr);

    /**
     * Get the rule type
     * @see Rule#ENTITY_RULE
     * @see Rule#TEMPLATE_RULE
     */
    public int getRuleType();

    /**
     * Get the body of the rule
     */
    public List<TypeExpr> getBody();
}
