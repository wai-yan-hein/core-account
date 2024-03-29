/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.inventory.entity.StockUnit;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Lenovo
 */
public class UnitComboBoxModel implements ComboBoxModel<StockUnit> {

    private List<StockUnit> data;
    private StockUnit selected;

    public UnitComboBoxModel(List<StockUnit> data) {
        this.data = data;
    }

    public UnitComboBoxModel() {
    }

    public List<StockUnit> getData() {
        return data;
    }

    public void setData(List<StockUnit> data) {
        this.data = data;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (StockUnit) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    public StockUnit getSelectedObject() {
        return selected;
    }

    @Override
    public int getSize() {
        return data == null ? 0 : data.size();
    }

    @Override
    public StockUnit getElementAt(int index) {
        return data == null ? null : data.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        // Not implemented
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        // Not implemented
    }
}
