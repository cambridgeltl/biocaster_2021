/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.wordlist;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import srl.tools.struct.CollectionChangeListener;
import srl.tools.struct.ListenableSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import srl.corpus.Processor;
import srl.test.SRLGUITestSuite;

/**
 *
 * @author john
 */
public class WordListSetTest {

    static WordListSet mainInstance;

    public WordListSetTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        if(mainInstance != null)
            return;
        WordListSet wls = new WordListSet("test", SRLGUITestSuite.proj.processor);
        SRLGUITestSuite.proj.wordlists.add(wls);
        SRLGUITestSuite.proj.corpus.listenToWordListSet(wls);
        mainInstance = wls;

    }

    @After
    public void tearDown() {
    }

  

    /**
     * Test of addList method, of class WordListSet.
     */
    @Test
    public void testAddList() {
        System.out.println("addList");
        String name = "test_list";
        WordListSet instance = mainInstance;
        boolean expResult = true;
        boolean result = instance.addList(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of removeList method, of class WordListSet.
     */
    @Test
    public void testRemoveList() {
        System.out.println("removeList");
        String name = "test_list";
        WordListSet instance = mainInstance;
        boolean expResult = true;
        boolean result = instance.removeList(name);
        assertEquals(expResult, result);
        instance.addList(name);
        SRLGUITestSuite.proj.corpus.listenToWordList(name, WordListSet.getWordList(name));
        WordListSet.getWordList(name).add(new WordListEntry("geneva", SRLGUITestSuite.proj.processor));
    }

    /**
     * Test of getLists method, of class WordListSet.
     */
    @Test
    public void testGetLists() {
        System.out.println("getLists");
        WordListSet instance = mainInstance;
        Set<String> expResult = new TreeSet<String>();
        expResult.add("test_list");
        Set<String> result = instance.getLists();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWordList method, of class WordListSet.
     */
    @Test
    public void testGetWordList() {
        System.out.println("getWordList");
        String wordListName = "test_list";
        WordList result = WordListSet.getWordList(wordListName);
        assertEquals(1, result.size());
    }

    /**
     * Test of getAllWordListNames method, of class WordListSet.
     */
    @Test
    public void testGetAllWordListNames() {
        System.out.println("getAllWordListNames");
        Set<String> expResult = new HashSet<String>();
        expResult.add("test_list");
        Set<String> result = WordListSet.getAllWordListNames();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWordListSetByList method, of class WordListSet.
     */
    @Test
    public void testGetWordListSetByList() {
        System.out.println("getWordListSetByList");
        String wordListName = "test_list";
        WordListSet expResult = mainInstance;
        WordListSet result = WordListSet.getWordListSetByList(wordListName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getWordListSetByName method, of class WordListSet.
     */
    @Test
    public void testGetWordListSetByName() {
        System.out.println("getWordListSetByName");
        String wordListName = "test";
        WordListSet expResult = mainInstance;
        WordListSet result = WordListSet.getWordListSetByName(wordListName);
        assertEquals(expResult, result);
    }

    /**
     * Test of addToList method, of class WordListSet.
     */
    @Test
    public void testAddToList_String_Collection() {
        System.out.println("addToList");
        String wordListName = "test_list";
        Collection<String> entries = new HashSet<String>();
        entries.add("bern");
        WordListSet.addToList(wordListName, entries);
        assertEquals(2, WordListSet.getWordList("test_list").size());
    }

    /**
     * Test of addToList method, of class WordListSet.
     */
    @Test
    public void testAddToList_String_StringArr() {
        System.out.println("addToList");
        String wordListName = "test_list";
        String[] entries = { "zürich" };
        WordListSet.addToList(wordListName, entries);
        assertEquals(3, WordListSet.getWordList("test_list").size());
    }

    /**
     * Test of getMatchSet method, of class WordListSet.
     */
    @Test
    public void testGetMatchSet() {
        System.out.println("getMatchSet");
        System.out.println(" Tested in Rule.getMatches()");
    }

   

    /**
     * Test of getEntry method, of class WordListSet.
     */
    @Test
    public void testGetEntry() {
        System.out.println("getEntry");
        System.out.println(" Deprecated");
    }

     /**
     * Test of die method, of class WordListSet.
     */
    @Test
    public void testDie() {
        System.out.println("die");
        WordListSet instance = mainInstance;
        instance.die();
    }

    /**
     * Test of restore method, of class WordListSet.
     */
    @Test
    public void testRestore() {
        System.out.println("restore");
        WordListSet instance = mainInstance;
        instance.restore();
    }

}