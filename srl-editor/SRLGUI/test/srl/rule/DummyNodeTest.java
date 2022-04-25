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
public class DummyNodeTest {

    public DummyNodeTest() {
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
     * Test of getQuery method, of class DummyNode.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        System.out.println("skip");
    }

    /**
     * Test of matches method, of class DummyNode.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        Token token = new Token("literal",0,7);
        int no = 0;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        DummyNode instance = new DummyNode();
        TypeExpr expResult = instance;
        TypeExpr result = instance.matches(token, no, stack, lookBackStack);
        assertEquals(expResult, result);
    }

    /**
     * Test of reset method, of class DummyNode.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of setNext method, of class DummyNode.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }


    /**
     * Test of canEnd method, of class DummyNode.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        DummyNode instance = new DummyNode();
        boolean expResult = true;
        boolean result = instance.canEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of copy method, of class DummyNode.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        DummyNode instance = new DummyNode();
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

}