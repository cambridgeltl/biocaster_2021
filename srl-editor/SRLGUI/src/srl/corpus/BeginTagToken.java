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

import java.util.regex.*;

/** Represents the opening tag of an entity */
public class BeginTagToken extends SrlToken {
    public String type, val;

    public BeginTagToken(String val, int begin, int end) {
        super("",begin,end);
        Matcher m = Pattern.compile("<(.*) cl=\"(.*)\">").matcher(val);
	if(!m.matches())
	    throw new IllegalArgumentException();
	type = m.group(1).toLowerCase();
	this.val = m.group(2).toLowerCase();
    }
    
    public String getTag() { return "<" + type + " cl=\"" + val + "\">"; }
}