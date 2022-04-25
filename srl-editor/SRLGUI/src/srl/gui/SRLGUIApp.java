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

import java.awt.Image;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import java.util.*;
import java.io.*;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.tree.*;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import srl.corpus.CorpusConcurrencyException;
import srl.corpus.Processor;
import srl.project.SrlProject;
import srl.rule.*;
import srl.wordlist.*;

/**
 * The main class of the application.
 */
public class SRLGUIApp extends SingleFrameApplication {

    HashMap<String,RuleSet> entityRuleSets;
    HashMap<String,RuleSet> templateRuleSets;
    HashMap<String,WordListSet> wordLists;
    public SrlProject proj;
    
    public static final int SRL_ENTITY_RULESET = 1;
    public static final int SRL_TEMPLATE_RULESET = 2;
    public static final int SRL_WORDLIST = 3;
    public static final int SRL_CORPUS = 4;
    public static final int SRL_PROJECT = 5;
    public static final int SRL_SEARCH = 6;
    public static final int SRL_SHOW_DOC = 7;
    public UndoManager undoManager = new UndoManager();
    LinkedList<SRLPlugin> plugins = new LinkedList<SRLPlugin>();
    LinkedList<String> pluginJARs = new LinkedList<String>();
    LinkedList<String> pluginClass = new LinkedList<String>();
    private Preferences prefs;

