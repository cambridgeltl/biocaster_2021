/* 
 * Copyright (c) 2008, National Institute of Informatics
 *
 * This file is part of SRL, and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://www.fsf.org/licensing/licenses/info/GPLv2.html.
 */
package srl.project;

import srl.rule.*;
import srl.wordlist.*;
import srl.corpus.*;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import srl.tools.strings.Strings;
import srl.tools.struct.ListenableList;
import srl.tools.struct.Pair;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import srl.rule.parser.ParseException;

/**
 * The Srl Project is used to group together all data used by SRL system
 *
 * @author John McCrae, National Institute of Informatics
 */
public class SrlProject {
    /** A list of the sets of rules used for entity extraction */
    public List<RuleSet> entityRulesets;
    /** A list of the sets of rules used for template extraction */
    public List<RuleSet> templateRulesets;
    /** A list of the sets of wordlist */
    public ListenableList<WordListSet> wordlists;
    /** The project name */
    public final StringBuffer name = new StringBuffer();
    /** The project description */
    public final StringBuffer description = new StringBuffer();
    /** The tools used for linguistic processing, i.e., tokenizer, splitter */
    public Processor processor;
    /** The types of entities used in the project. eg <u>name</u>ed entities
     * for <u>disease</u>s may be represented as a pair <code>[ "name", 
     * "disease" ]</code>*/
    public ListenableList<Pair<String,String>> entities = 
            new ListenableList<Pair<String, String>>(new LinkedList<Pair<String, String>>());
    /** The corpus object. This is null if the project was created with
     * openCorpus=false
     */
    public Corpus corpus;
    File path;
    boolean modified;

    /**
     * Create a new (empty) SRL project
     * @param path The path where this is located
     * @param processor The analyzer used for this corpus
     * @throws java.lang.IllegalArgumentException If path exists and is not an empty directory
     * @throws IOException If a disk error occurred
     */
    public SrlProject(File path, Processor processor) throws IllegalArgumentException, IOException {
        this(path,processor,true);
    }
    
    /**
     * Create a new (empty) SRL project
     * @param path The path where this is located
     * @param processor The analyzer used for this corpus
     * @param openCorpus If this is false no corpus is created
     * @throws java.lang.IllegalArgumentException If path exists and is not an empty directory
     * @throws IOException If a disk error occurred
     */
    public SrlProject(File path, Processor processor, boolean openCorpus) throws IllegalArgumentException, IOException {
        if (path.exists()) {
            if (!path.isDirectory()) {
                throw new IllegalArgumentException(path.toString() + " is not a directory!");
            } else if (path.listFiles().length != 0) {
                throw new IllegalArgumentException(path.toString() + " is not empty!");
            }
        } else if (!path.mkdir()) {
            throw new IOException("Could not create directory " + path);
        }
        if(openCorpus)
            corpus = Corpus.openCorpus(new File(path, "corpus"), processor, true);
        this.processor = processor;
        entityRulesets = new LinkedList<RuleSet>();
        if (!(new File(path, "entity_rules")).mkdir()) {
            throw new IOException("Could not create directory " + path.toString() + "entity_rules");
        }
        templateRulesets = new LinkedList<RuleSet>();
        if (!(new File(path, "template_rules")).mkdir()) {
            throw new IOException("Could not create directory " + path.toString() + "template_rules");
        }
        WordListSet.reset();
        wordlists = new ListenableList<WordListSet>(new LinkedList<WordListSet>());
        if (!(new File(path, "wordlists")).mkdir()) {
            throw new IOException("Could not create directory " + path.toString() + "wordlists");
        }
        modified = true;
        this.path = path;
    }

    private SrlProject() {
        entityRulesets = new LinkedList<RuleSet>();
        templateRulesets = new LinkedList<RuleSet>();
        wordlists = new ListenableList<WordListSet>(new LinkedList<WordListSet>());
    }

    /**
     * Open an existing SRL project
     * @param path The directory containing all the project files
     */
    public static SrlProject openSrlProject(File path) throws IllegalArgumentException, IOException, SAXException {
        return openSrlProject(path,true);
    }
    
