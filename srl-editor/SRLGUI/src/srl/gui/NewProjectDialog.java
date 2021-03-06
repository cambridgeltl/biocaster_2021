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

import java.io.File;
import java.util.*;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.table.TableModel;
import org.apache.lucene.analysis.Analyzer;
import srl.corpus.Processor;

/**
 *
 * @author  john
 */
public class NewProjectDialog extends javax.swing.JDialog {
 
    public boolean returnVal;
    
    
    /** Creates new form NewProjectDialog */
    public NewProjectDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getRootPane().setDefaultButton(okButton);
        for(ProcessorPlugin pp : Processor.plugins) {
            ((DefaultComboBoxModel)analyzerCombo.getModel()).addElement(pp.getProcessorName());
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        projectPathField = new javax.swing.JTextField();
        analyzerCombo = new javax.swing.JComboBox();
        projectPathBrowseButton = new javax.swing.JButton();
        analyzerBrowseButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(srl.gui.SRLGUIApp.class).getContext().getResourceMap(NewProjectDialog.class);
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        projectPathField.setText(resourceMap.getString("projectPathField.text")); // NOI18N
        projectPathField.setName("projectPathField"); // NOI18N

        analyzerCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "English", "Brazilian", "Chinese", "CJK", "Czech", "Dutch", "French", "German", "Greek", "Russian", "Thai", "Japanese", "Pre-tokenized" }));
        analyzerCombo.setName("analyzerCombo"); // NOI18N

        projectPathBrowseButton.setIcon(resourceMap.getIcon("projectPathBrowseButton.icon")); // NOI18N
        projectPathBrowseButton.setText(resourceMap.getString("projectPathBrowseButton.text")); // NOI18N
        projectPathBrowseButton.setName("projectPathBrowseButton"); // NOI18N
        projectPathBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectPathBrowseButtonActionPerformed(evt);
            }
        });

        analyzerBrowseButton.setIcon(resourceMap.getIcon("analyzerBrowseButton.icon")); // NOI18N
        analyzerBrowseButton.setText(resourceMap.getString("analyzerBrowseButton.text")); // NOI18N
        analyzerBrowseButton.setName("analyzerBrowseButton"); // NOI18N

        cancelButton.setText(resourceMap.getString("cancelButton.text")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText(resourceMap.getString("okButton.text")); // NOI18N
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(okButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(analyzerCombo, 0, 231, Short.MAX_VALUE)
                            .add(projectPathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(projectPathBrowseButton)
                            .add(analyzerBrowseButton))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectPathBrowseButton)
                    .add(jLabel1)
                    .add(projectPathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(analyzerBrowseButton)
                    .add(jLabel2)
                    .add(analyzerCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(okButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        returnVal = true;
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        returnVal = false;
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    
    
    private void projectPathBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectPathBrowseButtonActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File f = new File(projectPathField.getText());
        if(f.exists()) {
            if(f.isDirectory())
                jfc.setSelectedFile(f);
            else
                jfc.setSelectedFile(f.getParentFile());
        }
        if(jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            projectPathField.setText(jfc.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_projectPathBrowseButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewProjectDialog dialog = new NewProjectDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    public ComboBoxModel getAnalyzerModel() {
        return new DefaultComboBoxModel(Processor.langs);
    }
    
    public void setDefaultFile(String s) {
        projectPathField.setText(s);
    }
    
    public File getPath() {
        return new File(projectPathField.getText());
    }
    
    public Processor getProcessor()  {
        return Processor.getProcessor(analyzerCombo.getSelectedItem().toString());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton analyzerBrowseButton;
    private javax.swing.JComboBox analyzerCombo;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton okButton;
    private javax.swing.JButton projectPathBrowseButton;
    private javax.swing.JTextField projectPathField;
    // End of variables declaration//GEN-END:variables
    
}
