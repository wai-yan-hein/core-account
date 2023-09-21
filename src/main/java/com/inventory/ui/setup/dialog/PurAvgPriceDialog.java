/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.setup.dialog;

import com.common.Global;
import com.common.Util1;
import com.inventory.editor.UnitAutoCompleter;
import com.inventory.model.PurHisDetail;
import com.inventory.model.StockUnit;
import com.repo.InventoryRepo;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import javax.swing.JFormattedTextField;

import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 *
 * @author DELL
 */
public class PurAvgPriceDialog extends javax.swing.JDialog {

    private UnitAutoCompleter unitAutoCompleter;
    private UnitAutoCompleter avgunitAutoCompleter;
    private InventoryRepo inventoryRepo;
    private PurHisDetail pd;
    private List<StockUnit> listUnit;

    public UnitAutoCompleter getAvgunitAutoCompleter() {
        return avgunitAutoCompleter;
    }

    public void setAvgunitAutoCompleter(UnitAutoCompleter avgunitAutoCompleter) {
        this.avgunitAutoCompleter = avgunitAutoCompleter;
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
     * Creates new form PurAvgPriceDialog
     *
     * @param frame
     */
    public PurAvgPriceDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initTextBox();
        initListener();
    }

    private void initListener() {
        txtQty.addFocusListener(fa);
        txtPrice.addFocusListener(fa);
        txtUnit.addFocusListener(fa);
        txtAvgQty.addFocusListener(fa);
        txtAvgPrice.addFocusListener(fa);
        txtAvgUnit.addFocusListener(fa);
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            } else if (e.getSource() instanceof JFormattedTextField txt) {
                txt.selectAll();
            }
        }

    };

    public void initMain() {
        initCombo();
        initData();
    }

    private void initCombo() {
        unitAutoCompleter = new UnitAutoCompleter(txtUnit, null);
        unitAutoCompleter.setListUnit(listUnit);
        avgunitAutoCompleter = new UnitAutoCompleter(txtAvgUnit, null);
        avgunitAutoCompleter.setListUnit(listUnit);
    }

    private void initTextBox() {
        txtQty.setFormatterFactory(Util1.getDecimalFormat());
        txtPrice.setFormatterFactory(Util1.getDecimalFormat());
        txtAvgQty.setFormatterFactory(Util1.getDecimalFormat());
        txtAvgPrice.setFormatterFactory(Util1.getDecimalFormat());

        txtQty.setHorizontalAlignment(JTextField.RIGHT);
        txtPrice.setHorizontalAlignment(JTextField.RIGHT);
        txtUnit.setHorizontalAlignment(JTextField.RIGHT);
        txtAvgQty.setHorizontalAlignment(JTextField.RIGHT);
        txtAvgPrice.setHorizontalAlignment(JTextField.RIGHT);
        txtAvgUnit.setHorizontalAlignment(JTextField.RIGHT);
    }

    private void initData() {
        txtQty.setValue(pd.getQty());
        txtPrice.setValue(pd.getPrice());
        inventoryRepo.findUnit(pd.getUnitCode()).subscribe((t) -> {
            unitAutoCompleter.setStockUnit(t);
        });
        txtAvgQty.setValue(pd.getQty());
        txtAvgPrice.setValue(pd.getPrice());
        inventoryRepo.findUnit(pd.getUnitCode()).subscribe((t) -> {
            avgunitAutoCompleter.setStockUnit(t);
        });
        txtStockName.setText(pd.getStockName());
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAvgQty = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtAvgPrice = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        txtUnit = new javax.swing.JTextField();
        txtAvgUnit = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        txtStockName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Purchase Avg Price");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Qty");

        txtQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQty.setFont(Global.lableFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Unit");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Price");

        txtPrice.setFont(Global.lableFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Avg Qty");

        txtAvgQty.setFont(Global.lableFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Avg Unit");

        txtAvgPrice.setFont(Global.lableFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Avg Price");

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtUnit.setFont(Global.textFont);

        txtAvgUnit.setFont(Global.textFont);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtStockName.setFont(Global.menuFont);
        txtStockName.setForeground(Global.selectionColor);
        txtStockName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtStockName.setText("Stock Name");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtStockName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtStockName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAvgQty, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                            .addComponent(txtAvgUnit)
                            .addComponent(txtAvgPrice)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtQty)
                            .addComponent(txtUnit)
                            .addComponent(txtPrice))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtQty)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPrice)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtAvgQty)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAvgUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtAvgPrice)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.dispose();
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JFormattedTextField txtAvgPrice;
    private javax.swing.JFormattedTextField txtAvgQty;
    private javax.swing.JTextField txtAvgUnit;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JFormattedTextField txtQty;
    private javax.swing.JLabel txtStockName;
    private javax.swing.JTextField txtUnit;
    // End of variables declaration//GEN-END:variables
}
