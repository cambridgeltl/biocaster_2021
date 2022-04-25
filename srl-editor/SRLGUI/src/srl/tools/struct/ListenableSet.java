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
import java.util.*;
/**
 * A set which can be listened to for changes. Whenever an object is added or
 * removed from the set a CollectionChangeEvent is fired to all listeners. For
 * the purpose of CollectionChangeEvents there is no reference value
 * @see CollectionChangeEvent
 * @see CollectionChangeListener
 * @author John McCrae
 */
public class ListenableSet<E> extends AbstractSet<E> implements Serializable, Cloneable {
    Set<E> set;
    transient List<CollectionChangeListener<E>> listeners;
    
    /**
     * Create a new listenable set
     * @param set The set to wrap
     */
    public ListenableSet(Set<E> set) {
        this.set = set;
        listeners = new LinkedList<CollectionChangeListener<E>>();
    }
    
    private void fireEvent(CollectionChangeEvent<E> e) {
        for(CollectionChangeListener<E> listener : listeners) {
            listener.collectionChanged(e);
        }
    }
    
    /**
     * Add a listener to changes in this collection
     * @param listener The listener
     */
    public void addCollectionChangeListener(CollectionChangeListener<E> listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a listener to changes in this collection
     * @param listener The listener
     */
    public void removeCollectionChangeListener(CollectionChangeListener<E> listener) {
        listeners.remove(listener);
    }

    /**
     * Get the size of the collection
     * @return The size of the collection
     */
    @Override
    public int size() {
        return set.size();
    }

    /**
     * Add an element to the collection
     * @param e The new element
     * @return <code>true</code> if the collection changed
     */
    @Override
    public boolean add(E e) {
        boolean rv = set.add(e);
        fireEvent(new CollectionChangeEvent(null, e, null));
        return rv;
    }
    
    /**
     * Get an iterator
     * @return The iterator object
     */
    @Override
    public Iterator<E> iterator() {
        return new ListenableSetIterator(set.iterator());
    }
    
    private class ListenableSetIterator implements Iterator<E> {
        Iterator<E> iter;
        E last;
        
        ListenableSetIterator(Iterator<E> iter) {
            this.iter = iter;
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public E next() {
            return last = iter.next();
        }

        public void remove() {
            iter.remove();
            fireEvent(new CollectionChangeEvent<E>(last, null, null));
        }
        
    }

    /**
     * Does this collection contain a specified object.
     * @param o The object
     * @return <code>true</code> if the object is in the collection
     */
    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    /**
     * Return the base set
     * @return The set wrapped by ListenableSet
     */
    public Set<E> getSet() {
        return set;
    }

}
