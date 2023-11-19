/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup;

import com.repo.AccountRepo;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.inventory.model.MessageType;
import com.inventory.model.OutputCost;
import com.inventory.model.OutputCostKey;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.common.OutputCostTabelModel;
import com.repo.UserRepo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Slf4j
@Component
public class OutputCostSetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private int selectRow = -1;
    private OutputCost outputCost = new OutputCost();
    private final OutputCostTabelModel outputCostTableModel = new OutputCostTabelModel();
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;
    private SelectionObserver observer;
    private JProgressBar progress;
    private TableRowSorter<TableModel> sorter;

    enum Header {
        UserCode,
        Name,
        Address,
        PhoneNo,
        Email,
        ContactPerson,
        Region,
        Remark,
        Nrc,
        Group,
        Department
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form CustomerSetup
     */
    public OutputCostSetup() {
        initComponents();
        initKeyListener();
    }

    public void initMain() {
        initCombo();
        initTable();
        searchOutputCost();
    }

    private void initCombo() {
    }

    private void initTable() {
        tblOutptCost.setModel(outputCostTableModel);
        tblOutptCost.getTableHeader().setFont(Global.textFont);
        tblOutptCost.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOutptCost.getColumnModel().getColumn(0).setPreferredWidth(5);// Code
        tblOutptCost.getColumnModel().getColumn(1).setPreferredWidth(80);// Code
        tblOutptCost.getColumnModel().getColumn(2).setPreferredWidth(320);// Name
        tblOutptCost.getColumnModel().getColumn(3).setPreferredWidth(40);// Active   
        tblOutptCost.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblOutptCost.setDefaultRenderer(Object.class, new TableCellRender());
        tblOutptCost.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblOutptCost.getSelectedRow() >= 0) {
                    selectRow = tblOutptCost.convertRowIndexToModel(tblOutptCost.getSelectedRow());
                    setOutputCost(outputCostTableModel.getCutputCost(selectRow));
                }

            }
        });
        sorter = new TableRowSorter(tblOutptCost.getModel());
        tblOutptCost.setRowSorter(sorter);
        sorter.toggleSortOrder(1);
    }

    private void searchOutputCost() {
        progress.setIndeterminate(true);
        inventoryRepo.getOutputCost()
                .subscribe((t) -> {
                    outputCostTableModel.setListOutputCost(t);
                    lblRecord.setText(String.valueOf(t.size()));
                    progress.setIndeterminate(false);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    progress.setIndeterminate(false);
                });

    }

    private void setOutputCost(OutputCost optCost) {
        outputCost = optCost;
        txtSysCode.setText(outputCost.getKey().getOutputCostCode());
        txtCusCode.setText(outputCost.getUserCode());
        chkActive.setSelected(outputCost.isActive());
        lblStatus.setText("EDIT");
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtOutputName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Trader Name can't be empty");
            txtOutputName.requestFocus();
            status = false;
        } else {
            outputCost.setUserCode(txtCusCode.getText());
            outputCost.setActive(chkActive.isSelected());
            if (lblStatus.getText().equals("NEW")) {
//                outputCost.setMacId(Global.macId);
                outputCost.setCreatedBy(Global.loginUser.getUserCode());
                outputCost.setCreatedDate(LocalDateTime.now());
//                outputCost.setDeptId(Global.deptId);
                OutputCostKey key = new OutputCostKey();
                key.setCompCode(Global.compCode);
                key.setOutputCostCode(null);
                outputCost.setKey(key);
            } else {
                outputCost.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void saveOutputCost() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
            observer.selected("save", false);
            inventoryRepo.saveOutputCost(outputCost).doOnSuccess((t) -> {
                if (lblStatus.getText().equals("EDIT")) {
                    outputCostTableModel.setOutputCost(selectRow, outputCost);
                } else {
                    outputCostTableModel.addOutputCost(outputCost);
                }
                clear();
                sendMessage(t.getName());
            }).doOnError((e) -> {
                observer.selected("save", true);
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            }).subscribe();

        }
    }

    private void sendMessage(String mes) { // later
        inventoryRepo.sendDownloadMessage(MessageType.TRADER_INV, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
        accountRepo.sendDownloadMessage(MessageType.TRADER_ACC, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    public void clear() {
        observer.selected("save", true);
        progress.setIndeterminate(false);
        outputCost = new OutputCost();
        txtSysCode.setText(null);
        txtCusCode.setText(null);
        chkActive.setSelected(true);
        lblStatus.setText("NEW");
        outputCostTableModel.refresh();
        txtCusCode.requestFocus();
        lblRecord.setText(String.valueOf(outputCostTableModel.getListOutputCost().size()));
    }
    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String tmp1 = entry.getStringValue(0).toUpperCase().replace(" ", "");
            String tmp2 = entry.getStringValue(1).toUpperCase().replace(" ", "");
            String tmp3 = entry.getStringValue(2).toUpperCase().replace(" ", "");
            String tmp4 = entry.getStringValue(3).toUpperCase().replace(" ", "");
            String text = txtFilter.getText().toUpperCase().replace(" ", "");
            return tmp1.startsWith(text) || tmp2.startsWith(text) || tmp3.startsWith(text) || tmp4.startsWith(text);
        }
    };

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
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

        panelEntry = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCusCode = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtSysCode = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        txtOutputPrice = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtOutputName = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblOutptCost = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelEntry.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("User Code");

        txtCusCode.setFont(Global.textFont);
        txtCusCode.setName("txtCusCode"); // NOI18N
        txtCusCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCusCodeKeyReleased(evt);
            }
        });

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Sys Code");

        txtSysCode.setEditable(false);
        txtSysCode.setFont(Global.textFont);
        txtSysCode.setName("txtCusCode"); // NOI18N
        txtSysCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSysCodeKeyReleased(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Price");

        txtOutputPrice.setFont(Global.textFont);
        txtOutputPrice.setName("txtCusCode"); // NOI18N
        txtOutputPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOutputPriceKeyReleased(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Name");

        txtOutputName.setFont(Global.textFont);
        txtOutputName.setName("txtCusCode"); // NOI18N
        txtOutputName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOutputNameKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelEntryLayout = new javax.swing.GroupLayout(panelEntry);
        panelEntry.setLayout(panelEntryLayout);
        panelEntryLayout.setHorizontalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStatus)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(chkActive))
                            .addGroup(panelEntryLayout.createSequentialGroup()
                                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSysCode, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                                    .addComponent(txtOutputName, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCusCode, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                                    .addComponent(txtOutputPrice))))))
                .addContainerGap())
        );
        panelEntryLayout.setVerticalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtSysCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtOutputName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtOutputPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkActive)
                    .addComponent(lblStatus))
                .addGap(107, 107, 107))
        );

        panelEntryLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel10});

        tblOutptCost.setAutoCreateRowSorter(true);
        tblOutptCost.setFont(Global.textFont);
        tblOutptCost.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOutptCost.setName("tblOutptCost"); // NOI18N
        tblOutptCost.setRowHeight(Global.tblRowHeight);
        tblOutptCost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblOutptCostKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblOutptCost);

        jLabel6.setText("Record :");

        lblRecord.setText("0");

        txtFilter.setFont(Global.textFont);
        txtFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFilterFocusGained(evt);
            }
        });
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE))
                    .addComponent(txtFilter)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1186, Short.MAX_VALUE))
                    .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblRecord))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblOutptCostKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblOutptCostKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblOutptCostKeyReleased

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        String filter = txtFilter.getText();
        if (filter.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtFilterFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFilterFocusGained
        // TODO add your handling code here:
        txtFilter.selectAll();
    }//GEN-LAST:event_txtFilterFocusGained

    private void txtOutputNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOutputNameKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOutputNameKeyReleased

    private void txtOutputPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOutputPriceKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOutputPriceKeyReleased

    private void txtSysCodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSysCodeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSysCodeKeyReleased

    private void txtCusCodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCusCodeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusCodeKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelEntry;
    private javax.swing.JTable tblOutptCost;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtOutputName;
    private javax.swing.JTextField txtOutputPrice;
    private javax.swing.JTextField txtSysCode;
    // End of variables declaration//GEN-END:variables

    private void initKeyListener() {
        txtCusCode.addKeyListener(this);
        chkActive.addKeyListener(this);
        tblOutptCost.addKeyListener(this);

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JComboBox) {
            ctrlName = ((JComboBox) sourceObj).getName();
        } else if (sourceObj instanceof JFormattedTextField) {
            ctrlName = ((JFormattedTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTextComponent) {
            ctrlName = ((JTextComponent) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtOutputName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtOutputName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                break;
            case "txtOutputPrice":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtOutputPrice.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {

                }
                tabToTable(e);
                break;
            default:
                break;
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblOutptCost.requestFocus();
            if (tblOutptCost.getRowCount() >= 0) {
                tblOutptCost.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void save() {
        saveOutputCost();
    }

    @Override
    public void delete() {
        if (selectRow >= 0) {
            OutputCost t = outputCostTableModel.getCutputCost(selectRow);
            inventoryRepo.deleteOutputCost(t.getKey()).subscribe((t1) -> {
                if (t1 == 1) {
                    outputCostTableModel.deleteOutputCost(selectRow);
                    clear();
                    JOptionPane.showMessageDialog(this, "Deleted.");
                }
            });

        }
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        searchOutputCost();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    private void printFile() {
        try {
            progress.setIndeterminate(true);
            String CSV_FILE_PATH = "customers.csv";
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(Header.class)
                    .setSkipHeaderRecord(false)
                    .build();
            FileWriter out = new FileWriter(CSV_FILE_PATH);
            try (CSVPrinter printer = csvFormat.print(out)) {
                printer.flush();
            }
            progress.setIndeterminate(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

}
