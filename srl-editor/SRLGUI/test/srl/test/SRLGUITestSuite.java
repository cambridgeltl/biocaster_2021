package srl.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import srl.corpus.CorpusExtractorTest;
import srl.corpus.CorpusTest;
import srl.corpus.SrlDocumentTest;
import srl.corpus.StandardSplitterTest;
import srl.project.SrlProject;
import srl.project.SrlProjectTest;
import srl.rule.BeginTagTest;
import srl.rule.CaseSensitiveLiteralTest;
import srl.rule.DummyNodeTest;
import srl.rule.EndTagTest;
import srl.rule.EntityTest;
import srl.rule.ListMatchTest;
import srl.rule.LiteralTest;
import srl.rule.NegativeLiteralTest;
import srl.rule.OptionalLiteralTest;
import srl.rule.OrthoTest;
import srl.rule.PartialLiteralTest;
import srl.rule.RegexTest;
import srl.rule.RuleTest;
import srl.rule.SkipWordsTest;
import srl.wordlist.WordListEntryTest;
import srl.wordlist.WordListSetTest;

/**
 *
 * @author john
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({SrlProjectTest.class,CorpusTest.class,WordListEntryTest.class,WordListSetTest.class,RuleTest.class,
CorpusExtractorTest.class, SrlDocumentTest.class, StandardSplitterTest.class, BeginTagTest.class, CaseSensitiveLiteralTest.class,
DummyNodeTest.class, EndTagTest.class, EntityTest.class, ListMatchTest.class, LiteralTest.class, NegativeLiteralTest.class,
OptionalLiteralTest.class, OrthoTest.class, PartialLiteralTest.class, RegexTest.class, SkipWordsTest.class, Finalize.class})
public class SRLGUITestSuite {

    public static SrlProject proj;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

}