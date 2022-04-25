/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package srl.gui;

import java.awt.Component;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import srl.corpus.*;

/**
 *
 * @author john
 */
public abstract class ProcessorPlugin implements SRLPlugin {

    public JMenuItem getMenu() {
        return null;
    }

    public String getPanelID(JPanel panel) {
        return null;
    }

    public abstract int getPluginID();

    public void init() {

    }

    public void kill() throws CannotDieException {
        if(SRLGUIApp.getApplication().proj.processor.getName().equals(getProcessorName()))
            throw new CannotDieException("Processor is the current processor of the open project");
    }

    public boolean panelIs(Component c, String id) {
        return false;
    }



    public abstract String getDisplayName();

    public abstract String getProcessorName();

    public abstract Processor getProcessor();
}
