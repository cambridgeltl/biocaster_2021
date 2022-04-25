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
import srl.corpus.*;
import org.apache.lucene.analysis.*;
import java.util.Stack;

/**
 * This interface represents a single matcher from the SRL language.
 * @author John McCrae, National Institute of Informatics
 */
public interface TypeExpr {
    /** Get a query in a format suitable for Lucene. This works by adding the type
     * expression value to the current query. Note that the query string is built up
     * with double quotes so it is expected that all matchers should append
     * at least <code>" "</code> to <code>query.query</code>. For example, the rule
     * <ul><li><code>
     *      :- "a" "b" words(,1) "c"
     * </code></li></ul>
     * Is built up as
     * <ol>
     *   <li>" <em>(from initialization)</em></li>
     *   <li>a </li>
     *   <li>b </li>
     *   <li>" " <em>(from </em><code>words(,1)</code><em>)</em></li>
     *   <li>c</li>
     *   <li>" <em>(added at the end)</em></li>
     * </ol>
     * To obtain <code>"a b" "c"</code> as the query
     * @param query The query to be built on.
     */
    public void getQuery(SrlQuery query);
    /** Query if this TypeExpr matches the current token 
     * @param token A single token in the string
     * @param tokenNo The current token number
     * @param stack The stack of backtrace points, as pairs of token numbers & entities
     * @param lookBackStack The stack representing previously matched tokens.
     * @return The next TypeExpr to be checked against the string of tokens,
     * some expressions will return themselves if they can match multiple tokens,
     * <code>null</code> is returned if the match failed.
     */
    public TypeExpr matches(Token token, int tokenNo, Stack<MatchFork> stack, List<Token> lookBackStack);
    /** At the end of a match reset all variables (eg skipwords number)
     */
    public void reset();
    /** Add the next typeExpr in this rule. 
     * @param te The next object
     */
    public void setNext(TypeExpr te);
    /** Is it possible to progress from here to the end of the automata?
     */
    public boolean canEnd();
    /** Create a functional copy of this node. This is not used within SRL, it was
     * used in another project.
     */
    public TypeExpr copy();
}
