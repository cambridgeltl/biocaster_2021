/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.corpus;

import java.util.Collection;
import java.util.List;
import org.apache.lucene.analysis.Token;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import srl.test.SRLGUITestSuite;
import static org.junit.Assert.*;

/**
 *
 * @author john
 */
public class StandardSplitterTest {

    public StandardSplitterTest() {
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
     * Test of split method, of class StandardSplitter.
     */
    @Test
    public void testSplit_String_String() {
        System.out.println("split");
        String string = "SRL Editor was developed by John McCrae, Dr. Mike Conway " +
                "and Prof. Nigel Collier among others. We're glad you're reading this, " +
                "as it means you are paying attention to test cases!!!";
        String docName = "name";
        StandardSplitter instance = new StandardSplitter();
        List<SrlDocument> result = instance.split(string, docName);
        assertEquals(2, result.size());
    }

    /**
     * Test of split method, of class StandardSplitter.
     */
    @Test
    public void testSplit_Collection_String() {
        System.out.println("split");
        Collection<Token> doc = new SrlDocument("name", "SRL Editor was developed by John McCrae, Dr. Mike Conway " +
                "and Prof. Nigel Collier among others. We're glad you're reading this, " +
                "as it means you are paying attention to test cases!!!", SRLGUITestSuite.proj.processor);
        String docName = "name";
        StandardSplitter instance = new StandardSplitter();
        List<SrlDocument> result = instance.split(doc, docName);
        assertEquals(2, result.size());
    }

}