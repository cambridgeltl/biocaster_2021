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
package srl.rule;

import java.io.StringReader;
import java.util.*;


// A dummy state to denote the end of the rule
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import srl.tools.strings.Strings;
import srl.corpus.SrlDocument;
import srl.rule.parser.ParseException;
import srl.rule.parser.SrlParser;
import srl.tools.struct.*;
import org.apache.lucene.analysis.Token;
import srl.corpus.BeginTagToken;
import srl.corpus.EndTagToken;
import srl.corpus.SrlQuery;
import srl.rule.parser.TokenMgrError;


/**
 * Represents a rule. The rule object allows for creation modification and
 * matching of rules
 * @author john
 */
public class Rule implements Expr, Comparable<Rule> {
    /** A list of the heads of the rules. That is everything before the ":-" */
    public ListenableSet<Head> heads;
    /** A list of the body elements of the rules. That is everything
     * after the ":-" */
    public ListenableList<TypeExpr> body;
    /**
     * The map from the variables to the entities. If the rule is not created from
     * a string this will be null
     */
    public Map<String,Entity> variableEntityMap;
    static TypeExpr successState;
    /** The rule comment */
    public String comment = "";
    public final int ruleType;
    /** Constant used to signify the rule is an entity rule */
    public static final int ENTITY_RULE = 0;
    /** Constant used to signify the rule is a template rule */
    public static final int TEMPLATE_RULE = 1;
    

    static {
        successState = new DummyNode();
    }

