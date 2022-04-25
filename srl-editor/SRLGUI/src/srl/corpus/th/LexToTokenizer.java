/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.corpus.th;

import com.sansarn.lexto.LongLexTo;
import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.*;
import srl.corpus.SrlToken;

/**
 *
 * @author john
 */
public class LexToTokenizer extends Tokenizer {

    TokenStream tokenStream;
    LongLexTo lextoTokenizer;
    String line;
    int last;
    boolean first = true;
    
    public LexToTokenizer(Reader r) {
        tokenStream = new srl.corpus.token.StandardTokenizer(r);
        try {
            lextoTokenizer = new LongLexTo();
        } catch(IOException x) {
            x.printStackTrace();
            throw new RuntimeException(x.getMessage());
        }
    }

    @Override
    public Token next() throws IOException {
        if(first || !lextoTokenizer.hasNext()) {
            do {
                Token t = tokenStream.next();
                if(t == null)
                    return null;
                line = t.termText();
                line = line.trim();
            } while(line.length() == 0);
                
            lextoTokenizer.wordInstance(line);
            last = lextoTokenizer.first();
            first = false;
        }
        int next = lextoTokenizer.next();
        Token rv = new Token(line.substring(last,next),last,next);
        last = next;
        return rv;
    }

    @Override
    public void close() throws IOException {
        tokenStream.close();
    }

    @Override
    public void reset() throws IOException {
        tokenStream.reset();
    }
}
