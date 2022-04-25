/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.test;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author john
 */
public class Finalize {

    public Finalize() {
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

    // Not a real test... we just delete the project directory
    @Test
    public void endTest() throws IOException {
        SRLGUITestSuite.proj.corpus.closeCorpus();
        deleteDir(new File("test_project/"));
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
}