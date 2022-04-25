/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.corpus;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import srl.tools.process.ProgressMonitor;
import srl.tools.process.StopSignal;
import srl.tools.struct.Pair;
import org.apache.lucene.document.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import srl.corpus.CorpusExtractor.Overlap;
import srl.rule.Entity;
import srl.rule.Rule;
import srl.rule.RuleSet;
import srl.rule.SrlMatchRegion;
import srl.test.SRLGUITestSuite;

/**
 *
 * @author john
 */
public class CorpusExtractorTest {

    static CorpusExtractor instance;

    public CorpusExtractorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        if(instance == null) {
            instance = new CorpusExtractor(SRLGUITestSuite.proj.corpus);
        }
    }

    @After
    public void tearDown() {
    }

    int matches;

    /**
     * Test of tagCorpus method, of class CorpusExtractor.
     */
    @Test
    public void testTagCorpus_Collection_Collection() throws Exception {
        System.out.println("tagCorpus");
        Collection<RuleSet> ruleSets = SRLGUITestSuite.proj.entityRulesets;
        Collection<Overlap> overlaps = new LinkedList<Overlap>();
        instance.tagCorpus(ruleSets, overlaps,true);
        SrlQuery query = new SrlQuery();
        query.query.delete(0, 1);
        query.entities.add(new Pair<String,String>("name","location"));
        matches = 0;
        SRLGUITestSuite.proj.corpus.query(query, new Corpus.QueryHit() {

            public void hit(Document d, StopSignal signal) {
                matches++;
            }
        });
        assertEquals(3,matches);
    }

    /**
     * Test of tagCorpus method, of class CorpusExtractor.
     */
    @Test
    public void testTagCorpus_3args() throws Exception {
        System.out.println("tagCorpus");
        System.out.println(" skip");
    }

    /**
     * Test of tagSentences method, of class CorpusExtractor.
     */
    @Test
    public void testTagSentences() throws Exception {
        System.out.println("tagSentences");
        List<SrlDocument> sents = new LinkedList<SrlDocument>();
        sents.add(new SrlDocument("name", "Geneva is a city in Switzerland", SRLGUITestSuite.proj.processor));
        Collection<RuleSet> ruleSets = SRLGUITestSuite.proj.entityRulesets;
        Processor p = SRLGUITestSuite.proj.processor;
        List<SrlDocument> result = CorpusExtractor.tagSentences(sents, ruleSets, p);
        assertEquals("<name cl=\"location\"> Geneva </name> is a city in Switzerland", result.get(0).toStringTagged());
    }

    /**
     * Test of sortMatches method, of class CorpusExtractor.
     */
    @Test
    public void testSortMatches() {
        System.out.println("sortMatches");
        List<HashMap<Entity, SrlMatchRegion>> matches = new LinkedList<HashMap<Entity,SrlMatchRegion>>();
        HashMap<Entity,SrlMatchRegion> matches1 = new HashMap<Entity, SrlMatchRegion>();
        HashMap<Entity,SrlMatchRegion> matches2 = new HashMap<Entity, SrlMatchRegion>();
        matches1.put(new Entity("name", "location", "X", Rule.ENTITY_RULE),
                new SrlMatchRegion(4,5,"geneva", null, 4,5));
        matches1.put(new Entity("name", "location", "Y", Rule.ENTITY_RULE),
                new SrlMatchRegion(3,7,"in geneva they", null, 3, 7));
        matches2.put(new Entity("name", "location", "X", Rule.ENTITY_RULE),
               new SrlMatchRegion(1,2, "was", null, 1,2));
        matches2.put(new Entity("name", "location", "Y", Rule.ENTITY_RULE),
               new SrlMatchRegion(4,5, "geneva", null, 4,5));
        matches.add(matches1);
        matches.add(matches2);
        
        Vector<Pair<Entity, SrlMatchRegion>> result = CorpusExtractor.sortMatches(matches);
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).second.beginRegion);
        assertEquals(7, result.get(2).second.endRegion);
    }

    /**
     * Test of addTagsToDocument method, of class CorpusExtractor.
     */
    @Test
    public void testAddTagsToDocument() throws Exception {
        System.out.println("addTagsToDocument");
        String docName = "testDoc2";
        List<Vector<Pair<Entity, SrlMatchRegion>>> matches = new LinkedList<Vector<Pair<Entity,SrlMatchRegion>>>();
        Vector<Pair<Entity,SrlMatchRegion>> matches2 = new Vector<Pair<Entity,SrlMatchRegion>>();
        matches.add(matches2);
        matches2.add(new Pair<Entity, SrlMatchRegion>(new Entity("name", "organization", "X", Rule.ENTITY_RULE),
                new SrlMatchRegion(1,4, "world health organization", null, 1,4)));
        instance.addTagsToDocument(docName, matches,true);
        assertEquals("The <name cl=\"organization\"> World Health Organization </name> announced that early attempts at pandemic " +
            "A ( H1N1 ) vaccine production were going ahead .",
                SRLGUITestSuite.proj.corpus.getDocTaggedContents("testDoc2").get(0));
    }

    /**
     * Test of extractTemplates method, of class CorpusExtractor.
     */
    @Test
    public void testExtractTemplates_Collection() throws Exception {
        System.out.println("extractTemplates");
        LinkedList<RuleSet> ruleSets = new LinkedList<RuleSet>();
        ruleSets.add(new RuleSet(Rule.TEMPLATE_RULE, "rules"));
        ruleSets.get(0).rules.add(new Pair<String,Rule>("R1", Rule.ruleFromString("location(X) :- name(location,X)", Rule.TEMPLATE_RULE)));
        instance.extractTemplates(ruleSets,true);
        assertEquals("location(\"Geneva\")",
                SRLGUITestSuite.proj.corpus.getDocTemplateExtractions("testDoc2").get(3));
    }

    /**
     * Test of extractTemplates method, of class CorpusExtractor.
     */
    @Test
    public void testExtractTemplates_Collection_ProgressMonitor() throws Exception {
        System.out.println("extractTemplates");
        System.out.println(" skip");
    }

}