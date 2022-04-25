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
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import org.apache.lucene.analysis.*;
import srl.corpus.token.StandardTokenizer;

/**
 * Sentence splitter implementation. 
 * @author John McCrae, National Institute of Informatics
 */
public class StandardSplitter implements Splitter {
    static int FULL_STOP = 0;
    static int PUNCT = 1;
    static int PRE_EXC = 2;
    static int POST_EXC = 3;
    static int BOTH_EXC = 4;
    static int NEW_LINE = 5;
    static int CLOSING_PUNCT = 6;
    static int OTHER = 7;
    static int TOKEN_TYPES = 8;
    
    HashSet<String> knownAbbreviations; 
    
    /** A list of abbreviations commonly found with full stops after them
     * like "Prof." */
    public static final String[] englishAbbrevs = { "AG", "APR", "AUG", "Adm", "Brig", "CO", "CORP", "Capt", "Cmdr", 
        "Co", "Col", "Comdr", "DEC", "DR", "Dr", "FEB", "Fig", "FRI", "GMBH", "Gen", "Gov", "INC",
        "JAN", "JUL", "JUN", "LTD", "Lt", "Ltd", "MAR", "MON", "MP", "Maj", "Mr", "Mrs", "Ms", "NA",
        "NOV", "NV", "OCT", "Oy", "PLC", "Prof", "Rep", "SA", "SAT", "SEP", "SIR", "SR", "SUN", "Sen",
        "Sgt", "SpA", "St", "THU", "THUR", "TUE", "VP", "WED", "ad", "al", "ed", "eds", "eg", "et", "etc",
        "fig", "p", "usu", "vs", "yr", "yrs", "e", "g", "i", };
    
    DFSMState initial, terminating;
    
    /** Create a splitter using default splitting rules */
    public StandardSplitter() {
        this(englishAbbrevs);
    }
    
    /** Create a splitter with your custom abbreviation list */
    public StandardSplitter(String[] knownAbbreviations) {
       this.knownAbbreviations = new HashSet<String>();
       for(String s : knownAbbreviations) {
           this.knownAbbreviations.add(s);
       }
       buildDFSM();
    }
    
    private void buildDFSM() {
        DFSMState state0 = new DFSMState();
        DFSMState state1 = new DFSMState();
        state0.trans[PRE_EXC] = state1;
        state0.trans[BOTH_EXC] = state1;
        state1.trans[PRE_EXC] = state1;
        state1.trans[BOTH_EXC] = state1;
        state0.trans[POST_EXC] = state0;
        state0.trans[CLOSING_PUNCT] = state0;
        state0.trans[OTHER] = state0;
        for(int i = 0; i < TOKEN_TYPES; i++) {
            state1.trans[i] = state0;
        }
        DFSMState state2 = new DFSMState();
        DFSMState state3 = new DFSMState();
        DFSMState state4 = new DFSMState();
        DFSMState state5 = new DFSMState();
        DFSMState stateF = new DFSMState();
        state0.trans[FULL_STOP] = state2;
        state0.trans[PUNCT] = state4;
        state0.trans[NEW_LINE] = state5;
        for(int i = 0; i < TOKEN_TYPES; i++) {
            state2.trans[i] = stateF;
            state3.trans[i] = stateF;
            state4.trans[i] = stateF;
            state5.trans[i] = state0;
        }
        state2.trans[FULL_STOP] = state0;
        state2.trans[POST_EXC] = state0;
        state2.trans[BOTH_EXC] = state0;
        state2.trans[CLOSING_PUNCT] = state3;
        state3.trans[CLOSING_PUNCT] = state3;
        state4.trans[PUNCT] = state4;
        state5.trans[NEW_LINE] = stateF;
        initial = state0;
        terminating = stateF;
    }
    
     class DFSMState {
	DFSMState[] trans;
        public DFSMState() {
            trans = new DFSMState[TOKEN_TYPES];
        }
     }
    
     /** 
      * Split a string into sentences. Uses srl.corpus.token.StandardTokenizer for
      * tokenization but includes whitespace as is.
      * @return The tokens split into sentences
      * @see srl.corpus.token.StandardTokenizer
      */
     public List<SrlDocument> split(String string, String docName) {
         DFSMState currentState = initial;
         SrlDocument currentSentence = new SrlDocument(docName + " 0");
         LinkedList<SrlDocument> rval = new LinkedList<SrlDocument>();
         StandardTokenizer tok = new StandardTokenizer(new StringReader(string));
         tok.outputWhitespace = true;
         try {
         Token currentToken = tok.next();
         while(currentToken != null) { 
             currentState = currentState.trans[getType(currentToken.termText())];
             if(currentState == terminating) {
                 rval.add(currentSentence);
                 currentState = initial;
                 currentSentence = new SrlDocument(docName + " " + rval.size());
             } else {
                 currentSentence.add(currentToken);
                 currentToken = tok.next();
             }
         }
         if(!currentSentence.isEmpty())
            rval.add(currentSentence);
         } catch(IOException x) {
             x.printStackTrace();
             throw new RuntimeException(x.getMessage());
         }
         return rval;
     }
     
     
     /**
      * Split a list of tokens into sentences.
      * @param doc A list of tokens, see Tokeniser
      * @return The tokens split into sentences
      */
     public List<SrlDocument> split(Collection<Token> doc, String docName) {
         DFSMState currentState = initial;
         SrlDocument currentSentence = new SrlDocument(docName + " 0");
         LinkedList<SrlDocument> rval = new LinkedList<SrlDocument>();
         Iterator<Token> tokIter = doc.iterator();
         Token currentToken;
         if(tokIter.hasNext())
             currentToken = tokIter.next();
         else
             return rval;
         while(tokIter.hasNext()) { 
             currentState = currentState.trans[getType(currentToken.termText())];
             if(currentState == terminating) {
                 rval.add(currentSentence);
                 currentState = initial;
                 currentSentence = new SrlDocument(docName + " " + rval.size());
             } else {
                 currentSentence.add(currentToken);
                 currentToken = tokIter.next();
             }
         }
         currentSentence.add(currentToken);
         if(!currentSentence.isEmpty())
            rval.add(currentSentence);
         return rval;
     }
     
    private int getType(String s) {
        if(s.equals(".") || s.equals("\u3002") || s.equals("\uff61") || s.equals("\uff0e")) { // Include CJK full-stop and full-width full-stop
            return FULL_STOP;
        } else if(s.equals("?") || s.equals("\uff1f") ||
                s.equals("!") || s.equals("\uff01")) { // Other terminating punctuation
            return PUNCT;
        } else if(s.matches("\\p{Ll}.*") || s.matches("\\p{Lu}\\p{Lu}+")) { // Starts with lowercase or is all uppercase
            return POST_EXC;
        } else if(s.matches("\\p{Lu}")) { // Single uppercase
            return BOTH_EXC;
        } else if(s.matches("\\p{Nd}+") || knownAbbreviations.contains(s)) { // Numbers or known abbreviation
            return PRE_EXC;
        } else if(s.matches("\n") || s.matches("\r")) { // New line
            return NEW_LINE;
        } else if(s.matches("\\p{Pe}+") || s.matches("\\p{Pf}+")) // Closing punctuation e.g., ) } "
            return CLOSING_PUNCT;
        else
            return OTHER;
    }
}
