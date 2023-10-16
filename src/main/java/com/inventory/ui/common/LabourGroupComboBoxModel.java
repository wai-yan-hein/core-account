/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.inventory.model.LabourGroup;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Lenovo
 */
public class LabourGroupComboBoxModel implements ComboBoxModel<LabourGroup> {

    private List<LabourGroup> data;
    private LabourGroup selected;

    public LabourGroupComboBoxModel(List<LabourGroup> data) {
        this.data = data;
    }

    public LabourGroupComboBoxModel() {
    }

    public List<LabourGroup> getData() {
        return data;
    }

    public void setData(List<LabourGroup> data) {
        this.data = data;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (LabourGroup) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    public LabourGroup getSelectedObject() {
        return selected;
    }

    @Override
    public int getSize() {
        return data == null ? 0 : data.size();
    }

    @Override
    public LabourGroup getElementAt(int index) {
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
