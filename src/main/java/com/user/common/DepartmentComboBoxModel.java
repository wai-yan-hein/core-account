/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.common;

import com.user.model.Branch;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Lenovo
 */
public class DepartmentComboBoxModel extends AbstractListModel<Branch> implements ComboBoxModel<Branch> {

    private List<Branch> data;
    private Branch selected;

    public List<Branch> getData() {
        return data;
    }

    public void setData(List<Branch> data) {
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
        } else if (anItem instanceof Branch coa) {
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
    public Branch getElementAt(int index) {
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
