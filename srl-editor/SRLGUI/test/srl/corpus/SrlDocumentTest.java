/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.corpus;

import java.util.Iterator;
import java.util.ListIterator;
import org.apache.lucene.analysis.Token;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author john
 */
public class SrlDocumentTest {

    public SrlDocumentTest() {
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
     * Test of add method, of class SrlDocument.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Token arg0 = new Token("test", 0,4);
        SrlDocument instance = new SrlDocument("name");
        boolean expResult = true;
        boolean result = instance.add(arg0);
        assertEquals(expResult, result);
        assertEquals("test", instance.toString());
    }

    /**
     * Test of getName method, of class SrlDocument.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        SrlDocument instance = new SrlDocument("name");
        String expResult = "name";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of iterator method, of class SrlDocument.
     */
    @Test
    public void testIterator() {
        System.out.println("iterator");
        System.out.println("skip");
    }

    private void fillDoc(SrlDocument instance, String[] tokens) {
        int j = 0;
        for(int i = 0; i < tokens.length; i++) {
            instance.add(new Token(tokens[i], j, (j+=tokens[i].length())));
            j++;
        }
    }

    /**
     * Test of listIterator method, of class SrlDocument.
     */
    @Test
    public void testListIterator() {
        System.out.println("listIterator");
        int index = 0;
        SrlDocument instance = new SrlDocument("name");
        String[] tokens = { "this", "is", "a", "test", "string" };
        fillDoc(instance, tokens);
        ListIterator<Token> result = instance.listIterator(index);
        int i;
        for(i = 0; result.hasNext(); i++)
            result.next();
        assertEquals(tokens.length, i);
    }

    /**
     * Test of get method, of class SrlDocument.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        int index = 2;
        SrlDocument instance = new SrlDocument("name");
        String[] tokens = { "this", "is", "a", "test", "string" };
        fillDoc(instance, tokens);
        Token expResult = new Token("a", 8,9);
        Token result = instance.get(index);
        assertEquals(expResult, result);
    }

    /**
     * Test of size method, of class SrlDocument.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        SrlDocument instance = new SrlDocument("name");
        String[] tokens = { "this", "is", "a", "test", "string" };
        fillDoc(instance, tokens);
        int expResult = tokens.length;
        int result = instance.size();
        assertEquals(expResult, result);
    }

}