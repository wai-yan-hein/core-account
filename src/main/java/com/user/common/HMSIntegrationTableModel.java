/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.common;

import com.model.VoucherInfo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author DELL
 */
public class HMSIntegrationTableModel extends AbstractTableModel {

    private List<VoucherInfo> listVoucher = new ArrayList();
    private final String[] columnNames = {"Option", "Vou No", "HMS Vou Total", "Acc Vou Total", "Diff Total"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0, 1 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            VoucherInfo obj = listVoucher.get(row);
            return switch (column) {
                case 0 ->
                    obj.getOption();
                case 1 ->
                    obj.getVouNo();
                case 2 ->
                    obj.getHmsVouTotal();
                case 3 ->
                    obj.getAccVouTotal();
                case 4 ->
                    obj.getDiffAmt();
                default ->
                    null;
            };
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return listVoucher.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherInfo> getListVoucher() {
        return listVoucher;
    }

    public void setListVoucher(List<VoucherInfo> listVoucher) {
        this.listVoucher = listVoucher;
        fireTableDataChanged();
    }

    public void delete(String vouNo) {
        listVoucher.removeIf((v) -> v.getVouNo().equals(vouNo));
        fireTableDataChanged();
    }

}
