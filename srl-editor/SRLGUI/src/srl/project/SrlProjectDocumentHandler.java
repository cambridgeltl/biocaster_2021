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

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import srl.tools.struct.Pair;
import srl.corpus.Processor;
import srl.rule.Rule;
import srl.rule.parser.ParseException;
import srl.rule.parser.TokenMgrError;

/**
 * The XML parser for project files.
 * @author John McCrae, National Institute of Informatics
 */
public class SrlProjectDocumentHandler extends DefaultHandler {

    boolean dontOpenCorpus = false;
    SrlProject proj;
    private int tag;
    private static final int srlproject = 0;
    private static final int rulesets = 1;
    private static final int wordlists = 2;
    private static final int corpus = 3;
    private static final int ruleset = 4;
    private static final int wordlist = 5;
    private static final int description = 6;
    private static final int entity = 7;
    private static final int entities = 8;
    private static final int val = 9;
    private static final int type = 10;
    private int ruleType = -1;

    public SrlProjectDocumentHandler(SrlProject proj) {
        this.proj = proj;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.toLowerCase().equals("srlproject")) {
            tag = srlproject;
            proj.name.replace(0,proj.name.length(),attributes.getValue(uri,"name"));
        } else if (localName.toLowerCase().equals("entity_rulesets")) {
            ruleType = Rule.ENTITY_RULE;
            tag = rulesets;
        } else if(localName.toLowerCase().equals("template_rulesets")) {
            ruleType = Rule.TEMPLATE_RULE;
            tag = rulesets;
        } else if (localName.toLowerCase().equals("wordlists")) {
            tag = wordlists;
        } else if (localName.toLowerCase().equals("corpus")) {
            tag = corpus;
            String processorName = attributes.getValue(uri, "processor");
            // 0.1.7a to 1.0rc1 transition code
            if(processorName == null) {
                String analyzerClassName = attributes.getValue(uri, "analyzer");
                processorName = Processor.getLang(analyzerClassName);
            }
            try {
                proj.processor = Processor.getProcessor(processorName);
                if(!dontOpenCorpus)
                    proj.openCorpus(proj.processor);
            } catch (Exception x) {
                x.printStackTrace();
                throw new RuntimeException(x.getMessage());
            }
        } else if (localName.toLowerCase().equals("ruleset")) {
            tag = ruleset;
        } else if (localName.toLowerCase().equals("wordlist")) {
            tag = wordlist;
        } else if(localName.toLowerCase().equals("description")) {
            tag = description;
        } else if(localName.toLowerCase().equals("entities")) {
            tag = entities;
        } else if(localName.toLowerCase().equals("entity")) {
            tag = entity;
        } else if(localName.toLowerCase().equals("type")) {
            tag = type;
        } else if(localName.toLowerCase().equals("val")) {
            tag = val;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (tag) {
            case wordlist:
                tag = wordlists;
                break;
            case ruleset:
                tag = ruleset;
                break;
            case entity:
                tag = entities;
                break;
            case type:
            case val:
                tag = entity;
                break;
            default:
                tag = srlproject;
        }
    }

    static String typeTemp;
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String s = "";
        try {
            s = new String(ch, start, length);
            switch (tag) {
                case wordlist:
                    s = s.replaceAll("\\s", "");    
                    proj.openWordList(s);
                    break;
                case ruleset:
                    s = s.replaceAll("\\s", "");
                    proj.openRuleSet(s,ruleType);
                    break;
                case description:
                    proj.description.replace(0, proj.description.length(), s);
                    break;
                case type:
                    s = s.replaceAll("\\s", "");
                    typeTemp = s;
                    break;
                case val:
                    s = s.replaceAll("\\s", "");
                    proj.entities.add(new Pair<String, String>(typeTemp,s));
                    break;
            }
        } catch(ParseException x) {
            x.printStackTrace();
            throw new RuntimeException(s + ": " + x.getMessage());
        } catch(TokenMgrError x) {
            x.printStackTrace();
            throw new RuntimeException(s + ": " + x.getMessage());
        }
        catch (Exception x) {
            x.printStackTrace();
            throw new RuntimeException(x.getMessage());
        }
    }
}
