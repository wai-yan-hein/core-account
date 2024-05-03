/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.acc.model.VDescription;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.entity.Job;
import com.inventory.entity.LabourOutputDetail;
import com.inventory.entity.OrderHis;
import com.inventory.entity.Trader;
import com.inventory.entity.VouStatus;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author DELL
 */
@Slf4j
public class LabourOutputTableModel extends AbstractTableModel {

    private String[] columnNames = {"Labour Name", "Order No", "Customer Name", "Description", "Remark",
        "Job", "Status", "Reject Qty", "Output Qty", "Print Qty", "Price", "Amount"};
    @Setter
    private JTable parent;
    private List<LabourOutputDetail> listDetail = new ArrayList();
    @Setter
    private SelectionObserver observer;
    @Setter
    private JLabel lblRecord;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public int getRowCount() {
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 7, 8, 9, 10, 11 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 2;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            LabourOutputDetail sd = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    return sd.getLabourName();
                }
                case 1 -> {
                    //code
                    return Util1.isNull(sd.getRefNo(), sd.getOrderVouNo());
                }
                case 2 -> {
                    return sd.getTraderName();
                }
                case 3 -> {
                    return sd.getDescription();
                }
                case 4 -> {
                    return sd.getRemark();
                }
                case 5 -> {
                    return sd.getJobName();
                }
                case 6 -> {
                    return sd.getVouStatusName();
                }
                case 7 -> {
                    //qty
                    return Util1.toNull(sd.getRejectQty());
                }
                case 8 -> {
                    return Util1.toNull(sd.getOutputQty());
                }
                case 9 -> {
                    return Util1.toNull(sd.getPrintQty());
                }
                case 10 -> {
                    return Util1.toNull(sd.getPrice());
                }
                default -> {
                    return Util1.toNull(sd.getAmount());
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            LabourOutputDetail sd = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0 -> {
                        if (value instanceof Trader t) {
                            sd.setLabourCode(t.getKey().getCode());
                            sd.setLabourName(t.getTraderName());
                            setSelection(row, column + 1);
                        }
                    }
                    case 1 -> {
                        if (value instanceof OrderHis oh) {
                            sd.setRefNo(oh.getRefNo());
                            sd.setOrderVouNo(oh.getKey().getVouNo());
                            sd.setTraderCode(oh.getTraderCode());
                            sd.setTraderName(oh.getTraderName());
                            sd.setPrintQty(1);
                            setSelection(row, column + 2);
                            addNewRow();
                        }
                    }
                    case 3 -> {
                        if (value instanceof VDescription d) {
                            sd.setDescription(d.getDescription());
                        } else {
                            sd.setDescription(value.toString());
                        }
                        setSelection(row, column + 1);
                    }
                    case 4 -> {
                        sd.setRemark(value.toString());
                        setSelection(row, column + 1);
                    }
                    case 5 -> {
                        if (value instanceof Job j) {
                            sd.setJobNo(j.getKey().getJobNo());
                            sd.setJobName(j.getJobName());
                            sd.setPrice(Util1.getDouble(j.getOutputCost()));
                            setSelection(row, column + 1);
                        }
                    }
                    case 6 -> {
                        if (value instanceof VouStatus s) {
                            sd.setVouStatusCode(s.getKey().getCode());
                            sd.setVouStatusName(s.getDescription());
                            setSelection(row, column + 1);
                        }
                    }
                    case 7 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setRejectQty(qty);
                            setSelection(row, column + 1);
                        }
                    }
                    case 8 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setOutputQty(qty);
                            setSelection(row, column + 1);
                        }
                    }
                    case 9 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setPrintQty(qty);
                            setSelection(row, column + 1);
                        }
                    }

                    case 10 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            sd.setPrice(price);
                            setSelection(row + 1, 0);
                        }
                    }
                }
                addNewRow();
                calAmount(sd);
                fireTableRowsUpdated(row, row);
                setRecord(listDetail.size() - 1);
                parent.requestFocusInWindow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void calAmount(LabourOutputDetail d) {
        double amount = d.getOutputQty() * d.getPrice() * d.getPrintQty();
        d.setAmount(amount);
        observer.selected("LABOUR_TOTAL", "LABOUR_TOTAL");
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                LabourOutputDetail pd = LabourOutputDetail.builder().build();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        if (listDetail.size() >= 1) {
            LabourOutputDetail get = listDetail.get(listDetail.size() - 1);
            if (Util1.isNullOrEmpty(get.getOrderVouNo())) {
                return true;
            }
        }
        return false;
    }

    public List<LabourOutputDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<LabourOutputDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void setRecord(int size) {
        lblRecord.setText("Records : " + size);
    }

    public boolean isValidEntry() {
        for (LabourOutputDetail sdh : listDetail) {
            if (sdh.getOrderVouNo() != null) {
                if (sdh.getOutputQty() <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Output Qty.");
                    return false;
                } else if (Util1.isNullOrEmpty(sdh.getJobNo())) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Job.");
                    parent.requestFocus();
                    return false;
                } else if (Util1.isNullOrEmpty(sdh.getLabourCode())) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Labour.");
                    parent.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    public void delete(int row) {
        listDetail.remove(row);
        addNewRow();
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
        parent.requestFocus();
    }

    public void addOrder(LabourOutputDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public LabourOutputDetail getOrderEntry(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
        }
    }
}
