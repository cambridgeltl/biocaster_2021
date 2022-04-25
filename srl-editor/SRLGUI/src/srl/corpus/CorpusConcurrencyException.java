/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.corpus;

/**
 *
 * @author john
 */
public class CorpusConcurrencyException extends Exception {

    /**
     * Creates a new instance of <code>CorpusConcurrencyException</code> without detail message.
     */
    public CorpusConcurrencyException() {
    }


    /**
     * Constructs an instance of <code>CorpusConcurrencyException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CorpusConcurrencyException(String msg) {
        super(msg);
    }
}
