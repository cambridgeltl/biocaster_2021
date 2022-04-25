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
 * A list which can be listened to for changes. Whenever an object is added or
 * removed from the list a CollectionChangeEvent is fired to all listeners. For
 * the purpose of CollectionChangeEvents the reference is the index of the list
 * @see CollectionChangeEvent
 * @see CollectionChangeListener
 * @author John McCrae
 */
public class ListenableList<E> extends AbstractList<E> implements Cloneable, Serializable {
    List<E> list;
    transient List<CollectionChangeListener<E>> listeners;
    
    /**
     * Create a new instance
     * @param list A List object to listen to
     */
    public ListenableList(List<E> list) {
        this.list = list;
        listeners = new LinkedList<CollectionChangeListener<E>>();
    }
    
    private void fireEvent(CollectionChangeEvent<E> e) {
        for(CollectionChangeListener<E> listener : listeners) {
            listener.collectionChanged(e);
        }
    }
    
    /**
     * Add a change listener
     * @param listener The listener
     */
    public void addCollectionChangeListener(CollectionChangeListener<E> listener) {
        listeners.add(listener);
    }

    /**
     * Remove a change listener
     * @param listener The listener
     */
    public void removeCollectionChangeListener(CollectionChangeListener<E> listener) {
        listeners.remove(listener);
    }

    /**
     * Get the size of the list
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * Set the element at a specific index
     * @param index The index
     * @param element The element
     * @return The element previously at the specified index
     */

    @Override
    public E set(int index, E element) {
        E oldVal = list.get(index);
        E rv = list.set(index, element);
        fireEvent(new CollectionChangeEvent<E>(oldVal,element,(Integer)index));
        return rv;
    }

    /**
     * Remove an element at a specific index
     * @param index The index
     * @return The element at the index
     */
    @Override
    public E remove(int index) {
        E oldVal = list.get(index);
        E rv = list.remove(index);
        fireEvent(new CollectionChangeEvent<E>(oldVal, null, (Integer)index));
        return rv;
    }

    /**
     * Remove an element
     * @param o The element
     * @return <code>true</code> if the list changed
     */
    @Override
    public boolean remove(Object o) {
        Integer index = list.indexOf(o);
        boolean rv = list.remove(o);
        fireEvent(new CollectionChangeEvent<E>((E)o, null, (Integer)index));
        return rv;
    }

    private class ListenableListIterator implements ListIterator<E> {
        ListIterator<E> iter;
        
        public ListenableListIterator(ListIterator<E> iter) {
            this.iter = iter;
        }
        
        
        public void add(E e) {
            iter.add(e);
            fireEvent(new CollectionChangeEvent<E>(null, e, (Integer)(iter.previousIndex()+1)));
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public boolean hasPrevious() {
            return iter.hasPrevious();
        }

        public E next() {
            return iter.next();
        }

        public int nextIndex() {
            return iter.nextIndex();
        }

        public E previous() {
            return iter.previous();
        }

        public int previousIndex() {
            return iter.previousIndex();
        }

        public void remove() {
            iter.previous();
            E elem = iter.next();
            iter.remove();
            fireEvent(new CollectionChangeEvent<E>(elem, null, (Integer)(iter.previousIndex()+1)));
        }

        public void set(E e) {
            iter.previous();
            E elem = iter.next();
            iter.set(e);
            fireEvent(new CollectionChangeEvent<E>(elem, e, new Integer(iter.previousIndex()+1)));
        }
        
    }

    /**
     * Get a list iterator
     * @param index The first index
     * @return The iterator object
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListenableListIterator(list.listIterator(index));
    }

    /**
     * Get an iterator
     * @return The iterator object
     */
    @Override
    public Iterator<E> iterator() {
        return new ListenableListIterator(list.listIterator());
    }

    /**
     * Add an element at a specific index
     * @param index The index
     * @param element The element
     */
    @Override
    public void add(int index, E element) {
        list.add(index, element);
        fireEvent(new CollectionChangeEvent<E>(null, element, (Integer)index));
    }

    /**
     * Add an element to the end of the list
     * @param e The element
     * @return <code>true</code> (as the list has changed)
     */
    @Override
    public boolean add(E e) {
        boolean rval = list.add(e);
        fireEvent(new CollectionChangeEvent<E>(null, e, (Integer)(list.size()-1)));
        return rval;
    }

    /**
     * Get the element at a specific index
     * @param index The index
     * @return The element
     */
    @Override
    public E get(int index) {
        return list.get(index);
    }
}
