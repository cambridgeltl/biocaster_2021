/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.gui;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author john
 */
public abstract class SimpleUndoableEdit implements UndoableEdit {
        protected boolean undone = false;
        protected boolean amIDead = false;

        public boolean addEdit(UndoableEdit arg0) {
            return false; // Not supported
        }

        public boolean canRedo() {
            return undone && !amIDead;
        }

        public boolean canUndo() {
            return !undone && !amIDead;
        }

        public void die() {
            amIDead = true;
        }


        public String getRedoPresentationName() {
            return "Redo \"" + getPresentationName() + "\"";
        }

        public String getUndoPresentationName() {
            return "Undo \"" + getPresentationName() + "\"";
        }

        public boolean isSignificant() {
            return true; // Most edits are significant
        }


        public boolean replaceEdit(UndoableEdit arg0) {
            return false; // Not Supported
        }

}
