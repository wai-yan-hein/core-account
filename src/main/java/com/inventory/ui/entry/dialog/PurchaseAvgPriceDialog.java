/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.Global;
import com.common.Util1;
import com.inventory.model.PurHisDetail;
import com.inventory.model.StockUnit;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.UnitComboBoxModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class PurchaseAvgPriceDialog extends javax.swing.JDialog {

    private UnitComboBoxModel unitModel;
    private UnitComboBoxModel lossUnitModel;
    private List<StockUnit> listUnit;
    private PurHisDetail pd;
    private InventoryRepo inventoryRepo;
    private boolean confirm = false;

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public PurHisDetail getPd() {
        return pd;
    }

    public void setPd(PurHisDetail pd) {
        this.pd = pd;
    }

    public List<StockUnit> getListUnit() {
        return listUnit;
    }

    public void setListUnit(List<StockUnit> listUnit) {
        this.listUnit = listUnit;
    }

    /**
     * Creates new form PurchaseAvgPriceDialog
     *
     * @param parent
     */
    public PurchaseAvgPriceDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
    }

    public void initMain() {
        initTextBox();
        initComboBoxModel();
        initData();
    }

    private void initComboBoxModel() {
        unitModel = new UnitComboBoxModel(listUnit);
        lossUnitModel = new UnitComboBoxModel(listUnit);
        cboUnit.setModel(unitModel);
        cboAvgUnit.setModel(lossUnitModel);
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            ((JFormattedTextField) e.getSource()).selectAll();
        }
    };

    private void initData() {
        inventoryRepo.findStock(pd.getStockCode()).subscribe((s) -> {
            if (s != null) {
                txtRelName.setText(pd.getRelName());
                txtQty.setValue(pd.getQty());
                float orgPrice = Util1.getFloat(pd.getOrgPrice());
                txtPrice.setValue(orgPrice > 0 ? orgPrice : pd.getPrice());
                inventoryRepo.findUnit(pd.getUnitCode(), Global.deptId).subscribe((t) -> {
                    unitModel.setSelectedItem(t);
                    cboUnit.setModel(unitModel);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });
                txtAvgQty.setValue(pd.getAvgQty());
                txtAvgPrice.setValue(Util1.getFloat(pd.getPrice()));
                inventoryRepo.findUnit(s.getWeightUnit(), Global.deptId).subscribe((t) -> {
                    lossUnitModel.setSelectedItem(t);
                    cboAvgUnit.setModel(lossUnitModel);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });
                txtAvgQty.requestFocus();
            }
        });

    }

    private void initTextBox() {
        txtQty.setFormatterFactory(Util1.getDecimalFormat());
        txtPrice.setFormatterFactory(Util1.getDecimalFormat());
        txtAvgQty.setFormatterFactory(Util1.getDecimalFormat());
        txtAvgPrice.setFormatterFactory(Util1.getDecimalFormat());
        txtDiffPrice.setFormatterFactory(Util1.getDecimalFormat());
        txtAvgQty.addActionListener(action);
        txtAvgQty.addFocusListener(fa);
    }
    private final ActionListener action = (ActionEvent e) -> {
        calPrice();
    };

    private void calPrice() {
        if (lossUnitModel.getSelectedItem() instanceof StockUnit unit) {
            String unitCode = unit.getKey().getUnitCode();
            inventoryRepo.getSmallQty(pd.getStockCode(), unitCode).subscribe((t) -> {
                float qty = t.getQty();
                float avgQty = Util1.getFloat(txtAvgQty.getValue());
                float price = Util1.getFloat(txtPrice.getValue());
                float avgPrice = avgQty / qty * price;
                pd.setPrice(avgPrice);
                pd.setAvgQty(avgQty);
                txtAvgPrice.setValue(avgPrice);
                txtDiffPrice.setValue(price - avgPrice);
                btnConfirm.requestFocus();
            });

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtQty = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        txtAvgPrice = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtAvgQty = new javax.swing.JFormattedTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        txtDiffPrice = new javax.swing.JFormattedTextField();
        btnConfirm = new javax.swing.JButton();
        cboAvgUnit = new javax.swing.JComboBox<>();
        cboUnit = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        txtRelName = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Purchase Avg Price Dialog");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Qty");

        txtQty.setEditable(false);
        txtQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQty.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Price");

        txtPrice.setEditable(false);
        txtPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrice.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Avg Qty");

        txtAvgPrice.setEditable(false);
        txtAvgPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAvgPrice.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Avg Price");

        txtAvgQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAvgQty.setFont(Global.textFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Diff Price");

        txtDiffPrice.setEditable(false);
        txtDiffPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiffPrice.setFont(Global.textFont);

        btnConfirm.setFont(Global.lableFont);
        btnConfirm.setText("Confirm");
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        cboAvgUnit.setFont(Global.textFont);
        cboAvgUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboAvgUnitActionPerformed(evt);
            }
        });

        cboUnit.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Relation");

        txtRelName.setEditable(false);
        txtRelName.setFont(Global.textFont);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrice))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAvgPrice)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtAvgQty, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboAvgUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiffPrice))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnConfirm))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRelName)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtQty)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3, jLabel4, jLabel6});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRelName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtQty)
                        .addComponent(cboUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPrice)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAvgQty)
                        .addComponent(cboAvgUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtAvgPrice)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiffPrice))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConfirm)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        // TODO add your handling code here:
        setConfirm(true);
        this.dispose();
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_formKeyPressed

    private void cboAvgUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboAvgUnitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboAvgUnitActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JComboBox<StockUnit> cboAvgUnit;
    private javax.swing.JComboBox<StockUnit> cboUnit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JFormattedTextField txtAvgPrice;
    private javax.swing.JFormattedTextField txtAvgQty;
    private javax.swing.JFormattedTextField txtDiffPrice;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JFormattedTextField txtQty;
    private javax.swing.JTextField txtRelName;
    // End of variables declaration//GEN-END:variables
}
