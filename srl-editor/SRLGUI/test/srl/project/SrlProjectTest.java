/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.project;

import srl.test.SRLGUITestSuite;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import srl.corpus.Processor;

/**
 *
 * @author john
 */
public class SrlProjectTest {

    public static SrlProject mainInstance;

    public SrlProjectTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    @Before
    public void setUp() {
        if(SRLGUITestSuite.proj != null)
            return;
        File path = new File("test_project/");
        try {
            if(path.exists()) {
                System.err.println("For some reason the project already exists! I can't carry on the tests");
                System.exit(0);
            }
        } catch(Exception x) {
            x.printStackTrace();
            fail("Could not make directory test_project/");
        }
        try {
            mainInstance = new SrlProject(path, Processor.getProcessor("English"), true);
            mainInstance.writeProject();
        } catch(Exception x) {
            x.printStackTrace();
        }
        SRLGUITestSuite.proj = mainInstance;
    }

    @After
    public void tearDown() {
    }


    /**
     * Test of openSrlProject method, of class SrlProject.
     */
    @Test
    public void testOpenSrlProject_File_boolean() throws Exception {
        System.out.println("openSrlProject");
        boolean openCorpus = false;
        SrlProject expResult = null;
        SrlProject result = SrlProject.openSrlProject(new File("test_project/"), openCorpus);
    }

    /**
     * Test of writeProject method, of class SrlProject.
     */
    @Test
    public void testWriteProject_0args() throws Exception {
        System.out.println("writeProject");
        SrlProject instance = mainInstance;
        instance.writeProject();
    }
}