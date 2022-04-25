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
public class OrthoTest {

    public OrthoTest() {
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
     * Test of getQuery method, of class Ortho.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        SrlQuery query = new SrlQuery();
        SrlQuery expResult = new SrlQuery();
        expResult.query.append("\" \"");
        Ortho instance = new Ortho("4Nd");
        instance.getQuery(query);
    }

    /**
     * Test of matches method, of class Ortho.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        Token token = new Token("2009",0,4);
        int no = 0;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        Ortho instance = new Ortho("4Nd");
        TypeExpr expResult = new DummyNode();
        instance.setNext(expResult);
        TypeExpr result = instance.matches(token, no, stack, lookBackStack);
        assertEquals(expResult, result);
    }

    /**
     * Test of setNext method, of class Ortho.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }

    /**
     * Test of reset method, of class Ortho.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of toString method, of class Ortho.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Ortho instance = new Ortho("4Nd");
        String expResult = "ortho(\"4Nd\")";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of canEnd method, of class Ortho.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        Ortho instance = new Ortho("4Nd");
        boolean expResult = false;
        boolean result = instance.canEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Ortho.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        System.out.println("skip");
    }

    /**
     * Test of copy method, of class Ortho.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        Ortho instance = new Ortho("4Nd");
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

}