    /**
     * Create a new rule
     * @param ruleType The rule's type. (entity or template)
     */
    public Rule(int ruleType) {
        body = new ListenableList<TypeExpr>(new LinkedList<TypeExpr>());
        heads = new ListenableSet<Head>(new TreeSet<Head>());
        this.ruleType = ruleType;
        if (ruleType != ENTITY_RULE && ruleType != TEMPLATE_RULE) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Create a new rule from its string representation
     * @param s The rule as a string
     * @param ruleType The rule's type. (entity or template)
     */
    public static Rule ruleFromString(String s, int ruleType)
            throws ParseException {
        SrlParser parser = new SrlParser(new StringReader(s));
        if (ruleType == ENTITY_RULE) {
            Rule r;
            try {
                r = parser.readNERule();
            } catch(TokenMgrError e) {
                throw new ParseException(e.getMessage());
            }
            if(!r.validateRule())
                throw new ParseException("Rule is not valid (repeated variables in head or body or head variable not in body)");
            return r;
        } else if (ruleType == TEMPLATE_RULE) {
            Rule r = parser.readTRRule();
            if(!r.validateRule())
                throw new ParseException("Rule is not valid (repeated variables in head or body or head variable not in body)");
            return r;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * Check the rule is sound. This means that it has a unique variable for every head, 
     * and each is represented by exactly one entity in the body
     * @return True if the rule is valid
     */
    protected boolean validateRule() {
        Set<String> headVars = new HashSet<String>();
        for(Head head : heads) {
            if(head.var.matches("\".*\""))
                continue; // Ignore literal heads
            if(!headVars.add(head.var))
                return false;
        }
        variableEntityMap = new HashMap<String,Entity>();
        for(TypeExpr te : body) {
            if(te instanceof Entity) {
                Entity e = (Entity)te;
                if(variableEntityMap.put(e.var, e) != null)
                    return false;
            }
        }
        if(!variableEntityMap.keySet().containsAll(headVars))
            return false;
        return true;
    }

    /**
     * Get a query suitable for finding potentially matching strings in a corpus
     * @return The lucene query
     */
    public SrlQuery getCorpusQuery() {
        SrlQuery query = new SrlQuery();
        for (TypeExpr te : body) {
            te.getQuery(query);
        }
        query.query.append("\"");
        Pattern p = Pattern.compile("(?<!\\\\)\"\"");
        Matcher m = p.matcher(query.query);
        query.query = new StringBuffer(m.replaceAll(""));
        return query;
    }

    /**
     * Check if a sentence as a sequence of tokens
     * @param sentence A tokenized string
     * @return True if there is at least one match in the string
     */
    public boolean matches(SrlDocument sentence) {
        return !getMatch(sentence, true).isEmpty();
    }

    /**
     * Find the matches. This function matches multiple times and so a list
     * of match results are returned in order of first matched element.
     * This function returns where every entity matched, giving the result
     * for each entity in the rule as an SrlMatchRegion object.
     * @param sentence A tokenizer
     * @param firstOnly Only look for the first match
     * @return The list of matches.
     */
    public List<HashMap<Entity, SrlMatchRegion>> getMatch(SrlDocument sentence, boolean firstOnly) {
        TypeExpr typeExpr;
        ListIterator<Token> iter1 = sentence.listIterator();
        LinkedList<HashMap<Entity, SrlMatchRegion>> rval = new LinkedList<HashMap<Entity, SrlMatchRegion>>();
        Stack<MatchFork> stack = new Stack<MatchFork>();
        LinkedList<Token> lookBackStack = new LinkedList<Token>();
        
        int i = -1;
        Token tk = null;
        MAIN: while (iter1.hasNext()) {
            while(!stack.empty() && stack.peek().used) {
                stack.pop();
            }
            // If the stack is not empty, keep looping until it is
            if(stack.empty()) {
                // Read next token
                if(tk != null)
                    lookBackStack.add(tk);
                tk = iter1.next();
                i++;
            }
            // Ignore empty tokens, do not count tag tokens
            if (!(tk instanceof BeginTagToken) && !(tk instanceof EndTagToken) && tk.termText().matches("\\s*")) {
                continue;
            } else if ((tk instanceof BeginTagToken || tk instanceof EndTagToken) && stack.empty()) {
                i--;
            }
            // Reset search
            if(body.isEmpty())
                return rval;
            typeExpr = body.get(0);
            resetSearch();
            // Match first token
            if ((typeExpr = typeExpr.matches(tk, i, stack, lookBackStack)) != null) {
                // Otherwise carry on matching
                int j = i+1;
                // Check for single token match
                if (typeExpr == successState) {
                    onMatch(rval,i + (tk instanceof BeginTagToken ? 1 : 0),j);
                    if (firstOnly) {
                        return rval;
                    }
                    continue;
                }
                Iterator<Token> iter2 = sentence.listIterator(iter1.nextIndex());
                while (iter2.hasNext()) {
                    Token tk2 = iter2.next();
                    // Skip whitespace tokens
                    if (!(tk2 instanceof BeginTagToken) && !(tk2 instanceof EndTagToken) && tk2.termText().matches("\\s*")) {
                        j++;
                        lookBackStack.add(tk2);
                        continue;
                    } else if (tk2 instanceof BeginTagToken || tk2 instanceof EndTagToken) {
                        j--;
                    }
                    if (typeExpr == null) {
                        break;
                    } // Match failed

                    // Check next token
                    typeExpr = typeExpr.matches(tk2, j++,stack,lookBackStack);
                    if (typeExpr == successState) {
                        onMatch(rval,i + (tk instanceof BeginTagToken ? 1 : 0),j);
                        if (firstOnly) {
                            return rval;
                        }
                        while(lookBackStack.size() > i && !lookBackStack.isEmpty())
                            lookBackStack.removeLast();
                        continue MAIN;
                    }
                    lookBackStack.add(tk2);
                }
                while(lookBackStack.size() > i && !lookBackStack.isEmpty())
                    lookBackStack.removeLast();
                // Check to see if we are caught in "words(1,) <EOL>" trap
                if (typeExpr != null && typeExpr.canEnd()) {
                    if(typeExpr instanceof Entity) {
                       ((Entity)typeExpr).match.endRegion = j;
                    }   
                    onMatch(rval,i + (tk instanceof BeginTagToken ? 1 : 0),j);
                    if (firstOnly) {
                        return rval;
                    }
                }
            }
        }
        resetSearch();
        return rval;
    }

    private void onMatch(LinkedList<HashMap<Entity, SrlMatchRegion>> rval, int ruleBegin, int ruleEnd) {
        HashMap<Entity, SrlMatchRegion> match = new HashMap<Entity, SrlMatchRegion>();
        
        for (TypeExpr te : body) {
            if (te instanceof Entity) {
                SrlMatchRegion smr = ((Entity) te).match;
                smr.ruleBegin = ruleBegin;
                smr.ruleEnd = ruleEnd;
                match.put((Entity) te, smr);
                ((Entity)te).match.sourceRule = this;
            }
        }
        rval.add(match);
        resetSearch();
    }

    private void resetSearch() {
        for (TypeExpr te : body) {
            te.reset();
        }
    }

    /** Match the rule and output on the heads. The output is given by
     * replacing each head's variable by the region of the sentence matched
     * @param sentence The sentence to match
     * @return The matches
     */
    public List<String> getHeads(SrlDocument sentence) {
        List<String> rv = new LinkedList<String>();
        List<HashMap<Entity, SrlMatchRegion>> ents = getMatch(sentence, false);
        //Vector<Pair<Entity,SrlMatchRegion>> matches = srl.corpus.CorpusExtractor.sortMatches(ents);
        for (HashMap<Entity,SrlMatchRegion> matches : ents) {
            StringBuffer headStr = new StringBuffer();
            LOOP: for (Head head : heads) {
                if(headStr.length() > 0)
                    headStr.append(";");
                if(head.var.matches("\".*\"")) {
                    headStr.append(head.name + "(" + head.var + ")");
                } else {
                    for(Map.Entry<Entity,SrlMatchRegion> match : matches.entrySet()) {
                        if(match.getKey().var.equals(head.var)) {
                            headStr.append(head.name + "(" + match.getValue().value + ")");
                            continue LOOP;
                        }
                    }
                    System.err.println(head.toString());
                } 
            }
            rv.add(headStr.toString());
        }
        return rv;
    }

    /** Add a head to the rule
     * @param clasz The identifier of the output template
     * @param var The variable to match in the body
     */
    public void addHead(String clasz,
            String var) {
        heads.add(new Head(clasz, var));
    }

    /** Add a type expresstion to the end of the body
     * @param typeExpr The typeExpr
     */
    public void addTypeExpr(TypeExpr typeExpr) {
        if (!body.isEmpty()) {
            body.get(body.size() - 1).setNext(typeExpr);
        }
        typeExpr.setNext(successState);
        body.add(typeExpr);
    }

    @Override
    public String toString() {
        return Strings.join(";", heads) + " :- " + Strings.join(" ", body);
    }

    /** Get the rule's type */
    public int getRuleType() {
        return ruleType;
    }

    /**
     * Get the body of the rule
     */
    public List<TypeExpr> getBody() {
        return body;
    }

    public int compareTo(Rule arg0) {
        Iterator<Head> argIter = arg0.heads.iterator();
        for(Head h : heads) {
            if(!argIter.hasNext()) {
                return -1;
            }
            String s1 = h.toString();
            String s2 = argIter.next().toString();
            int i = s1.compareTo(s2);
            if(i != 0)
                return i;
        }
        if(argIter.hasNext())
            return 1;
        Iterator<TypeExpr> bodyIter = arg0.body.iterator();
        for(TypeExpr te : body) {
            if(!bodyIter.hasNext())
                return -1;
            String s1 = te.toString();
            String s2 = bodyIter.next().toString();
            int i = s1.compareTo(s2);
            if(i != 0)
                return i;
        }
        if(argIter.hasNext())
            return 1;
        return 0;
    }
}


