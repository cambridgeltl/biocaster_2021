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
import srl.corpus.EndTagToken;
import static org.junit.Assert.*;
import srl.corpus.SrlQuery;

/**
 *
 * @author john
 */
public class EntityTest {

    public EntityTest() {
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
     * Test of getQuery method, of class Entity.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        SrlQuery query = new SrlQuery();
        SrlQuery expResult = new SrlQuery();
        expResult.query.append("\" \"\" \"");
        expResult.entities.add(new Pair<String, String>("type","val"));
        Entity instance = new Entity("type", "val", "X", Rule.TEMPLATE_RULE);
        instance.getQuery(query);
        assertEquals(expResult, query);
    }

    /**
     * Test of matches method, of class Entity.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        Token token = new BeginTagToken("<type cl=\"val\">", 0,15);
        int tokenNo = 0;
        Stack<MatchFork> stack = new Stack<MatchFork>();
        List<Token> lookBackStack = new LinkedList<Token>();
        Entity instance = new Entity("type", "val", "X", Rule.TEMPLATE_RULE);
        instance.addTypeExpr(new Literal("literal"));
        TypeExpr expResult = instance;
        TypeExpr finalTE = new Literal("end");
        instance.setNext(finalTE);
        TypeExpr result = instance.matches(token, tokenNo++, stack, lookBackStack);
        assertEquals(expResult, result);

        token = new Token("literal",16,23);
        expResult = instance;
        result = instance.matches(token, tokenNo++, stack, lookBackStack);
        assertEquals(expResult, result);

        token = new EndTagToken("</type>", 24, 31);
        expResult = finalTE;
        result = instance.matches(token, tokenNo, stack, lookBackStack);
        assertEquals(expResult, result);
    }

    /**
     * Test of canEnd method, of class Entity.
     */
    @Test
    public void testCanEnd() {
        System.out.println("canEnd");
        Entity instance = new Entity("type", "val", "X", Rule.TEMPLATE_RULE);
        instance.setNext(new DummyNode());
        boolean expResult = false;
        boolean result = instance.canEnd();
        assertEquals(expResult, result);

        instance = new Entity("type", "val", "X", Rule.ENTITY_RULE);
        instance.addTypeExpr(new SkipWords(0, 3));
        instance.setNext(new DummyNode());
        expResult = true;
        result = instance.canEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of setNext method, of class Entity.
     */
    @Test
    public void testSetNext() {
        System.out.println("setNext");
        System.out.println("skip");
    }

    /**
     * Test of reset method, of class Entity.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        System.out.println("skip");
    }

    /**
     * Test of addTypeExpr method, of class Entity.
     */
    @Test
    public void testAddTypeExpr() {
        System.out.println("addTypeExpr");
        System.out.println("skip");
    }

   
    /**
     * Test of getRuleType method, of class Entity.
     */
    @Test
    public void testGetRuleType() {
        System.out.println("getRuleType");
        System.out.println("skip");
    }

    /**
     * Test of equals method, of class Entity.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        System.out.println("skip");
    }

    /**
     * Test of compareTo method, of class Entity.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        System.out.println("skip");
    }

    /**
     * Test of hashCode method, of class Entity.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        System.out.println("skip");
    }

    /**
     * Test of toString method, of class Entity.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Entity instance = new Entity("type", "var", "X", Rule.ENTITY_RULE);
        instance.addTypeExpr(new SkipWords(1,1));
        String expResult = "type(var,X)";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getBody method, of class Entity.
     */
    @Test
    public void testGetBody() {
        System.out.println("getBody");
        System.out.println("skip");
    }

    /**
     * Test of copy method, of class Entity.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        Entity instance = new Entity("type", "var", "X", Rule.ENTITY_RULE);
        TypeExpr expResult = instance;
        TypeExpr result = instance.copy();
        assertEquals(expResult, result);
    }

}