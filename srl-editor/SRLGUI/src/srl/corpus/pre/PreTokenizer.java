/* Generated By:JavaCC: Do not edit this line. PreTokenizer.java */
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
package srl.corpus.pre;
import java.io.*;
import srl.corpus.*;

public class PreTokenizer extends org.apache.lucene.analysis.Tokenizer implements PreTokenizerConstants {
    boolean finished = false;

public static char SPLITTER_CHAR1 = '\n', SPLITTER_CHAR2 = '\r';

  /** Constructs a tokenizer for this Reader. */
  public PreTokenizer(Reader reader) {
    this(new FastCharStream(reader));
    this.input = reader;
  }

  public org.apache.lucene.analysis.Token next() throws IOException {
    try {
        if(finished)
            return null;
        org.apache.lucene.analysis.Token rv = next2();
        if(rv == null)
            finished = true;
        return rv;
    } catch(ParseException x) {
        x.printStackTrace();
        throw new RuntimeException(x);
    }
  }

/** Returns the next token in the stream, or null at EOS.
 * <p>The returned token's type is set to an element of {@link
 * StandardTokenizerConstants#tokenImage}.
 */
  final public org.apache.lucene.analysis.Token next2() throws ParseException, IOException {
  Token tk = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BEGIN_TAG:
    case END_TAG:
    case SPLITTER:
    case ANYTHING:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BEGIN_TAG:
        tk = jj_consume_token(BEGIN_TAG);
        break;
      case END_TAG:
        tk = jj_consume_token(END_TAG);
        break;
      case SPLITTER:
        tk = jj_consume_token(SPLITTER);
        break;
      case ANYTHING:
        tk = jj_consume_token(ANYTHING);
        break;
      default:
        jj_la1[0] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
        {if (true) return
            SrlToken.makeToken2(tk.image,tk.kind,tk.beginColumn,tk.endColumn);}
      break;
    case 0:
      jj_consume_token(0);
      {if (true) return null;}
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public PreTokenizerTokenManager token_source;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[2];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x1e,0x1f,};
   }

  /** Constructor with user supplied CharStream. */
  public PreTokenizer(CharStream stream) {
    token_source = new PreTokenizerTokenManager(stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(CharStream stream) {
    token_source.ReInit(stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public PreTokenizer(PreTokenizerTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(PreTokenizerTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List jj_expentries = new java.util.ArrayList();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[7];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 2; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 7; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