    /**
     * Open a existing SRL project
     * @param path The directory containing all the project files
     * @param openCorpus Set to false to supress opening the corpus
     */
    public static SrlProject openSrlProject(File path, boolean openCorpus) throws IllegalArgumentException, IOException, SAXException {
        SrlProject proj = new SrlProject();
        proj.path = path;
        WordListSet.reset();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        SrlProjectDocumentHandler handler = new SrlProjectDocumentHandler(proj);
        handler.dontOpenCorpus = !openCorpus;
        xr.setContentHandler(handler);
        xr.setErrorHandler(handler);
        xr.parse(new InputSource(new FileInputStream(new File(path, "project.xml"))));
        proj.modified = false;
        return proj;
    }

    /**
     * Create a new corpus
     * @param processor The linguistic processor, if null SrlProject.processor is used
     */
    void openCorpus(Processor processor) throws IOException {
	if(processor == null)
	    processor = this.processor;
        corpus = Corpus.openCorpus(new File(path, "corpus"), processor, false);
    }

    /**
     * Create a new wordlist and add it to the word list set list
     * @param wordList The word list name
     */
    void openWordList(String wordList) throws IOException {
        wordlists.add(WordListSet.loadFromFile(new File(new File(path.getPath(), "wordlists"), wordList + ".wordlist.srl"), processor));
        modified = true;
    }

    /**
     * Create a new rule set and add it to the rule set list
     * @param ruleSet The rule set name
     * @param ruleType The rule type 
     * @see Rule#ENTITY_RULE
     * @see Rule#TEMPLATE_RULE
     */
    void openRuleSet(String ruleSet, int ruleType) throws IOException, ParseException {
        if(ruleType == Rule.ENTITY_RULE)
            entityRulesets.add(RuleSet.loadFromFile(new File(new File(path, "entity_rules"), ruleSet + ".rule.srl"),ruleType));
        else if(ruleType == Rule.TEMPLATE_RULE)
            templateRulesets.add(RuleSet.loadFromFile(new File(new File(path, "template_rules"), ruleSet + ".rule.srl"),ruleType));
        else
            throw new IllegalArgumentException();
        modified = true;
    }

    /**
     * Write the project to disk
     */
    public void writeProject() throws IOException, CorpusConcurrencyException {
        for (WordListSet wl : wordlists) {
            File f = new File(new File(path, "wordlists"), wl.name + ".wordlist.srl");
            if (!f.exists()) {
                f.createNewFile();
            }
            wl.write(f);
        }
        for (RuleSet rs : entityRulesets) {
            File f = new File(new File(path, "entity_rules"), rs.name + ".rule.srl");
            if (!f.exists()) {
                f.createNewFile();
            }
            rs.write(f);
        }
        for (RuleSet rs : templateRulesets) {
            File f = new File(new File(path, "template_rules"), rs.name + ".rule.srl");
            if (!f.exists()) {
                f.createNewFile();
            }
            rs.write(f);
        }
        corpus.optimizeIndex(0);
        writeXML();
        modified = false;
    }

    public void writeProject(File location) throws IOException, CorpusConcurrencyException {
        if(path.equals(location)) {
            writeProject();
            return;
        }
         if (location.exists()) {
            if (!location.isDirectory()) {
                throw new IllegalArgumentException(location.toString() + " is not a directory!");
            } else if (location.listFiles().length != 0) {
                throw new IllegalArgumentException(location.toString() + " is not empty!");
            }
        } else if (!location.mkdir()) {
            throw new IOException("Could not create directory " + location);
        }

        if(!(new File(location, "corpus").mkdir())) {
            throw new IOException("Could not create directory " + location.toString() + "corpus");
        }
        File[] corpusFiles = (new File(path, "corpus")).listFiles();
        for(File f : corpusFiles) {
            copy(f, new File(new File(location, "corpus"),f.getName()));
        }
        corpus.optimizeIndex(0);
         corpus = Corpus.openCorpus(new File(location, "corpus"), processor, false);
         path = location;
        if (!(new File(path, "entity_rules")).mkdir()) {
            throw new IOException("Could not create directory " + path.toString() + "entity_rules");
        }
        if (!(new File(path, "template_rules")).mkdir()) {
            throw new IOException("Could not create directory " + path.toString() + "template_rules");
        }
        if (!(new File(path, "wordlists")).mkdir()) {
            throw new IOException("Could not create directory " + path.toString() + "wordlists");
        }
        modified = true;
        for (WordListSet wl : wordlists) {
            File f = new File(new File(path, "wordlists"), wl.name + ".wordlist.srl");
            if (!f.exists()) {
                f.createNewFile();
            }
            wl.write(f);
        }
        for (RuleSet rs : entityRulesets) {
            File f = new File(new File(path, "entity_rules"), rs.name + ".rule.srl");
            if (!f.exists()) {
                f.createNewFile();
            }
            rs.write(f);
        }
        for (RuleSet rs : templateRulesets) {
            File f = new File(new File(path, "template_rules"), rs.name + ".rule.srl");
            if (!f.exists()) {
                f.createNewFile();
            }
            rs.write(f);
        }
        corpus.optimizeIndex(0);
        writeXML();
        modified = false;
    }

