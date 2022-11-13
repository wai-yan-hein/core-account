/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.common;

import com.common.SelectionObserver;
import com.acc.model.OpeningBalance;

import com.acc.editor.DepartmentAutoCompleter;
import com.common.Util1;
import com.inventory.editor.TraderAutoCompleter;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author myoht
 */
public class OpeningBalanceTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(OpeningBalanceTableModel.class);
    private List<OpeningBalance> listOpening = new ArrayList();
    private final String[] columnsName = {"Code", "Chart Of Account", "Trader Code", "Trader Name", "Dept:", "Currency", "Dr-Amt", "Cr-Amt"};
    private JTable parent;
    private DepartmentAutoCompleter deptAutoCompleter;
    private TraderAutoCompleter tradeAutoCompleter;
    private SelectionObserver selectionObserver;

    public DepartmentAutoCompleter getDeptAutoCompleter() {
        return deptAutoCompleter;
    }

    public void setDeptAutoCompleter(DepartmentAutoCompleter deptAutoCompleter) {
        this.deptAutoCompleter = deptAutoCompleter;
    }

    public TraderAutoCompleter getTradeAutoCompleter() {
        return tradeAutoCompleter;
    }

    public void setTradeAutoCompleter(TraderAutoCompleter tradeAutoCompleter) {
        this.tradeAutoCompleter = tradeAutoCompleter;
    }

    public SelectionObserver getObserver() {
        return selectionObserver;
    }

    public void setObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    
    public void addNewRow() {
        if (listOpening != null) {
            if (!isEmptyRow()) {
                OpeningBalance opening = new OpeningBalance();
                //opening.setOpDate(Util1.getTodayDate());
                listOpening.add(opening);
                fireTableRowsInserted(listOpening.size() - 1, listOpening.size() - 1);
            }
        }
    }

    //check if the list is empty
    private boolean isEmptyRow() {
        boolean status = false;
        if (listOpening.size() >= 1) {
            OpeningBalance opening = listOpening.get(listOpening.size() - 1);
            if (opening.getDeptCode() == null || opening.getSourceAccId() == null) {
                status = true;
            }
        }
        return status;
    }

    @Override
    public int getRowCount() {
        return listOpening == null ? 0 : listOpening.size();
    }

    @Override
    public int getColumnCount() {
        return columnsName.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnsName[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (listOpening == null || listOpening.isEmpty()) {
            return null;
        }
        try {
            OpeningBalance opBalance = listOpening.get(rowIndex);
            return switch (columnIndex) {
                case 0 ->
                    opBalance.getOpId();
                case 1 ->
                    opBalance.getOpDate();
                case 2 ->
                    opBalance.getSourceAccId();
                case 3 ->
                    opBalance.getCurAcc();
                case 4 ->
                    opBalance.getCrAmt();
                case 5 ->
                    opBalance.getCrAmt();
                case 6 ->
                    opBalance.getUerCode();
                case 7 ->
                    opBalance.getCompCode();
                case 8 ->
                    opBalance.getCreatedDate();
                case 9 ->
                    opBalance.getDeptCode();
                case 10 ->
                    opBalance.getTraderCode();
                case 11 ->
                    opBalance.getTranSource();

                default ->
                    null;

            };
        } catch (Exception e) {
            log.error("get value at " + e.getStackTrace()[0].getLineNumber() + " - " + e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param value
     * @param rowIndex
     * @param columnIndex
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        try {
            OpeningBalance opening = listOpening.get(rowIndex);
            switch (columnIndex) {
                case 0,1 -> {
                    if (value != null) {
                        opening.setOpId(1);
                    }
                }
            }

        } catch (Exception e) {
            log.error("set value at " + e.getStackTrace()[0].getLineNumber() + " - " + e.getMessage());
        }
    }

    @Override
    public Class
            getColumnClass(int Column) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public OpeningBalance getOpeningBalnace(int rowIndex) {
        return listOpening.get(rowIndex);
    }

    public void addOpeningBalance(OpeningBalance opening) {
        listOpening.add(opening);
        fireTableRowsInserted(listOpening.size() - 1, listOpening.size() - 1);
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public List<OpeningBalance> getListOpening() {
        return listOpening;
    }

    public void setListOpening(List<OpeningBalance> listOpening) {
        this.listOpening = listOpening;
        fireTableDataChanged();
    }

    public void clear() {
        if (listOpening != null) {
            listOpening.clear();
            fireTableDataChanged();
        }
    }

}
