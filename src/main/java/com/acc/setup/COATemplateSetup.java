/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.setup;

import com.acc.common.AccountRepo;
import com.acc.common.BusinessTypeComboBoxModel;
import com.acc.common.COATemplateGroupTableModel;
import com.acc.common.COATemplateHeadTableModel;
import com.acc.common.COATemplateTableModel;
import com.acc.editor.COA3CellEditor;
import com.acc.model.BusinessType;
import com.acc.model.COATemplate;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class COATemplateSetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private int selectRow = -1;
    private final COATemplateHeadTableModel coaHeadTableModel = new COATemplateHeadTableModel();
    private final COATemplateGroupTableModel coaGroupTableModel = new COATemplateGroupTableModel();
    private final COATemplateTableModel coaTemplateTableModel = new COATemplateTableModel();
    private TableFilterHeader filterHeader;
    private AccountRepo accountRepo;
    private JProgressBar progress;
    private SelectionObserver observer;
    private JComboBox<BusinessType> cboBusType;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public JComboBox<BusinessType> getCboBusType() {
        return cboBusType;
    }

    public void setCboBusType(JComboBox<BusinessType> cboBusType) {
        this.cboBusType = cboBusType;
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    /**
     * Creates new form CoaSetup
     */
    public COATemplateSetup() {
        initComponents();
    }

    public void initMain() {
        initKeyListener();
        initTable();
    }

    private void initTable() {
        tblCOAHead();
        tblCOAGroup();
        tblCOA();
//        searchCOA();
    }

    private void tblCOAHead() {
//        tblCoaHead.setModel(coaHeadTableModel);
//        tblCoaHead.getTableHeader().setFont(Global.tblHeaderFont);
        tblCoaHead.setCellSelectionEnabled(true);
        tblCoaHead.setShowGrid(true);
        tblCoaHead.getTableHeader().setFont(Global.tblHeaderFont);
        tblCoaHead.setModel(coaHeadTableModel);
//        coaGroupTableModel.setParent(tblCoaGroup);
//        coaGroupTableModel.setParetnDesp(lblCoaGroup);
        coaHeadTableModel.setAccountRepo(accountRepo);
        tblCoaHead.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCoaHead.getColumnModel().getColumn(0).setPreferredWidth(10);// Code
        tblCoaHead.getColumnModel().getColumn(1).setPreferredWidth(500);// Name
        tblCoaHead.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblCoaHead.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCoaHead.setDefaultRenderer(Object.class, new TableCellRender());
        tblCoaHead.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblCoaHead.getSelectionModel().addListSelectionListener((e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCoaHead.getSelectedRow() >= 0) {
                    selectRow = tblCoaHead.convertRowIndexToModel(tblCoaHead.getSelectedRow());
                    getCOAGroup(selectRow);
                }
            }
        });
        filterHeader = new TableFilterHeader(tblCoaHead, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
        searchCOA();
    }

    private void searchCOA() {
        coaGroupTableModel.clear();
        coaTemplateTableModel.clear();
        BusinessType type = (BusinessType) cboBusType.getSelectedItem();
        Integer busId = type.getBusId();
        if (busId != null) {
            accountRepo.getCOAChildTemplate("#", busId)
                    .collectList()
                    .subscribe((t) -> {
                        coaHeadTableModel.setList(t);
                        coaHeadTableModel.addEmptyRow();
                        coaHeadTableModel.setBusId(busId);
                        reqCoaHead();
                    }, (e) -> {
                        log.error(e.getMessage());
                    });
        }
    }

    private void tblCOAGroup() {
        tblCoaGroup.setCellSelectionEnabled(true);
        tblCoaGroup.setShowGrid(true);
        tblCoaGroup.getTableHeader().setFont(Global.tblHeaderFont);
        tblCoaGroup.setModel(coaGroupTableModel);
        coaGroupTableModel.setParent(tblCoaGroup);
        coaGroupTableModel.setParetnDesp(lblCoaGroup);
        coaGroupTableModel.setAccountRepo(accountRepo);
        tblCoaGroup.getColumnModel().getColumn(0).setPreferredWidth(1);// no
        tblCoaGroup.getColumnModel().getColumn(1).setPreferredWidth(20);// Sys Code
        tblCoaGroup.getColumnModel().getColumn(2).setPreferredWidth(500);// Name
        tblCoaGroup.getColumnModel().getColumn(3).setPreferredWidth(1);// Active
        tblCoaGroup.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCoaGroup.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCoaGroup.getColumnModel().getColumn(3).setCellEditor(new COA3CellEditor(accountRepo, 1));
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
        filterHeader = new TableFilterHeader(tblCoaGroup, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);

    }

    private void tblCOA() {
        tblCOAGroupChild.setCellSelectionEnabled(true);
        tblCOAGroupChild.setShowGrid(true);
        tblCOAGroupChild.getTableHeader().setFont(Global.tblHeaderFont);
        tblCOAGroupChild.setModel(coaTemplateTableModel);
        coaTemplateTableModel.setParent(tblCOAGroupChild);
        coaTemplateTableModel.setAccountRepo(accountRepo);
        tblCOAGroupChild.getColumnModel().getColumn(0).setPreferredWidth(1);// no
        tblCOAGroupChild.getColumnModel().getColumn(1).setPreferredWidth(20);// Sys Code
        tblCOAGroupChild.getColumnModel().getColumn(2).setPreferredWidth(500);// Name
        tblCOAGroupChild.getColumnModel().getColumn(3).setPreferredWidth(1);// Active
        tblCOAGroupChild.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCOAGroupChild.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCOAGroupChild.getColumnModel().getColumn(3).setCellEditor(new COA3CellEditor(accountRepo, 2));
        tblCOAGroupChild.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCOAGroupChild.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        filterHeader = new TableFilterHeader(tblCOAGroupChild, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
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
            COATemplate coa = coaTemplateTableModel.getChartOfAccount(row);
            if (coa != null) {
                int yn = JOptionPane.showConfirmDialog(this, "Are you sure to delete?", "Delete COA", JOptionPane.WARNING_MESSAGE);
                if (yn == JOptionPane.YES_OPTION) {
                    accountRepo.delete(coa.getKey()).subscribe((t) -> {
                        if (t) {
                            coaTemplateTableModel.delete(row);
                        }
                    });
                }
            }
        }
    }

    private void getCOAGroup(int row) {
        clear();
        COATemplate c = coaHeadTableModel.getCOATemplate(row);
        String coaCode = c.getKey().getCoaCode();
        if (coaCode != null) {
            BusinessType bt = (BusinessType) cboBusType.getSelectedItem();
            if (bt.getBusId() != null) {
                accountRepo.getCOAChildTemplate(coaCode, bt.getBusId())
                        .collectList()
                        .subscribe((t) -> {
                            coaGroupTableModel.setCoaHeadCode(c.getKey().getCoaCode());
                            coaGroupTableModel.setListCOA(t);
                            coaGroupTableModel.addEmptyRow();
                            coaGroupTableModel.setBusId(bt.getBusId());
                            lblCoaGroup.setText(c.getCoaNameEng());
                            reqCoaGroup();
                        });
            }
        }
    }
    
    private void reqCoaHead() {
        int row = tblCoaHead.getRowCount();
        if (row > 0) {
            tblCoaHead.setRowSelectionInterval(row - 1, row - 1);
            tblCoaHead.setColumnSelectionInterval(1, 1);
            tblCoaHead.requestFocus();
        } else {
            tblCoaHead.setRowSelectionInterval(row, row);
            tblCoaHead.setColumnSelectionInterval(1, 1);
            tblCoaHead.requestFocus();
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
        coaTemplateTableModel.clear();
        COATemplate c = coaGroupTableModel.getChartOfAccount(row);
        String coaCode = c.getKey().getCoaCode();
        if (coaCode != null) {
            BusinessType bt = (BusinessType) cboBusType.getSelectedItem();
            if (bt.getBusId() != null) {
                accountRepo.getCOAChildTemplate(coaCode, bt.getBusId())
                        .collectList()
                        .subscribe((t) -> {
                            coaTemplateTableModel.setCoaGroupCode(c.getKey().getCoaCode());
                            coaTemplateTableModel.setListCOA(t);
                            coaTemplateTableModel.addEmptyRow();
                            coaTemplateTableModel.setBusId(bt.getBusId());
                            lblCoaChild.setText(c.getCoaNameEng());
                            reqCOAGroupChild();
                        });
            }

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
        coaTemplateTableModel.clear();
        lblCoaChild.setText("...");
        lblCoaGroup.setText("...");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCoaHead = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCoaGroup = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
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
        jScrollPane1.setViewportView(tblCoaHead);

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
        jScrollPane2.setViewportView(tblCoaGroup);

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
        jScrollPane3.setViewportView(tblCOAGroupChild);

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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
                    .addComponent(jSeparator3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCoaGroup)
                        .addGap(7, 7, 7)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCoaChild, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
        observer.selected("control", this);
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
    }//GEN-LAST:event_tblCoaHeadMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblCoaChild;
    private javax.swing.JLabel lblCoaGroup;
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
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
