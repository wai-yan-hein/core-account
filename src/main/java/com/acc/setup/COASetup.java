/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.setup;

import com.repo.AccountRepo;
import com.acc.common.COAGroupChildTableModel;
import com.acc.common.COAGroupTableModel;
import com.acc.common.COAHeadTableModel;
import com.acc.dialog.FindDialog;
import com.acc.editor.COA3CellEditor;
import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.PanelControl;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Lenovo
 */
public class COASetup extends javax.swing.JPanel implements KeyListener, PanelControl {
    
    private int selectRow = -1;
    private final COAHeadTableModel coaHeadTableModel = new COAHeadTableModel();
    private final COAGroupTableModel coaGroupTableModel = new COAGroupTableModel();
    private final COAGroupChildTableModel cOAGroupChildTableModel = new COAGroupChildTableModel();
    private AccountRepo accountRepo;
    private JProgressBar progress;
    private SelectionObserver observer;
    private FindDialog findDialog;
    
    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }
    
    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }
    
    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form CoaSetup
     */
    public COASetup() {
        initComponents();
    }
    
    public void initMain() {
        batchLock(!Global.batchLock);
        initKeyListener();
        initTable();
        initFind();
    }
    
    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblCoaHead, tblCoaGroup, tblCOAGroupChild);
    }
    
    private void initTable() {
        tblCOAHead();
        tblCOAGroup();
        tblCOA();
        initRowHeaderGroup();
        initRowHeaderChild();
    }
    
    private void initRowHeaderGroup() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblCoaGroup, 30);
        s2.setRowHeaderView(list);
    }
    
    private void initRowHeaderChild() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblCOAGroupChild, 30);
        s3.setRowHeaderView(list);
    }
    
    private void batchLock(boolean lock) {
        coaGroupTableModel.setEdit(lock);
        cOAGroupChildTableModel.setEdit(lock);
        observer.selected("save", lock);
        observer.selected("delete", lock);
    }
    
    private void tblCOAHead() {
        tblCoaHead.setModel(coaHeadTableModel);
        tblCoaHead.getTableHeader().setFont(Global.tblHeaderFont);
        tblCoaHead.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCoaHead.getColumnModel().getColumn(0).setPreferredWidth(20);// Code
        tblCoaHead.getColumnModel().getColumn(1).setPreferredWidth(500);// Name
        tblCoaHead.setDefaultRenderer(Object.class, new TableCellRender());
        tblCoaHead.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        searchHead();
    }
    
    private void searchHead() {
        progress.setIndeterminate(true);
        accountRepo.getCOAChild("#").collectList().subscribe((t) -> {
            coaHeadTableModel.setListCoaHead(t);
            progress.setIndeterminate(false);
            tblCoaHead.requestFocus();
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
    }
    
    private void tblCOAGroup() {
        tblCoaGroup.setCellSelectionEnabled(true);
        tblCoaGroup.setShowGrid(true);
        tblCoaGroup.getTableHeader().setFont(Global.tblHeaderFont);
        tblCoaGroup.setModel(coaGroupTableModel);
        coaGroupTableModel.setProgress(progress);
        coaGroupTableModel.setParent(tblCoaGroup);
        coaGroupTableModel.setParetnDesp(lblCoaGroup);
        coaGroupTableModel.setAccountRepo(accountRepo);
        tblCoaGroup.getColumnModel().getColumn(0).setPreferredWidth(20);// Sys Code
        tblCoaGroup.getColumnModel().getColumn(1).setPreferredWidth(20);// Usr Code
        tblCoaGroup.getColumnModel().getColumn(2).setPreferredWidth(500);// Name
        tblCoaGroup.getColumnModel().getColumn(3).setPreferredWidth(1);// Active
        tblCoaGroup.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCoaGroup.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCoaGroup.getColumnModel().getColumn(4).setCellEditor(new COA3CellEditor(accountRepo, 1));
        tblCoaGroup.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCoaGroup.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblCoaGroup.getSelectionModel().addListSelectionListener((e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCoaGroup.getSelectedRow() >= 0) {
                    selectRow = tblCoaGroup.convertRowIndexToModel(tblCoaGroup.getSelectedRow());
                    getCOAGroupChild(selectRow);
                }
            }
        });
        
    }
    
    private void tblCOA() {
        tblCOAGroupChild.setCellSelectionEnabled(true);
        tblCOAGroupChild.setShowGrid(true);
        tblCOAGroupChild.getTableHeader().setFont(Global.tblHeaderFont);
        tblCOAGroupChild.setModel(cOAGroupChildTableModel);
        cOAGroupChildTableModel.setParent(tblCOAGroupChild);
        cOAGroupChildTableModel.setAccountRepo(accountRepo);
        cOAGroupChildTableModel.setProgress(progress);
        tblCOAGroupChild.getColumnModel().getColumn(0).setPreferredWidth(20);// Sys Code
        tblCOAGroupChild.getColumnModel().getColumn(1).setPreferredWidth(20);// Usr Code
        tblCOAGroupChild.getColumnModel().getColumn(2).setPreferredWidth(500);// Name
        tblCOAGroupChild.getColumnModel().getColumn(3).setPreferredWidth(1);// Active
        tblCOAGroupChild.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCOAGroupChild.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCOAGroupChild.getColumnModel().getColumn(4).setCellEditor(new COA3CellEditor(accountRepo, 2));
        tblCOAGroupChild.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCOAGroupChild.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblCOAGroupChild.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblCOAGroupChild.getActionMap().put(solve, new DeleteAction());
    }
    
    private class DeleteAction extends AbstractAction {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteLv3();
        }
    }
    
    private void deleteLv3() {
        int row = tblCOAGroupChild.convertRowIndexToModel(tblCOAGroupChild.getSelectedRow());
        if (row >= 0) {
            ChartOfAccount coa = cOAGroupChildTableModel.getChartOfAccount(row);
            if (coa != null) {
                int yn = JOptionPane.showConfirmDialog(this, "Are you sure to delete?", "Delete COA", JOptionPane.WARNING_MESSAGE);
                if (yn == JOptionPane.YES_OPTION) {
                    accountRepo.delete(coa.getKey()).subscribe((t) -> {
                        if (t) {
                            cOAGroupChildTableModel.delete(row);
                        }
                    }, (e) -> {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    });
                }
            }
        }
    }
    
    private void getCOAGroup(int row) {
        clear();
        ChartOfAccount c = coaHeadTableModel.getChartOfAccount(row);
        String coaCode = c.getKey().getCoaCode();
        if (coaCode != null) {
            progress.setIndeterminate(true);
            accountRepo.getCOAChild(coaCode).collectList().subscribe((t) -> {
                coaGroupTableModel.setCoaHeadCode(c.getKey().getCoaCode());
                coaGroupTableModel.setListCOA(t);
                coaGroupTableModel.addEmptyRow();
                lblCoaGroup.setText(c.getCoaNameEng());
                reqCoaGroup();
                progress.setIndeterminate(false);
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });
        } else {
            coaGroupTableModel.clear();
            coaGroupTableModel.addEmptyRow();
        }
    }
    
    private void reqCoaGroup() {
        int row = tblCoaGroup.getRowCount();
        if (row > 0) {
            tblCoaGroup.setRowSelectionInterval(row - 1, row - 1);
            tblCoaGroup.setColumnSelectionInterval(1, 1);
            tblCoaGroup.requestFocus();
        } else {
            tblCoaGroup.setRowSelectionInterval(row, row);
            tblCoaGroup.setColumnSelectionInterval(1, 1);
            tblCoaGroup.requestFocus();
        }
    }
    
    private void getCOAGroupChild(int row) {
        cOAGroupChildTableModel.clear();
        ChartOfAccount c = coaGroupTableModel.getChartOfAccount(row);
        String coaCode = c.getKey().getCoaCode();
        if (coaCode != null) {
            progress.setIndeterminate(true);
            accountRepo.getCOAChild(coaCode).collectList().subscribe((t) -> {
                cOAGroupChildTableModel.setCoaGroupCode(c.getKey().getCoaCode());
                cOAGroupChildTableModel.setListCOA(t);
                cOAGroupChildTableModel.addEmptyRow();
                lblCoaChild.setText(c.getCoaNameEng());
                reqCOAGroupChild();
                progress.setIndeterminate(false);
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });
        } else {
            cOAGroupChildTableModel.clear();
            cOAGroupChildTableModel.addEmptyRow();
        }
    }
    
    private void reqCOAGroupChild() {
        int row = tblCOAGroupChild.getRowCount();
        if (row > 0) {
            tblCOAGroupChild.setRowSelectionInterval(row - 1, row - 1);
            tblCOAGroupChild.setColumnSelectionInterval(1, 1);
            tblCOAGroupChild.requestFocus();
        } else {
            tblCOAGroupChild.setRowSelectionInterval(0, 0);
            tblCOAGroupChild.setColumnSelectionInterval(1, 1);
            tblCOAGroupChild.requestFocus();
        }
    }
    
    private void clear() {
        coaGroupTableModel.clear();
        cOAGroupChildTableModel.clear();
        lblCoaChild.setText("...");
        lblCoaGroup.setText("...");
    }
    
    private void setCOAHead() {
        selectRow = tblCoaHead.convertRowIndexToModel(tblCoaHead.getSelectedRow());
        if (selectRow >= 0) {
            getCOAGroup(selectRow);
        }
    }
    
    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
        observer.selected("print", false);
        observer.selected("history", false);
        observer.selected("delete", true);
        observer.selected("refresh", true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        s1 = new javax.swing.JScrollPane();
        tblCoaHead = new javax.swing.JTable();
        s2 = new javax.swing.JScrollPane();
        tblCoaGroup = new javax.swing.JTable();
        s3 = new javax.swing.JScrollPane();
        tblCOAGroupChild = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        lblCoaChild = new javax.swing.JLabel();
        lblCoaGroup = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCoaHead.setFont(Global.textFont);
        tblCoaHead.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblCoaHead.setName("tblCoaHead"); // NOI18N
        tblCoaHead.setRowHeight(Global.tblRowHeight);
        tblCoaHead.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCoaHeadMouseClicked(evt);
            }
        });
        tblCoaHead.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCoaHeadKeyReleased(evt);
            }
        });
        s1.setViewportView(tblCoaHead);

        tblCoaGroup.setFont(Global.textFont);
        tblCoaGroup.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblCoaGroup.setName("tblCoaGroup"); // NOI18N
        tblCoaGroup.setRowHeight(Global.tblRowHeight);
        tblCoaGroup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCoaGroupMouseClicked(evt);
            }
        });
        tblCoaGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblCoaGroupKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCoaGroupKeyReleased(evt);
            }
        });
        s2.setViewportView(tblCoaGroup);

        tblCOAGroupChild.setFont(Global.textFont);
        tblCOAGroupChild.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblCOAGroupChild.setName("tblCOAGroupChild"); // NOI18N
        tblCOAGroupChild.setRowHeight(Global.tblRowHeight);
        tblCOAGroupChild.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCOAGroupChildKeyReleased(evt);
            }
        });
        s3.setViewportView(tblCOAGroupChild);

        jLabel1.setFont(Global.menuFont);
        jLabel1.setText("Account Head");

        lblCoaChild.setFont(Global.menuFont);
        lblCoaChild.setText("...");

        lblCoaGroup.setFont(Global.menuFont);
        lblCoaGroup.setText("...");

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(s1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                    .addComponent(jSeparator4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(s3, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                    .addComponent(s2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblCoaGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCoaChild, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblCoaGroup))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(s2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCoaChild, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(s3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(s1, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)))))
                .addGap(10, 10, 10))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, lblCoaChild, lblCoaGroup});

    }// </editor-fold>//GEN-END:initComponents

    private void tblCoaGroupKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCoaGroupKeyPressed

    }//GEN-LAST:event_tblCoaGroupKeyPressed

    private void tblCoaHeadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCoaHeadKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblCoaHeadKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblCOAGroupChildKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCOAGroupChildKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblCOAGroupChildKeyReleased

    private void tblCoaGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCoaGroupKeyReleased
        // TODO add your handling code here:??
    }//GEN-LAST:event_tblCoaGroupKeyReleased

    private void tblCoaGroupMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCoaGroupMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblCoaGroupMouseClicked

    private void tblCoaHeadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCoaHeadMouseClicked
        // TODO add your handling code here:
        setCOAHead();
    }//GEN-LAST:event_tblCoaHeadMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblCoaChild;
    private javax.swing.JLabel lblCoaGroup;
    private javax.swing.JScrollPane s1;
    private javax.swing.JScrollPane s2;
    private javax.swing.JScrollPane s3;
    private javax.swing.JTable tblCOAGroupChild;
    private javax.swing.JTable tblCoaGroup;
    private javax.swing.JTable tblCoaHead;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
        
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    
    private void initKeyListener() {
        tblCoaHead.addKeyListener(this);
        tblCoaGroup.addKeyListener(this);
        tblCOAGroupChild.addKeyListener(this);
    }
    
    @Override
    public void save() {
    }
    
    @Override
    public void delete() {
    }
    
    @Override
    public void newForm() {
    }
    
    @Override
    public void history() {
    }
    
    @Override
    public void print() {
    }
    
    @Override
    public void refresh() {
        searchHead();
    }
    
    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }
    
    @Override
    public String panelName() {
        return this.getName();
    }
}
