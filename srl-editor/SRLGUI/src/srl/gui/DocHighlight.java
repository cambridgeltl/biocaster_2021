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
package srl.gui;

import java.awt.Color;

/**
 * ShowDocPanel's highlights
 * 
 * @author John McCrae, National Institute of Informatics
 */
 public class DocHighlight implements Comparable<DocHighlight> {
        public final int sentence;
        public final int beginToken, endToken;
        public final Color colour;

        public DocHighlight(int sentence, int beginToken, int endToken, Color colour) {
            this.sentence = sentence;
            this.beginToken = beginToken;
            this.endToken = endToken;
            this.colour = colour;
            if(beginToken >= endToken)
                throw new IllegalArgumentException("Highlight region invalid");
        }

        public int compareTo(DocHighlight o) {
            if(sentence < o.sentence) {
                return -1;
            }
            if(sentence > o.sentence) {
                return 1;
            }
            if(beginToken < o.beginToken) {
                return -1;
            }
            if(beginToken > o.beginToken) {
                return 1;
            }
            return 0;
        }
        
        
    }
