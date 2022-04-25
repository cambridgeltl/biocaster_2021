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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.apache.lucene.document.Document;
import org.jdesktop.application.Action;
import srl.rule.*;
import javax.swing.table.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import srl.tools.process.StopSignal;
import srl.tools.strings.Strings;
import srl.tools.struct.Pair;
import org.apache.lucene.analysis.TokenStream;
import srl.rule.parser.ParseException;
import srl.corpus.*;
import srl.rule.parser.TokenMgrError;

/**
 *
 * @author  john
 */
public class RuleSetPanel extends javax.swing.JPanel implements Closeable {

    RuleSet ruleSet;
    int ruleCount;
    boolean userChangeFlag = true;
    HashMap<String, Rule> ruleLookup;
    int oldSelectIndex = -1;

    /** Creates new form RuleSetPanel */
    public RuleSetPanel(RuleSet ps) {
        initComponents();
        initRuleList();

        ruleSet = ps;
        DefaultTableModel dtm = (DefaultTableModel) ruleList.getModel();
        ruleLookup = new HashMap<String, Rule>();

        for (int i = 0; i < ps.rules.size(); i++) {
            Object[] objs = new Object[2];
            objs[0] = ps.rules.get(i).first;
            objs[1] = ps.rules.get(i).second.toString();
            dtm.addRow(objs);
            ruleLookup.put(ps.rules.get(i).first, ps.rules.get(i).second);
        }
        commentField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                if (userChangeFlag) {
                    onCommentChange();
                }
            }

            public void removeUpdate(DocumentEvent e) {
                if (userChangeFlag) {
                    onCommentChange();
                }
            }

