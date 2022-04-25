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

/**
 * The event for when a collection changes. This event stores the previous value,
 * the new value and an (optional) reference. In general when a new value is added
 * to a collection the event has a null old value and when an object is removed the
 * the event has a null new value. 
 * 
 * @see CollectionChangeListener
 * @author John McCrae
 */
public class CollectionChangeEvent<E> {
    E oldVal, newVal;
    Object reference;
    
    /**
     * Create an instance of a collection change event
     * @param oldVal The previous value (or null if the event is an add event)
     * @param newVal The new value (or null if the event is a delete event)
     * @param reference An optional reference to indicate the object being edited. 
     * For example for List an Integer indexing the value
     */
    public CollectionChangeEvent(E oldVal, E newVal, Object reference) {
        this.oldVal = oldVal;
        this.newVal = newVal;
        this.reference = reference;
    }

    /**
     * Get the old value
     */
    public E getOldVal() {
        return oldVal;
    }

    /**
     * Get the new value
     */
    public E getNewVal() {
        return newVal;
    }

    /**
     * Get the source of the event
     */
    public Object getReference() {
        return reference;
    }
}
