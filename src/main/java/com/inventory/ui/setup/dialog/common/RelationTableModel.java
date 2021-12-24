/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.common.Global;
import com.inventory.common.Util1;
import com.inventory.model.RelationKey;
import com.inventory.model.StockUnit;
import com.inventory.model.UnitRelation;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class RelationTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(RelationTableModel.class);
    private final String[] columnNames = {"From Unit", "To Unit", "Factor"};
    private List<UnitRelation> listRelation = new ArrayList<>();
    private JTable parent;
    private String patternId;

    public String getPatternId() {
        return patternId;
    }

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public int getRowCount() {
        return listRelation.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        UnitRelation rel = listRelation.get(rowIndex);
        switch (columnIndex) {
            case 0:
                if (rel.getUnitKey() != null) {
                    return rel.getUnitKey().getFromUnit();
                }
            case 1:
                if (rel.getUnitKey() != null) {
                    return rel.getUnitKey().getToUnit();
                }
            case 2:
                if (rel.getFactor() != null) {
                    return rel.getFactor().toString();
                }
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        UnitRelation relation = listRelation.get(rowIndex);
        switch (columnIndex) {
            case 0:
                if (aValue != null) {
                    if (aValue instanceof StockUnit) {
                        StockUnit stock = (StockUnit) aValue;
                        relation.getUnitKey().setFromUnit(stock.getUnitCode());
                    }
                    parent.setColumnSelectionInterval(1, 1);
                }
                break;
            case 1:
                if (aValue != null) {
                    if (aValue instanceof StockUnit) {
                        StockUnit stock = (StockUnit) aValue;
                        relation.getUnitKey().setToUnit(stock.getUnitCode()
                        );
                    }
                    parent.setColumnSelectionInterval(2, 2);
                }
                break;
            case 2:
                if (aValue != null) {
                    relation.setFactor(Util1.getFloat(aValue));
                    save(relation);
                    parent.setRowSelectionInterval(rowIndex + 1, rowIndex + 1);
                    parent.setColumnSelectionInterval(0, 0);
                }
                break;

        }
        parent.requestFocusInWindow();
    }

    private void save(UnitRelation unit) {
        try {
            if (isValidEntry(unit)) {
                unit.getUnitKey().setPatternId(patternId);
                /*UnitRelation save = relationService.save(unit);
                if (save != null) {
                Global.hmRelation.put(unit.getUnitKey(), unit.getFactor());
                addEmptyRow();
                }*/
            }
        } catch (Exception e) {
            log.info("Save Relation :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, "LOST SERVER CONNECTION");
        }
    }

    private boolean isValidEntry(UnitRelation unit) {
        boolean status = true;
        if (unit.getUnitKey().getFromUnit() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid From Unit.");
        }
        if (unit.getUnitKey().getToUnit() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid From Unit.");

        }
        return status;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public List<UnitRelation> getListRelation() {
        return listRelation;
    }

    public void setListRelation(List<UnitRelation> listRelation) {
        this.listRelation = listRelation;
        fireTableDataChanged();
    }

    public UnitRelation getRelation(int row) {
        return listRelation.get(row);
    }

    public void setRelation(UnitRelation rel, int row) {
        if (!listRelation.isEmpty()) {
            listRelation.set(row, rel);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addRelation(UnitRelation item) {
        if (!listRelation.isEmpty()) {
            addEmptyRow();
            fireTableRowsInserted(listRelation.size() - 1, listRelation.size() - 1);
        }
    }

    private void addEmptyRow() {
        RelationKey key = new RelationKey();
        UnitRelation relation = new UnitRelation();
        relation.setUnitKey(key);
        listRelation.add(relation);
        fireTableRowsInserted(listRelation.size() - 1, listRelation.size() - 1);
    }

    public void addNewRow() {
        if (listRelation != null) {
            if (isEmptyRow()) {
                addEmptyRow();
            }
        }
    }

    private boolean isEmptyRow() {
        if (!listRelation.isEmpty()) {
            UnitRelation get = listRelation.get(listRelation.size() - 1);
            return get.getUnitKey() != null;
        } else {
            return true;
        }
    }

    public void clear() {
        if (listRelation != null) {
            listRelation.clear();
            fireTableDataChanged();
        }
    }

}
