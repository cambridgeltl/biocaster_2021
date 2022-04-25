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
package srl.corpus.jp;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.*;

/**
 * @author John McCrae, National Institute of Informatics
 */
public class JapaneseAnalyzer extends Analyzer {
    
    public static String[] JAPANESE_STOP_WORDS = {
        "でも",
        "そんな",
        "なら",
        "ほど",
        "よう",
        "あの",
        "ように",
        "これ",
        "ね",
        "な",
        "より",
        "ので",
        "まで",
        "し",
        "ない",
        "もの",
        "へ",
        "それ",
        "よ",
        "から",
        "には",
        "も",
        "この",
        "です",
        "こと",
        "だ",
        "か",
        "その",
        "で",
        "と",
        "が",
        "に",
        "の",
        "を",
        "は"
    };
    
    public JapaneseAnalyzer() {}

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        try {
            TokenStream result = new JapaneseTokenizer(reader);
            result = new LowerCaseFilter(result);
            result = new StopFilter(result, JAPANESE_STOP_WORDS);
            return result;
        } catch(IOException x) {
            throw new RuntimeException(x);
        }
    }
    
    

}
