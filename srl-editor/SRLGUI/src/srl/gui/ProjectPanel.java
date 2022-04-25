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

import java.util.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument.ElementEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import srl.tools.struct.CollectionChangeEvent;
import srl.tools.struct.CollectionChangeListener;
import srl.tools.struct.Pair;
import srl.project.SrlProject;

/**
 *
 * @author  john
 */
public class ProjectPanel extends javax.swing.JPanel {
    SrlProject proj;

    private String lastProjectName, lastDescription;

    /** Creates new form ProjectPanel */
    public ProjectPanel(SrlProject proj) {
        initComponents();
        this.proj = proj;
        lastProjectName = proj.name.toString();
        lastDescription = proj.description.toString();
        for(Pair<String,String> entity : proj.entities) {
            String[] ss = { entity.first, entity.second };
            ((DefaultTableModel)entityTable.getModel()).addRow(ss);
        }
        proj.entities.addCollectionChangeListener(new CollectionChangeListener<Pair<String, String>>() {

            public void collectionChanged(CollectionChangeEvent<Pair<String, String>> e) {
                userChangeFlag = false;
                if(e.getOldVal() != null) {
                    for(int i = 0; i < entityTable.getRowCount(); i++) {
                        if(entityTable.getValueAt(i, 0).equals(e.getOldVal().first) &&
                                entityTable.getValueAt(i, 1).equals(e.getOldVal().second)) {
                            ((DefaultTableModel)entityTable.getModel()).removeRow(i);
                        }
                    }
                }
                if(e.getNewVal() != null) {
                    Object[] rowData = { e.getNewVal().first, e.getNewVal().second };
                    ((DefaultTableModel)entityTable.getModel()).addRow(rowData);
                }
            }
        });
        projectNameField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {

                SRLGUIApp.getApplication().proj.name.replace(0, SRLGUIApp.getApplication().proj.name.length(), projectNameField.getText());
                if(userChangeFlag) {
                    SRLGUIApp.getApplication().addUndoableEdit(new NameEdit(lastProjectName, projectNameField.getText()));
                    lastProjectName = projectNameField.getText();
                }
                SrlProject proj = SRLGUIApp.getApplication().proj;
            }

            public void removeUpdate(DocumentEvent e) {
                SRLGUIApp.getApplication().proj.name.replace(0, SRLGUIApp.getApplication().proj.name.length(), projectNameField.getText());
                if(userChangeFlag) {
                    SRLGUIApp.getApplication().addUndoableEdit(new NameEdit(lastProjectName, projectNameField.getText()));
                    lastProjectName = projectNameField.getText();
                }
                SrlProject proj = SRLGUIApp.getApplication().proj;
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });
        descriptionField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                SRLGUIApp.getApplication().proj.description.replace(0, SRLGUIApp.getApplication().proj.description.length(), descriptionField.getText());
                if(userChangeFlag) {
                    SRLGUIApp.getApplication().addUndoableEdit(new DescriptionEdit(lastDescription, descriptionField.getText()));
                    lastDescription = descriptionField.getText();
                }
            }

            public void removeUpdate(DocumentEvent e) {
                SRLGUIApp.getApplication().proj.description.replace(0, SRLGUIApp.getApplication().proj.description.length(), descriptionField.getText());
                if(userChangeFlag) {
                    SRLGUIApp.getApplication().addUndoableEdit(new DescriptionEdit(lastDescription, descriptionField.getText()));
                    lastDescription = descriptionField.getText();
                }
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });
        entityTable.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                onTableChange(e);
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        projectNameField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionField = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        entityTable = new javax.swing.JTable();
        removeEntityButton = new javax.swing.JButton();
        addEntityButton = new javax.swing.JButton();

        setName("Main"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(srl.gui.SRLGUIApp.class).getContext().getResourceMap(ProjectPanel.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        projectNameField.setText(SRLGUIApp.getApplication().proj.name.toString());
        projectNameField.setName("projectNameField"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        descriptionField.setColumns(20);
        descriptionField.setRows(5);
        descriptionField.setText(SRLGUIApp.getApplication().proj.description.toString());
        descriptionField.setName("descriptionField"); // NOI18N
        jScrollPane1.setViewportView(descriptionField);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        entityTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Entity Type", "Entity Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        entityTable.setName("entityTable"); // NOI18N
        jScrollPane2.setViewportView(entityTable);

        removeEntityButton.setIcon(resourceMap.getIcon("removeEntityButton.icon")); // NOI18N
        removeEntityButton.setText(resourceMap.getString("removeEntityButton.text")); // NOI18N
        removeEntityButton.setToolTipText(resourceMap.getString("removeEntityButton.toolTipText")); // NOI18N
        removeEntityButton.setName("removeEntityButton"); // NOI18N
        removeEntityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeEntityButtonActionPerformed(evt);
            }
        });

        addEntityButton.setIcon(resourceMap.getIcon("addEntityButton.icon")); // NOI18N
        addEntityButton.setText(resourceMap.getString("addEntityButton.text")); // NOI18N
        addEntityButton.setToolTipText(resourceMap.getString("addEntityButton.toolTipText")); // NOI18N
        addEntityButton.setName("addEntityButton"); // NOI18N
        addEntityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEntityButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                            .add(projectNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(addEntityButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeEntityButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(projectNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(removeEntityButton)
                    .add(addEntityButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private boolean userChangeFlag = true;
    
    private void removeEntityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEntityButtonActionPerformed
        userChangeFlag = false;
        int idx = entityTable.getSelectedRow();
        Pair<String,String> s = new Pair<String, String>((String)entityTable.getValueAt(idx, 0),
            (String)entityTable.getValueAt(idx,1));
        ((DefaultTableModel)entityTable.getModel()).removeRow(idx);
        proj.entities.remove(s);
        String[] oldVal = { s.first, s.second };
        SRLGUIApp.getApplication().addUndoableEdit(new EntityEdit(oldVal, null, idx));
        userChangeFlag = true;
    }//GEN-LAST:event_removeEntityButtonActionPerformed

    private void addEntityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEntityButtonActionPerformed
        userChangeFlag = false;
        DefaultTableModel dlm = (DefaultTableModel)entityTable.getModel();
        if(dlm.getDataVector().contains(""))
            return;
        String[] data = { "", "" };
        dlm.insertRow(dlm.getRowCount(), data);
        proj.entities.add(new Pair<String,String>("",""));
        SRLGUIApp.getApplication().addUndoableEdit(new EntityEdit(null, data, dlm.getRowCount()-1));
        userChangeFlag = true;
    }//GEN-LAST:event_addEntityButtonActionPerformed
    
    
    private void onTableChange(TableModelEvent e) {
        if(!userChangeFlag)
            return;
        int idx = e.getFirstRow();
        proj.entities.remove(idx);
        String[] oldVal = { proj.entities.get(idx).first, proj.entities.get(idx).second };
        proj.entities.add(idx,new Pair<String,String>((String)entityTable.getValueAt(idx, 0),
                (String)entityTable.getValueAt(idx, 1)));
        String[] newVal = { proj.entities.get(idx).first, proj.entities.get(idx).second };
        SRLGUIApp.getApplication().addUndoableEdit(new EntityEdit(oldVal, newVal, idx));
    }

    private class NameEdit extends SimpleUndoableEdit {
        String oldVal;
        String newVal;

        public NameEdit(String oldVal, String newVal) {
            this.oldVal = oldVal;
            this.newVal = newVal;
        }

        public String getPresentationName() {
            return "Change project name to " + newVal;
        }

        public void undo() throws CannotUndoException {
            undone = true;
            userChangeFlag = false;
            SRLGUIApp.getApplication().proj.name.replace(0, SRLGUIApp.getApplication().proj.name.length(), oldVal);
            projectNameField.setText(oldVal);
            userChangeFlag = true;
        }

        public void redo() throws CannotRedoException {
            undone = false;
            userChangeFlag = false;
            SRLGUIApp.getApplication().proj.name.replace(0, SRLGUIApp.getApplication().proj.name.length(), newVal);
            projectNameField.setText(newVal);
            userChangeFlag = true;
        }


    }

    private class DescriptionEdit extends SimpleUndoableEdit {
        String oldVal, newVal;

        public DescriptionEdit(String oldVal, String newVal) {
            this.oldVal = oldVal;
            this.newVal = newVal;
        }

        public String getPresentationName() {
            return "Change project description";
        }

        public void redo() throws CannotRedoException {
            undone = false;
            userChangeFlag = false;
            SRLGUIApp.getApplication().proj.description.replace(0, SRLGUIApp.getApplication().proj.name.length(), newVal);
            descriptionField.setText(newVal);
            userChangeFlag = true;
        }

        public void undo() throws CannotUndoException {
            undone = true;
            userChangeFlag = false;
            SRLGUIApp.getApplication().proj.description.replace(0, SRLGUIApp.getApplication().proj.name.length(), oldVal);
            descriptionField.setText(oldVal);
            userChangeFlag = true;
        }

    }

    private class EntityEdit extends SimpleUndoableEdit {
        String[] oldVal, newVal;
        int idx;

        public EntityEdit(String[] oldVal, String[] newVal, int idx) {
            this.oldVal = oldVal;
            this.newVal = newVal;
            this.idx = idx;
        }

        public String getPresentationName() {
            if(oldVal == null) {
                return "Add entity " + newVal[0] + "/" + newVal[1];
            } else if(newVal == null) {
                return "Remove entity " + oldVal[0] + "/" + newVal[1];
            } else {
                return "Change entity " + oldVal[0] + "/" + newVal[1] + " to " + newVal[0] + "/" + newVal[1];
            }
        }

        public void redo() throws CannotRedoException {
            undone = false;
            userChangeFlag = false;
            DefaultTableModel dlm = (DefaultTableModel)entityTable.getModel();
            if(oldVal != null) {
                proj.entities.remove(idx);
                dlm.removeRow(idx);
            }
            if(newVal != null) {
                proj.entities.add(idx, new Pair<String,String>(newVal[0], newVal[1]));
                dlm.insertRow(idx, newVal);
            }
            userChangeFlag = true;
        }

        public void undo() throws CannotUndoException {
            undone = true;
            userChangeFlag = false;
            DefaultTableModel dlm = (DefaultTableModel)entityTable.getModel();
            if(newVal != null) {
                proj.entities.remove(idx);
                dlm.removeRow(idx);
            }
            if(oldVal != null) {
                proj.entities.add(idx, new Pair<String,String>(oldVal[0], oldVal[1]));
                dlm.insertRow(idx, oldVal);
            }
            userChangeFlag = true;
        }


    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEntityButton;
    private javax.swing.JTextArea descriptionField;
    private javax.swing.JTable entityTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField projectNameField;
    private javax.swing.JButton removeEntityButton;
    // End of variables declaration//GEN-END:variables
    
}
