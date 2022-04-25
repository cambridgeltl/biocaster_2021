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
package srl.rule;

/**
 * Represents a head of a rule.
 * @author John McCrae, National Institute of Informatics
 */
public class Head implements Comparable<Head> {

    /**
     * The value of the name and variable
     */
    public final String name, var;

    /**
     * Create an instance
     * @param name The name of the head
     * @param var The variable the head is bound to
     */
    public Head(String name, String var) {
        this.name = name;
        this.var = var;
    }

    @Override
    public String toString() {
        return name + "(" + var + ")";
    }

    public int compareTo(Head o) {
        if(o instanceof Head) {
            return name.compareTo(((Head)o).name);
        }
        return new Integer(o.hashCode()).compareTo(this.hashCode());
    }
}
