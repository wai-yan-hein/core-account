/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.inventory.model.StockUnit;
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

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (StockUnit) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public StockUnit getElementAt(int index) {
        return data.get(index);
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
