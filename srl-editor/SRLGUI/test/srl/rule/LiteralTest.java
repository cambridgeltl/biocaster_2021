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
public class LiteralTest {

    public LiteralTest() {
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
     * Test of getQuery method, of class Literal.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        SrlQuery query = new SrlQuery();
        SrlQuery expResult = new SrlQuery();
        expResult.query.append("literal");
        Literal instance = new Literal("literal");
        instance.getQuery(query);
        assertEquals(expResult,query);
    }

    /**
     * Test of matches method, of class Literal.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        Token token = new Token("literal",0,7);
        int no = 0;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        Literal instance = new Literal("literal");
        TypeExpr expResult = new DummyNode();
        instance.setNext(expResult);
        TypeExpr result = instance.matches(token, no, stack, lookBackStack);
        assertEquals(expResult, result);
    }

    /**
     * Test of setNext method, of class Literal.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }

    /**
     * Test of reset method, of class Literal.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of getVal method, of class Literal.
     */
    @Test
    public void testGetVal() {
        System.out.println("getVal");
        Literal instance = new Literal("literal");
        String expResult = "literal";
        String result = instance.getVal();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Literal.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Literal instance = new Literal("literal");
        String expResult = "\"literal\"";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of canEnd method, of class Literal.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        Literal instance = new Literal("literal");
        boolean expResult = false;
        boolean result = instance.canEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Literal.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        System.out.println("skip");
    }

    /**
     * Test of copy method, of class Literal.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        Literal instance = new Literal("literal");
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

}