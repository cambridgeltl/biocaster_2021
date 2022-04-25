/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.rule;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import org.apache.lucene.analysis.Token;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import srl.corpus.SrlQuery;

/**
 *
 * @author john
 */
public class OptionalLiteralTest {

    public OptionalLiteralTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of matches method, of class OptionalLiteral.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        Token token = new Token("literal",0,7);
        int tokenNo = 0;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        OptionalLiteral instance = new OptionalLiteral("\"optional\"");
        TypeExpr te = new Literal("literal");
        instance.setNext(te);
        TypeExpr expResult = new DummyNode();
        te.setNext(expResult);
        TypeExpr result = instance.matches(token, tokenNo, stack, lookBackStack);
        assertEquals(expResult, result);

        instance.reset();
        token = new Token("optional",0,8);
        expResult = te;
        result = instance.matches(token, tokenNo, stack, lookBackStack);
        assertEquals(expResult, result);
    }

    /**
     * Test of canEnd method, of class OptionalLiteral.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        OptionalLiteral instance = new OptionalLiteral("\"literal\"");
        boolean expResult = true;
        instance.setNext(new DummyNode());
        boolean result = instance.canEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of copy method, of class OptionalLiteral.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        OptionalLiteral instance = new OptionalLiteral("\"optional\"");
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

    /**
     * Test of getQuery method, of class OptionalLiteral.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        SrlQuery query = new SrlQuery();
        SrlQuery expResult = new SrlQuery();
        expResult.query.append("\" \"");
        OptionalLiteral instance = new OptionalLiteral("\"optional\"");
        instance.getQuery(query);
        assertEquals(expResult, query);
    }

    /**
     * Test of reset method, of class OptionalLiteral.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of setNext method, of class OptionalLiteral.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }

   
    /**
     * Test of toString method, of class OptionalLiteral.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        OptionalLiteral instance = new OptionalLiteral("\"optional\"");
        String expResult = "optional(\"optional\")";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

}