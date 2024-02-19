/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dms.commom;

import com.common.Util1;
import com.dms.model.ContractDto;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class ContractSearchTableModel extends AbstractTableModel {

    private List<ContractDto> listDetail = new ArrayList();
    private final String[] columnNames = {"Contract Date", "Contract No", "Contract Name", "Customer Name", "Remark"};
    @Getter
    private int size;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                ContractDto his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //vou no
                        return Util1.convertToLocalStorage(his.getZonedDateTime());
                    }
                    case 1 -> {
                        //vou date
                        return his.getContractNo();
                    }
                    case 2 -> {
                        //vou date
                        return his.getContractName();
                    }
                    case 3 -> {
                        //remark
                        return his.getTraderName();
                    }
                    case 4 -> {
                        return his.getRemark();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<ContractDto> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<ContractDto> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public ContractDto getSelectVou(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(ContractDto t) {
        listDetail.add(t);
        size += 1;
        int lastIndex = listDetail.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void clear() {
        size = 0;
        listDetail.clear();
        fireTableDataChanged();
    }
}
