/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.setup;

import com.common.SelectionObserver;
import com.common.PanelControl;
import com.common.KeyPropagate;
import com.common.Global;
import com.common.Util1;
import com.acc.common.AccountRepo;
import com.inventory.ui.common.InventoryRepo;

import com.acc.model.OpeningBalance;
import com.acc.model.Department;
import com.user.model.Currency;

import com.acc.common.OpeningBalanceTableModel;

import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.COAAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.editor.CurrencyAutoCompleter;
import com.inventory.editor.RegionAutoCompleter;

import com.toedter.calendar.JTextFieldDateEditor;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.List;
import java.util.ArrayList;
//import com.cv.accountswing.util.Util1;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import net.coderazzi.filters.gui.TableFilterHeader;

/**
 *
 * @author myoht
 */
@Slf4j
@Component
public class COAOpeningBalanceSetup extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    private final OpeningBalanceTableModel openingTableModel = new OpeningBalanceTableModel();
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private AccountRepo accountRepo;

    private DepartmentAutoCompleter departmenttAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private COAAutoCompleter coaAutoCompleter;
    private RegionAutoCompleter regionAutoCompleter;
    private TraderAutoCompleter tradeAutoCompleter;
    private SelectionObserver observer;

    private OpeningBalance opBalance;
    private List<OpeningBalance> listOpening = new ArrayList();
    private List<Department> listDept = new ArrayList();
    private List<Currency> listCurrency = new ArrayList();

    private TableFilterHeader filterHeader;

    /**
     * Creates new form COAOpeningBalanceSetup
     */
    //Constructor for opening balance
    public COAOpeningBalanceSetup() {
        initComponents();
        actionMapping();
        initKeyListener();
        initDate();

    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObservaer(SelectionObserver observer) {
        this.observer = observer;
    }

    public void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblOpening.getActionMap().put(solve, new DeleteAction());
    }

    public class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            // deleteTran();
        }
    }

    public void initMain() {
        txtDate.setDate(Util1.getTodayDate());
        initComboBox();
        initTable();
    }

    private void initComboBox() {
//        List<Department> listDepart=accountRepo.getDepartment();
//        System.out.println(listDepart);
        departmenttAutoCompleter = new DepartmentAutoCompleter(txtDept, accountRepo.getDepartment(), null, true, false);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, inventoryRepo.getCurrency(), null, false);
        coaAutoCompleter = new COAAutoCompleter(txtCOA, accountRepo.getChartOfAccount(), null, false);
    }

    private void initDate() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().setName("txtDate");

    }

    private void initKeyListener() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
    }

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            ((JTextFieldDateEditor) e.getSource()).selectAll();
        }
    };

    private void initTable() {
        openingTableModel.setDeptAutoCompleter(departmenttAutoCompleter);
        openingTableModel.setTradeAutoCompleter(tradeAutoCompleter);
        tblOpening.setModel(openingTableModel);
        openingTableModel.setParent(tblOpening);
        openingTableModel.setObserver(this);
        openingTableModel.addNewRow();
        tblOpening.getTableHeader().setFont(Global.tblHeaderFont);
        tblOpening.setCellSelectionEnabled(true);
        tblOpening.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblOpening.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblOpening.getColumnModel().getColumn(2).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(3).setPreferredWidth(250);
        tblOpening.getColumnModel().getColumn(4).setPreferredWidth(5);
        tblOpening.getColumnModel().getColumn(5).setPreferredWidth(10);
        tblOpening.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(7).setPreferredWidth(20);
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOpening.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
//        tblOpening.getColumnModel().getColumn(0).setCellEditor(new COA3 CellEditor(accountRepo),false);
//        tblOpening.getColumnModel().getColumn(1).setCellEditor();
//        tblOpening.getColumnModel().getColumn(3).setCellEditor();
//        tblOpening.getColumnModel().getColumn(4).setCellEditor();
//        tblOpening.getColumnModel().getColumn(5).setCellEditor();
//        tblOpening.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//
//        tblOpening.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());//
        tblOpening.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();
        txtDept = new javax.swing.JTextField();
        txtCurrency = new javax.swing.JTextField();
        txtCOA = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();
        txtSaleDate = new com.toedter.calendar.JDateChooser();
        txtDate = new com.toedter.calendar.JDateChooser();

        jScrollPane2.setViewportView(jEditorPane1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtDept.setFont(Global.textFont);

        txtCurrency.setFont(Global.textFont);

        txtCOA.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("COA");

        jCheckBox2.setText("Customer");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("Supplier");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jButton1.setText("Generate Zero");

        tblOpening.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblOpening.setName("tblOpening"); // NOI18N
        jScrollPane1.setViewportView(tblOpening);

        txtSaleDate.setDateFormatString("dd/MM/yyyy");
        txtSaleDate.setFont(Global.textFont);
        txtSaleDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleDateFocusGained(evt);
            }
        });

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDateFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCOA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox2)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1))
                .addGap(18, 18, 18))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(385, 385, 385)
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(385, 385, 385)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCOA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(jLabel5)
                        .addComponent(jCheckBox3)
                        .addComponent(jCheckBox2)
                        .addComponent(jButton1)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(177, 177, 177)
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(177, 177, 177)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void txtSaleDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleDateFocusGained

    private void txtDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblOpening;
    private javax.swing.JTextField txtCOA;
    private javax.swing.JTextField txtCurrency;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDept;
    private com.toedter.calendar.JDateChooser txtSaleDate;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyEvent(KeyEvent e) {

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
