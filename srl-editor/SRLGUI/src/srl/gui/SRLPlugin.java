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

import java.awt.Component;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * A plugin to the SRL system. A plugin is a class that implements this interface and a JPanel
 * to perform the work of the plug-in. Furthermore the plugin must have a constructor
 * that takes no parameters
 *
 * @author john
 */
public interface SRLPlugin {
    /**
     * This method identifies if a particular instance of a panel belongs to this
     * plug-in
     * @param c The JPanel object
     * @param id The identifier of the panel
     * @return true If this panel is the given object
     */
    public boolean panelIs(Component c, String id);

    /**
     * Return the menu to be incorporated into the Tools settings
     * @return The menu item, or null if no menu is required
     */
    public JMenuItem getMenu();

    /**
     * Return a unique identifier for this plugin. This should be a unique constant
     * value and is used by SRLGUIView#getPanel(int,String)
     * @return The id of the plugin
     * @see SRLGUIView#getPanel(int,String)
     */
    public int getPluginID();

    /**
     * Return a unique identifier for a panel. This should be a unique constant value
     * and is used by SRLGUIView#getPanel(int,String)
     * @param panel The panel
     * @return The id of the panel
     * @see SRLGUIView#getPanel(int,String)
     */
    public String getPanelID(JPanel panel);


    /**
     * @return The name of the plugin as it is intended to be presented to the user
     */
    public String getDisplayName();

    /**
     * Instructs the plugin to initialize itself
     */
    public void init();

    /**
     * Instructs the plugin to close and remove all panels from the document
     */
    public void kill() throws CannotDieException;

    /**
     * Get the long description of the plugin
     */
    public String getComment();
}
