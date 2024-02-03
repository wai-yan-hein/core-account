/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.user.dialog;

import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.user.model.PropertyKey;
import com.user.model.SysProperty;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockSettingDialog extends javax.swing.JDialog {

    private SelectionObserver observer;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    private final ActionListener action = (ActionEvent e) -> {
        if (e.getSource() instanceof JCheckBox chk) {
            String key = chk.getName();
            String value = Util1.getString(chk.isSelected());
            SysProperty p = new SysProperty();
            PropertyKey pKey = new PropertyKey();
            pKey.setPropKey(key);
            p.setKey(pKey);
            p.setPropValue(value);
            save(p);
        } else if (e.getSource() instanceof JTextField txt) {
            String key = txt.getName();
            String value = txt.getText();
            SysProperty p = new SysProperty();
            PropertyKey pKey = new PropertyKey();
            pKey.setPropKey(key);
            p.setKey(pKey);
            p.setPropValue(value);
            save(p);
        } else if (e.getSource() instanceof JRadioButton txt) {
            String key = txt.getName();
            String value = Util1.getString(txt.isSelected());
            SysProperty p = new SysProperty();
            PropertyKey pKey = new PropertyKey();
            pKey.setPropKey(key);
            p.setKey(pKey);
            p.setPropValue(value);
            save(p);
        }
    };

    /**
     * Creates new form ReportNameDialog
     *
     * @param frame
     */
    public StockSettingDialog(JFrame frame) {
        super(frame, false);
        initComponents();
        addActionListener(panel1);
        addActionListener(panel2);
        addActionListener(panel3);
        initTextBox();

    }

    private void initTextBox() {
        chkDisableMill.setName(ProUtil.DISABLE_MILL);
        chkDisableSale.setName(ProUtil.DISABLE_SALE);
        chkDisablePur.setName(ProUtil.DISABLE_PUR);
        chkDisableRI.setName(ProUtil.DISABLE_RETIN);
        chkDisableRO.setName(ProUtil.DISABLE_RETOUT);
        chkDisableStockIO.setName(ProUtil.DISABLE_PATTERN_IO);
        chkWeight.setName(ProUtil.STOCK_USE_WEIHGT);
        chkSWB.setName(ProUtil.STOCK_NAME_WITH_BRAND);
        chkWeightPoint.setName(ProUtil.WEIGHT_POINT);
        chkRec.setName(ProUtil.DEFAULT_STOCK_REC);
        chkPay.setName(ProUtil.DEFAULT_STOCK_PAY);
    }

    public void setData(HashMap<String, String> hmProperty) {
        chkDisableSale.setSelected(Util1.getBoolean(hmProperty.get(chkDisableSale.getName())));
        chkDisablePur.setSelected(Util1.getBoolean(hmProperty.get(chkDisablePur.getName())));
        chkDisableRI.setSelected(Util1.getBoolean(hmProperty.get(chkDisableRI.getName())));
        chkDisableRO.setSelected(Util1.getBoolean(hmProperty.get(chkDisableRO.getName())));
        chkDisableMill.setSelected(Util1.getBoolean(hmProperty.get(chkDisableMill.getName())));
        chkDisableStockIO.setSelected(Util1.getBoolean(hmProperty.get(chkDisableStockIO.getName())));
        chkWeight.setSelected(Util1.getBoolean(hmProperty.get(chkWeight.getName())));
        chkSWB.setSelected(Util1.getBoolean(hmProperty.get(chkSWB.getName())));
        chkWeightPoint.setSelected(Util1.getBoolean(hmProperty.get(chkWeightPoint.getName())));
        chkRec.setSelected(Util1.getBoolean(hmProperty.get(chkRec.getName())));
        chkPay.setSelected(Util1.getBoolean(hmProperty.get(chkPay.getName())));
    }

    private void save(SysProperty p) {
        int yn = JOptionPane.showConfirmDialog(this, "Are you sure to change setting?", "Setting", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (yn == JOptionPane.YES_OPTION) {
            observer.selected("save", p);
        }
    }

    private void addActionListener(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JTextField txt) {
                txt.addActionListener(action);
            } else if (component instanceof JCheckBox txt) {
                txt.addActionListener(action);
            } else if (component instanceof JRadioButton txt) {
                txt.addActionListener(action);
            }
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

        panel1 = new javax.swing.JPanel();
        chkDisableStockIO = new javax.swing.JCheckBox();
        chkDisableMill = new javax.swing.JCheckBox();
        chkDisableRO = new javax.swing.JCheckBox();
        chkDisableRI = new javax.swing.JCheckBox();
        chkDisablePur = new javax.swing.JCheckBox();
        chkDisableSale = new javax.swing.JCheckBox();
        panel2 = new javax.swing.JPanel();
        chkSWB = new javax.swing.JCheckBox();
        chkWeight = new javax.swing.JCheckBox();
        chkWeightPoint = new javax.swing.JCheckBox();
        panel3 = new javax.swing.JPanel();
        chkRec = new javax.swing.JCheckBox();
        chkPay = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Disable"));

        chkDisableStockIO.setText("Disable Pattern in Stock I/O");

        chkDisableMill.setText("Disable Calculate Stock in Milling");

        chkDisableRO.setText("Disable Calculate Stock in Return Out");

        chkDisableRI.setText("Disable Calculate Stock in Return In");

        chkDisablePur.setText("Disable Calculate Stock in Purchase");

        chkDisableSale.setText("Disable Calculate Stock in Sale");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkDisablePur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableSale, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableRO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableRI, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableStockIO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableMill, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkDisableSale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisablePur)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableRI)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableRO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableMill)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableStockIO)
                .addContainerGap(153, Short.MAX_VALUE))
        );

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Other"));

        chkSWB.setText("Stock Name With Brand");

        chkWeight.setText("Weight");

        chkWeightPoint.setText("Weight Point");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSWB)
                    .addComponent(chkWeight)
                    .addComponent(chkWeightPoint))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addComponent(chkSWB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkWeight)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkWeightPoint)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Default"));

        chkRec.setText("S - Receivable");

        chkPay.setText("S - Payable");

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkRec)
                    .addComponent(chkPay))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addComponent(chkRec)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPay)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkDisableMill;
    private javax.swing.JCheckBox chkDisablePur;
    private javax.swing.JCheckBox chkDisableRI;
    private javax.swing.JCheckBox chkDisableRO;
    private javax.swing.JCheckBox chkDisableSale;
    private javax.swing.JCheckBox chkDisableStockIO;
    private javax.swing.JCheckBox chkPay;
    private javax.swing.JCheckBox chkRec;
    private javax.swing.JCheckBox chkSWB;
    private javax.swing.JCheckBox chkWeight;
    private javax.swing.JCheckBox chkWeightPoint;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JPanel panel3;
    // End of variables declaration//GEN-END:variables
}
