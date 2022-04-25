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
package srl.corpus.th;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.th.ThaiWordFilter;
import java.io.*;

/**
 * @author John McCrae, National Institute of Informatics
 */
public class ThaiTokenizer extends Tokenizer {
    TokenStream tokenStream;
    
    public ThaiTokenizer(Reader r) {
        tokenStream = new ThaiWordFilter(new srl.corpus.token.StandardTokenizer(r));
    }

    @Override
    public Token next() throws IOException {
        return tokenStream.next();
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
