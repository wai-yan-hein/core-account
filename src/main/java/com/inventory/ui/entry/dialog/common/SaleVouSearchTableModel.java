/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.inventory.common.Util1;
import com.inventory.model.SaleHis;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
@Slf4j
public class SaleVouSearchTableModel extends AbstractTableModel {

    private List<SaleHis> listSaleHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "Created By", "V-Total"};
    private JTable parent;

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
        if (listSaleHis == null) {
            return 0;
        }
        return listSaleHis.size();
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
        return column == 4 ? Float.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            SaleHis his = listSaleHis.get(row);

            switch (column) {
                case 0 -> {
                    //date
                    return Util1.toDateStr(his.getVouDate(), "dd/MM/yyyy");
                }
                case 1 -> {
                    //vou-no
                    if (Util1.getBoolean(his.getDeleted())) {
                        return his.getVouNo() + "***";
                    } else {
                        return his.getVouNo();
                    }
                }
                case 2 -> {
                    //customer
                    return his.getTrader() == null ? null : his.getTrader().getTraderName();
                }
                case 3 -> {
                    //user
                    return his.getCreatedBy().getUserShort();
                }
                case 4 -> {
                    //v-total
                    return his.getVouTotal();
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<SaleHis> getListSaleHis() {
        return listSaleHis;
    }

    public void setListSaleHis(List<SaleHis> listSaleHis) {
        this.listSaleHis = listSaleHis;
        fireTableDataChanged();
    }

    public SaleHis getSelectVou(int row) {
        if (listSaleHis != null) {
            if (!listSaleHis.isEmpty()) {
                return listSaleHis.get(row);
            }
        }
        return null;
    }
}
