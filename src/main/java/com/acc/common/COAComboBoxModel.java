/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.common;

import com.acc.model.ChartOfAccount;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Lenovo
 */
public class COAComboBoxModel extends AbstractListModel<ChartOfAccount> implements ComboBoxModel<ChartOfAccount> {

    private List<ChartOfAccount> data;
    private ChartOfAccount selected;

    public List<ChartOfAccount> getData() {
        return data;
    }

    public void setData(List<ChartOfAccount> data) {
        if (data == null) {
            return;
        }
        this.data = data;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem == null) {
            this.selected = null;
            fireContentsChanged(this, -1, -1);
        } else if (anItem instanceof ChartOfAccount coa) {
            this.selected = coa;
            fireContentsChanged(this, -1, -1);
        }

    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    @Override
    public int getSize() {
        return data == null ? 0 : data.size();
    }

    @Override
    public ChartOfAccount getElementAt(int index) {
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
