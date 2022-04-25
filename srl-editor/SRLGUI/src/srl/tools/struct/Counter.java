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
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * This data structure for keeping a running tally on multiple objects. This essentially wraps
 * Map<E,Integer> but makes it a little easier to use.
 * 
 * @author John McCrae, National Institute of Informatics
 */
public class Counter<E> extends AbstractSet<E> implements Serializable, Cloneable {

    private Map<E,Integer> map;

    /** Create a new instance */
    public Counter() {
        this(false);
    }

    /** Create a new instance. This allows to specify if the objects are stored
     * by hashing or binary tree.
     * @param comparable If true, the underlying data structure is a TreeMap, otherwise a HashMap
     */
    public Counter(boolean comparable) {
        if(comparable) {
            map = new TreeMap<E,Integer>();
        } else {
            map = new HashMap<E,Integer>();
        }
    }

    private Counter(Map<E,Integer> map) {
        this.map = map;
    }
    
    /**
     * Get an iterator
     * @return The iterator object
     */
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    /**
     * Get the size of the collection. (This is the number of object with a count
     * greater than zero)
     * @return The size of the collection
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * Increment the count by 1.
     * @param e The value to increment
     * @return True if the collection changed
     */
    @Override
    public boolean add(E e) {
        if(map.containsKey(e)) {
            map.put(e, map.get(e) + 1);
            return false;
        } else {
            map.put(e, 1);
            return true;
        }
    }

    /**
     * Set the value of a particular count
     * @param e The value to set
     * @param val The value to be set to
     */
    public void set(E e, int val) {
        map.put(e,val);
    }
    
    /**
     * Get the value of a particular count
     * @param e The key to get the value from
     * @return The value 
     */ 
    public int get(E e) {
        if(map.containsKey(e))
            return map.get(e);
        else
            return 0;
    }
    
    /**
     * Decrement the count by 1.
     * @param e The value to decrease
     * @return True if the collection changed
     */
    public boolean subtract(E e) {
        if(map.containsKey(e)) {
            if(map.get(e) > 1) {
                map.put(e, map.get(e) + 1);
                return false;
            } else {
                map.remove(e);
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        Iterator<Map.Entry<E,Integer>> iter = map.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<E,Integer> ent = iter.next();
            sb.append(ent.getKey() + ":" + ent.getValue());
            if(iter.hasNext())
                sb.append(", ");
        }
        sb.append(" ]");
        return sb.toString();
    }
}
