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

/**
 *
 * @author john
 */
public class CannotDieException extends Exception {

    /**
     * Creates a new instance of <code>CannotDieException</code> without detail message.
     */
    public CannotDieException() {
    }


    /**
     * Constructs an instance of <code>CannotDieException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CannotDieException(String msg) {
        super(msg);
    }
}
