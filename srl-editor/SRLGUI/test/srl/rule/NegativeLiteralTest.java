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
public class NegativeLiteralTest {

    public NegativeLiteralTest() {
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
     * Test of matches method, of class NegativeLiteral.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        Token token = new Token("test", 4,8);
        int tokenNo = 1;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        lookBackStack.add(new Token("not",0,3));
        NegativeLiteral instance = new NegativeLiteral("\"not\"");
        TypeExpr expResult = null;
        TypeExpr result = instance.matches(token, tokenNo, stack, lookBackStack);
        assertEquals(expResult, result);
    }

    /**
     * Test of canEnd method, of class NegativeLiteral.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        NegativeLiteral instance = new NegativeLiteral("\"not\"");
        boolean expResult = true;
        instance.setNext(new DummyNode());
        boolean result = instance.canEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of copy method, of class NegativeLiteral.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        NegativeLiteral instance = new NegativeLiteral("\"not\"");
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

    /**
     * Test of getQuery method, of class NegativeLiteral.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        SrlQuery query = new SrlQuery();
        SrlQuery expResult = new SrlQuery();
        expResult.query.append("\" \"");
        NegativeLiteral instance = new NegativeLiteral("\"literal\"");
        instance.getQuery(query);
        assertEquals(expResult, query);
    }

    /**
     * Test of reset method, of class NegativeLiteral.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of setNext method, of class NegativeLiteral.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }


    /**
     * Test of toString method, of class NegativeLiteral.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        NegativeLiteral instance = new NegativeLiteral("\"not\"");
        String expResult = "not(\"not\")";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

}