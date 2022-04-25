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
public class CaseSensitiveLiteralTest {

    public CaseSensitiveLiteralTest() {
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
     * Test of getQuery method, of class CaseSensitiveLiteral.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        SrlQuery query = new SrlQuery();
        SrlQuery expResult = new SrlQuery();
        expResult.query.append("literal");
        CaseSensitiveLiteral instance = new CaseSensitiveLiteral("literal");
        instance.getQuery(query);
        assertEquals(expResult, query);
    }

    /**
     * Test of matches method, of class CaseSensitiveLiteral.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        Token token = new Token("LiTeRaL", 0, 7);
        int no = 0;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        CaseSensitiveLiteral instance = new CaseSensitiveLiteral("LiTeRaL");
        TypeExpr expResult = new Literal("test");
        instance.setNext(expResult);
        TypeExpr result = instance.matches(token, no, stack, lookBackStack);
        assertEquals(expResult, result);
        token = new Token("literal",0,7);
        result = instance.matches(token, no, stack, lookBackStack);
        assertEquals(null, result);
    }

    /**
     * Test of setNext method, of class CaseSensitiveLiteral.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }

  

    /**
     * Test of reset method, of class CaseSensitiveLiteral.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of getVal method, of class CaseSensitiveLiteral.
     */
    @Test
    public void testGetVal() {
        System.out.println("getVal");
        CaseSensitiveLiteral instance = new CaseSensitiveLiteral("LiTeRaL");
        String expResult = "LiTeRaL";
        String result = instance.getVal();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class CaseSensitiveLiteral.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        CaseSensitiveLiteral instance = new CaseSensitiveLiteral("LiTeRaL");
        String expResult = "case(\"LiTeRaL\")";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of canEnd method, of class CaseSensitiveLiteral.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        CaseSensitiveLiteral instance = new CaseSensitiveLiteral("LiTeRaL");
        boolean expResult = false;
        boolean result = instance.canEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class CaseSensitiveLiteral.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        System.out.println("skip");
    }

    /**
     * Test of copy method, of class CaseSensitiveLiteral.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        CaseSensitiveLiteral instance = new CaseSensitiveLiteral("LiTeRaL");
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

}