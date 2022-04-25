/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.rule;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import srl.tools.struct.Pair;
import org.apache.lucene.analysis.Token;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import srl.corpus.BeginTagToken;
import static org.junit.Assert.*;
import srl.corpus.SrlQuery;

/**
 *
 * @author john
 */
public class BeginTagTest {

    public BeginTagTest() {
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
     * Test of getQuery method, of class BeginTag.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        SrlQuery query = new SrlQuery();
        query.entities.add(new Pair<String,String>("type","val"));
        query.query.append("\" \"");
        BeginTag instance = new BeginTag("type", "val");
        SrlQuery result = new SrlQuery();
        instance.getQuery(result);
        assertEquals(query,result);
    }

    /**
     * Test of matches method, of class BeginTag.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        Token token = new BeginTagToken("<type cl=\"val\">", 0, 15);
        int tokenNo = 0;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        BeginTag instance = new BeginTag("type", "val");
        TypeExpr expResult = new EndTag("type");
        instance.setNext(expResult);
        TypeExpr result = instance.matches(token, tokenNo, stack, lookBackStack);
        assertEquals(expResult, result);
    }

    /**
     * Test of setNext method, of class BeginTag.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }

    /**
     * Test of reset method, of class BeginTag.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of canEnd method, of class BeginTag.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        BeginTag instance = new BeginTag("type", "val");
        boolean expResult = false;
        boolean result = instance.canEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class BeginTag.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        System.out.println("skip");
    }

    /**
     * Test of copy method, of class BeginTag.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        BeginTag instance = new BeginTag("type", "val");
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

}