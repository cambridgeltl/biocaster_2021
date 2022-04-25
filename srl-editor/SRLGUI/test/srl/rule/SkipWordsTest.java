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
public class SkipWordsTest {

    public SkipWordsTest() {
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
     * Test of getQuery method, of class SkipWords.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        SrlQuery query = new SrlQuery();
        SrlQuery expResult = new SrlQuery();
        expResult.query.append("\" \"");
        SkipWords instance = new SkipWords(0, 10);
        instance.getQuery(query);
        assertEquals(expResult,query);
    }

    /**
     * Test of setSkipTags method, of class SkipWords.
     */
    @Test
    public void testSetSkipTags() {
        System.out.println("setSkipTags");
        System.out.println("skip");
    }

    /**
     * Test of matches method, of class SkipWords.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        Token token = new Token("literal",0,7);
        int no = 0;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        SkipWords instance = new SkipWords(0, 1);
        TypeExpr expResult = new DummyNode();
        instance.setNext(expResult);
        TypeExpr result = instance.matches(token, no, stack, lookBackStack);
        assertEquals(expResult, result);
        assertTrue(!stack.isEmpty());
    }

    /**
     * Test of setNext method, of class SkipWords.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }


    /**
     * Test of reset method, of class SkipWords.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of toString method, of class SkipWords.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        SkipWords instance = new SkipWords(0, Integer.MAX_VALUE);
        String expResult = "words(,)";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of canEnd method, of class SkipWords.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        SkipWords instance = new SkipWords(0, 1);
        instance.setNext(new DummyNode());
        boolean expResult = true;
        boolean result = instance.canEnd();
        assertEquals(expResult, result);

        instance = new SkipWords(1, 2);
        instance.setNext(new DummyNode());
        expResult = false;
        result = instance.canEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class SkipWords.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
    }

    /**
     * Test of copy method, of class SkipWords.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        SkipWords instance = new SkipWords(0, 7);
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

}