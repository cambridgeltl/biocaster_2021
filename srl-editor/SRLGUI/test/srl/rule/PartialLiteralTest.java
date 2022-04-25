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
public class PartialLiteralTest {

    public PartialLiteralTest() {
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
     * Test of matches method, of class PartialLiteral.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        Token token = new Token("geneva",0,6);
        int tokenNo = 0;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        PartialLiteral instance = new PartialLiteral("ge", PartialLiteral.BEGIN);
        TypeExpr expResult = new DummyNode();
        instance.setNext(expResult);
        TypeExpr result = instance.matches(token, tokenNo, stack, lookBackStack);
        assertEquals(expResult, result);

        instance = new PartialLiteral("va", PartialLiteral.END);
        expResult = new DummyNode();
        instance.setNext(expResult);
        result = instance.matches(token, tokenNo, stack, lookBackStack);
        assertEquals(expResult, result);

        instance = new PartialLiteral("ne", PartialLiteral.CONTAINS);
        expResult = new DummyNode();
        instance.setNext(expResult);
        result = instance.matches(token, tokenNo, stack, lookBackStack);
        assertEquals(expResult, result);
    }

    /**
     * Test of canEnd method, of class PartialLiteral.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        PartialLiteral instance = new PartialLiteral("part", PartialLiteral.BEGIN);
        boolean expResult = false;
        boolean result = instance.canEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of copy method, of class PartialLiteral.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        PartialLiteral instance = new PartialLiteral("part", PartialLiteral.END);
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

    /**
     * Test of getQuery method, of class PartialLiteral.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        SrlQuery query = new SrlQuery();
        SrlQuery expResult = new SrlQuery();
        expResult.query.append("\" \"");
        PartialLiteral instance = new PartialLiteral("part", PartialLiteral.END);
        instance.getQuery(query);
        assertEquals(expResult, query);
    }

    /**
     * Test of reset method, of class PartialLiteral.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of setNext method, of class PartialLiteral.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }


    /**
     * Test of toString method, of class PartialLiteral.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        PartialLiteral instance = new PartialLiteral("part", PartialLiteral.BEGIN);
        String expResult = "begins(\"part\")";
        String result = instance.toString();
        assertEquals(expResult, result);

        instance = new PartialLiteral("part", PartialLiteral.END);
        expResult = "ends(\"part\")";
        result = instance.toString();
        assertEquals(expResult, result);

        instance = new PartialLiteral("part", PartialLiteral.CONTAINS);
        expResult = "contains(\"part\")";
        result = instance.toString();
        assertEquals(expResult, result);
    }

}