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
import srl.test.SRLGUITestSuite;
import srl.wordlist.WordListEntry;
import srl.wordlist.WordListSet;

/**
 *
 * @author john
 */
public class ListMatchTest {

    public ListMatchTest() {
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
     * Test of getQuery method, of class ListMatch.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        SrlQuery query = new SrlQuery();
        SrlQuery expResult = new SrlQuery();
        expResult.query.append("\" \"");
        expResult.wordLists.add("list");
        ListMatch instance = new ListMatch("@list");
        instance.getQuery(query);
        assertEquals(expResult,query);
    }

    /**
     * Test of matches method, of class ListMatch.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        // Only works if list has been created in WordListTest
        Token token = new Token("bern",0,4);
        int no = 0;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        ListMatch instance = new ListMatch("@test_list");
        TypeExpr expResult = new Literal("literal");
        instance.setNext(expResult);
        TypeExpr result = instance.matches(token, no, stack, lookBackStack);
        assertEquals(expResult, result);
    }

    /**
     * Test of setNext method, of class ListMatch.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }

    /**
     * Test of reset method, of class ListMatch.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of toString method, of class ListMatch.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        ListMatch instance = new ListMatch("@list");
        String expResult = "list(@list)";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of canEnd method, of class ListMatch.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        System.out.println("skip");
       /* WordListSet wls = new WordListSet("dragon_set", SRLGUITestSuite.proj.processor);
        wls.addList("dragons");
        wls.getWordList("dragons").add(new WordListEntry("fire breathing dragons", SRLGUITestSuite.proj.processor));

        ListMatch instance = new ListMatch("@dragons");
        instance.setNext(new DummyNode());
        boolean expResult = false;
        instance.matches(new Token("fire", 0,4), 0, new Stack<MatchFork>(), new LinkedList<Token>());
        boolean result = instance.canEnd();
        assertEquals(expResult, result);

        instance.reset();
        wls.getWordList("dragons").add(new WordListEntry("fire", SRLGUITestSuite.proj.processor));
        expResult = true;
        instance.matches(new Token("fire", 0,4), 0, new Stack<MatchFork>(), new LinkedList<Token>());
        result = instance.canEnd();
        assertEquals(expResult, result);*/
    }

    /**
     * Test of equals method, of class ListMatch.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        System.out.println("skip");
    }

    /**
     * Test of copy method, of class ListMatch.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        ListMatch instance = new ListMatch("@list");
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

}