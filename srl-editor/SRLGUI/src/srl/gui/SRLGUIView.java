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

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.ChangeEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.tree.*;
import javax.swing.undo.UndoManager;
import srl.tools.jar.JarClassLoader;
import srl.corpus.Corpus;
import srl.corpus.CorpusConcurrencyException;
import srl.corpus.CorpusExtractor;
import srl.project.SrlProject;
import srl.rule.*;
import srl.wordlist.WordListSet;

/**
 * The application's main frame.
 */
public class SRLGUIView extends FrameView {

    private static SRLGUIView singleton;

    public SRLGUIView(SingleFrameApplication app) {
        super(app);
        singleton = this;
        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        ruleSetIcon = resourceMap.getIcon("srl.ruleSetIcon");
        wordListIcon = resourceMap.getIcon("srl.wordListIcon");
        corpusIcon = resourceMap.getIcon("srl.corpusIcon");
        closeTabIcon = resourceMap.getIcon("srl.closeTabIcon");
        searchIcon = resourceMap.getIcon("srl.searchTabIcon");
        copyIcon = resourceMap.getIcon("srl.copyIcon");
        cutIcon = resourceMap.getIcon("srl.cutIcon");
        pasteIcon = resourceMap.getIcon("srl.pasteIcon");
        getFrame().setIconImage(resourceMap.getImageIcon("mainFrame.iconImage").getImage());

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        JMenuItem cutMenu = new JMenuItem(new DefaultEditorKit.CutAction());
        cutMenu.setText("Cut");
        cutMenu.setMnemonic(KeyEvent.VK_T);
        cutMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutMenu.setIcon(cutIcon);
        editMenu.add(cutMenu);

        JMenuItem copyMenu = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyMenu.setText("Copy");
        copyMenu.setMnemonic(KeyEvent.VK_C);
        copyMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyMenu.setIcon(copyIcon);
        editMenu.add(copyMenu);

        JMenuItem pasteMenu = new JMenuItem(new DefaultEditorKit.PasteAction());
        pasteMenu.setText("Paste");
        pasteMenu.setMnemonic(KeyEvent.VK_P);
        pasteMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteMenu.setIcon(pasteIcon);
        editMenu.add(pasteMenu);

        JButton cutButton = new JButton(new DefaultEditorKit.CutAction());
        cutButton.setText("");
        cutButton.setIcon(cutIcon);
        cutButton.setFocusable(false);
        cutButton.setPreferredSize(new Dimension(28,28));
        mainToolBar.add(cutButton,7);

        JButton copyButton = new JButton(new DefaultEditorKit.CopyAction());
        copyButton.setText("");
        copyButton.setIcon(copyIcon);
        copyButton.setFocusable(false);
        copyButton.setPreferredSize(new Dimension(28,28));
        mainToolBar.add(copyButton,8);

        JButton pasteButton = new JButton(new DefaultEditorKit.PasteAction());
        pasteButton.setText("");
        pasteButton.setIcon(pasteIcon);
        pasteButton.setFocusable(false);
        pasteButton.setPreferredSize(new Dimension(28,28));
        mainToolBar.add(pasteButton,9);

        if(SRLGUIApp.getApplication().getPreference("ON_START_LOAD_PROJECT_TOGGLE").equals("true")) {
             try {
                SRLGUIApp.getApplication().proj = SrlProject.openSrlProject(new File(SRLGUIApp.getApplication().getPreference("ON_START_LOAD_PROJECT_PATH")));
                SrlProject proj = SRLGUIApp.getApplication().proj;
                for (WordListSet wl : proj.wordlists) {
                    proj.corpus.listenToWordListSet(wl);
                    for (String l : wl.getLists()) {
                        proj.corpus.listenToWordList(l, WordListSet.getWordList(l));
                    }
                }
                reloadProject();
            } catch (RuntimeException x) {
                if (x.getMessage().matches("Lock obtain timed out: SimpleFSLock.*")) {
                    if(JOptionPane.showConfirmDialog(this.getFrame(), "Corpus locked! This may occur if SRL Editor failed to shut down properly.\nPlease ensure no other copies of SRL Editor are running.\n Do you wish to clear the lock?",
                            "Corpus Lock", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE)
                            == JOptionPane.YES_OPTION) {
                        try {
                            File f = new File(SRLGUIApp.getApplication().getPreference("ON_START_LOAD_PROJECT_PATH") + "/corpus/write.lock");
                            f.delete();
                            SRLGUIApp.getApplication().proj = SrlProject.openSrlProject(new File(SRLGUIApp.getApplication().getPreference("ON_START_LOAD_PROJECT_PATH")));
                            SrlProject proj = SRLGUIApp.getApplication().proj;
                            for (WordListSet wl : proj.wordlists) {
                                proj.corpus.listenToWordListSet(wl);
                                for (String l : wl.getLists()) {
                                    proj.corpus.listenToWordList(l, WordListSet.getWordList(l));
                                }
                            }
                            reloadProject();
                         } catch(Exception x2) {
                           error(x2, "Could not load project");
                       }
                    }
                } else {
                    error(x, "Could not open project");
                }
            } catch (Exception x) {
                error(x, "Could not open project");
            }
        }
        String[] pluginJARs = SRLGUIApp.getApplication().getIndexedPreferences(SRLGUIApp.PLUGIN_LOAD_JAR_KEY);
        String[] pluginClasses = SRLGUIApp.getApplication().getIndexedPreferences(SRLGUIApp.PLUGIN_LOAD_CLASS_KEY);

        for(int i = 0; i < pluginJARs.length; i++) {
            try {
                JarClassLoader jcl = new JarClassLoader(pluginJARs[i]);
                Class c = jcl.loadClass(pluginClasses[i]);
                SRLPlugin instance = (SRLPlugin)c.getConstructor().newInstance();
                SRLGUIApp.getApplication().addPlugin(instance, pluginJARs[i], pluginClasses[i]);
            } catch(IOException x) {
                System.err.println("The JAR file " + pluginJARs[i] + " is missing or corrupted. " +
                        "Removing auto-load for " + pluginClasses[i]);
                x.printStackTrace();
                SRLGUIApp.getApplication().removeIndexedPreference(SRLGUIApp.PLUGIN_LOAD_CLASS_KEY, i);
                SRLGUIApp.getApplication().removeIndexedPreference(SRLGUIApp.PLUGIN_LOAD_JAR_KEY, i);
            } catch(Exception x) {
                System.err.println("Error loading plug-in " + pluginClasses[i]);
                x.printStackTrace();
            }
        }

        rightPane.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent arg0) {
                Component c = rightPane.getSelectedComponent();
                if(c instanceof RuleSetPanel) {
                    addRuleMenuItem.setEnabled(true);
                    removeRuleMenuItem.setEnabled(true);
                    deleteRuleSetMenuItem.setEnabled(true);
                    addWordListMenuItem.setEnabled(false);
                    removeWordListMenuItem.setEnabled(false);
                } else if(c instanceof WordListPanel) {
                    addRuleMenuItem.setEnabled(false);
                    removeRuleMenuItem.setEnabled(false);
                    deleteRuleSetMenuItem.setEnabled(false);
                    addWordListMenuItem.setEnabled(true);
                    removeWordListMenuItem.setEnabled(true);
                } else {
                    addRuleMenuItem.setEnabled(false);
                    removeRuleMenuItem.setEnabled(false);
                    deleteRuleSetMenuItem.setEnabled(false);
                    addWordListMenuItem.setEnabled(false);
                    removeWordListMenuItem.setEnabled(false);
                }
            }
        });
    }

    public static SRLGUIView getView() {
        return singleton;
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SRLGUIApp.getApplication().getMainFrame();
            aboutBox = new SRLGUIAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SRLGUIApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainTree = new javax.swing.JTree();
        rightPane = new javax.swing.JTabbedPane();
        mainToolBar = new javax.swing.JToolBar();
        newProjTBButton = new javax.swing.JButton();
        openProjTBButton = new javax.swing.JButton();
        saveProjTBButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        undoTBButton = new javax.swing.JButton();
        redoTBButton = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        jSeparator12 = new javax.swing.JToolBar.Separator();
        newRulesTBButton = new javax.swing.JButton();
        newWordListTBButton = new javax.swing.JButton();
        addCorpusTBButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        tagCorpusTBButton = new javax.swing.JButton();
        extractTemplatesTBButton = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        searchTBButton = new javax.swing.JButton();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        helpTBButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newProjectMenuItem = new javax.swing.JMenuItem();
        openProjectMenuItem = new javax.swing.JMenuItem();
        saveProjectMenuItem = new javax.swing.JMenuItem();
        saveProjectAsMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        rulesMenu = new javax.swing.JMenu();
        addRuleSetMenuItem = new javax.swing.JMenuItem();
        importRuleSetMenuItem = new javax.swing.JMenuItem();
        deleteRuleSetMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        addRuleMenuItem = new javax.swing.JMenuItem();
        removeRuleMenuItem = new javax.swing.JMenuItem();
        wordListsMenu = new javax.swing.JMenu();
        addWLSMenuItem = new javax.swing.JMenuItem();
        importWLSMenuItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        addWordListMenuItem = new javax.swing.JMenuItem();
        removeWordListMenuItem = new javax.swing.JMenuItem();
        corpusMenu = new javax.swing.JMenu();
        addToCorpusMenuItem = new javax.swing.JMenuItem();
        entityTagMenuItem = new javax.swing.JMenuItem();
        extractTemplatesMenuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        searchMenuItem = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        importTaggedMenuItem = new javax.swing.JMenuItem();
        writeTaggedMenuItem = new javax.swing.JMenuItem();
        writeTemplatesMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        plugInsMenuItem = new javax.swing.JMenuItem();
        settingMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        wikiMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        languageDescriptionMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        mainTree.setModel(new DefaultTreeModel(SRLGUIApp.getApplication().getMainTreeNode()));
        mainTree.setEnabled(false);
        mainTree.setName("mainTree"); // NOI18N
        mainTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mainTreeMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mainTreeMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(mainTree);

        rightPane.setEnabled(false);
        rightPane.setName("rightPane"); // NOI18N

        mainToolBar.setRollover(true);
        mainToolBar.setName("mainToolBar"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(srl.gui.SRLGUIApp.class).getContext().getActionMap(SRLGUIView.class, this);
        newProjTBButton.setAction(actionMap.get("newProject")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(srl.gui.SRLGUIApp.class).getContext().getResourceMap(SRLGUIView.class);
        newProjTBButton.setText(resourceMap.getString("newProjTBButton.text")); // NOI18N
        newProjTBButton.setFocusable(false);
        newProjTBButton.setMinimumSize(new java.awt.Dimension(20, 20));
        newProjTBButton.setName("newProjTBButton"); // NOI18N
        newProjTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        mainToolBar.add(newProjTBButton);

        openProjTBButton.setAction(actionMap.get("openProject")); // NOI18N
        openProjTBButton.setText(resourceMap.getString("openProjTBButton.text")); // NOI18N
        openProjTBButton.setFocusable(false);
        openProjTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openProjTBButton.setMinimumSize(new java.awt.Dimension(20, 20));
        openProjTBButton.setName("openProjTBButton"); // NOI18N
        openProjTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        openProjTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(openProjTBButton);

        saveProjTBButton.setAction(actionMap.get("saveProject")); // NOI18N
        saveProjTBButton.setText(resourceMap.getString("saveProjTBButton.text")); // NOI18N
        saveProjTBButton.setFocusable(false);
        saveProjTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveProjTBButton.setMinimumSize(new java.awt.Dimension(20, 20));
        saveProjTBButton.setName("saveProjTBButton"); // NOI18N
        saveProjTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        saveProjTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(saveProjTBButton);

        jSeparator3.setName("jSeparator3"); // NOI18N
        mainToolBar.add(jSeparator3);

        undoTBButton.setAction(actionMap.get("undo")); // NOI18N
        undoTBButton.setText(resourceMap.getString("undoTBButton.text")); // NOI18N
        undoTBButton.setFocusable(false);
        undoTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoTBButton.setName("undoTBButton"); // NOI18N
        undoTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        undoTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(undoTBButton);

        redoTBButton.setAction(actionMap.get("redo")); // NOI18N
        redoTBButton.setText(resourceMap.getString("redoTBButton.text")); // NOI18N
        redoTBButton.setFocusable(false);
        redoTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoTBButton.setName("redoTBButton"); // NOI18N
        redoTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        redoTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(redoTBButton);

        jSeparator11.setName("jSeparator11"); // NOI18N
        mainToolBar.add(jSeparator11);

        jSeparator12.setName("jSeparator12"); // NOI18N
        mainToolBar.add(jSeparator12);

        newRulesTBButton.setAction(actionMap.get("addRuleSet")); // NOI18N
        newRulesTBButton.setText(resourceMap.getString("newRulesTBButton.text")); // NOI18N
        newRulesTBButton.setFocusable(false);
        newRulesTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newRulesTBButton.setMinimumSize(new java.awt.Dimension(20, 20));
        newRulesTBButton.setName("newRulesTBButton"); // NOI18N
        newRulesTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        newRulesTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(newRulesTBButton);

        newWordListTBButton.setAction(actionMap.get("addWordList")); // NOI18N
        newWordListTBButton.setText(resourceMap.getString("newWordListTBButton.text")); // NOI18N
        newWordListTBButton.setFocusable(false);
        newWordListTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newWordListTBButton.setMinimumSize(new java.awt.Dimension(20, 20));
        newWordListTBButton.setName("newWordListTBButton"); // NOI18N
        newWordListTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        newWordListTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(newWordListTBButton);

        addCorpusTBButton.setAction(actionMap.get("addCorpusDoc")); // NOI18N
        addCorpusTBButton.setFocusable(false);
        addCorpusTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addCorpusTBButton.setMinimumSize(new java.awt.Dimension(20, 20));
        addCorpusTBButton.setName("addCorpusTBButton"); // NOI18N
        addCorpusTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        addCorpusTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(addCorpusTBButton);

        jSeparator4.setName("jSeparator4"); // NOI18N
        mainToolBar.add(jSeparator4);

        tagCorpusTBButton.setAction(actionMap.get("tagCorpus")); // NOI18N
        tagCorpusTBButton.setText(resourceMap.getString("tagCorpusTBButton.text")); // NOI18N
        tagCorpusTBButton.setFocusable(false);
        tagCorpusTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tagCorpusTBButton.setMinimumSize(new java.awt.Dimension(20, 20));
        tagCorpusTBButton.setName("tagCorpusTBButton"); // NOI18N
        tagCorpusTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        tagCorpusTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(tagCorpusTBButton);

        extractTemplatesTBButton.setAction(actionMap.get("extractTemplates")); // NOI18N
        extractTemplatesTBButton.setText(resourceMap.getString("extractTemplatesTBButton.text")); // NOI18N
        extractTemplatesTBButton.setFocusable(false);
        extractTemplatesTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        extractTemplatesTBButton.setName("extractTemplatesTBButton"); // NOI18N
        extractTemplatesTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        extractTemplatesTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(extractTemplatesTBButton);

        jSeparator6.setName("jSeparator6"); // NOI18N
        mainToolBar.add(jSeparator6);

        searchTBButton.setAction(actionMap.get("searchCorpus")); // NOI18N
        searchTBButton.setText(resourceMap.getString("searchTBButton.text")); // NOI18N
        searchTBButton.setFocusable(false);
        searchTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        searchTBButton.setName("searchTBButton"); // NOI18N
        searchTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        searchTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(searchTBButton);

        jSeparator13.setName("jSeparator13"); // NOI18N
        mainToolBar.add(jSeparator13);

        helpTBButton.setAction(actionMap.get("openWiki")); // NOI18N
        helpTBButton.setText(resourceMap.getString("helpTBButton.text")); // NOI18N
        helpTBButton.setFocusable(false);
        helpTBButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        helpTBButton.setName("helpTBButton"); // NOI18N
        helpTBButton.setPreferredSize(new java.awt.Dimension(28, 28));
        helpTBButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(helpTBButton);

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 213, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(rightPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addContainerGap())
            .add(mainToolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .add(mainToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, rightPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE))
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setMnemonic('f');
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newProjectMenuItem.setAction(actionMap.get("newProject")); // NOI18N
        newProjectMenuItem.setText(resourceMap.getString("newProjectMenuItem.text")); // NOI18N
        newProjectMenuItem.setName("newProjectMenuItem"); // NOI18N
        fileMenu.add(newProjectMenuItem);

        openProjectMenuItem.setAction(actionMap.get("openProject")); // NOI18N
        openProjectMenuItem.setText(resourceMap.getString("openProjectMenuItem.text")); // NOI18N
        openProjectMenuItem.setName("openProjectMenuItem"); // NOI18N
        fileMenu.add(openProjectMenuItem);

        saveProjectMenuItem.setAction(actionMap.get("saveProject")); // NOI18N
        saveProjectMenuItem.setText(resourceMap.getString("saveProjectMenuItem.text")); // NOI18N
        saveProjectMenuItem.setName("saveProjectMenuItem"); // NOI18N
        fileMenu.add(saveProjectMenuItem);

        saveProjectAsMenuItem.setAction(actionMap.get("saveProjectAs")); // NOI18N
        saveProjectAsMenuItem.setName("saveProjectAsMenuItem"); // NOI18N
        fileMenu.add(saveProjectAsMenuItem);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText(resourceMap.getString("editMenu.text")); // NOI18N
        editMenu.setName("editMenu"); // NOI18N

        undoMenuItem.setAction(actionMap.get("undo")); // NOI18N
        undoMenuItem.setText(resourceMap.getString("undoMenuItem.text")); // NOI18N
        undoMenuItem.setName("undoMenuItem"); // NOI18N
        editMenu.add(undoMenuItem);

        redoMenuItem.setAction(actionMap.get("redo")); // NOI18N
        redoMenuItem.setText(resourceMap.getString("redoMenuItem.text")); // NOI18N
        redoMenuItem.setName("redoMenuItem"); // NOI18N
        editMenu.add(redoMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        editMenu.add(jSeparator1);

        menuBar.add(editMenu);

        rulesMenu.setMnemonic('r');
        rulesMenu.setText(resourceMap.getString("rulesMenu.text")); // NOI18N
        rulesMenu.setEnabled(false);
        rulesMenu.setName("rulesMenu"); // NOI18N

        addRuleSetMenuItem.setAction(actionMap.get("addRuleSet")); // NOI18N
        addRuleSetMenuItem.setName("addRuleSetMenuItem"); // NOI18N
        rulesMenu.add(addRuleSetMenuItem);

        importRuleSetMenuItem.setAction(actionMap.get("importRuleSet")); // NOI18N
        importRuleSetMenuItem.setName("importRuleSetMenuItem"); // NOI18N
        rulesMenu.add(importRuleSetMenuItem);

        deleteRuleSetMenuItem.setAction(actionMap.get("deleteRuleSet")); // NOI18N
        deleteRuleSetMenuItem.setName("deleteRuleSetMenuItem"); // NOI18N
        rulesMenu.add(deleteRuleSetMenuItem);

        jSeparator5.setName("jSeparator5"); // NOI18N
        rulesMenu.add(jSeparator5);

        addRuleMenuItem.setAction(actionMap.get("addRule")); // NOI18N
        addRuleMenuItem.setName("addRuleMenuItem"); // NOI18N
        rulesMenu.add(addRuleMenuItem);

        removeRuleMenuItem.setAction(actionMap.get("removeRule")); // NOI18N
        removeRuleMenuItem.setName("removeRuleMenuItem"); // NOI18N
        rulesMenu.add(removeRuleMenuItem);

        menuBar.add(rulesMenu);

        wordListsMenu.setMnemonic('w');
        wordListsMenu.setText(resourceMap.getString("wordListsMenu.text")); // NOI18N
        wordListsMenu.setEnabled(false);
        wordListsMenu.setName("wordListsMenu"); // NOI18N

        addWLSMenuItem.setAction(actionMap.get("addWordList")); // NOI18N
        addWLSMenuItem.setName("addWLSMenuItem"); // NOI18N
        wordListsMenu.add(addWLSMenuItem);

        importWLSMenuItem.setAction(actionMap.get("importWordList")); // NOI18N
        importWLSMenuItem.setName("importWLSMenuItem"); // NOI18N
        wordListsMenu.add(importWLSMenuItem);

        jSeparator8.setName("jSeparator8"); // NOI18N
        wordListsMenu.add(jSeparator8);

        addWordListMenuItem.setAction(actionMap.get("addWordListToSet")); // NOI18N
        addWordListMenuItem.setName("addWordListMenuItem"); // NOI18N
        wordListsMenu.add(addWordListMenuItem);

        removeWordListMenuItem.setAction(actionMap.get("removeWordListFromSet")); // NOI18N
        removeWordListMenuItem.setName("removeWordListMenuItem"); // NOI18N
        wordListsMenu.add(removeWordListMenuItem);

        menuBar.add(wordListsMenu);

        corpusMenu.setMnemonic('c');
        corpusMenu.setText(resourceMap.getString("corpusMenu.text")); // NOI18N
        corpusMenu.setEnabled(false);
        corpusMenu.setName("corpusMenu"); // NOI18N

        addToCorpusMenuItem.setAction(actionMap.get("addCorpusDoc")); // NOI18N
        addToCorpusMenuItem.setText(resourceMap.getString("addToCorpusMenuItem.text")); // NOI18N
        addToCorpusMenuItem.setName("addToCorpusMenuItem"); // NOI18N
        corpusMenu.add(addToCorpusMenuItem);

        entityTagMenuItem.setAction(actionMap.get("tagCorpus")); // NOI18N
        entityTagMenuItem.setName("entityTagMenuItem"); // NOI18N
        corpusMenu.add(entityTagMenuItem);

        extractTemplatesMenuItem.setAction(actionMap.get("extractTemplates")); // NOI18N
        extractTemplatesMenuItem.setName("extractTemplatesMenuItem"); // NOI18N
        corpusMenu.add(extractTemplatesMenuItem);

        jSeparator9.setName("jSeparator9"); // NOI18N
        corpusMenu.add(jSeparator9);

        searchMenuItem.setAction(actionMap.get("searchCorpus")); // NOI18N
        searchMenuItem.setName("searchMenuItem"); // NOI18N
        corpusMenu.add(searchMenuItem);

        jSeparator10.setName("jSeparator10"); // NOI18N
        corpusMenu.add(jSeparator10);

        importTaggedMenuItem.setAction(actionMap.get("importTagged")); // NOI18N
        importTaggedMenuItem.setName("importTaggedMenuItem"); // NOI18N
        corpusMenu.add(importTaggedMenuItem);

        writeTaggedMenuItem.setAction(actionMap.get("writeTagged")); // NOI18N
        writeTaggedMenuItem.setName("writeTaggedMenuItem"); // NOI18N
        corpusMenu.add(writeTaggedMenuItem);

        writeTemplatesMenuItem.setAction(actionMap.get("writeTemplates")); // NOI18N
        writeTemplatesMenuItem.setName("writeTemplatesMenuItem"); // NOI18N
        corpusMenu.add(writeTemplatesMenuItem);

        menuBar.add(corpusMenu);

        toolsMenu.setMnemonic('t');
        toolsMenu.setText(resourceMap.getString("toolsMenu.text")); // NOI18N
        toolsMenu.setName("toolsMenu"); // NOI18N

        plugInsMenuItem.setAction(actionMap.get("openPlugInDialog")); // NOI18N
        plugInsMenuItem.setName("plugInsMenuItem"); // NOI18N
        toolsMenu.add(plugInsMenuItem);

        settingMenuItem.setAction(actionMap.get("openSettings")); // NOI18N
        settingMenuItem.setName("settingMenuItem"); // NOI18N
        toolsMenu.add(settingMenuItem);

        menuBar.add(toolsMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        wikiMenuItem.setAction(actionMap.get("openWiki")); // NOI18N
        wikiMenuItem.setName("wikiMenuItem"); // NOI18N
        helpMenu.add(wikiMenuItem);

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        helpMenu.add(jMenuItem1);

        languageDescriptionMenuItem.setAction(actionMap.get("openLanguageDescription")); // NOI18N
        languageDescriptionMenuItem.setName("languageDescriptionMenuItem"); // NOI18N
        helpMenu.add(languageDescriptionMenuItem);

        jSeparator7.setName("jSeparator7"); // NOI18N
        helpMenu.add(jSeparator7);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 558, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents
    private void mainTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainTreeMousePressed
        if (!mainTree.isEnabled()) {
            return;
        }
        if (evt.getClickCount() == 2 && !evt.isPopupTrigger()) {
            final TreePath path = mainTree.getPathForLocation(evt.getX(), evt.getY());
            if (path == null || path.getPath() == null) {
                return;
            }
            if (path.getPath().length == 3 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Template Rules")) {
                String patternSetName = (String) ((DefaultMutableTreeNode) path.getPath()[2]).getUserObject();
                openRuleSetPane(patternSetName, Rule.TEMPLATE_RULE);
            } else if (path.getPath().length == 3 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Entity Rules")) {
                String patternSetName = (String) ((DefaultMutableTreeNode) path.getPath()[2]).getUserObject();
                openRuleSetPane(patternSetName, Rule.ENTITY_RULE);
            } else if (path.getPath().length == 3 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Word List Sets")) {
                String wordListName = (String) ((DefaultMutableTreeNode) path.getPath()[2]).getUserObject();
                openWordListPane(wordListName);
            } else if (path.getPath().length == 2 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Corpus")) {
                openCorpusPane();
            }
        } else if (evt.isPopupTrigger()) {
            onMainTreePopupTrigger(evt);
        }
    }//GEN-LAST:event_mainTreeMousePressed

private void mainTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainTreeMouseReleased
        if(mainTree.isEnabled() && evt.isPopupTrigger()) {
            onMainTreePopupTrigger(evt);
        }
}//GEN-LAST:event_mainTreeMouseReleased

    private void onMainTreePopupTrigger(java.awt.event.MouseEvent evt) {
        final TreePath path = mainTree.getPathForLocation(evt.getX(), evt.getY());
        if (path == null || path.getPath() == null) {
            return;
        }
        if (path.getPath().length == 3 && (((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Template Rules") ||
                ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Entity Rules"))) {
            final int ruleType = ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Entity Rules") ? Rule.ENTITY_RULE : Rule.TEMPLATE_RULE;
            JPopupMenu menu = new JPopupMenu();
            JMenuItem addItem = new JMenuItem();
            addItem.setAction(new AbstractAction("Add rule set") {

                public void actionPerformed(ActionEvent e) {
                    addRuleSet(ruleType);
                }
            });
            menu.add(addItem);
            JMenuItem removeItem = new JMenuItem();
            final String name = ((DefaultMutableTreeNode) path.getPath()[2]).getUserObject().toString();
            removeItem.setAction(new AbstractAction("Remove rule set \"" + name + "\"") {

                public void actionPerformed(ActionEvent e) {
                    SrlProject proj = SRLGUIApp.getApplication().proj;
                    List<RuleSet> ruleSetList = (ruleType == Rule.ENTITY_RULE ? proj.entityRulesets : proj.templateRulesets);
                    Iterator<RuleSet> rsIter = ruleSetList.iterator();
                    RuleSet rs = null;
                    while (rsIter.hasNext()) {
                        RuleSet rs2 = rsIter.next();
                        if (rs2.name.equals(name)) {
                            rs = rs2;
                            break;
                        }
                    }
                    removeRuleSet(ruleType, name,
                            (DefaultMutableTreeNode) path.getLastPathComponent());
                    SRLGUIApp.getApplication().addUndoableEdit(new RemoveRuleSetEdit(name, ruleType, rs));
                }
            });
            menu.add(removeItem);
            JMenuItem openItem = new JMenuItem();
            openItem.setAction(new AbstractAction("Open \"" + name + "\" pane") {

                public void actionPerformed(ActionEvent e) {
                    openRuleSetPane(name, ruleType);
                }
            });
            menu.add(openItem);
            menu.show(mainTree, evt.getX(), evt.getY());
        } else if (path.getPath().length == 2 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Corpus")) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem reInit = new JMenuItem();
            reInit.setAction(new AbstractAction("Re-initialize") {

                public void actionPerformed(ActionEvent e) {
                    Thread t = new Thread(new Runnable() {

                        public void run() {
                            try {
                                SRLGUIApp.getApplication().proj.corpus.resupport(false);
                                JOptionPane.showMessageDialog(getFrame(), "Corpus re-initialized", "Corpus", JOptionPane.INFORMATION_MESSAGE);
                            } catch (IOException x) {
                                error(x, "Cannot re-initialize corpus");
                            } catch(CorpusConcurrencyException x) {
                                error(x, "Corpus locked");
                            }
                        }
                    });
                    t.start();
                }
            });
            menu.add(reInit);
            JMenuItem openItem = new JMenuItem();
            openItem.setAction(new AbstractAction("Open corpus pane") {

                public void actionPerformed(ActionEvent e) {
                    openCorpusPane();
                }
            });
            menu.add(openItem);
            menu.show(mainTree, evt.getX(), evt.getY());
        } else if (path.getPath().length == 3 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Word List Sets")) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem addItem = new JMenuItem();
            addItem.setAction(new AbstractAction("Add word list") {

                public void actionPerformed(ActionEvent e) {
                    addWordList();
                }
            });
            menu.add(addItem);
            JMenuItem removeItem = new JMenuItem();
            final String name = ((DefaultMutableTreeNode) path.getPath()[2]).getUserObject().toString();
            removeItem.setAction(new AbstractAction("Remove word list \"" + name + "\"") {

                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
                    String name = (String)node.getUserObject();
                    WordListSet wls = WordListSet.getWordListSetByName(name);
                    removeWordList((DefaultMutableTreeNode) path.getLastPathComponent());
                    SRLGUIApp.getApplication().addUndoableEdit(new RemoveWordListSetEdit(name, node, wls));
                }
            });
            menu.add(removeItem);
            JMenuItem openItem = new JMenuItem();
            openItem.setAction(new AbstractAction("Open \"" + name + "\" pane") {

                public void actionPerformed(ActionEvent e) {
                    openWordListPane(name);
                }
            });
            menu.add(openItem);
            menu.show(mainTree, evt.getX(), evt.getY());
        } else if (path.getPath().length == 2 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Word List Sets")) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem addItem = new JMenuItem();
            addItem.setAction(new AbstractAction("Add word list") {

                public void actionPerformed(ActionEvent e) {
                    addWordList();
                }
            });
            menu.add(addItem);
            menu.show(mainTree, evt.getX(), evt.getY());
        } else if (path.getPath().length == 2 && (((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Template Rules") ||
                ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Entity Rules"))) {
            final int ruleType = ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Entity Rules") ? Rule.ENTITY_RULE : Rule.TEMPLATE_RULE;
            JPopupMenu menu = new JPopupMenu();
            JMenuItem addItem = new JMenuItem();
            addItem.setAction(new AbstractAction("Add rule set") {

                public void actionPerformed(ActionEvent e) {
                    addRuleSet(ruleType);
                }
            });
            menu.add(addItem);
            menu.show(mainTree, evt.getX(), evt.getY());
        }

    }

    private void openCorpusPane() {
        JPanel p = getPanel(SRLGUIApp.SRL_CORPUS, "");
        if (p != null) {
            rightPane.setSelectedComponent(p);
        } else {
            Component c = new CorpusDocumentPanel();
            rightPane.addTab("Corpus", corpusIcon, c);
            try {
                rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(SRLGUIApp.SRL_CORPUS, "Corpus",corpusIcon));
            } catch(NoSuchMethodError e) {
                // Java 1.5 compatibility
                rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
            }
            rightPane.setSelectedComponent(c);
        }
    }

    private void openWordListPane(String wordListName) {
        JPanel p = getPanel(SRLGUIApp.SRL_WORDLIST, wordListName);
        if (p != null) {
            rightPane.setSelectedComponent(p);
        } else {
            Component c = new WordListPanel(SRLGUIApp.getApplication().wordLists.get(wordListName));
            rightPane.addTab(wordListName, wordListIcon, c);
            try {
                rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(SRLGUIApp.SRL_WORDLIST, wordListName,wordListIcon));
            } catch(NoSuchMethodError e) {
                // Java 1.5 compatibility
                rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
            }
            rightPane.setSelectedComponent(c);
        }
    }

    private class CloseTabIcon implements Icon {
 
	private final Icon icon;
	private JTabbedPane tabbedPane = null;
	private transient Rectangle position = null;
 
	/**
	 * Creates a new instance of CloseTabIcon.
	 */
	public CloseTabIcon() {
		this.icon = closeTabIcon;
	}
 
	/**
	 * when painting, remember last position painted so we can see if the user clicked on the icon.
	 */
	public void paintIcon(Component component, Graphics g, int x, int y) {
 
		// Lazily create a link to the owning JTabbedPane and attach a listener to it, so clicks on the
		// selector tab can be intercepted by this code.
		if (tabbedPane == null) {
			tabbedPane = (JTabbedPane) component;
 
			tabbedPane.addMouseListener(new MouseAdapter() {
 
				@Override
				public void mouseReleased(MouseEvent e) {
					// asking for isConsumed is *very* important, otherwise more than one tab might get closed!
					if (! e.isConsumed() && position.contains(e.getX(), e.getY())) {
						Component p = tabbedPane.getSelectedComponent();
						if (p instanceof Closeable) {
                                                    if (!((Closeable) p).onClose()) {
                                                        e.consume();
                                                        return;
                                                    }
                                                }
                                                rightPane.remove(p);
						e.consume();
					}
				}
			});
		}
 
		position = new Rectangle(x, y, getIconWidth(), getIconHeight());
		icon.paintIcon(component, g, x, y);
	}
 
	/**
	 * just delegate
	 */
	public int getIconWidth() {
		return icon.getIconWidth();
	}
 
	/**
	 * just delegate
	 */
	public int getIconHeight() {
		return icon.getIconHeight();
	}
 
    }
    
    private void openRuleSetPane(String ruleSetName, int ruleType) {
        JPanel p = getPanel(ruleType + 1, ruleSetName);
        if (p != null) {
            rightPane.setSelectedComponent(p);
        } else {
            RuleSet rs;
            if (ruleType == Rule.ENTITY_RULE) {
                rs = SRLGUIApp.getApplication().entityRuleSets.get(ruleSetName);
            } else {
                rs = SRLGUIApp.getApplication().templateRuleSets.get(ruleSetName);
            }
            Component c = new RuleSetPanel(rs);
            rightPane.addTab(ruleSetName, ruleSetIcon, c);
            try {
                rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(ruleType + 1, ruleSetName,ruleSetIcon));
            } catch(NoSuchMethodError e) {
                // Java 1.5 compatibility
                rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
            }
            rightPane.setSelectedComponent(c);
        }
    }
    
    void openShowDocPane(String docName, TreeSet<DocHighlight> highlights, int mode, String msg) {
        ShowDocPanel p = new ShowDocPanel(docName, highlights, mode, msg);
        rightPane.addTab(docName + " (" + msg + ")", p);
        try {
            rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(SRLGUIApp.SRL_SHOW_DOC, p.name,corpusIcon));
        } catch(NoSuchMethodError e) {
            // Java 1.5 compatibility
            rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
        }
        rightPane.setSelectedComponent(p);
    }

    private void error(Exception x, String title) {
        x.printStackTrace();
        JOptionPane.showMessageDialog(getFrame(), x.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    public JPanel getPanel(int type, String name) {
        for (int i = 0; i < rightPane.getTabCount(); i++) {
            Component c = rightPane.getComponentAt(i);
            switch (type) {
                case SRLGUIApp.SRL_ENTITY_RULESET:
                    if (c instanceof RuleSetPanel && ((RuleSetPanel) c).ruleSet.name.equals(name) && ((RuleSetPanel) c).ruleSet.ruleType == Rule.ENTITY_RULE) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_TEMPLATE_RULESET:
                    if (c instanceof RuleSetPanel && ((RuleSetPanel) c).ruleSet.name.equals(name) && ((RuleSetPanel) c).ruleSet.ruleType == Rule.TEMPLATE_RULE) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_WORDLIST:
                    if (c instanceof WordListPanel && ((WordListPanel) c).wl.name.equals(name)) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_CORPUS:
                    if (c instanceof CorpusDocumentPanel) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_PROJECT:
                    if (c instanceof ProjectPanel) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_SEARCH:
                    if (c instanceof SearchPanel && ((SearchPanel)c).name.equals(name)) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_SHOW_DOC:
                    if (c instanceof ShowDocPanel && ((ShowDocPanel)c).name.equals(name)) {
                        return (JPanel) c;
                    }
                    break;
                default:
                    for(SRLPlugin plugin : SRLGUIApp.getApplication().plugins) {
                        if(plugin.panelIs(c, name))
                            return (JPanel)c;
                    }
            }
        }
        return null;
    }

    public void addPanel(String title, JPanel c, int id) {
        rightPane.addTab(title, ruleSetIcon, c);
            try {
                rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(id,title,ruleSetIcon));
            } catch(NoSuchMethodError e) {
                // Java 1.5 compatibility
                rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
            }
            rightPane.setSelectedComponent(c);
    }

    public void closeTab(int type, String name) {
        JPanel p = getPanel(type, name);
        if (p instanceof Closeable) {
            if (!((Closeable) p).onClose()) {
                return;
            }
        }
        rightPane.remove(p);
    }
    JFileChooser jfc;

    @Action
    public void openPatternFile() {
        String[] opts = {"Entity", "Template"};
        int ruleType = JOptionPane.showOptionDialog(this.getFrame(), "Open as entity or template rule set?", "Open rule set", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opts, "Entity");
        if (ruleType == JOptionPane.CLOSED_OPTION) {
            return;
        }
        if (jfc == null) {
            jfc = new JFileChooser();
        }
        if (jfc.showOpenDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            RuleSet ps;
            try {
                ps = RuleSet.loadFromFile(jfc.getSelectedFile(), ruleType);
            } catch (Exception x) {
                error(x, "Could not open file");
                return;
            }
            if (ruleType == Rule.ENTITY_RULE) {
                SRLGUIApp.getApplication().entityRuleSets.put(ps.name, ps);
            } else {
                SRLGUIApp.getApplication().templateRuleSets.put(ps.name, ps);
            }
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(ps.name);
            DefaultMutableTreeNode ruleSet = (ruleType == Rule.ENTITY_RULE ? SRLGUIApp.getApplication().entityRules : SRLGUIApp.getApplication().templateRules);
            ((DefaultTreeModel) mainTree.getModel()).insertNodeInto(node,
                    ruleSet,
                    ruleSet.getChildCount());
            mainTree.scrollPathToVisible(new TreePath(node.getPath()));

        }
    }

    @Action
    public void newProject() {
        SrlProject proj = SRLGUIApp.getApplication().proj;
        if (proj != null && proj.isModified()) {
            int option = JOptionPane.showConfirmDialog(SRLGUIApp.getApplication().getMainFrame(),
                    "Current project has been modified, do you want save?", "SRL Project", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    proj.writeProject();
                    SRLGUIApp.getApplication().clearModified();
                } catch (IOException x) {
                    error(x, "Could not save project");
                    return;
                } catch (CorpusConcurrencyException x) {
                    error(x, "Could not save project");
                    return;
                }
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        if(proj != null) {
            try {
                proj.corpus.closeCorpus();
            } catch(IOException x) {
                x.printStackTrace();
                error(x, "Cannot close project");
                return;
            }
        }
        NewProjectDialog dial = new NewProjectDialog(this.getFrame(), true);
        if (jfc == null) {
            jfc = new JFileChooser();
        }
        if (jfc.getSelectedFile() != null) {
            dial.setDefaultFile(jfc.getSelectedFile().getPath());
        }
        dial.setVisible(true);
        if (dial.returnVal) {
            try {
                proj = new SrlProject(dial.getPath(), dial.getProcessor());
            } catch (Exception x) {
                error(x, "Could not create project");
                return;
            }
            SRLGUIApp.getApplication().proj = proj;
            reloadProject();
            SRLGUIApp.getApplication().setModified();
        }
    }

    private void reloadProject() {
        DefaultTreeModel dtm = (DefaultTreeModel) mainTree.getModel();
        DefaultMutableTreeNode entityRules = SRLGUIApp.getApplication().entityRules;
        DefaultMutableTreeNode templateRules = SRLGUIApp.getApplication().templateRules;
        SRLGUIApp a = SRLGUIApp.getApplication();
        while (entityRules.getChildCount() > 0) {
            dtm.removeNodeFromParent((MutableTreeNode) entityRules.getChildAt(0));
        }
        while (templateRules.getChildCount() > 0) {
            dtm.removeNodeFromParent((MutableTreeNode) templateRules.getChildAt(0));
        }
        DefaultMutableTreeNode wordlists = SRLGUIApp.getApplication().wordList;
        a.wordLists.clear();
        while (wordlists.getChildCount() > 0) {
            dtm.removeNodeFromParent((MutableTreeNode) wordlists.getChildAt(0));
        }
        SrlProject proj = SRLGUIApp.getApplication().proj;
        a.templateRuleSets.clear();
        a.entityRuleSets.clear();
        if (proj != null) {
            for (WordListSet wl : proj.wordlists) {
                dtm.insertNodeInto(new DefaultMutableTreeNode(wl.name), wordlists, wordlists.getChildCount());
                a.wordLists.put(wl.name, wl);
            }
            for (RuleSet rs : proj.entityRulesets) {
                dtm.insertNodeInto(new DefaultMutableTreeNode(rs.name), entityRules, entityRules.getChildCount());
                a.entityRuleSets.put(rs.name, rs);
            }
            for (RuleSet rs : proj.templateRulesets) {
                dtm.insertNodeInto(new DefaultMutableTreeNode(rs.name), templateRules, templateRules.getChildCount());
                a.templateRuleSets.put(rs.name, rs);
            }
        }
        rightPane.removeAll();
        mainTree.setEnabled(true);
        newRulesTBButton.setEnabled(true);
        newWordListTBButton.setEnabled(true);
        addCorpusTBButton.setEnabled(true);
        tagCorpusTBButton.setEnabled(true);
        extractTemplatesTBButton.setEnabled(true);
        searchTBButton.setEnabled(true);
        rulesMenu.setEnabled(true);
        wordListsMenu.setEnabled(true);
        corpusMenu.setEnabled(true);
        rightPane.setEnabled(true);
        rightPane.add(new ProjectPanel(proj));
        SRLGUIApp.getApplication().clearAllEdits();
 //       SRLGUIApp.getApplication().addUndoableEdit(new NullEdit());
    }

    @Action
    public void openProject() {
        SrlProject proj = SRLGUIApp.getApplication().proj;
        if (proj != null && proj.isModified()) {
            int option = JOptionPane.showConfirmDialog(SRLGUIApp.getApplication().getMainFrame(),
                    "Current project has been modified, do you want save?", "SRL Project", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    proj.writeProject();
                    SRLGUIApp.getApplication().clearModified();
                } catch (IOException x) {
                    error(x, "Could not save project");
                    return;
                } catch (CorpusConcurrencyException x) {
                    error(x, "Could not save project");
                    return;
                }
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        try {
            if(proj != null)
                proj.corpus.closeCorpus(); 
        } catch(IOException x) {
            x.printStackTrace();
            error(x, "Cannot close project");
            return;
        }
        if (jfc == null) {
            jfc = new JFileChooser();
        }
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        try {
        if (jfc.showOpenDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            try {
                SRLGUIApp.getApplication().proj = SrlProject.openSrlProject(jfc.getSelectedFile());
                proj = SRLGUIApp.getApplication().proj;
                for (WordListSet wl : proj.wordlists) {
                    proj.corpus.listenToWordListSet(wl);
                    for(String l : wl.getLists()) {
                        proj.corpus.listenToWordList(l, WordListSet.getWordList(l));
                    }
                }
                reloadProject();
            } catch (RuntimeException x) {
                if (x.getMessage().matches("Lock obtain timed out: SimpleFSLock.*")) {
                    if(JOptionPane.showConfirmDialog(this.getFrame(), "Corpus locked! This may occur if SRL Editor failed to shut down properly.\nPlease ensure no other copies of SRL Editor are running.\n Do you wish to clear the lock?",
                            "Corpus Lock", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE)
                            == JOptionPane.YES_OPTION) {
                        try {
                            File f = new File(SRLGUIApp.getApplication().getPreference("ON_START_LOAD_PROJECT_PATH") + "/corpus/write.lock");
                            f.delete();
                            SRLGUIApp.getApplication().proj = SrlProject.openSrlProject(new File(SRLGUIApp.getApplication().getPreference("ON_START_LOAD_PROJECT_PATH")));
                            for (WordListSet wl : proj.wordlists) {
                                proj.corpus.listenToWordListSet(wl);
                                for (String l : wl.getLists()) {
                                    proj.corpus.listenToWordList(l, WordListSet.getWordList(l));
                                }
                            }
                            reloadProject();
                         } catch(Exception x2) {
                           error(x2, "Could not load project");
                       }
                    }
                } else {
                    error(x, "Could not open project");
                }
            } catch (Exception x) {
                error(x, "Could not open project");
            }
        }
        } finally {
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
    }

    @Action
    public void saveProject() {
        SrlProject proj = SRLGUIApp.getApplication().proj;
        if (proj != null && proj.isModified()) {
            try {
                proj.writeProject();
                    SRLGUIApp.getApplication().clearModified();
            } catch (IOException x) {
                error(x, "Could not save project");
            } catch (CorpusConcurrencyException x) {
                error(x, "Could not save project");
            }
        }
    }

    @Action
    public void addRuleSet() {
        String[] opts = {"Entity", "Template"};
        int ruleType = JOptionPane.showOptionDialog(this.getFrame(), "Open as entity or template rule set?", "Open rule set", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opts, "Entity");
        if (ruleType == JOptionPane.CLOSED_OPTION) {
            return;
        }
        addRuleSet(ruleType);
    }

    public void addRuleSet(int ruleType) {
        String name = JOptionPane.showInputDialog(this.getFrame(), "Rule Set Name: ");
        if (name == null) {
            return;
        }
        if(name.matches(".*[<>:\"/\\\\\\|\\?\\*].*") ||
                name.matches(".*\\s.*") ||
                name.equals("")) {
            JOptionPane.showMessageDialog(getFrame(), "Rule set name cannot contain whitespace or the following characters: < > : \" \\ | ? *", 
                    "Invalid rule set name", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SrlProject proj = SRLGUIApp.getApplication().proj;
        for(RuleSet rs : ruleType == Rule.ENTITY_RULE ? proj.entityRulesets : proj.templateRulesets) {
            if(rs.name.equals(name)) {
                JOptionPane.showMessageDialog(this.getFrame(), name + " already exists", "Cannot add rule set", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        DefaultMutableTreeNode dmtn = addRuleSet(name, ruleType, new RuleSet(ruleType, name));
        SRLGUIApp.getApplication().addUndoableEdit(new AddRuleSetEdit(name, ruleType, dmtn));
    }

    private DefaultMutableTreeNode addRuleSet(String name, int ruleType, RuleSet rs) {
        SrlProject proj = SRLGUIApp.getApplication().proj;
        
        if (ruleType == Rule.ENTITY_RULE) {
            proj.entityRulesets.add(rs);
        } else {
            proj.templateRulesets.add(rs);
        }
        if (ruleType == Rule.ENTITY_RULE) {
            SRLGUIApp.getApplication().entityRuleSets.put(name, rs);
        } else {
            SRLGUIApp.getApplication().templateRuleSets.put(name, rs);
        }
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
        DefaultMutableTreeNode ruleSet = (ruleType == Rule.ENTITY_RULE ? SRLGUIApp.getApplication().entityRules : SRLGUIApp.getApplication().templateRules);
        ((DefaultTreeModel) mainTree.getModel()).insertNodeInto(node,
                ruleSet,
                ruleSet.getChildCount());
        mainTree.scrollPathToVisible(new TreePath(node.getPath()));
        return node;
    }

    public void removeRuleSet(int ruleType, String setName, DefaultMutableTreeNode node) {
        JPanel panel = getPanel(ruleType+1, setName);
        if(panel != null) {
            mainPanel.remove(panel);
        }

        SrlProject proj = SRLGUIApp.getApplication().proj;
        List<RuleSet> ruleSetList = (ruleType == Rule.ENTITY_RULE ? proj.entityRulesets : proj.templateRulesets);
        Iterator<RuleSet> rsIter = ruleSetList.iterator();
        while (rsIter.hasNext()) {
            RuleSet rs = rsIter.next();
            if (rs.name.equals(setName)) {
                rsIter.remove();
                break;
            }
        }
        if (ruleType == Rule.ENTITY_RULE) {
            SRLGUIApp.getApplication().entityRuleSets.remove(setName);
        } else {
            SRLGUIApp.getApplication().templateRuleSets.remove(setName);
        }
        DefaultTreeModel dtm = (DefaultTreeModel) mainTree.getModel();
        dtm.removeNodeFromParent(node);
    }

    @Action
    public void addWordList() {
        String name = JOptionPane.showInputDialog(this.getFrame(), "Word List Set Name: ");
        if (name == null) {
            return;
        }
        if(name.matches(".*\\W.*") ||
                name.equals("")) {
            JOptionPane.showMessageDialog(getFrame(), "Word list name cannot contain non-word characters. (Not A-Z or _)", 
                    "Invalid word list name", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SrlProject proj = SRLGUIApp.getApplication().proj;
        for(WordListSet wl : proj.wordlists) {
            if(wl.name.equals(name)) {
                 JOptionPane.showMessageDialog(this.getFrame(), name + " already exists", "Cannot add word list set", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        DefaultMutableTreeNode node = addWordList(name, new WordListSet(name, proj.corpus.getProcessor()));
        SRLGUIApp.getApplication().addUndoableEdit(new AddWordListSetEdit(name, node));
    }

    private DefaultMutableTreeNode addWordList(String name, WordListSet wl) {
        SrlProject proj = SRLGUIApp.getApplication().proj;
        proj.corpus.listenToWordListSet(wl);
        proj.wordlists.add(wl);
        wl.restore();
        SRLGUIApp.getApplication().wordLists.put(name, wl);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
        ((DefaultTreeModel) mainTree.getModel()).insertNodeInto(node,
                SRLGUIApp.getApplication().wordList,
                SRLGUIApp.getApplication().wordList.getChildCount());
        mainTree.scrollPathToVisible(new TreePath(node.getPath()));
        return node;
    }

    public void removeWordList(DefaultMutableTreeNode node) {
        String name = node.getUserObject().toString();
        JPanel panel = getPanel(SRLGUIApp.SRL_WORDLIST, name);
        if(panel != null) {
            mainPanel.remove(panel);
        }
        SrlProject proj = SRLGUIApp.getApplication().proj;
        WordListSet wl = SRLGUIApp.getApplication().wordLists.get(name);
        wl.die();
        proj.wordlists.remove(wl);
        SRLGUIApp.getApplication().wordLists.remove(name);
        ((DefaultTreeModel) mainTree.getModel()).removeNodeFromParent(node);
    }

    void addPlugin(SRLPlugin plugin) {
        JMenuItem pluginMenu = plugin.getMenu();
        if(pluginMenu == null)
            return;
        int idx;
        for(idx = 0; idx < toolsMenu.getComponentCount(); idx++) {
            if(toolsMenu.getComponent(idx) instanceof JSeparator) {
                break;
            }
        }
        if(idx == toolsMenu.getComponentCount()) {
            idx = 0;
            toolsMenu.add(new JSeparator(),0);
        }
        toolsMenu.add(pluginMenu,idx);
    }

    private class CustomEncodingFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File f) {
            return true;
        }

        @Override
        public String getDescription() {
            return "Plain text (Non-default encoding)";
        }
        
    }
    
    private class NullTask extends Task {

        public NullTask() {
            super(SRLGUIApp.getApplication());
        }

        @Override
        protected Object doInBackground() throws Exception {
            return null;
        }
        
    }
    
    @Action
    public Task addCorpusDoc() {
        try {
            if(jfc != null)
                jfc = new JFileChooser(jfc.getSelectedFile());
            else
                jfc = new JFileChooser();
            jfc.setMultiSelectionEnabled(true);
            jfc.addChoosableFileFilter(new CustomEncodingFilter());
            jfc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

                 @Override
                   public boolean accept(File f) {
                         return true;
                  }

            @Override
            public String getDescription() {
                return "Plain text (" + Charset.defaultCharset().name() + ")";
            }
        });
        String encoding = null;
        if (jfc.showOpenDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            if(jfc.getFileFilter() instanceof CustomEncodingFilter) {
                encoding = JOptionPane.showInputDialog(this.getFrame(), "Enter encoding (e.g., \"UTF-8\"): ", "");
                if(encoding == null) {
                    jfc.setMultiSelectionEnabled(false);
                    jfc.resetChoosableFileFilters();
                    return new NullTask();
                }
                try {
                    Charset.forName(encoding);
                } catch(Exception x) {
                    JOptionPane.showMessageDialog(this.getFrame(), "Invalid encoding", "Cannot load", JOptionPane.WARNING_MESSAGE);
                    jfc.setMultiSelectionEnabled(false);
                    jfc.resetChoosableFileFilters();
                    return new NullTask();
                }
            }
        }
        File[] sf = jfc.getSelectedFiles();
        return new DocumentLoadThread(encoding, sf, false);
        } finally {
            jfc.setMultiSelectionEnabled(false);
        jfc.resetChoosableFileFilters();

        }
    }

    private class AddCorpusDocTask extends org.jdesktop.application.Task<Object, Void> {
        AddCorpusDocTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to AddCorpusDocTask fields, here.
            super(app);
        }
        @Override protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            return null;  // return your result
        }
        @Override protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }
    
    private class DocumentLoadThread extends Task {
        String encoding;
        File[] selectedFiles;
        boolean tagged;
        
        public DocumentLoadThread(String encoding, File[] selectedFiles, boolean tagged)
        {
            super(SRLGUIApp.getApplication());
            this.encoding = encoding;
            this.selectedFiles = selectedFiles;
            this.tagged = tagged;
        }

        
        public Object doInBackground() throws Exception {
            try {
                Corpus corpus = SRLGUIApp.getApplication().proj.corpus;
                long lockID = corpus.reopenIndex();
                try {
                    int replaceDoc = 0; // 0=? 1=YES -1=NO
                    JPanel p = getPanel(SRLGUIApp.getApplication().SRL_CORPUS, "");
                    int i = 0;
                    for (File file : selectedFiles) {
                        String fName = file.getName().replaceAll("[^A-Za-z0-9]", "");
                        setMessage("Adding " + fName);
                        setProgress((float)i++ / (float)selectedFiles.length);
                        BufferedReader br;
                        if(encoding == null) {
                            br= new BufferedReader(new FileReader(file));
                        } else {
                            br = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));
                        }
                        StringBuffer contents = new StringBuffer();
                        String in = br.readLine();
                        while (in != null) {
                            contents.append(in + "\n");
                            in = br.readLine();
                        }
                        br.close();
                        if(corpus.containsDoc(fName)) {
                            if(replaceDoc == 0) {
                                String[] opts = { "Skip", "Replace", "Skip All", "Replace All" };
                                int opt = JOptionPane.showOptionDialog(SRLGUIApp.getApplication().getMainFrame(), "Document called "+fName+" already exists", "Duplicate Document", 
                                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
                                if(opt == 2) { replaceDoc = -1; }
                                if(opt == 3) { replaceDoc = 1; }
                                if(opt == 0 || opt == 2) {
                                    corpus.updateDoc(fName, contents.toString(),false);
                                }
                            } else if(replaceDoc == 1) {
                                corpus.updateDoc(fName, contents.toString(),false);
                            }
                        } else
                            corpus.addDoc(fName, contents.toString(), tagged,false);
                        if (p != null) {
                            ((CorpusDocumentPanel) p).addDoc(fName);
                        }
                    }
                } finally {
                    corpus.optimizeIndex(lockID);
                }
            } catch (Exception x) {
                error(x, "Could not add documents to corpus");
            }
            if(selectedFiles.length > 0) {
                setMessage("All documents added");
                setProgress(1.0f);
            }
            return null;
        }
    }

    @Action
    public Task tagCorpus() {
        return new TagCorpusTask();
    }
    
    public void enableSave() {
        saveProjTBButton.setEnabled(true);
        saveProjectMenuItem.setEnabled(true);
        saveProjectAsMenuItem.setEnabled(true);
    }
    
    public void disableSave() {
        saveProjTBButton.setEnabled(false);
        saveProjectMenuItem.setEnabled(false);
    }

    private class TagCorpusTask extends Task implements srl.tools.process.ProgressMonitor {

        TagCorpusTask() {
            super(SRLGUIApp.getApplication());
        }

        public Object doInBackground() throws Exception {
            try {
                CorpusExtractor ce = new CorpusExtractor(SRLGUIApp.getApplication().proj.corpus);
                LinkedList<CorpusExtractor.Overlap> overlaps = new LinkedList<CorpusExtractor.Overlap>();
                ce.tagCorpus(SRLGUIApp.getApplication().proj.entityRulesets,overlaps, this, false);
                if(overlaps.isEmpty())
                    JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), "Corpus tagging complete", "Corpus tagger", JOptionPane.INFORMATION_MESSAGE);
                else {
                    OverlapMessageDialog omd = new OverlapMessageDialog(SRLGUIApp.getApplication().getMainFrame(), true, overlaps);
                    omd.setVisible(true);
                }
            } catch (IOException x) {
                error(x, "Corpus Tagging Failed");
            }
            return null;
        }

        public void setMessageVal(String s) {
            setMessage(s);
        }

        public void setProgressVal(float f) {
            setProgress(f);
        }
        
        
    }

    @Action
    public Task extractTemplates() {
        return new ExtractTemplatesTask();
    }
    
    private class ExtractTemplatesTask extends Task implements srl.tools.process.ProgressMonitor {

        public ExtractTemplatesTask() {
            super(SRLGUIApp.getApplication());
        }
        

        public Object doInBackground() throws Exception {
            try {
                CorpusExtractor ce = new CorpusExtractor(SRLGUIApp.getApplication().proj.corpus);
                ce.extractTemplates(SRLGUIApp.getApplication().proj.templateRulesets, this,false);
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), "Template Extraction Complete", "Template Extraction", JOptionPane.INFORMATION_MESSAGE);
            } catch(IOException x) {
                error(x, "Corpus Tagging Failed");
            }
            return null;
        }

        public void setMessageVal(String s) {
            setMessage(s);
        }

        public void setProgressVal(float f) {
            setProgress(f);
        }
        
    }

    public static int searchCount = 1;
    
    @Action
    public void searchCorpus() {
        String query = JOptionPane.showInputDialog(getFrame(), "Query", "");
        if(query != null && query.length() != 0) {
            String title = "Search " + searchCount++;
            JPanel c =  new SearchPanel(query, title);
            rightPane.addTab(title, c);
             try {
                rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(SRLGUIApp.SRL_SEARCH, title,searchIcon));
            } catch(NoSuchMethodError e) {
                // Java 1.5 compatibility
                rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
            }
            rightPane.setSelectedComponent(c);
        }
    }

    @Action
    public void openWiki() {
        try {
            Desktop.getDesktop().browse(new URI("http://code.google.com/p/srl-editor/w/list"));
        } catch(Exception x) {
            error(x, "Could not open external browser");
        }
    }

    @Action
    public Task writeTagged() {
        return new WriteTaggedTask(getApplication());
    }

    private class WriteTaggedTask extends org.jdesktop.application.Task<Object, Void> {
        WriteTaggedTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to WriteTaggedTask fields, here.
            super(app);
        }
        @Override protected Object doInBackground() {
            if(jfc == null)
                jfc = new JFileChooser();
            jfc.setFileSelectionMode(jfc.DIRECTORIES_ONLY);
            try {
                if(jfc.showOpenDialog(SRLGUIApp.getApplication().getMainFrame()) !=
                        jfc.APPROVE_OPTION)
                    return null;
                File directory = jfc.getSelectedFile();
                Corpus corpus = SRLGUIApp.getApplication().proj.corpus;
                Set<String> docNames = corpus.getDocNames();
                float i = 0;
                for(String docName : docNames) {
                    setMessage("Writing tagged: " + docName);
                    setProgress((float)i++ / (float)docNames.size());
                    List<String> cont = corpus.getDocTaggedContents(docName);
                    PrintStream out = new PrintStream(new File(directory, docName + ".tagged"));
                    for(String c : cont) {
                        out.println(c);
                    }
                    out.close();
                }
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), "Tagged documents written", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch(IOException x) {
                error(x, "Could not write document contents");
            } catch(CorpusConcurrencyException x) {
                error(x, "Could not write document contents");
            } finally {
                jfc.setFileSelectionMode(jfc.FILES_ONLY);
            }
            return null;  // return your result
        }
        @Override protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public Task writeTemplates() {
        return new WriteTemplatesTask(getApplication());
    }

    private class WriteTemplatesTask extends org.jdesktop.application.Task<Object, Void> {
        WriteTemplatesTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to WriteTemplatesTask fields, here.
            super(app);
        }
        @Override protected Object doInBackground() {
            if(jfc == null)
                jfc = new JFileChooser();
            jfc.setFileSelectionMode(jfc.DIRECTORIES_ONLY);
            try {
                if(jfc.showOpenDialog(SRLGUIApp.getApplication().getMainFrame()) !=
                        jfc.APPROVE_OPTION)
                    return null;
                File directory = jfc.getSelectedFile();
                Corpus corpus = SRLGUIApp.getApplication().proj.corpus;
                Set<String> docNames = corpus.getDocNames();
                float i = 0;
                for(String docName : docNames) {
                    setMessage("Writing template: " + docName);
                    setProgress(i++ / (float)docNames.size());
                    List<String> cont = corpus.getDocTemplateExtractions(docName);
                    PrintStream out = new PrintStream(new File(directory, docName + ".templates"));
                    for(String c : cont) {
                        out.println(c);
                    }
                    out.close();
                }
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), "Templates documents written", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch(IOException x) {
                error(x, "Could not write document contents");
            } catch(CorpusConcurrencyException x) {
                error(x, "Could not write document contents");
            } finally {
                jfc.setFileSelectionMode(jfc.FILES_ONLY);
            }
            return null;  // return your result
        }
        @Override protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public void importRuleSet() {
        try {
            jfc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.getName().matches(".*\\.rule\\.srl") || f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "SRL rule files (*.rule.srl)";
                }
            });
            if(jfc.showOpenDialog(getFrame()) != jfc.APPROVE_OPTION)
                return;
            String[] opts = {"Entity", "Template"};
            int ruleType = JOptionPane.showOptionDialog(this.getFrame(), "Open as entity or template rule set?", "Open rule set", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opts, "Entity");
            File f = jfc.getSelectedFile();
            String name;
            if(f.getName().matches(".*\\.rule\\.srl"))
                name = f.getName().substring(0, f.getName().length()-9);
            else
                name = f.getName();
            SrlProject proj = SRLGUIApp.getApplication().proj;
            RuleSet rs = RuleSet.loadFromFile(f, ruleType);
            if (ruleType == Rule.ENTITY_RULE) {
                proj.entityRulesets.add(rs);
            } else {
                proj.templateRulesets.add(rs);
            }
           if (ruleType == Rule.ENTITY_RULE) {
                SRLGUIApp.getApplication().entityRuleSets.put(name, rs);
            } else {
                SRLGUIApp.getApplication().templateRuleSets.put(name, rs);
            }
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
            DefaultMutableTreeNode ruleSet = (ruleType == Rule.ENTITY_RULE ? SRLGUIApp.getApplication().entityRules : SRLGUIApp.getApplication().templateRules);
            ((DefaultTreeModel) mainTree.getModel()).insertNodeInto(node,
                ruleSet,
                ruleSet.getChildCount());
            mainTree.scrollPathToVisible(new TreePath(node.getPath()));
            SRLGUIApp.getApplication().addUndoableEdit(new ImportRuleSetEdit(name, ruleType, node, rs));
        } catch(Exception x) {
            error(x, "Could not import rule set");
        } finally {
            jfc.resetChoosableFileFilters();
        }
    }

    @Action
    public void importWordList() {
        try {
            jfc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.getName().matches(".*\\.wordlist\\.srl") || f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "SRL word list files (*.wordlist.srl)";
                }
            });
            if(jfc.showOpenDialog(getFrame()) != jfc.APPROVE_OPTION)
                return;
            File f = jfc.getSelectedFile();
            String name;
            if(f.getName().matches(".*\\.wordlist\\.srl"))
                name = f.getName().substring(0, f.getName().length()-13);
            else
                name = f.getName();
            if(name.matches(".*\\W.*") || name.length()==0) {
                JOptionPane.showMessageDialog(getFrame(), name + " is not a valid name... name must be only word characters", "Cannot add word list set", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SrlProject proj = SRLGUIApp.getApplication().proj;
            for(WordListSet wl : proj.wordlists) {
               if(wl.name.equals(name)) {
                     JOptionPane.showMessageDialog(this.getFrame(), name + " already exists", "Cannot add word list set", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            WordListSet wl = WordListSet.loadFromFile(f, proj.processor);
            proj.corpus.listenToWordListSet(wl);
            proj.wordlists.add(wl);
            SRLGUIApp.getApplication().wordLists.put(name, wl);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
            ((DefaultTreeModel) mainTree.getModel()).insertNodeInto(node,
                    SRLGUIApp.getApplication().wordList,
                    SRLGUIApp.getApplication().wordList.getChildCount());
            mainTree.scrollPathToVisible(new TreePath(node.getPath()));
            SRLGUIApp.getApplication().addUndoableEdit(new ImportWordListSetEdit(name, node, wl));
        } catch(Exception x) {
            error(x, "Could not import word list");
        } finally {
            jfc.resetChoosableFileFilters();
        }
    }

    @Action
    public Task importTagged() {
         try {
            // JOptionPane.showMessageDialog(getFrame(), "This needs fixing... please email jmccrae@nii.ac.jp if this has somehow made it to a release version");
            if (jfc == null) {
               jfc = new JFileChooser();
            } else {
                jfc = new JFileChooser(jfc.getSelectedFile());
            }
            jfc.setMultiSelectionEnabled(true);
            jfc.addChoosableFileFilter(new CustomEncodingFilter());
            jfc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

                 @Override
                   public boolean accept(File f) {
                         return true;
                  }

            @Override
            public String getDescription() {
                return "Plain text (" + Charset.defaultCharset().name() + ")";
            }
        });
        String encoding = null;
        if (jfc.showOpenDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            if(jfc.getFileFilter() instanceof CustomEncodingFilter) {
                encoding = JOptionPane.showInputDialog(this.getFrame(), "Enter encoding (e.g., \"UTF-8\"): ", "");
                if(encoding == null) {
                    jfc.setMultiSelectionEnabled(false);
                    jfc.resetChoosableFileFilters();
                    return new NullTask();
                }
                try {
                    Charset.forName(encoding);
                } catch(Exception x) {
                    JOptionPane.showMessageDialog(this.getFrame(), "Invalid encoding", "Cannot load", JOptionPane.WARNING_MESSAGE);
                    jfc.setMultiSelectionEnabled(false);
                    jfc.resetChoosableFileFilters();
                    return new NullTask();
                }
            }
        }
        File[] sf = jfc.getSelectedFiles();
        return new DocumentLoadThread(encoding, sf, true);
        } finally {
            jfc.setMultiSelectionEnabled(false);
        jfc.resetChoosableFileFilters();

        }
    }

    void onUndoableEditAdd() {
        UndoManager undoManager = SRLGUIApp.getApplication().undoManager;
        undoTBButton.setEnabled(undoManager.canUndo());
        undoTBButton.setToolTipText(undoManager.canUndo() ? undoManager.getUndoPresentationName() : "Cannot undo");
        undoMenuItem.setEnabled(undoManager.canUndo());
        undoMenuItem.setToolTipText(undoManager.canUndo() ? undoManager.getUndoPresentationName() : "Cannot undo");
        redoTBButton.setEnabled(undoManager.canRedo());
        redoTBButton.setToolTipText(undoManager.canRedo() ? undoManager.getRedoPresentationName() : "Cannot redo");
        redoMenuItem.setEnabled(undoManager.canRedo());
        redoMenuItem.setToolTipText(undoManager.canRedo() ? undoManager.getRedoPresentationName() : "Cannot redo");
    }

    @Action
    public void undo() {
        UndoManager undoManager = SRLGUIApp.getApplication().undoManager;
        if(undoManager.canUndo()) {
            undoManager.undo();
        } else {
            JOptionPane.showMessageDialog(this.getComponent(), "The last action cannot be undone. (Please file bug report)", "Undo Not Possible", JOptionPane.WARNING_MESSAGE);
        }
        SRLGUIApp.getApplication().proj.setModified();
        enableSave();
       onUndoableEditAdd();
    }

    @Action
    public void redo() {
        UndoManager undoManager = SRLGUIApp.getApplication().undoManager;
        if(undoManager.canRedo()) {
            undoManager.redo();
        } else {
            JOptionPane.showMessageDialog(this.getComponent(), "The last action cannot be undone. (Please file bug report)", "Undo Not Possible", JOptionPane.WARNING_MESSAGE);
        }
        SRLGUIApp.getApplication().proj.setModified();
        enableSave();
        onUndoableEditAdd();
    }

    @Action
    public void cut() {
    }

    @Action
    public void copy() {

    }

    @Action
    public void paste() {
    }

    @Action
    public void deleteRuleSet() {
        TreePath path = mainTree.getSelectionPath();
        if(path == null || path.getPath() == null || path.getPath().length != 3)
            return;
        Object o = ((DefaultMutableTreeNode)path.getPath()[1]).getUserObject();
        int ruleType;
        if(o.equals("Template Rules"))
            ruleType = Rule.TEMPLATE_RULE;
        else if(o.equals("Entity Rules"))
            ruleType = Rule.ENTITY_RULE;
        else
            return;
        String setName = (String)((DefaultMutableTreeNode)path.getPath()[2]).getUserObject();
        if(JOptionPane.showConfirmDialog(this.getFrame(), "Remove " + (ruleType == Rule.TEMPLATE_RULE ? "template" : "entity")
               + " rule set " + setName + "?", "Remove Rule Set", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
           return;
        List<RuleSet> rsList = ruleType == Rule.TEMPLATE_RULE ? SRLGUIApp.getApplication().proj.templateRulesets :
            SRLGUIApp.getApplication().proj.entityRulesets;
        RuleSet rs = null;
        for(RuleSet rs2 : rsList) {
            if(rs2.name.equals(setName))
                rs = rs2;
        }
        removeRuleSet(ruleType, setName, (DefaultMutableTreeNode)path.getPath()[2]);
        SRLGUIApp.getApplication().addUndoableEdit(new RemoveRuleSetEdit(setName, ruleType, rs));
    }

    @Action
    public void addRule() {
        Component c = rightPane.getSelectedComponent();
        if(c == null || !(c instanceof RuleSetPanel))
            return;
        RuleSetPanel panel = (RuleSetPanel)c;
        panel.addRule();
    }

    @Action
    public void removeRule() {
        Component c = rightPane.getSelectedComponent();
        if(c == null || !(c instanceof RuleSetPanel))
            return;
        RuleSetPanel panel = (RuleSetPanel)c;
        panel.removeRule();
    }

    @Action
    public void saveProjectAs() {
        SrlProject proj = SRLGUIApp.getApplication().proj;
        jfc.setSelectedFile(proj.getPath());
        if(jfc.showSaveDialog(getFrame()) != JFileChooser.APPROVE_OPTION)
            return;
        if (proj != null && proj.isModified()) {
            try {
                proj.writeProject(jfc.getSelectedFile());
                    SRLGUIApp.getApplication().clearModified();
            } catch (IOException x) {
                error(x, "Could not save project");
            } catch (CorpusConcurrencyException x) {
                error(x, "Could not save project");
            }
        }
    }

    @Action
    public void addWordListToSet() {
        Component c = rightPane.getSelectedComponent();
        if(c == null || !(c instanceof WordListPanel))
            return;
        WordListPanel panel = (WordListPanel)c;
        panel.addListAction();
    }

    @Action
    public void removeWordListFromSet() {
        Component c = rightPane.getSelectedComponent();
        if(c == null || !(c instanceof WordListPanel))
            return;
        WordListPanel panel = (WordListPanel)c;
        panel.deleteListAction();
    }

    @Action
    public void openLanguageDescription() {
         try {
            Desktop.getDesktop().browse(new URI("http://code.google.com/p/srl-editor/wiki/SRLLanguageDescription"));
        } catch(Exception x) {
            JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not open external browser", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class NullEdit extends SimpleUndoableEdit {
        public NullEdit() {}

        public String getPresentationName() {
            return "";
        }

        public void redo() throws CannotRedoException {
            throw new CannotRedoException();
        }

        public void undo() throws CannotUndoException {
            throw new CannotUndoException();
        }

        public boolean canRedo() {
            return false;
        }

        public boolean canUndo() {
            return false;
        }
    }

    private class AddRuleSetEdit extends SimpleUndoableEdit {
        String ruleSetName;
        int ruleType;
        DefaultMutableTreeNode dmtn;

        public AddRuleSetEdit(String ruleSetName, int ruleType, DefaultMutableTreeNode dmtn) {
            this.ruleSetName = ruleSetName;
            this.ruleType = ruleType;
            this.dmtn = dmtn;
        }

        public String getPresentationName() {
            return "Add rule set " + ruleSetName;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            dmtn = addRuleSet(ruleSetName, ruleType, new RuleSet(ruleType, ruleSetName));
        }

        public void undo() throws CannotUndoException {
            undone = true;
            removeRuleSet(ruleType, ruleSetName, dmtn);
        }
    }

     private class ImportRuleSetEdit extends SimpleUndoableEdit {
        String ruleSetName;
        int ruleType;
        DefaultMutableTreeNode dmtn;
        RuleSet rs;

        public ImportRuleSetEdit(String ruleSetName, int ruleType, DefaultMutableTreeNode dmtn, RuleSet rs) {
            this.ruleSetName = ruleSetName;
            this.ruleType = ruleType;
            this.dmtn = dmtn;
            this.rs = rs;
        }


        public String getPresentationName() {
            return "Import rule set " + ruleSetName;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            dmtn = addRuleSet(ruleSetName, ruleType, rs);
        }

        public void undo() throws CannotUndoException {
            undone = true;
            removeRuleSet(ruleType, ruleSetName, dmtn);
        }
    }

    private class RemoveRuleSetEdit extends SimpleUndoableEdit {
        String ruleSetName;
        int ruleType;
        DefaultMutableTreeNode node;
        RuleSet rs;

        public RemoveRuleSetEdit(String ruleSetName, int ruleType, RuleSet rs) {
            this.ruleSetName = ruleSetName;
            this.ruleType = ruleType;
            this.node = null;
            this.rs = rs;
        }

        public String getPresentationName() {
            return "Remove rule set " + ruleSetName;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            removeRuleSet(ruleType, ruleSetName, node);
        }

        public void undo() throws CannotUndoException {
            undone = true;
            node = addRuleSet(ruleSetName, ruleType, rs);
        }
    }

    private class AddWordListSetEdit extends SimpleUndoableEdit {
        String wordListName;
        DefaultMutableTreeNode node;

        public AddWordListSetEdit(String wordListName, DefaultMutableTreeNode node) {
            this.wordListName = wordListName;
            this.node = node;
        }

        public String getPresentationName() {
            return "Add Word List Set " + wordListName;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            node = addWordList(wordListName, new WordListSet(wordListName, SRLGUIApp.getApplication().proj.processor));
        }

        public void undo() throws CannotUndoException {
            undone = true;
            removeWordList(node);
        }
    }

    private class ImportWordListSetEdit extends SimpleUndoableEdit {
        String wordListName;
        DefaultMutableTreeNode node;
        WordListSet wls;

        public ImportWordListSetEdit(String wordListName, DefaultMutableTreeNode node, WordListSet wls) {
            this.wordListName = wordListName;
            this.node = node;
            this.wls = wls;
        }

        public String getPresentationName() {
            return "Add Word List Set " + wordListName;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            node = addWordList(wordListName, wls);
        }

        public void undo() throws CannotUndoException {
            undone = true;
            removeWordList(node);
        }
    }

    private class RemoveWordListSetEdit extends SimpleUndoableEdit {
        String wordListName;
        DefaultMutableTreeNode node;
        WordListSet set;

        public RemoveWordListSetEdit(String wordListName, DefaultMutableTreeNode node, WordListSet set) {
            this.wordListName = wordListName;
            this.node = node;
            this.set = set;
        }


        public String getPresentationName() {
            return "Remove Word List Set " + wordListName;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            removeWordList(node);
        }

        public void undo() throws CannotUndoException {
            undone = true;
            node = addWordList(wordListName, set);
        }


    }

    @Action
    public void openPlugInDialog() {
        PluginManagerDialog manager = new PluginManagerDialog();
        manager.setVisible(true);
    }

    @Action
    public void openSettings() {
        SettingsDialog dialog = new SettingsDialog(this.getFrame(), true);
        dialog.setVisible(true);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCorpusTBButton;
    private javax.swing.JMenuItem addRuleMenuItem;
    private javax.swing.JMenuItem addRuleSetMenuItem;
    private javax.swing.JMenuItem addToCorpusMenuItem;
    private javax.swing.JMenuItem addWLSMenuItem;
    private javax.swing.JMenuItem addWordListMenuItem;
    private javax.swing.JMenu corpusMenu;
    private javax.swing.JMenuItem deleteRuleSetMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem entityTagMenuItem;
    private javax.swing.JMenuItem extractTemplatesMenuItem;
    private javax.swing.JButton extractTemplatesTBButton;
    private javax.swing.JButton helpTBButton;
    private javax.swing.JMenuItem importRuleSetMenuItem;
    private javax.swing.JMenuItem importTaggedMenuItem;
    private javax.swing.JMenuItem importWLSMenuItem;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator12;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JMenuItem languageDescriptionMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JTree mainTree;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton newProjTBButton;
    private javax.swing.JMenuItem newProjectMenuItem;
    private javax.swing.JButton newRulesTBButton;
    private javax.swing.JButton newWordListTBButton;
    private javax.swing.JButton openProjTBButton;
    private javax.swing.JMenuItem openProjectMenuItem;
    private javax.swing.JMenuItem plugInsMenuItem;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JButton redoTBButton;
    private javax.swing.JMenuItem removeRuleMenuItem;
    private javax.swing.JMenuItem removeWordListMenuItem;
    private javax.swing.JTabbedPane rightPane;
    private javax.swing.JMenu rulesMenu;
    private javax.swing.JButton saveProjTBButton;
    private javax.swing.JMenuItem saveProjectAsMenuItem;
    private javax.swing.JMenuItem saveProjectMenuItem;
    private javax.swing.JMenuItem searchMenuItem;
    private javax.swing.JButton searchTBButton;
    private javax.swing.JMenuItem settingMenuItem;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton tagCorpusTBButton;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JButton undoTBButton;
    private javax.swing.JMenuItem wikiMenuItem;
    private javax.swing.JMenu wordListsMenu;
    private javax.swing.JMenuItem writeTaggedMenuItem;
    private javax.swing.JMenuItem writeTemplatesMenuItem;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private final Icon ruleSetIcon;
    private final Icon wordListIcon;
    private final Icon corpusIcon;
    private final Icon closeTabIcon;
    private final Icon searchIcon;
    private final Icon copyIcon;
    private final Icon cutIcon;
    private final Icon pasteIcon;
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
