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
package srl.corpus;

import srl.gui.ProcessorPlugin;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * Wraps the language processor, for easy interaction. That is the tokenizer
 * splitter and search indexer analyzer (essentially the tokenizer plus a stop
 * words list)
 * 
 * @author John McCrae, National Institute of Informatics
 */
public class Processor {
    final String analyzer, tokenizer, splitter, name;
    
    /** The names of the processors packaged with Lucene */
    public static final String[] langs = { "English", 
      "Brazilian", 
      "Chinese", 
      "Czech", 
      "Dutch", 
      "French", 
      "German", 
      "Greek", 
      "Russian",
      "Thai",
      "Japanese",
      "Pre-tokenized"
    };
    
    static final String[] analyzers = {
        "org.apache.lucene.analysis.standard.StandardAnalyzer",
        "org.apache.lucene.analysis.br.BrazilianAnalyzer", 
        "srl.corpus.zh.ChineseAnalyzer",
        "org.apache.lucene.analysis.cz.CzechAnalyzer", 
        "org.apache.lucene.analysis.nl.DutchAnalyzer", 
        "org.apache.lucene.analysis.fr.FrenchAnalyzer", 
        "org.apache.lucene.analysis.de.GermanAnalyzer", 
        "org.apache.lucene.analysis.el.GreekAnalyzer",
        "org.apache.lucene.analysis.ru.RussianAnalyzer", 
        "org.apache.lucene.analysis.th.ThaiAnalyzer",
        "srl.corpus.jp.JapaneseAnalyzer",
        "srl.corpus.pre.PreAnalyzer"
    };
    
    static final String[] tokenizers = {
        "srl.corpus.token.StandardTokenizer",
        "srl.corpus.token.StandardTokenizer",
        "srl.corpus.zh.ChineseTokenizer",
        "srl.corpus.token.StandardTokenizer",
        "srl.corpus.token.StandardTokenizer",
        "srl.corpus.token.StandardTokenizer",
        "srl.corpus.token.StandardTokenizer",
        "srl.corpus.token.StandardTokenizer",
        "srl.corpus.token.StandardTokenizer",
        "srl.corpus.th.LexToTokenizer",
        "srl.corpus.jp.JapaneseTokenizer",
        "srl.corpus.pre.PreTokenizer"
    };
    
    static final String[] splitters = {
        "srl.corpus.StandardSplitter",
        "srl.corpus.StandardSplitter",
        "srl.corpus.StandardSplitter",
        "srl.corpus.StandardSplitter",
        "srl.corpus.StandardSplitter",
        "srl.corpus.StandardSplitter",
        "srl.corpus.StandardSplitter",
        "srl.corpus.StandardSplitter",
        "srl.corpus.StandardSplitter",
        "srl.corpus.StandardSplitter",
        "srl.corpus.StandardSplitter",
        "srl.corpus.pre.PreSplitter"
    };

    /** The plugins currently loaded */
    public static final LinkedList<ProcessorPlugin> plugins = new LinkedList<ProcessorPlugin>();


    /** Construct a processor using on the standard names
     * @param name A name from langs
     */
    public static Processor getProcessor(String name) {
        for(int i = 0; i < langs.length; i++) {
            if(name.equals(langs[i])) {
                String analyzer = analyzers[i];
                String tokenizer = tokenizers[i];
                String splitter = splitters[i];
                return new Processor(name,analyzer, tokenizer, splitter);
            }
        }
        for(ProcessorPlugin pp : plugins) {
            if(pp.getProcessorName().equals(name))
                return pp.getProcessor();
        }
        throw new IllegalArgumentException("Language " + name + " not known");
    }

    
    /** Construct a custom processor
     * @param analyzer The name of the analyzer class (must extend org.apache.lucene.analysis.Analyzer)
     * @param tokenizer The name of the tokenizer class (must extend org.apache.lucene.analysis.Tokenizer)
     */
    public Processor(String name, String analyzer, String tokenizer, String splitter) {
        this.name = name;
        this.analyzer = analyzer;
        this.tokenizer = tokenizer;
        this.splitter = splitter;
    }
    
    private Analyzer a;
    
    /** Get an instance of the analyzer */
    public Analyzer getAnalyzer() {
        if(a == null) {
            try {
                return a = (Analyzer)Class.forName(analyzer).newInstance();
            } catch(Exception x) {
                x.printStackTrace();
                return null;
            }
        } else {
            return a;
        }
    }
    
    /** 
     * Get a stream of tokens 
     * @param s The value to tokenize
     * @return The token stream
     */
    public TokenStream getTokenStream(String s) {
        try {
            Class[] params = { java.io.Reader.class };
            Constructor c = Class.forName(tokenizer).getConstructor(params);
            Object[] p = new Object[1];
            p[0] = new StringReader(s);
            return (TokenStream)c.newInstance(p);
        } catch(Exception x) {
            x.printStackTrace();
            return null;
        }
            
    }
    
    private Splitter s;
    
    /**
     * Get a sentence splitter
     * @return The sentence splitter
     */
    public Splitter getSplitter() {
        if(s == null) {
            try {
                return s = (Splitter)Class.forName(splitter).newInstance();
            } catch(Exception x) {
                x.printStackTrace();
                return null;
            }
        } else {
            return s;
        }
    }

    /** Get the analyzer class name */
    //public String getAnalyzerName() { return analyzer; }
    /** Get the tokenizer class name */
    //public String getTokenizerName() { return tokenizer; }
    /** Get the splitter class name */
    //public String getSplitterName() { return splitter; }
    /** Get the language name */
    public String getName() { return name; }

    /**
     * Get language for analyzer class name. Used to transition 0.1.7a or earlier projects
     * to new format.
     * @param analyzer The analyzer class value
     * @return The corresponding language name
     */
    public static String getLang(String analyzer) {
        for(int i = 0; i < analyzers.length; i++) {
            if(analyzer.equals(analyzers[i]))
                return langs[i];
        }
        return null;
    }
}


