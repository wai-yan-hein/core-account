/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.acc.common.COAComboBoxModel;
import com.acc.common.DepartmentAccComboBoxModel;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.common.Global;
import com.common.Util1;
import com.inventory.model.PurHis;
import com.inventory.model.SaleHis;
import com.repo.AccountRepo;
import com.repo.InventoryRepo;
import javax.swing.JFrame;

/**
 *
 * @author Lenovo
 */
public class AccountOptionDialog extends javax.swing.JDialog {

    private AccountRepo accountRepo;
    private InventoryRepo inventoryRepo;
    private final DepartmentAccComboBoxModel departmentComboBoxModel = new DepartmentAccComboBoxModel();
    private final COAComboBoxModel cashComboModel = new COAComboBoxModel();
    private final COAComboBoxModel purchaseComboModel = new COAComboBoxModel();
    private final COAComboBoxModel payableComboModel = new COAComboBoxModel();
    

    private Object object;

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    /**
     * Creates new form PurchaseMoreDialog
     *
     * @param frame
     */
    public AccountOptionDialog(JFrame frame) {
        super(frame, false);
        initComponents();
    }

    public void initMain() {
        initCompleter();
    }

    private void initCompleter() {
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentComboBoxModel.setData(t);
            cboDepartment.setModel(departmentComboBoxModel);
        }).subscribe();
        accountRepo.getCashBank().doOnSuccess((t) -> {
            cashComboModel.setData(t);
            cboCash.setModel(cashComboModel);
        }).subscribe();
        accountRepo.getPurchaseAcc().doOnSuccess((t) -> {
            purchaseComboModel.setData(t);
            cboPurchase.setModel(purchaseComboModel);
        }).subscribe();
        accountRepo.getPayableAcc().doOnSuccess((t) -> {
            payableComboModel.setData(t);
            cboPayable.setModel(payableComboModel);
        }).subscribe();
    }

    public void setObject(Object obj) {
        if (obj instanceof PurHis p) {
            setDepartment(p.getDeptCode());
            setCash(p.getCashAcc());
            setPurchase(p.getPurchaseAcc());
            setPayable(p.getPayableAcc());
        }
        this.object = obj;
    }

    private void setPayable(String payableAcc) {
        if (!Util1.isNullOrEmpty(payableAcc)) {
            accountRepo.findCOA(payableAcc).doOnSuccess((t) -> {
                payableComboModel.setSelectedItem(t);
            }).subscribe();
        }
    }

    private String getPayable() {
        if (cboPayable.getSelectedItem() instanceof ChartOfAccount coa) {
            if (coa.getKey() != null) {
                return coa.getKey().getCoaCode();
            }
        }
        return null;
    }

    private void setPurchase(String purAcc) {
        if (!Util1.isNullOrEmpty(purAcc)) {
            accountRepo.findCOA(purAcc).doOnSuccess((t) -> {
                purchaseComboModel.setSelectedItem(t);
            }).subscribe();
        }
    }

    private String getPurchase() {
        if (cboPurchase.getSelectedItem() instanceof ChartOfAccount coa) {
            if (coa.getKey() != null) {
                return coa.getKey().getCoaCode();
            }
        }
        return null;
    }

    private void setCash(String cashAcc) {
        if (!Util1.isNullOrEmpty(cashAcc)) {
            accountRepo.findCOA(cashAcc).doOnSuccess((t) -> {
                cashComboModel.setSelectedItem(t);
            }).subscribe();
        }
    }

    private String getCash() {
        if (cboCash.getSelectedItem() instanceof ChartOfAccount coa) {
            if (coa.getKey() != null) {
                return coa.getKey().getCoaCode();
            }
        }
        return null;
    }

    private void setDepartment(String deptCode) {
        if (!Util1.isNullOrEmpty(deptCode)) {
            accountRepo.findDepartment(deptCode).doOnSuccess((t) -> {
                departmentComboBoxModel.setSelectedItem(t);
            }).subscribe();
        }
    }

    private String getDepartment() {
        if (cboDepartment.getSelectedItem() instanceof DepartmentA dep) {
            if (dep.getKey() != null) {
                return dep.getKey().getDeptCode();
            }
        }
        return null;
    }

    private void confirm() {
        if (object instanceof PurHis p) {
            p.setDeptCode(getDepartment());
            p.setCashAcc(getCash());
            p.setPayableAcc(getPayable());
            p.setPurchaseAcc(getPurchase());
        } else if (object instanceof SaleHis s) {
            s.setDeptCode(getDepartment());
            s.setCashAcc(getCash());
            s.setReceivableAcc(getPayable());
            s.setSaleAcc(getPurchase());
        }
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cboDepartment = new javax.swing.JComboBox<>();
        cboCash = new javax.swing.JComboBox<>();
        cboPayable = new javax.swing.JComboBox<>();
        cboPurchase = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Purchase More Dialog");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Payable ");

        jButton1.setBackground(Global.selectionColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Purchase ");

        jLabel3.setText("Cash / Bank");

        jLabel4.setText("Department");

        cboDepartment.setFont(Global.textFont);

        cboCash.setFont(Global.textFont);

        cboPayable.setFont(Global.textFont);

        cboPurchase.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboPayable, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cboDepartment, 0, 287, Short.MAX_VALUE)
                                .addComponent(cboCash, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(cboPurchase, javax.swing.GroupLayout.Alignment.TRAILING, 0, 287, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboPurchase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboPayable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 149, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        confirm();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<ChartOfAccount> cboCash;
    private javax.swing.JComboBox<DepartmentA> cboDepartment;
    private javax.swing.JComboBox<ChartOfAccount> cboPayable;
    private javax.swing.JComboBox<ChartOfAccount> cboPurchase;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
