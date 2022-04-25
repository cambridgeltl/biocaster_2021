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

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 *
 * @author john
 */
public class PreAnalyzer extends Analyzer {

    @Override
    public TokenStream tokenStream(String arg0, Reader arg1) {
        try {
            return new LowerCaseFilter(new PreTokenizer(arg1));
        } catch(Exception x) {
            x.printStackTrace();
            return null;
        }
    }

}