            public void changedUpdate(DocumentEvent e) {
                if (userChangeFlag) {
                    onCommentChange();
                }
            }
        });
        if (ps.rules.size() > 0) {
            ruleList.getSelectionModel().setSelectionInterval(0, 0);
            ruleEditor.setEnabled(true);
            idEditor.setEnabled(true);
            commentField.setEnabled(true);
            onRuleSelect();
        }

        matchesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getFirstIndex() == -1) {
                    showMatchButton.setEnabled(false);
                } else {
                    showMatchButton.setEnabled(true);
                }
            }
        });
        ruleEditor.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ruleEditorActionPerformed(e);
            }
        });

        ruleList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                ruleIDListValueChanged(arg0);
            }
        });
        ruleList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumn col = ruleList.getColumnModel().getColumn(0);
        col.setMinWidth(40);
        col.setMaxWidth(40);
        col.setResizable(false);

    }
    private boolean dontMatch = false;

    private void initRuleList() {
        ruleList = new MultiLineTable();
        ruleList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Rule"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ruleList.setName("ruleList"); // NOI18N
        jScrollPane1.setViewportView(ruleList);
        ruleList.getColumnModel().getColumn(0).setCellRenderer(new MultiLineCellRenderer());
        ruleList.getColumnModel().getColumn(1).setCellRenderer(new MultiLineCellRenderer());
    }

    void addRule() {
        addButtonActionPerformed(null);
    }

    void ruleIDListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (oldSelectIndex >= 0 && oldSelectIndex != ruleList.getSelectedRow()) {
            try {
                Rule rule = Rule.ruleFromString(ruleEditor.getText(), ruleSet.ruleType);
                rule.comment = commentField.getText();
                ruleSet.rules.get(oldSelectIndex).second = rule;
                ruleSet.rules.get(oldSelectIndex).first = idEditor.getText();
                validateRule(rule);
            } catch (ParseException x) {
                if (JOptionPane.showConfirmDialog(this, "Rule syntax is not correct, discard changes?", "Can't save rule", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    int temp = oldSelectIndex;
                    oldSelectIndex = -1;
                    resetRuleEditor = false;
                    ruleList.getSelectionModel().setSelectionInterval(temp, temp);
                    return;
                }
            } catch (TokenMgrError x) {
                if (JOptionPane.showConfirmDialog(this, "Rule syntax is not correct, discard changes?", "Can't save rule", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    int temp = oldSelectIndex;
                    oldSelectIndex = -1;
                    resetRuleEditor = false;
                    ruleList.getSelectionModel().setSelectionInterval(temp, temp);
                    return;
                }
            }
        }
        if (ruleList.getSelectedRow() >= 0) {
            onRuleSelect();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        matchesTable = new javax.swing.JTable();
        matchesLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        commentField = new javax.swing.JEditorPane();
        idEditor = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        removeButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ruleList = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        showMatchButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        ruleEditor = new srl.gui.AutoCompleteTextField();

        setName("Form"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        matchesTable.setAutoCreateRowSorter(true);
        matchesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Document Name", "Matches"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        matchesTable.setName("matchesTable"); // NOI18N
        matchesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                matchesTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(matchesTable);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(srl.gui.SRLGUIApp.class).getContext().getResourceMap(RuleSetPanel.class);
        matchesLabel.setText(resourceMap.getString("matchesLabel.text")); // NOI18N
        matchesLabel.setName("matchesLabel"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        commentField.setEnabled(false);
        commentField.setMaximumSize(new java.awt.Dimension(120, 120));
        commentField.setName("commentField"); // NOI18N
        jScrollPane3.setViewportView(commentField);

        idEditor.setText(resourceMap.getString("idEditor.text")); // NOI18N
        idEditor.setEnabled(false);
        idEditor.setName("idEditor"); // NOI18N
        idEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idEditorActionPerformed(evt);
            }
        });

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 506));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        removeButton.setIcon(resourceMap.getIcon("removeButton.icon")); // NOI18N
        removeButton.setText(resourceMap.getString("removeButton.text")); // NOI18N
        removeButton.setToolTipText(resourceMap.getString("removeButton.toolTipText")); // NOI18N
        removeButton.setName("removeButton"); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        addButton.setIcon(resourceMap.getIcon("addButton.icon")); // NOI18N
        addButton.setText(resourceMap.getString("addButton.text")); // NOI18N
        addButton.setToolTipText(resourceMap.getString("addButton.toolTipText")); // NOI18N
        addButton.setName("addButton"); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        ruleList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Rule"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ruleList.setName("ruleList"); // NOI18N
        jScrollPane1.setViewportView(ruleList);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeButton))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(removeButton)
                    .add(addButton)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
        );

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(srl.gui.SRLGUIApp.class).getContext().getActionMap(RuleSetPanel.class, this);
        jButton1.setAction(actionMap.get("acceptRule")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        showMatchButton.setAction(actionMap.get("showMatch")); // NOI18N
        showMatchButton.setText(resourceMap.getString("showMatchButton.text")); // NOI18N
        showMatchButton.setName("showMatchButton"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        ruleEditor.setColumns(20);
        ruleEditor.setRows(3);
        ruleEditor.setName("ruleEditor"); // NOI18N
        jScrollPane4.setViewportView(ruleEditor);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(matchesLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 506, Short.MAX_VALUE)
                        .add(showMatchButton))
                    .add(layout.createSequentialGroup()
                        .add(idEditor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1))
                    .add(jScrollPane3, 0, 0, Short.MAX_VALUE)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(idEditor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel4))
                            .add(jButton1)
                            .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(showMatchButton)
                            .add(matchesLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed

        String ruleID = JOptionPane.showInputDialog(this, "Rule ID:", "");
        if (ruleID == null) {
            return;
        }
        if (!isRuleID(ruleID)) {
            JOptionPane.showMessageDialog(this, "Rule ID must start with an uppercase letter and contain only word characters", "Invalid Rule ID",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        addRule(ruleID);
        SRLGUIApp.getApplication().addUndoableEdit(new AddRuleEdit(ruleID, ruleList.getModel().getRowCount() - 1));
    }

    private void addRule(String ruleID) {
        addRule(ruleID, ruleList.getModel().getRowCount());
    }

    private void addRule(String ruleID, int idx) {
        DefaultTableModel dtm = (DefaultTableModel) ruleList.getModel();
        if (ruleLookup.containsKey(ruleID)) {
            JOptionPane.showMessageDialog(this, "ID already exists");
            return;
        }
        dontMatch = true;
        Rule rule = new Rule(ruleSet.ruleType);
        if (ruleSet.ruleType == Rule.TEMPLATE_RULE) {
            rule.addHead("head", "X");
        }
        ruleSet.rules.add(idx, new Pair<String, Rule>((String) ruleID, rule));
        ruleLookup.put(ruleID, rule);
        Object[] objs = {ruleID, rule.toString()};
        dtm.insertRow(idx, objs);
        dontMatch = false;
    }//GEN-LAST:event_addButtonActionPerformed

    void removeRule() {
        removeButtonActionPerformed(null);
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int idx = ruleList.getSelectedRow();
        if (idx < 0) {
            return;
        }
        RemoveRuleEdit rre = new RemoveRuleEdit(ruleSet.rules.get(idx).first, idx,
                ruleSet.rules.get(idx).second.toString(), ruleSet.rules.get(idx).second.comment);
        removeRule(idx);
        SRLGUIApp.getApplication().addUndoableEdit(rre);
    }

    private void removeRule(int idx) {
        userChangeFlag = false;
        if (idx < 0) {
            return;
        }
        ruleLookup.remove(ruleSet.rules.get(idx).first);
        DefaultTableModel dtm = (DefaultTableModel) ruleList.getModel();
        dtm.removeRow(idx);
        ruleSet.rules.remove(idx);
        idEditor.setText("");
        idEditor.setEditable(false);
        ruleEditor.setText("");
        ruleEditor.setEditable(false);
        matchesLabel.setText("Matches");
        DefaultTableModel dtm2 = (DefaultTableModel) matchesTable.getModel();
        dtm2.setRowCount(0);
        userChangeFlag = true;
        oldSelectIndex = -1;
    }//GEN-LAST:event_removeButtonActionPerformed

    public boolean onClose() {
        if (ruleList.getSelectedRow() == -1) {
            return true;
        }
        try {
            Rule rule = Rule.ruleFromString(ruleEditor.getText(), ruleSet.ruleType);
            rule.comment = commentField.getText();
            ruleSet.rules.get(ruleList.getSelectedRow()).second = rule;
            ruleSet.rules.get(ruleList.getSelectedRow()).first = idEditor.getText();
        } catch (ParseException x) {
            if (JOptionPane.showConfirmDialog(this, "Rule syntax is not correct, discard changes?", "Can't save rule", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return false;
            }
        } catch (TokenMgrError e) {
            if (JOptionPane.showConfirmDialog(this, "Rule syntax is not correct, discard changes?", "Can't save rule", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return false;
            }
        }
        return true;
    }
    private boolean resetRuleEditor = true;

    private boolean isRuleID(String s) {
        if (!s.matches("\\w+") ||
                !Character.isUpperCase(s.charAt(0))) {
            return false;
        }
        return true;
    }

private void idEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idEditorActionPerformed
        if (ruleList.getSelectedRow() == -1) {
            return;
        }
        String oldRuleID = ruleSet.rules.get(ruleList.getSelectedRow()).first;
        String ruleID = idEditor.getText();
        if (!isRuleID(ruleID)) {
            JOptionPane.showMessageDialog(this, "Rule ID must start with an uppercase letter and contain only word characters", "Invalid Rule ID",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        changeRuleID(ruleList.getSelectedRow(), ruleID);
        SRLGUIApp.getApplication().addUndoableEdit(new RuleIDChangeEdit(ruleList.getSelectedRow(), oldRuleID, ruleID));
    }

    private void changeRuleID(int idx, String ruleID) {
        ruleSet.rules.get(idx).first = ruleID;
        DefaultTableModel dtm = (DefaultTableModel) ruleList.getModel();
        Object[] objs = {ruleID, ruleEditor.getText()};
        dtm.setValueAt(ruleID, idx, 0);
        dtm.setValueAt(ruleEditor.getText(), idx, 1);
}//GEN-LAST:event_idEditorActionPerformed

private void ruleEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ruleEditorActionPerformed
    if (ruleList.getSelectedRow() == -1) {
        return;
    }
    Rule rule;
    try {
        String oldRule = ruleSet.rules.get(ruleList.getSelectedRow()).second.toString();
        rule = Rule.ruleFromString(ruleEditor.getText(), ruleSet.ruleType);
        ruleSet.rules.get(ruleList.getSelectedRow()).second = rule;
        DefaultTableModel dtm = (DefaultTableModel) ruleList.getModel();
        String ruleID = ruleSet.rules.get(ruleList.getSelectedRow()).first;
        dtm.setValueAt(ruleID,ruleList.getSelectedRow(), 0);
        dtm.setValueAt(rule.toString(), ruleList.getSelectedRow(), 1);
        rule.comment = commentField.getText();
        validateRule(rule);
        SRLGUIApp.getApplication().addUndoableEdit(new RuleChangeEdit(ruleID,
                ruleList.getSelectedRow(), oldRule, ruleEditor.getText(), rule.comment));
        if (matcherThread != null && matcherThread.isAlive()) {
            matchFinder.sig.stop();
        }
        matcherThread = new Thread(matchFinder = new RuleMatchFinder(rule));
        matchesLabel.setText("Matching...");
        if (!rule.body.isEmpty()) {
            matcherThread.start();
        }
    } catch (ParseException x) {
        JOptionPane.showMessageDialog(this, x.getMessage(), "Rule error", JOptionPane.WARNING_MESSAGE);
    } catch (IllegalArgumentException x) {
        JOptionPane.showMessageDialog(this, x.getMessage(), "Rule error", JOptionPane.WARNING_MESSAGE);
    }

}//GEN-LAST:event_ruleEditorActionPerformed

    private void changeRule(String ruleID, int ruleIdx, String rule, String comment) {
        if (ruleIdx == ruleList.getSelectedRow()) { // Changing selected rule
            ruleEditor.setText(rule);
            ruleEditorActionPerformed(null);
            return;
        }

        try {
            Rule newRule = Rule.ruleFromString(rule, ruleSet.ruleType);
            ruleSet.rules.get(ruleIdx).second = newRule;
            DefaultTableModel dtm = (DefaultTableModel) ruleList.getModel();
            dtm.setValueAt(ruleID, ruleIdx, 0);
            dtm.setValueAt(newRule.toString(), ruleIdx, 1);
            newRule.comment = comment;
            validateRule(newRule);
        } catch (ParseException x) {
            JOptionPane.showMessageDialog(this, x.getMessage(), "Rule error", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Action
    public void showMatch() {
        int selectedRow = matchesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        String[] ss = ((String) matchesTable.getValueAt(selectedRow, 0)).split(" ");
        HashMap<Entity, SrlMatchRegion> match = results.get((String) matchesTable.getValueAt(selectedRow, 0) + "###" + (String) matchesTable.getValueAt(selectedRow, 1));
        TreeSet<DocHighlight> highlights = getHighlights(match,
                Integer.parseInt(ss[1]));
        ((SRLGUIView) SRLGUIApp.getApplication().getMainView()).openShowDocPane(
                ss[0], highlights,
                ruleSet.ruleType == Rule.ENTITY_RULE ? ShowDocPanel.TEXT : ShowDocPanel.TAGGED,
                "Match " + idEditor.getText());
    }

private void matchesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_matchesTableMouseClicked
    if (evt.getClickCount() == 2 &&
            evt.getButton() == MouseEvent.BUTTON1) {
        showMatch();
    }

}//GEN-LAST:event_matchesTableMouseClicked
    static final Color[] colors = {Color.BLUE, Color.RED, Color.YELLOW, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.GREEN,
        new Color(0xda70d6), new Color(0x800080), new Color(0x00ffff), new Color(0xfa8072), new Color(0x6495ed),
        new Color(0x008080), new Color(0x00ff7f)};

    private TreeSet<DocHighlight> getHighlights(HashMap<Entity, SrlMatchRegion> match, int sentence) {
        HashMap<Entity, SrlMatchRegion> match2 = new HashMap<Entity, SrlMatchRegion>(match);
        TreeSet<DocHighlight> rv = new TreeSet<DocHighlight>();
        if (match.size() == 0) {
            return rv;
        }
        int begin = match.values().iterator().next().ruleBegin;
        int end = match.values().iterator().next().ruleEnd;
        Color base = new Color(0x808000);
        Vector<String> entityNames = new Vector<String>();
        while (!match2.isEmpty()) {
            Entity e = null;
            SrlMatchRegion r = null;
            int token = Integer.MAX_VALUE;
            for (Map.Entry<Entity, SrlMatchRegion> entry : match2.entrySet()) {
                if (entry.getValue().beginRegion < token) {
                    e = entry.getKey();
                    r = entry.getValue();
                    token = entry.getValue().beginRegion;
                }
            }

            if (begin != token) {
                rv.add(new DocHighlight(sentence, begin, token, base));
            }
            entityNames.add(e.entityType + " " + e.entityValue);
            rv.add(new DocHighlight(sentence, r.beginRegion, r.endRegion,
                    colors[entityNames.indexOf(e.entityType + " " + e.entityValue) % colors.length]));
            begin = r.endRegion;
            match2.remove(e);
        }
        if (begin != end) {
            rv.add(new DocHighlight(sentence, begin, end, base));
        }
        return rv;
    }

    private void validateRule(Rule rule) {
        for (TypeExpr te : rule.body) {
            if (te instanceof Literal) {
                Literal l = (Literal) te;
                TokenStream ts = SRLGUIApp.getApplication().proj.corpus.getProcessor().getTokenStream(l.getVal());
                List<String> tokens = new LinkedList<String>();
                while (true) {
                    try {
                        org.apache.lucene.analysis.Token s = ts.next();
                        if (s == null) {
                            break;
                        }
                        tokens.add(s.termText());
                    } catch (IOException x) {
                        x.printStackTrace();
                        break;
                    }
                }
                if (tokens.size() != 1) {
                    JOptionPane.showMessageDialog(this, "Literal is not single token so will not match: \n\"" + l.getVal() +
                            "\" should be \"" + Strings.join("\" \"", tokens) + "\"", "Invalid literal", JOptionPane.WARNING_MESSAGE);
                }
            } else if (te instanceof Entity) {
                Pair<String, String> ent = new Pair<String, String>(((Entity) te).entityType, ((Entity) te).entityValue);
                if (!SRLGUIApp.getApplication().proj.entities.contains(ent)) {
                    int opt = JOptionPane.showConfirmDialog(this, "Unknown entity type/value: " + ent.first + "/" + ent.second + ". Add to project?",
                            "Unknown entity", JOptionPane.YES_NO_OPTION);
                    if (opt == JOptionPane.YES_OPTION) {
                        SRLGUIApp.getApplication().proj.entities.add(ent);
                    }
                }
            }
        }
    }
    private Thread matcherThread;
    private RuleMatchFinder matchFinder;

    private void onRuleSelect() {
        if (oldSelectIndex == ruleList.getSelectedRow()) {
            return;
        }
        Rule r = ruleSet.rules.get(ruleList.getSelectedRow()).second;
        if (resetRuleEditor) {
            ruleEditor.setText(r.toString());
            idEditor.setText(ruleSet.rules.get(ruleList.getSelectedRow()).first);
        } else {
            resetRuleEditor = true;
        }
        commentField.setText(r.comment);
        ((DefaultTableModel) matchesTable.getModel()).setRowCount(0);
        if (matcherThread != null && matcherThread.isAlive()) {
            matchFinder.sig.stop();
        }
        if (!dontMatch) {
            matcherThread = new Thread(matchFinder = new RuleMatchFinder(r));
            if (!r.body.isEmpty()) {
                matchesLabel.setText("Matching...");
                matcherThread.start();
            }
        }
        oldSelectIndex = ruleList.getSelectedRow();
        ruleEditor.setEnabled(true);
        ruleEditor.setEditable(true);
        idEditor.setEnabled(true);
        idEditor.setEditable(true);
        commentField.setEnabled(true);
        commentField.setEditable(true);
    }

    private void onCommentChange() {
        if (ruleList.getSelectedRow() < 0) {
            return;
        }
        String oldComment = ruleSet.rules.get(ruleList.getSelectedRow()).second.comment;
        ruleSet.rules.get(ruleList.getSelectedRow()).second.comment =
                commentField.getText();
        SRLGUIApp.getApplication().addUndoableEdit(new RuleCommentChangeEdit(ruleSet.rules.get(ruleList.getSelectedRow()).first, ruleList.getSelectedRow(), oldComment, commentField.getText()));
    }
    public HashMap<String, HashMap<Entity, SrlMatchRegion>> results = new HashMap<String, HashMap<Entity, SrlMatchRegion>>();

    private class RuleMatchFinder implements Runnable {

        Rule rule;
        StopSignal sig;

        RuleMatchFinder(Rule r) {
            this.rule = r;
            sig = new StopSignal();
        }

        public void run() {
            final Corpus corpus = SRLGUIApp.getApplication().proj.corpus;
            try {
                showMatchButton.setEnabled(false);
                final List<String> docs = new LinkedList<String>();
                final List<String> vars = new LinkedList<String>();
                sig = new StopSignal();
                results = new HashMap<String, HashMap<Entity, SrlMatchRegion>>();
                corpus.query(rule.getCorpusQuery(), new Corpus.QueryHit() {

                    public void hit(Document d, StopSignal signal) {
                        if (signal.isStopped()) {
                            return;
                        }
                        SrlDocument doc = new SrlDocument(d, corpus.getProcessor(), ruleSet.ruleType == Rule.TEMPLATE_RULE);
                        List<HashMap<Entity, SrlMatchRegion>> results = rule.getMatch(doc, false);
                        //RuleSetPanel.this.results.addAll(results);
                        if (results != null) {
                            for (HashMap<Entity, SrlMatchRegion> result : results) {
                                docs.add(doc.getName());
                                StringBuffer s = new StringBuffer();

                                for (Map.Entry<Entity, SrlMatchRegion> entry : result.entrySet()) {
                                    s.append(entry.getKey().var + "=" + entry.getValue().toString() + "; ");
                                }
                                vars.add(s.substring(0, s.length() > 0 ? s.length() - 2 : 0));
                                RuleSetPanel.this.results.put(doc.getName() + "###" + s.substring(0, s.length() > 0 ? s.length() - 2 : 0),
                                        result);
                            }
                        }
                    }
                }, sig);
                if (sig.isStopped()) {
                    sig.confirmStop();
                    return;
                }
                DefaultTableModel dtm = (DefaultTableModel) matchesTable.getModel();
                dtm.setRowCount(0);
                Iterator<String> varIter = vars.iterator();
                for (String s : docs) {
                    Object[] rowData = new Object[2];
                    rowData[0] = s;
                    rowData[1] = varIter.next();
                    dtm.addRow(rowData);
                }
                matchesLabel.setText("Matches: " + docs.size());

            } catch (IOException x) {
                JOptionPane.showMessageDialog(RuleSetPanel.this, x.getMessage(), "Disk Error", JOptionPane.ERROR_MESSAGE);
            } catch (CorpusConcurrencyException x) {
                JOptionPane.showMessageDialog(RuleSetPanel.this, x.getMessage(), "Concurrency Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Action
    public void acceptRule() {
        ruleEditorActionPerformed(null);
    }

    // Undoable Edits
    private class AddRuleEdit extends SimpleUndoableEdit {

        String ruleName;
        int ruleID;

        public AddRuleEdit(String ruleName, int ruleID) {
            this.ruleName = ruleName;
            this.ruleID = ruleID;
        }

        public String getPresentationName() {
            return "Add rule " + ruleName;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            addRule(ruleName);
        }

        public void undo() throws CannotUndoException {
            undone = true;
            removeRule(ruleID);
        }
    }

    private class RemoveRuleEdit extends SimpleUndoableEdit {

        String ruleName;
        int ruleID;
        String ruleText;
        String comment;

        public RemoveRuleEdit(String ruleName, int ruleID, String ruleText, String comment) {
            this.ruleName = ruleName;
            this.ruleID = ruleID;
            this.ruleText = ruleText;
            this.comment = comment;
        }

        public String getPresentationName() {
            return "Remove Rule " + ruleName;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            removeRule(ruleID);
        }

        public void undo() throws CannotUndoException {
            undone = true;
            addRule(ruleName, ruleID);
            changeRule(ruleName, ruleID, ruleText, comment);
        }
    }

    private class RuleIDChangeEdit extends SimpleUndoableEdit {

        int ruleIdx;
        String oldVal, newVal;

        public RuleIDChangeEdit(int ruleIdx, String oldVal, String newVal) {
            this.ruleIdx = ruleIdx;
            this.oldVal = oldVal;
            this.newVal = newVal;
        }

        public String getPresentationName() {
            return "Rename rule " + oldVal + " to " + newVal;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            if (ruleIdx == ruleList.getSelectedRow()) {
                idEditor.setText(newVal);
            }
            changeRuleID(ruleIdx, newVal);
        }

        public void undo() throws CannotUndoException {
            undone = true;
            if (ruleIdx == ruleList.getSelectedRow()) {
                idEditor.setText(oldVal);
            }
            changeRuleID(ruleIdx, oldVal);
        }
    }

    private class RuleChangeEdit extends SimpleUndoableEdit {

        String ruleID;
        int ruleIdx;
        String newRule;
        String oldRule;
        String comment;

        public RuleChangeEdit(String ruleID, int ruleIdx, String newRule, String oldRule, String comment) {
            this.ruleID = ruleID;
            this.ruleIdx = ruleIdx;
            this.newRule = newRule;
            this.oldRule = oldRule;
            this.comment = comment;
        }

        public String getPresentationName() {
            return "Change to rule " + ruleID;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            if (ruleIdx == ruleList.getSelectedRow()) {
                ruleEditor.setText(newRule);
            }
            changeRule(ruleID, ruleIdx, newRule, comment);
        }

        public void undo() throws CannotUndoException {
            undone = true;
            if (ruleIdx == ruleList.getSelectedRow()) {
                ruleEditor.setText(oldRule);
            }
            changeRule(ruleID, ruleIdx, oldRule, comment);
        }
    }

    private class RuleCommentChangeEdit extends SimpleUndoableEdit {

        String ruleID;
        int ruleIdx;
        String oldComment;
        String newComment;

        public RuleCommentChangeEdit(String ruleID, int ruleIdx, String oldComment, String newComment) {
            this.ruleID = ruleID;
            this.ruleIdx = ruleIdx;
            this.oldComment = oldComment;
            this.newComment = newComment;
        }

        public String getPresentationName() {
            return "Change to rule " + ruleID + "'s comments";
        }

        public void redo() throws CannotRedoException {
            undone = false;
            if (ruleIdx == ruleList.getSelectedRow()) {
                userChangeFlag = false;
                commentField.setText(newComment);
                userChangeFlag = true;
            }
            ruleSet.rules.get(ruleIdx).second.comment = newComment;
        }

        public void undo() throws CannotUndoException {
            undone = true;
            if (ruleIdx == ruleList.getSelectedRow()) {
                userChangeFlag = false;
                commentField.setText(oldComment);
                userChangeFlag = true;
            }
            ruleSet.rules.get(ruleIdx).second.comment = oldComment;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JEditorPane commentField;
    private javax.swing.JTextField idEditor;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel matchesLabel;
    private javax.swing.JTable matchesTable;
    private javax.swing.JButton removeButton;
    private srl.gui.AutoCompleteTextField ruleEditor;
    private javax.swing.JTable ruleList;
    private javax.swing.JButton showMatchButton;
    // End of variables declaration//GEN-END:variables
}
