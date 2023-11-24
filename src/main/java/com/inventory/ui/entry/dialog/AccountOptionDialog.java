/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.acc.editor.COAAutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.common.Global;
import com.common.Util1;
import com.inventory.model.PurHis;
import com.inventory.model.SaleHis;
import com.repo.AccountRepo;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class AccountOptionDialog extends javax.swing.JDialog {

    private AccountRepo accountRepo;
    private DepartmentAutoCompleter departmentCompleter;
    private COAAutoCompleter cashCompleter;
    private COAAutoCompleter purhcaseCompleter;
    private COAAutoCompleter paybleCompleter;
    private Object object;
    private final String sale = "SALE";
    private final String purchase = "PURCHASE";
    private final String returnIn = "RETURN_IN";
    private final String returnOut = "RETURN_OUT";

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
        initModel();
    }

    private void initModel() {
        departmentCompleter = new DepartmentAutoCompleter(txtDep, null, false, false);
        cashCompleter = new COAAutoCompleter(txtCash, null, false);
        purhcaseCompleter = new COAAutoCompleter(txtPur, null, false);
        paybleCompleter = new COAAutoCompleter(txtPayble, null, false);
    }

    public void initMain() {
        initCompleter();
    }

    private void initCompleter() {
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentCompleter.setListDepartment(t);
        }).subscribe();
        accountRepo.getCashBank().doOnSuccess((t) -> {
            cashCompleter.setListCOA(t);
        }).subscribe();
        accountRepo.getPurchaseAcc().doOnSuccess((t) -> {
            purhcaseCompleter.setListCOA(t);
        }).subscribe();
        accountRepo.getPayableAcc().doOnSuccess((t) -> {
            paybleCompleter.setListCOA(t);
        }).subscribe();
    }

    public void setObject(Object obj) {
        this.object = obj;
        if (obj instanceof PurHis p) {
            setDepartment(p.getDeptCode());
            setCash(p.getCashAcc());
            setPurchase(p.getPurchaseAcc());
            setPayable(p.getPayableAcc());
        }
    }

    private void setPayable(String payableAcc) {
        accountRepo.findCOA(Util1.isNull(payableAcc, balanceAcc())).doOnSuccess((t) -> {
            paybleCompleter.setCoa(t);
        }).subscribe();
    }

    private String getPayable() {
        ChartOfAccount coa = paybleCompleter.getCOA();
        if (coa != null) {
            if (coa.getKey() != null) {
                return coa.getKey().getCoaCode();
            }
        }
        return null;
    }

    private void setPurchase(String purAcc) {
        accountRepo.findCOA(Util1.isNull(purAcc, sourceAcc())).doOnSuccess((t) -> {
            purhcaseCompleter.setCoa(t);
        }).subscribe();
    }

    private String getPurchase() {
        ChartOfAccount coa = purhcaseCompleter.getCOA();
        if (coa != null) {
            if (coa.getKey() != null) {
                return coa.getKey().getCoaCode();
            }
        }
        return null;
    }

    private void setCash(String cashAcc) {
        accountRepo.findCOA(Util1.isNull(cashAcc, payAcc())).doOnSuccess((t) -> {
            cashCompleter.setCoa(t);
        }).subscribe();
    }

    private String getCash() {
        ChartOfAccount coa = cashCompleter.getCOA();
        if (coa != null) {
            if (coa.getKey() != null) {
                return coa.getKey().getCoaCode();
            }
        }
        return null;
    }

    private void setDepartment(String deptCode) {
        accountRepo.findDepartment(Util1.isNull(deptCode, depAcc())).doOnSuccess((t) -> {
            departmentCompleter.setDepartment(t);
        }).subscribe();
    }

    private String getDepartment() {
        DepartmentA dep = departmentCompleter.getDepartment();
        if (dep != null) {
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

    private String sourceAcc() {
        if (object instanceof SaleHis) {
            return Global.hmAcc.get(sale).getSourceAcc();
        } else if (object instanceof PurHis) {
            return Global.hmAcc.get(purchase).getSourceAcc();
        }
        return null;
    }

    private String payAcc() {
        if (object instanceof SaleHis) {
            return Global.hmAcc.get(sale).getPayAcc();
        } else if (object instanceof PurHis) {
            return Global.hmAcc.get(purchase).getPayAcc();
        }
        return null;
    }

    private String balanceAcc() {
        if (object instanceof SaleHis) {
            return Global.hmAcc.get(sale).getBalanceAcc();
        } else if (object instanceof PurHis) {
            return Global.hmAcc.get(purchase).getBalanceAcc();
        }
        return null;
    }

    private String depAcc() {
        if (object instanceof SaleHis) {
            return Global.hmAcc.get(sale).getDeptCode();
        } else if (object instanceof PurHis) {
            return Global.hmAcc.get(purchase).getDeptCode();
        }
        return null;
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
        txtDep = new javax.swing.JTextField();
        txtCash = new javax.swing.JTextField();
        txtPur = new javax.swing.JTextField();
        txtPayble = new javax.swing.JTextField();

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
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDep, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                            .addComponent(txtCash, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPur, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPayble))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPayble, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtCash;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtPayble;
    private javax.swing.JTextField txtPur;
    // End of variables declaration//GEN-END:variables
}
