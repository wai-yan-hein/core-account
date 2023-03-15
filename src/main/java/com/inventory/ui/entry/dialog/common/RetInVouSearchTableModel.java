/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.model.VReturnIn;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class RetInVouSearchTableModel extends AbstractTableModel {

    private List<VReturnIn> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "Remark", "Created By", "Paid Amt", "V-Total"};
    private JTable parent;

    private JFormattedTextField txtPaid;
    private JFormattedTextField txtAmt;
    private JFormattedTextField txtRecord;

    public JFormattedTextField getTxtPaid() {
        return txtPaid;
    }

    public void setTxtPaid(JFormattedTextField txtPaid) {
        this.txtPaid = txtPaid;
    }

    public JFormattedTextField getTxtAmt() {
        return txtAmt;
    }

    public void setTxtAmt(JFormattedTextField txtAmt) {
        this.txtAmt = txtAmt;
    }

    public JFormattedTextField getTxtRecord() {
        return txtRecord;
    }

    public void setTxtRecord(JFormattedTextField txtRecord) {
        this.txtRecord = txtRecord;
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 6, 5 -> {
                return Float.class;
            }
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                VReturnIn his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //date
                        return his.getVouDate();
                    }
                    case 1 -> {
                        //vou-no
                        if (his.isDeleted()) {
                            return his.getVouNo() + "***";
                        } else {
                            return his.getVouNo();
                        }
                    }
                    case 2 -> {
                        //customer
                        return his.getTraderName();
                    }
                    case 3 -> {
                        //remark
                        return his.getRemark();
                    }
                    case 4 -> {
                        return Global.hmUser.get(his.getCreatedBy());
                    }
                    case 5 -> {
                        //v-total
                        return his.getPaid();
                    }
                    case 6 -> {
                        return his.getVouTotal();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<VReturnIn> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<VReturnIn> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public VReturnIn getSelectVou(int row) {
        return listDetail.get(row);
    }

    public void addObject(VReturnIn t) {
        txtAmt.setValue(Util1.getFloat(txtAmt.getValue()) + Util1.getFloat(t.getVouTotal()));
        txtPaid.setValue(Util1.getFloat(txtPaid.getValue()) + Util1.getFloat(t.getPaid()));
        listDetail.add(t);
        txtRecord.setValue(listDetail.size());
    }


    public void clear() {
        txtAmt.setValue(0);
        txtPaid.setValue(0);
        txtRecord.setValue(0);
        listDetail.clear();
        fireTableDataChanged();
    }
}
