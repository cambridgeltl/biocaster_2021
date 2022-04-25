/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.wordlist;

import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import srl.corpus.Processor;
import static org.junit.Assert.*;

/**
 *
 * @author john
 */
public class WordListEntryTest {

    public WordListEntryTest() {
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
     * Test of addWord method, of class WordListEntry.
     */
    @Test
    public void testAddWord() {
        System.out.println("addWord");
        String s = "word";
        WordListEntry instance = new WordListEntry(new LinkedList<String>());
        instance.addWord(s);
        assertEquals(s, instance.toString());
    }

    /**
     * Test of matchable method, of class WordListEntry.
     */
    @Test
    public void testMatchable() {
        System.out.println("matchable");
        WordListEntry e = new WordListEntry("fire breathing dragon", Processor.getProcessor("English"));
        WordListEntry instance = new WordListEntry("fire", Processor.getProcessor("English"));
        boolean expResult = true;
        boolean result = e.matchable(instance);
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class WordListEntry.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        WordListEntry e = new WordListEntry("fire breathing dragon", Processor.getProcessor("English"));
        WordListEntry instance = new WordListEntry("fire breathing dragon", Processor.getProcessor("English"));
        boolean expResult = true;
        boolean result = instance.equals(e);
        assertEquals(expResult, result);
    }

    /**
     * Test of compareTo method, of class WordListEntry.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        WordListEntry e = new WordListEntry("fire breathing dragon", Processor.getProcessor("English"));
        WordListEntry instance = new WordListEntry("fire", Processor.getProcessor("English"));
        int expResult = -1;
        int result = instance.compareTo(e);
        assertEquals(expResult, result);
    }
}