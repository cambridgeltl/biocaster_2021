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
package srl.tools.struct;

import java.io.Serializable;

/**
 * A pair of objects. This is often cleaner than using Object[] or List, as the
 * objects can both keep their original types
 * 
 * @author John McCrae, National Institute of Informatics
 */
public class Pair<E,F> implements Serializable {
    public E first;
    public F second;
    
    public Pair(E e, F f) {
        this.first = e;
        this.second = f;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + "," + second.toString() + ")";
    }

    @Override
    public int hashCode() {
        return first.hashCode() % 65536 + ((second.hashCode() % 65536) << 16);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pair) 
            return first.equals(((Pair)obj).first) &&
                   second.equals(((Pair)obj).second);
        else
            return false;
    }
    
    public void setSecond(F second) {
        this.second = second;
    }

    public void setFirst(E first) {
        this.first = first;
    }

    public F getSecond() {
        return second;
    }

    public E getFirst() {
        return first;
    }

}