    // Prefence Keys
    public static final String PLUGIN_LOAD_JAR_KEY = "PLUGIN_LOAD_JAR";
    public static final String PLUGIN_LOAD_CLASS_KEY = "PLUGIN_LOAD_CLASS";

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        entityRuleSets = new HashMap<String,RuleSet>();
        templateRuleSets = new HashMap<String, RuleSet>();                
        wordLists = new HashMap<String,WordListSet>();
        show(new SRLGUIView(this));
        ExitListener exitListen = new ExitListener() {

            public boolean canExit(EventObject arg0) {
                if(proj != null && proj.isModified()) {
                    int opt = JOptionPane.showConfirmDialog(SRLGUIApp.this.getMainFrame(), 
                            "Project modified, save before closing?", "Save", JOptionPane.YES_NO_CANCEL_OPTION);
                    if(opt == JOptionPane.YES_OPTION) {
                        try {
                            proj.writeProject();
                    SRLGUIApp.getApplication().clearModified();
                        } catch(IOException x) {
                            x.printStackTrace();
                            JOptionPane.showMessageDialog(SRLGUIApp.this.getMainFrame(), 
                                    x.getMessage(), "Could not save project", JOptionPane.ERROR_MESSAGE);
                            return false;
                        } catch(CorpusConcurrencyException x) {
                            x.printStackTrace();
                            JOptionPane.showMessageDialog(SRLGUIApp.this.getMainFrame(), 
                                    x.getMessage(), "Could not save project", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        return true;
                    } else if(opt == JOptionPane.NO_OPTION) {
                        return true;
                    } else if(opt == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
                return true;
            }

            public void willExit(EventObject arg0) {
                
            }
        };
        addExitListener(exitListen);
        getMainFrame().setTitle("SRL Editor - " + proj.name + (proj.isModified() ?"*":""));
    }

    @Override
    public void exit(EventObject event) {
        super.exit(event);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        if(proj != null) {
            try {
                proj.corpus.closeCorpus();
            } catch(IOException x) {
                x.printStackTrace();
            }
        }
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    DefaultMutableTreeNode mainTreeNode;
    DefaultMutableTreeNode entityRules;
    DefaultMutableTreeNode templateRules;
    DefaultMutableTreeNode wordList;
    DefaultMutableTreeNode corpus;
    
    
    public MutableTreeNode getMainTreeNode() {
        if(mainTreeNode == null) {
            mainTreeNode = new DefaultMutableTreeNode("SRL Elements");
            entityRules = new DefaultMutableTreeNode("Entity Rules");
            mainTreeNode.add(entityRules);
            templateRules = new DefaultMutableTreeNode("Template Rules");
            mainTreeNode.add(templateRules);
            
            wordList = new DefaultMutableTreeNode("Word List Sets");
            mainTreeNode.add(wordList);
            if(proj != null) {
                for(WordListSet wl : proj.wordlists) {
                    wordList.add(new DefaultMutableTreeNode(wl.name));
                }
                for(RuleSet rs : proj.entityRulesets) {
                    entityRules.add(new DefaultMutableTreeNode(rs.name));
                }
                for(RuleSet rs: proj.templateRulesets) {
                    templateRules.add(new DefaultMutableTreeNode(rs.name));
                }
            }
            corpus = new DefaultMutableTreeNode("Corpus");
            mainTreeNode.add(corpus);
        }
        
        return mainTreeNode;
    }
    
    public void closeTab(int tabType, String tabName) {
        ((SRLGUIView)getMainView()).closeTab(tabType, tabName);
    }
    
    void setModified() {
        proj.setModified();
        SRLGUIView view = SRLGUIView.getView();
        view.enableSave();
        getMainFrame().setTitle("SRL Editor - " + proj.name + "*");
    }
    
    public void clearModified() {
        SRLGUIView.getView().disableSave();
        getMainFrame().setTitle("SRL Editor - " + proj.name);
    }

    void addPlugin(SRLPlugin plugin, String jar, String clasz) {
        for(SRLPlugin p : plugins) {
            if(p.getClass().equals(plugin.getClass()))
                throw new IllegalArgumentException("Plugin " + plugin.getDisplayName() + " is already loaded");
            if(p.getPluginID() == plugin.getPluginID())
                throw new IllegalArgumentException("Plugin " + plugin.getDisplayName() + " is already loaded");
        }
        plugins.add(plugin);
        pluginJARs.add(jar);
        pluginClass.add(clasz);
        plugin.init();
        SRLGUIView.getView().addPlugin(plugin);
        if(plugin instanceof ProcessorPlugin) {
            Processor.plugins.add((ProcessorPlugin)plugin);
        }
    }

    void removePlugin(SRLPlugin plugin) throws CannotDieException {
        plugin.kill();
        plugins.remove(plugin);
    }

    public String getPreference(String key) {
        if(prefs == null) {
            try {
                prefs = Preferences.userNodeForPackage(this.getClass());
            } catch(Exception x) {
                System.err.println("Could not load user settings");
                x.printStackTrace();
                return "";
            }
        }
        return prefs.get(key, "");
    }

    public void setPreference(String key, String value) {
        if(prefs == null) {
            try {
                prefs = Preferences.userNodeForPackage(this.getClass());
            } catch(Exception x) {
                System.err.println("Could not load user settings");
                x.printStackTrace();
                return;
            }
        }
        prefs.put(key, value);
    }

    public void clearPreference(String key) {
        if(prefs == null) {
            try {
                prefs = Preferences.userNodeForPackage(this.getClass());
            } catch(Exception x) {
                System.err.println("Could not load user settings");
                x.printStackTrace();
                return;
            }
        }
        prefs.remove(key);
    }

    public String getIndexedPreference(String key, int index) {
        return getPreference(key + "_" + index);
    }

    public String[] getIndexedPreferences(String key) {
        int prefCount;
        try {
            prefCount = Integer.parseInt(getPreference(key + "_INDEX"));
        } catch(NumberFormatException x) {
            x.printStackTrace();
            return new String[0];
        }
        String[] rval = new String[prefCount];
        for(int i = 0; i < prefCount; i++) {
            rval[i] = getPreference(key + "_" + i);
        }
        return rval;
    }

    public void setIndexedPreference(String key, int index, String value) {
        setPreference(key + "_" + index, value);
    }

    public void addIndexedPreference(String key, String value) {
        int prefCount;
        try {
            prefCount = Integer.parseInt(getPreference(key + "_INDEX"));
        } catch(NumberFormatException x) {
            // Normally because the index isn't set yet
            prefCount = 0;
        }
        setPreference(key + "_INDEX", Integer.toString(prefCount+1));
        setPreference(key + "_" + prefCount, value);
    }

    public void removeIndexedPreference(String key, int index) {
        int prefCount;
        try {
            prefCount = Integer.parseInt(getPreference(key + "_INDEX"));
        } catch(NumberFormatException x) {
            x.printStackTrace();
            return;
        }
        if(prefCount == 0) {
            System.err.println("Removing index preference for " + key + " when preference already empty");
            return;
        }
        if(index < 0 || index >= prefCount) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        setPreference(key + "_INDEX", Integer.toString(prefCount - 1));
        for(int j = index; j < prefCount-1; j++) {
            setPreference(key + "_" + j, getIndexedPreference(key, j+1));
        }
        clearPreference(key + "_" + Integer.toString(prefCount-1));
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of SRLGUIApp
     */
    public static SRLGUIApp getApplication() {
        return Application.getInstance(SRLGUIApp.class);
    }

    public void addUndoableEdit(UndoableEdit edit) {
        undoManager.addEdit(edit);
        SRLGUIView.getView().onUndoableEditAdd();
        setModified();
    }

    public void clearAllEdits() {
        undoManager.discardAllEdits();
        SRLGUIView.getView().onUndoableEditAdd();
    }
    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(SRLGUIApp.class, args);
    }
}
