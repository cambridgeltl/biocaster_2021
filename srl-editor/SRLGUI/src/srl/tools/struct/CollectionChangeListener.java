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
 * A listener for changes to a collection. See classes such as
 * listenable list
 * 
 * @author John McCrae
 */
public interface CollectionChangeListener<E> {
    public void collectionChanged(CollectionChangeEvent<E> e);
}
