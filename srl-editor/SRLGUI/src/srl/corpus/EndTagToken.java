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

/** Represents the closing tag of the entity */
public class EndTagToken extends SrlToken {
    public String type;

    public EndTagToken(String val, int begin, int end) {
        super("",begin,end);
	if(!val.matches("</.*>"))
	    throw new IllegalArgumentException();
	this.type = val.substring(2,val.length()-1);
    }
    
    public String getTag() { return "</" + type + ">"; }
}