   private static void copy(File source, File dest) throws IOException {
        FileChannel in = null, out = null;
        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(dest).getChannel();

            long size = in.size();
            MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);

            out.write(buf);

        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }

    private void writeXML() throws IOException {
        PrintStream ps = new PrintStream(new File(path, "project.xml"));
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ps.println("\t<!DOCTYPE srlproject [");
        ps.println("\t<!ELEMENT srlproject (description, corpus, entities, entity_rulesets, template_rulesets, wordlists)>");
        ps.println("\t<!ELEMENT description (#PCDATA)>");
        ps.println("\t<!ATTLIST srlproject name CDATA #REQUIRED>");
        ps.println("\t<!ELEMENT entity_rulesets (ruleset*)>");
        ps.println("\t<!ELEMENT template_rulesets (ruleset*)>");
        ps.println("\t<!ELEMENT wordlists (wordlist*)>");
        ps.println("\t<!ELEMENT corpus EMPTY>");
        ps.println("\t<!ATTLIST corpus processor CDATA #REQUIRED>");
        ps.println("\t<!ELEMENT ruleset (#PCDATA)>");
        ps.println("\t<!ELEMENT wordlist (#PCDATA)>");
        ps.println("\t<!ELEMENT entities (entity*)>");
        ps.println("\t<!ELEMENT entity (type,val)>");
        ps.println("\t<!ELEMENT type (#PCDATA)>");
        ps.println("\t<!ELEMENT val (#PCDATA)>");
        ps.println("]>");
        ps.println("<srlproject name=\"" + Strings.chomp(name.toString()) + "\">");
        ps.println("<description>" + Strings.chomp(description.toString()) + "</description>");
        ps.println("\t<corpus processor=\"" + corpus.getProcessor().getName() +
                "\"/>");
        ps.println("\t<entities>");
        for(Pair<String,String> entity : entities) {
            ps.println("\t\t<entity>");
            ps.println("\t\t\t<type>" + entity.first + "</type>");
            ps.println("\t\t\t<val>" + entity.second + "</val>");
            ps.println("\t</entity>");
        }
        ps.println("\t</entities>");
        ps.println("\t<entity_rulesets>");
        for (RuleSet rs : entityRulesets) {
            ps.println("\t\t<ruleset>" + rs.name + "</ruleset>");
        }
        ps.println("\t</entity_rulesets>");
        ps.println("\t<template_rulesets>");
        for (RuleSet rs : templateRulesets) {
            ps.println("\t\t<ruleset>" + rs.name + "</ruleset>");
        }
        ps.println("\t</template_rulesets>");
        ps.println("\t<wordlists>");
        for (WordListSet wl : wordlists) {
            ps.println("\t\t<wordlist>" + wl.name + "</wordlist>");
        }
        ps.println("\t</wordlists>");
        ps.println("</srlproject>");
    }

    public boolean isModified() {
        return modified;
    }

    /**
     * Set the project to be modified. SRLGUIApp manages this variable through
     * the use of addUndoableEdit(UndoableEdit).
     */
    public void setModified() {
        modified = true;
    }

    public File getPath() {
        return path;
    }


}
