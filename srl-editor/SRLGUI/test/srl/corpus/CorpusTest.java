/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.corpus;

import java.io.File;
import java.lang.String;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import srl.tools.process.StopSignal;
import srl.tools.struct.ListenableSet;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import srl.corpus.Corpus.QueryHit;
import srl.corpus.Processor;
import srl.project.SrlProject;
import srl.test.SRLGUITestSuite;
import srl.wordlist.WordListEntry;
import srl.wordlist.WordListSet;

/**
 *
 * @author john
 */
public class CorpusTest {

    public CorpusTest() {
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
     * Test of closeCorpus method, of class Corpus.
     */
    @Test
    public void testCloseCorpus() throws Exception {
        System.out.println("closeCorpus");
        Corpus instance = SRLGUITestSuite.proj.corpus;
        instance.closeCorpus();
    }

    /**
     * Test of openCorpus method, of class Corpus.
     */
    @Test
    public void testOpenCorpus() throws Exception {
        System.out.println("openCorpus");
        SrlProject proj = SRLGUITestSuite.proj;
        File indexFile = new File(proj.getPath(), "corpus/");
        Processor processor = proj.processor;
        boolean newIndex = false;
        Corpus result = Corpus.openCorpus(indexFile, processor, newIndex);
        result.closeCorpus();
    }


    /**
     * Test of optimizeIndex method, of class Corpus.
     */
    @Test
    public void testOptimizeIndex() throws Exception {
        System.out.println("optimizeIndex");
        Corpus instance = SRLGUITestSuite.proj.corpus;
        instance.optimizeIndex(0);
    }

    /**
     * Test of closeIndex method, of class Corpus.
     */
    @Test
    public void testCloseIndex_0args() throws Exception {
        System.out.println("closeIndex");
        System.out.println(" skip");
    }


    /**
     * Test of reopenIndex method, of class Corpus.
     */
    @Test
    public void testReopenIndex_0args() throws Exception {
        System.out.println("reopenIndex");
        System.out.println(" skip");
    }


    static long lockID;
    /**
     * Test of reopenIndex method, of class Corpus.
     */
    @Test
    public void testReopenIndex_boolean() throws Exception {
        System.out.println("reopenIndex");
        boolean lock = true;
        Corpus instance = SRLGUITestSuite.proj.corpus;
        lockID = instance.reopenIndex(lock);
    }
    /**
     * Test of closeIndex method, of class Corpus.
     */
    @Test
    public void testCloseIndex_long() throws Exception {
        System.out.println("closeIndex");
        Corpus instance = SRLGUITestSuite.proj.corpus;
        instance.closeIndex(lockID);
    }

    /**
     * Test of setUseRAM method, of class Corpus.
     */
    @Test
    public void testSetUseRAM() throws Exception {
        System.out.println("setUseRAM");
        System.out.println(" Skipping deprecated function...");
    }

    /**
     * Test of getUseRAM method, of class Corpus.
     */
    @Test
    public void testGetUseRAM() {
        System.out.println("getUseRAM");
        System.out.println(" Skipping deprecated function...");
    }

    private String genevaDoc = "The World Health Organization announced that early attempts at pandemic " +
            "A(H1N1) vaccine production were going ahead. As virtually no one is " +
"immune to the virus most experts say that cases will require 1 or 2 " +
"doses. It was announced that early attemps at pandemic A(H1N1) vaccine " +
"production were going ahead by the World Health Organization.\n" +
"\"I think it is too early to tell what impact this will have\", the head " +
"of vaccine production said from Geneva.\n"+
"Major vaccine producers already have dossiers in place said the World " +
"Health Organization. Influenza virus planning continues in July. " +
"Planning will continue through July for the Influenza virus. Vaccine " +
"updates will be announced by the World Health Organization in Geneva on " +
"Monday. \n" +
"\"I think it is too early to tell what impact this will have\", the head " +
"of vaccine production said from Geneva.\"";

    /**
     * Test of addDoc method, of class Corpus.
     */
    @Test
    public void testAddDoc_3args() throws Exception {
        System.out.println("addDoc");
        String name = "testDoc";
        String contents = "This is a <name cl=\"entity\"> test </name> document";
        Corpus instance = SRLGUITestSuite.proj.corpus;
        instance.addDoc(name, contents, true, true);
        String name2 = "testDoc2";
        String contents2 = genevaDoc;
        instance.addDoc(name2, contents2, false, true);
    }

    /**
     * Test of clearTemplateExtractions method, of class Corpus.
     */
    @Test
    public void testClearTemplateExtractions() throws Exception {
        System.out.println("clearTemplateExtractions");
        Corpus instance = SRLGUITestSuite.proj.corpus;
        //instance.clearTemplateExtractions();
    }

    /**
     * Test of containsDoc method, of class Corpus.
     */
    @Test
    public void testContainsDoc() {
        System.out.println("containsDoc");
        String docName = "testDoc2";
        Corpus instance = SRLGUITestSuite.proj.corpus;
        boolean expResult = true;
        boolean result = instance.containsDoc(docName);
    }

    /**
     * Test of getDocNames method, of class Corpus.
     */
    @Test
    public void testGetDocNames() {
        System.out.println("getDocNames");
        Corpus instance = SRLGUITestSuite.proj.corpus;
        Set<String> expResult = new TreeSet<String>();
        expResult.add("testdoc");
        expResult.add("testdoc2");
        Set<String> result = instance.getDocNames();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDoc method, of class Corpus.
     */
    @Test
    public void testGetDoc() throws Exception {
        System.out.println("getDoc");
        System.out.println(" Test elsewhere");
    }

    /**
     * Test of getDocByUID method, of class Corpus.
     */
    @Test
    public void testGetDocByUID() throws Exception {
        System.out.println("getDocByUID");
        System.out.println(" Test elsewhere");
    }

    /**
     * Test of getPlainDocContents method, of class Corpus.
     */
    @Test
    public void testGetPlainDocContents() throws Exception {
        System.out.println("getPlainDocContents");
        String name = "testDoc2";
        Corpus instance = SRLGUITestSuite.proj.corpus;
        String expResult = genevaDoc;
        String result = instance.getPlainDocContents(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDocSentences method, of class Corpus.
     */
    @Test
    public void testGetDocSentences() throws Exception {
        System.out.println("getDocSentences");
        String name = "testDoc2";
        Corpus instance = SRLGUITestSuite.proj.corpus;
        List<String> result = instance.getDocSentences(name);
        assertEquals(9, result.size());
    }

    /**
     * Test of getDocTaggedContents method, of class Corpus.
     */
    @Test
    public void testGetDocTaggedContents() throws Exception {
        System.out.println("getDocTaggedContents");
        String name = "testDoc";
        Corpus instance = SRLGUITestSuite.proj.corpus;
        List<String> expResult = new LinkedList<String>();
        expResult.add("This is a <name cl=\"entity\"> test </name> document");
        List<String> result = instance.getDocTaggedContents(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDocTemplateExtractions method, of class Corpus.
     */
    @Test
    public void testGetDocTemplateExtractions() throws Exception {
        System.out.println("getDocTemplateExtractions");
        System.out.println(" Tested Elsewhere");
    }

    /**
     * Test of removeDoc method, of class Corpus.
     */
    @Test
    public void testRemoveDoc() throws Exception {
        System.out.println("removeDoc");
        String name = "testDoc";
        Corpus instance = SRLGUITestSuite.proj.corpus;
        instance.removeDoc(name,true);
        assertEquals(1, instance.getDocNames().size());
        String contents = "This is a <name cl=\"entity\"> test </name> document";
        boolean tagged = true;
        instance.addDoc(name, contents, tagged, true);
        assertEquals(2, instance.getDocNames().size());
    }

    /**
     * Test of updateDoc method, of class Corpus.
     */
    @Test
    public void testUpdateDoc() throws Exception {
        System.out.println("updateDoc");
        String name = "testDoc2";
        String contents = genevaDoc;
        Corpus instance = SRLGUITestSuite.proj.corpus;
        instance.updateDoc(name, contents,true);
    }

        /**
     * Test of query method, of class Corpus.
     */
    @Test
    public void testQuery_SrlQuery_CorpusQueryHit() throws Exception {
        System.out.println("query");
        System.out.println(" Tested Elsewhere: Rule.getMatch(...)");
    }

    /**
     * Test of query method, of class Corpus.
     */
    @Test
    public void testQuery_3args() throws Exception {
        System.out.println("query");
        System.out.println(" Tested Elsewhere: Rule.getMatch(...)");
    }

    /**
     * Test of query method, of class Corpus.
     */
    @Test
    public void testQuery_String() throws Exception {
        System.out.println("query");
        String query = "document";
        Corpus instance = SRLGUITestSuite.proj.corpus;
        Hits result = instance.query(query);
        assertEquals(1,result.length());
    }

    /**
     * Test of queryNoEscape method, of class Corpus.
     */
    @Test
    public void testQueryNoEscape() throws Exception {
        System.out.println("queryNoEscape");
        String query = "\"Vaccine producers\" \"world health\"";
        Corpus instance = SRLGUITestSuite.proj.corpus;
        Hits result = instance.queryNoEscape(query);
        assertEquals(1,result.length());
    }
}