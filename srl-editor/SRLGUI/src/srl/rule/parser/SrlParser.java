/* Generated By:JavaCC: Do not edit this line. SrlParser.java */
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
package srl.rule.parser;
import srl.rule.*;
import srl.tools.struct.Pair;

public class SrlParser implements SrlParserConstants {
    private static int autoVarNum = 0;

    private StringBuffer comment = new StringBuffer();
        private String cleanLiteral(String literal) {
            literal = literal.replaceAll("\\\"", "\"");
            return literal.substring(1,literal.length()-1);
        }

// TR_RULESET -> (TR_RULE | COMMENT | EOL)*
// TR_RULE -> VAR : WORD ( VAR | STRING_LITERAL ) HEAD_EXPR
  final public void readTRRules(RuleSet ruleSet) throws ParseException {
Token ruleHead, ruleHeadVar, ruleID;
Rule rule;
boolean cleanHead = false;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EOL:
      case VAR:
      case COMMENT:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case VAR:
        // First read the head, then the main body
             ruleID = jj_consume_token(VAR);
        jj_consume_token(COLON);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WORD:
          ruleHead = jj_consume_token(WORD);
          break;
        case STRMATCH:
          ruleHead = jj_consume_token(STRMATCH);
          break;
        case STRMATCH_REGEX:
          ruleHead = jj_consume_token(STRMATCH_REGEX);
          break;
        case STRMATCH_ORTH:
          ruleHead = jj_consume_token(STRMATCH_ORTH);
          break;
        case BEGINS:
          ruleHead = jj_consume_token(BEGINS);
          break;
        case ENDS:
          ruleHead = jj_consume_token(ENDS);
          break;
        case CONTAINS:
          ruleHead = jj_consume_token(CONTAINS);
          break;
        case WORDS:
          ruleHead = jj_consume_token(WORDS);
          break;
        case OPTIONAL:
          ruleHead = jj_consume_token(OPTIONAL);
          break;
        case NOT:
          ruleHead = jj_consume_token(NOT);
          break;
        case CASE:
          ruleHead = jj_consume_token(CASE);
          break;
        default:
          jj_la1[1] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        jj_consume_token(OPEN_PARA);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case VAR:
          ruleHeadVar = jj_consume_token(VAR);
        cleanHead = false;
          break;
        case STRING_LITERAL:
          ruleHeadVar = jj_consume_token(STRING_LITERAL);
        cleanHead = true;
          break;
        default:
          jj_la1[2] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        jj_consume_token(CLOSE_PARA);
       rule = new Rule(Rule.TEMPLATE_RULE);
       rule.addHead(ruleHead.image,ruleHeadVar.image);
       ruleSet.rules.add(new Pair<String,Rule>(ruleID.image, rule));
       headExpr(rule);
       rule.comment = comment.toString();
       comment = new StringBuffer();
        break;
      case COMMENT:
        ruleHead = jj_consume_token(COMMENT);
       String t = ruleHead.image;
       t = t.replaceAll("#\\s*","");
       comment.append(t);
        break;
      case EOL:
        jj_consume_token(EOL);
       comment = new StringBuffer();
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public Rule readTRRule() throws ParseException {
    Rule rule;
    Token ruleHead, ruleHeadVar;
    boolean cleanHead = false;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WORD:
      ruleHead = jj_consume_token(WORD);
      break;
    case STRMATCH:
      ruleHead = jj_consume_token(STRMATCH);
      break;
    case STRMATCH_REGEX:
      ruleHead = jj_consume_token(STRMATCH_REGEX);
      break;
    case STRMATCH_ORTH:
      ruleHead = jj_consume_token(STRMATCH_ORTH);
      break;
    case BEGINS:
      ruleHead = jj_consume_token(BEGINS);
      break;
    case ENDS:
      ruleHead = jj_consume_token(ENDS);
      break;
    case CONTAINS:
      ruleHead = jj_consume_token(CONTAINS);
      break;
    case WORDS:
      ruleHead = jj_consume_token(WORDS);
      break;
    case OPTIONAL:
      ruleHead = jj_consume_token(OPTIONAL);
      break;
    case NOT:
      ruleHead = jj_consume_token(NOT);
      break;
    case CASE:
      ruleHead = jj_consume_token(CASE);
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(OPEN_PARA);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case VAR:
      ruleHeadVar = jj_consume_token(VAR);
        cleanHead = false;
      break;
    case STRING_LITERAL:
      ruleHeadVar = jj_consume_token(STRING_LITERAL);
        cleanHead = true;
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(CLOSE_PARA);
       rule = new Rule(Rule.TEMPLATE_RULE);
       rule.addHead(ruleHead.image,ruleHeadVar.image);
       headExpr(rule);
       {if (true) return rule;}
    throw new Error("Missing return statement in function");
  }

// NE_RULESET -> ( NE_RULE | EOL | COMMENT ) *
// NE_RULE -> :- TYPE_EXPR
  final public void readNERules(RuleSet ruleSet) throws ParseException {
    Token ruleID;
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EOL:
      case VAR:
      case COMMENT:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case VAR:
        ruleID = jj_consume_token(VAR);
        jj_consume_token(COLON);
        jj_consume_token(IMPLIES);
       Rule r = new Rule(Rule.ENTITY_RULE);
       ruleSet.rules.add(new Pair<String,Rule>(ruleID.image, r));
       r.comment = comment.toString();
       comment = new StringBuffer();
       typeExpr(r);
        break;
      case COMMENT:
        ruleID = jj_consume_token(COMMENT);
       String t = ruleID.image;
       t = t.replaceAll("#\\s*","");
       comment.append(t);
        break;
      case EOL:
        jj_consume_token(EOL);
       comment = new StringBuffer();
        break;
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public Rule readNERule() throws ParseException {
    jj_consume_token(IMPLIES);
      Rule r = new Rule(Rule.ENTITY_RULE);
      typeExpr(r);
      {if (true) return r;}
    throw new Error("Missing return statement in function");
  }

// HEAD_EXPR -> ; WORD ( VAR | STRING_LITERAL ) HEAD_EXPR
//              | :- TYPE_EXPR
  final public void headExpr(Rule rule) throws ParseException {
Token ruleHead, ruleHeadVar;
boolean cleanHead = false;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SEMICOLON:
      jj_consume_token(SEMICOLON);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WORD:
        ruleHead = jj_consume_token(WORD);
        break;
      case STRMATCH:
        ruleHead = jj_consume_token(STRMATCH);
        break;
      case STRMATCH_REGEX:
        ruleHead = jj_consume_token(STRMATCH_REGEX);
        break;
      case STRMATCH_ORTH:
        ruleHead = jj_consume_token(STRMATCH_ORTH);
        break;
      case BEGINS:
        ruleHead = jj_consume_token(BEGINS);
        break;
      case ENDS:
        ruleHead = jj_consume_token(ENDS);
        break;
      case CONTAINS:
        ruleHead = jj_consume_token(CONTAINS);
        break;
      case WORDS:
        ruleHead = jj_consume_token(WORDS);
        break;
      case OPTIONAL:
        ruleHead = jj_consume_token(OPTIONAL);
        break;
      case NOT:
        ruleHead = jj_consume_token(NOT);
        break;
      case CASE:
        ruleHead = jj_consume_token(CASE);
        break;
      default:
        jj_la1[8] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(OPEN_PARA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case VAR:
        ruleHeadVar = jj_consume_token(VAR);
        cleanHead = false;
        break;
      case STRING_LITERAL:
        ruleHeadVar = jj_consume_token(STRING_LITERAL);
       cleanHead = true;
        break;
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(CLOSE_PARA);
      rule.addHead(ruleHead.image,ruleHeadVar.image);
      headExpr(rule);
      break;
    case IMPLIES:
      jj_consume_token(IMPLIES);
      typeExpr(rule);
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// TYPE_EXPR -> EOL | COMMENT | EOF |
//              strmatch ( WORDLIST ) TYPE_EXPR |
//              strmatch_regex ( STRING_LITERAL ) TYPE_EXPR |
//              strmatch_orth( STRING_LITERAL ) TYPE_EXPR |
//              words ( NUMBER? , NUMBER? ) TYPE_EXPR |
//              STRING_LITERAL TYPE_EXPR |
//              WORD ( WORD , VAR ) ENTITY_BRANCH 
  final public void typeExpr(Rule rule) throws ParseException {
    Token s1 = null, s2 = null, s3 = null, s4 = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EOL:
      jj_consume_token(EOL);
      break;
    case 0:
      jj_consume_token(0);
      break;
    case COMMENT:
      s1 = jj_consume_token(COMMENT);
          rule.comment = s1.image.replaceAll("^#\\s*","");
      break;
    case STRMATCH:
      jj_consume_token(STRMATCH);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(WORDLIST);
      jj_consume_token(CLOSE_PARA);
       rule.addTypeExpr(new ListMatch(s1.image));
       typeExpr(rule);
      break;
    case STRMATCH_REGEX:
      jj_consume_token(STRMATCH_REGEX);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
       rule.addTypeExpr(new Regex(cleanLiteral(s1.image)));
       typeExpr(rule);
      break;
    case STRMATCH_ORTH:
      jj_consume_token(STRMATCH_ORTH);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
       rule.addTypeExpr(new Ortho(cleanLiteral(s1.image)));
       typeExpr(rule);
      break;
    case WORDS:
      jj_consume_token(WORDS);
      jj_consume_token(OPEN_PARA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NUMBER:
        s1 = jj_consume_token(NUMBER);
        break;
      default:
        jj_la1[11] = jj_gen;
        ;
      }
      jj_consume_token(COMMA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NUMBER:
        s2 = jj_consume_token(NUMBER);
        break;
      default:
        jj_la1[12] = jj_gen;
        ;
      }
      jj_consume_token(CLOSE_PARA);
       int i1 = (s1 == null ? 0 : Integer.parseInt(s1.image));
       int i2 = (s2 == null ? Integer.MAX_VALUE : Integer.parseInt(s2.image));
       rule.addTypeExpr(new SkipWords(i1,i2));
       typeExpr(rule);
      break;
    case STRING_LITERAL:
      s1 = jj_consume_token(STRING_LITERAL);
       rule.addTypeExpr(new Literal(cleanLiteral(s1.image)));
       typeExpr(rule);
      break;
    case BEGINS:
      jj_consume_token(BEGINS);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        rule.addTypeExpr(new PartialLiteral(cleanLiteral(s1.image), 0));
        typeExpr(rule);
      break;
    case ENDS:
      jj_consume_token(ENDS);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        rule.addTypeExpr(new PartialLiteral(cleanLiteral(s1.image), 1));
        typeExpr(rule);
      break;
    case CONTAINS:
      jj_consume_token(CONTAINS);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        rule.addTypeExpr(new PartialLiteral(cleanLiteral(s1.image), 2));
        typeExpr(rule);
      break;
    case OPTIONAL:
      jj_consume_token(OPTIONAL);
      jj_consume_token(OPEN_PARA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STRING_LITERAL:
        s1 = jj_consume_token(STRING_LITERAL);
        break;
      case WORDLIST:
        s1 = jj_consume_token(WORDLIST);
        break;
      default:
        jj_la1[13] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(CLOSE_PARA);
        rule.addTypeExpr(new OptionalLiteral(s1.image));
        typeExpr(rule);
      break;
    case NOT:
      jj_consume_token(NOT);
      jj_consume_token(OPEN_PARA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STRING_LITERAL:
        s1 = jj_consume_token(STRING_LITERAL);
        break;
      case WORDLIST:
        s1 = jj_consume_token(WORDLIST);
        break;
      default:
        jj_la1[14] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(CLOSE_PARA);
         rule.addTypeExpr(new NegativeLiteral(s1.image));
         typeExpr(rule);
      break;
    case CASE:
      jj_consume_token(CASE);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        rule.addTypeExpr(new CaseSensitiveLiteral(cleanLiteral(s1.image)));
        typeExpr(rule);
      break;
    case WORD:
      s1 = jj_consume_token(WORD);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STAR:
        s4 = jj_consume_token(STAR);
        break;
      default:
        jj_la1[15] = jj_gen;
        ;
      }
      jj_consume_token(OPEN_PARA);
      s2 = jj_consume_token(WORD);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        jj_consume_token(COMMA);
        s3 = jj_consume_token(VAR);
        break;
      default:
        jj_la1[16] = jj_gen;
        ;
      }
      jj_consume_token(CLOSE_PARA);
       String varName;
       if(s3 == null) {
            varName = "EXPR" + autoVarNum++;
       } else {
            varName = s3.image;
       }
       Entity e = new Entity(s1.image,s2.image,varName,rule.getRuleType());
       rule.addTypeExpr(e);
       if(s4 != null) {
            e.addTypeExpr(new SkipWords(0,Integer.MAX_VALUE));
       }
       int i = entityBranch(rule,e);
       if(i != 0) {
           e.addTypeExpr(new SkipWords(1, (rule.getRuleType() == Rule.TEMPLATE_RULE ?
                                           Integer.MAX_VALUE : 1)));
        }
        if(s4 != null) {
            e.addTypeExpr(new SkipWords(0,Integer.MAX_VALUE));
        }
      break;
    default:
      jj_la1[17] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// ENTITY_BRANCH -> EOL | COMMENT | EOF |
//              strmatch ( WORDLIST ) TYPE_EXPR |
//              strmatch_regex ( STRING_LITERAL ) TYPE_EXPR |
//              strmatch_orth( STRING_LITERAL ) TYPE_EXPR |
//              words ( NUMBER? , NUMBER? ) TYPE_EXPR |
//              STRING_LITERAL TYPE_EXPR |
//              WORD ( WORD , VAR ) ENTITY_BRANCH |
//              { ENTITY_EXPR TYPE_EXPR 
  final public int entityBranch(Rule rule, Entity e) throws ParseException {
    Token s1 = null, s2 = null, s3 = null, s4 = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EOL:
      jj_consume_token(EOL);
       {if (true) return -1;}
      break;
    case 0:
      jj_consume_token(0);
       {if (true) return -1;}
      break;
    case COMMENT:
      s1 = jj_consume_token(COMMENT);
       if(rule instanceof Rule) {
          ((Rule)rule).comment = s1.image.replaceAll("^#\\s*","");
       }
       {if (true) return -1;}
      break;
    case STRMATCH:
      jj_consume_token(STRMATCH);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(WORDLIST);
      jj_consume_token(CLOSE_PARA);
       rule.addTypeExpr(new ListMatch(s1.image));
       typeExpr(rule);
      break;
    case STRMATCH_REGEX:
      jj_consume_token(STRMATCH_REGEX);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
       rule.addTypeExpr(new Regex(cleanLiteral(s1.image)));
       typeExpr(rule);
      break;
    case STRMATCH_ORTH:
      jj_consume_token(STRMATCH_ORTH);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
       rule.addTypeExpr(new Ortho(cleanLiteral(s1.image)));
       typeExpr(rule);
      break;
    case WORDS:
      jj_consume_token(WORDS);
      jj_consume_token(OPEN_PARA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NUMBER:
        s1 = jj_consume_token(NUMBER);
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
      jj_consume_token(COMMA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NUMBER:
        s2 = jj_consume_token(NUMBER);
        break;
      default:
        jj_la1[19] = jj_gen;
        ;
      }
      jj_consume_token(CLOSE_PARA);
       int i1 = (s1 == null ? 0 : Integer.parseInt(s1.image));
       int i2 = (s2 == null ? Integer.MAX_VALUE : Integer.parseInt(s2.image));
       rule.addTypeExpr(new SkipWords(i1,i2));
       typeExpr(rule);
      break;
    case STRING_LITERAL:
      s1 = jj_consume_token(STRING_LITERAL);
       rule.addTypeExpr(new Literal(cleanLiteral(s1.image)));
       typeExpr(rule);
      break;
    case BEGINS:
      jj_consume_token(BEGINS);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        rule.addTypeExpr(new PartialLiteral(cleanLiteral(s1.image), 0));
       typeExpr(rule);
      break;
    case ENDS:
      jj_consume_token(ENDS);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        rule.addTypeExpr(new PartialLiteral(cleanLiteral(s1.image), 1));
       typeExpr(rule);
      break;
    case CONTAINS:
      jj_consume_token(CONTAINS);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        rule.addTypeExpr(new PartialLiteral(cleanLiteral(s1.image), 2));
       typeExpr(rule);
      break;
    case OPTIONAL:
      jj_consume_token(OPTIONAL);
      jj_consume_token(OPEN_PARA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STRING_LITERAL:
        s1 = jj_consume_token(STRING_LITERAL);
        break;
      case WORDLIST:
        s1 = jj_consume_token(WORDLIST);
        break;
      default:
        jj_la1[20] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(CLOSE_PARA);
        rule.addTypeExpr(new OptionalLiteral(s1.image));
        typeExpr(rule);
      break;
    case NOT:
      jj_consume_token(NOT);
      jj_consume_token(OPEN_PARA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STRING_LITERAL:
        s1 = jj_consume_token(STRING_LITERAL);
        break;
      case WORDLIST:
        s1 = jj_consume_token(WORDLIST);
        break;
      default:
        jj_la1[21] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(CLOSE_PARA);
         rule.addTypeExpr(new NegativeLiteral(s1.image));
         typeExpr(rule);
      break;
    case CASE:
      jj_consume_token(CASE);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        rule.addTypeExpr(new CaseSensitiveLiteral(cleanLiteral(s1.image)));
        typeExpr(rule);
      break;
    case WORD:
      s1 = jj_consume_token(WORD);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STAR:
        s4 = jj_consume_token(STAR);
        break;
      default:
        jj_la1[22] = jj_gen;
        ;
      }
      jj_consume_token(OPEN_PARA);
      s2 = jj_consume_token(WORD);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        jj_consume_token(COMMA);
        s3 = jj_consume_token(VAR);
        break;
      default:
        jj_la1[23] = jj_gen;
        ;
      }
      jj_consume_token(CLOSE_PARA);
       String varName;
       if(s3 == null) {
            varName = "EXPR" + autoVarNum++;
       } else {
            varName = s3.image;
       }
       Entity e2 = new Entity(s1.image,s2.image,varName,rule.getRuleType());
       rule.addTypeExpr(e2);
       if(s4 != null) {
            e.addTypeExpr(new SkipWords(0,Integer.MAX_VALUE));
       }
       int i = entityBranch(rule,e2);
       if(i != 0) {
            e2.addTypeExpr(new SkipWords(1,(rule.getRuleType() == Rule.TEMPLATE_RULE ?
                                           Integer.MAX_VALUE : 1)));
        }
      break;
    case OPEN_BRACE:
      jj_consume_token(OPEN_BRACE);
       entityExpr(e);
       typeExpr(rule);
       {if (true) return 0;}
      break;
    default:
      jj_la1[24] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return +1;}
    throw new Error("Missing return statement in function");
  }

// ENTITY_EXPR ->
//              strmatch ( WORDLIST ) ENTITY_EXPR |
//              strmatch_regex ( STRING_LITERAL ) ENTITY_EXPR |
//              strmatch_orth( STRING_LITERAL ) ENTITY_EXPR |
//              words ( NUMBER? , NUMBER? ) ENTITY_EXPR |
//              STRING_LITERAL ENTITY_EXPR |
//              }
// *(unless ENTITY_BRANCH == }, if ENTITY_BRANCH = EOL throw parse exception)
  final public void entityExpr(Entity e) throws ParseException {
    Token s1 = null, s2 = null, s3;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STRMATCH:
      jj_consume_token(STRMATCH);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(WORDLIST);
      jj_consume_token(CLOSE_PARA);
       e.addTypeExpr(new ListMatch(s1.image));
       entityExpr(e);
      break;
    case STRMATCH_REGEX:
      jj_consume_token(STRMATCH_REGEX);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
       e.addTypeExpr(new Regex(cleanLiteral(s1.image)));
       entityExpr(e);
      break;
    case STRMATCH_ORTH:
      jj_consume_token(STRMATCH_ORTH);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
       e.addTypeExpr(new Ortho(cleanLiteral(s1.image)));
       entityExpr(e);
      break;
    case WORDS:
      jj_consume_token(WORDS);
      jj_consume_token(OPEN_PARA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NUMBER:
        s1 = jj_consume_token(NUMBER);
        break;
      default:
        jj_la1[25] = jj_gen;
        ;
      }
      jj_consume_token(COMMA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NUMBER:
        s2 = jj_consume_token(NUMBER);
        break;
      default:
        jj_la1[26] = jj_gen;
        ;
      }
      jj_consume_token(CLOSE_PARA);
       int i1 = (s1 == null ? 0 : Integer.parseInt(s1.image));
       int i2 = (s2 == null ? Integer.MAX_VALUE : Integer.parseInt(s2.image));
       e.addTypeExpr(new SkipWords(i1,i2));
       entityExpr(e);
      break;
    case STRING_LITERAL:
      s1 = jj_consume_token(STRING_LITERAL);
       e.addTypeExpr(new Literal(cleanLiteral(s1.image)));
     entityExpr(e);
      break;
    case BEGINS:
      jj_consume_token(BEGINS);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        e.addTypeExpr(new PartialLiteral(cleanLiteral(s1.image), 0));
        entityExpr(e);
      break;
    case ENDS:
      jj_consume_token(ENDS);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        e.addTypeExpr(new PartialLiteral(cleanLiteral(s1.image), 1));
        entityExpr(e);
      break;
    case CONTAINS:
      jj_consume_token(CONTAINS);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        e.addTypeExpr(new PartialLiteral(cleanLiteral(s1.image), 2));
        entityExpr(e);
      break;
    case OPTIONAL:
      jj_consume_token(OPTIONAL);
      jj_consume_token(OPEN_PARA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STRING_LITERAL:
        s1 = jj_consume_token(STRING_LITERAL);
        break;
      case WORDLIST:
        s1 = jj_consume_token(WORDLIST);
        break;
      default:
        jj_la1[27] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(CLOSE_PARA);
        e.addTypeExpr(new OptionalLiteral(s1.image));
        entityExpr(e);
      break;
    case NOT:
      jj_consume_token(NOT);
      jj_consume_token(OPEN_PARA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STRING_LITERAL:
        s1 = jj_consume_token(STRING_LITERAL);
        break;
      case WORDLIST:
        s1 = jj_consume_token(WORDLIST);
        break;
      default:
        jj_la1[28] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(CLOSE_PARA);
         e.addTypeExpr(new NegativeLiteral(s1.image));
        entityExpr(e);
      break;
    case CASE:
      jj_consume_token(CASE);
      jj_consume_token(OPEN_PARA);
      s1 = jj_consume_token(STRING_LITERAL);
      jj_consume_token(CLOSE_PARA);
        e.addTypeExpr(new CaseSensitiveLiteral(cleanLiteral(s1.image)));
        entityExpr(e);
      break;
    case CLOSE_BRACE:
      jj_consume_token(CLOSE_BRACE);
      break;
    default:
      jj_la1[29] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  /** Generated Token Manager. */
  public SrlParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[30];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x24000008,0x10003ff0,0x5000000,0x24000008,0x10003ff0,0x5000000,0x24000008,0x24000008,0x10003ff0,0x5000000,0x500000,0x8000000,0x8000000,0x3000000,0x3000000,0x800000,0x40000,0x31003ff9,0x8000000,0x8000000,0x3000000,0x3000000,0x800000,0x40000,0x31013ff9,0x8000000,0x8000000,0x3000000,0x3000000,0x1023ff0,};
   }

  /** Constructor with InputStream. */
  public SrlParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public SrlParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new SrlParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 30; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 30; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public SrlParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new SrlParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 30; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 30; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public SrlParser(SrlParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 30; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(SrlParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 30; i++) jj_la1[i] = -1;
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
    boolean[] la1tokens = new boolean[30];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 30; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 30; i++) {
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
