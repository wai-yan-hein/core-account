/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.inventory.model.LandingHisGrade;
import com.inventory.model.LandingHisGradeKey;
import com.inventory.model.LandingHisKey;
import com.inventory.model.LandingHisPrice;
import com.repo.InventoryRepo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class LandingGradeTableModel extends AbstractTableModel {

    private List<LandingHisGrade> listDetail = new ArrayList<>();
    private final String[] columnNames = {"Stock Name", "Match", "Percent", "Choose"};
    private JTable table;
    private InventoryRepo inventoryRepo;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public LandingGradeTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 3;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0 ->
                String.class;
            case 3 ->
                Boolean.class;
            default ->
                Double.class;
        };
    }

    public List<LandingHisGrade> getListGrade() {
        return listDetail;
    }

    public void setListGrade(List<LandingHisGrade> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            LandingHisGrade b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    b.getStockName();
                case 1 ->
                    b.getMatchCount();
                case 2 ->
                    b.getMatchPercent();
                case 3 ->
                    b.isChoose();
                default ->
                    null;
            }; //Code
            //Description
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    public void setGrade(LandingHisGrade t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addGrade(LandingHisGrade t) {
        listDetail.add(t);
        fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public LandingHisGrade getBatch(int row) {
        if (listDetail == null) {
            return null;
        } else if (listDetail.isEmpty()) {
            return null;
        } else {
            return listDetail.get(row);
        }
    }

    public int getSize() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }

    public void checkGrade(String formulaCode, List<LandingHisPrice> listCriteria) {
        Map<String, Integer> match = new HashMap<>();
        Map<String, String> hmStock = new HashMap<>();
        clear();
        if (listCriteria != null) {
            inventoryRepo.getCriteriaByFormula(formulaCode).doOnSuccess((listGrade) -> {
                listCriteria.forEach((c) -> {
                    log.info("matching : " + c.getCriteriaCode());
                    double percent = c.getPercent();
                    String c1 = c.getCriteriaCode();
                    listGrade.forEach((g) -> {
                        double minPercent = g.getMinPercent();
                        double maxPercent = g.getMaxPercent();
                        String c2 = g.getKey().getCriteriaCode();
                        String stockCode = g.getGradeStockCode();
                        if (c1.equals(c2)) {
                            if (percent >= minPercent && percent <= maxPercent) {
                                match.put(stockCode, match.getOrDefault(stockCode, 0) + 1);
                                log.info("match found : " + c1 + " : " + stockCode);
                            }
                        }
                        hmStock.put(stockCode, g.getStockName());
                    });
                });
                List<LandingHisGrade> list = new ArrayList<>();
                match.forEach((t, u) -> {
                    LandingHisGrade g = new LandingHisGrade();
                    LandingHisGradeKey key = new LandingHisGradeKey();
                    key.setCompCode(Global.compCode);
                    key.setStockCode(t);
                    g.setKey(key);
                    g.setStockName(hmStock.get(t));
                    g.setMatchCount(u);
                    g.setMatchPercent((u / listCriteria.size()) * 100);
                    g.setChoose(false);
                    list.add(g);
                });
                list.sort(Comparator.comparingDouble(LandingHisGrade::getMatchCount).reversed());
                if (!list.isEmpty()) {
                    double a = list.get(0).getMatchCount();
                    double b = 0;
                    if (list.size() > 1) {
                        b = list.stream().skip(0).mapToDouble((t) -> t.getMatchCount()).sum();
                    }
                    if (a > b) {
                        list.get(0).setChoose(true);
                    } else {
                        list.get(1).setChoose(true);
                    }
                }
                setListGrade(list);
            }).block();
        }

    }

}
