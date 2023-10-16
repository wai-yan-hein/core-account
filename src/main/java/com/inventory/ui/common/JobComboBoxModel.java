/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.inventory.model.Job;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Lenovo
 */
public class JobComboBoxModel implements ComboBoxModel<Job> {

    private List<Job> data;
    private Job selected;

    public JobComboBoxModel(List<Job> data) {
        this.data = data;
    }

    public JobComboBoxModel() {
    }

    public List<Job> getData() {
        return data;
    }

    public void setData(List<Job> data) {
        this.data = data;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (Job) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    public Job getSelectedObject() {
        return selected;
    }

    @Override
    public int getSize() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Job getElementAt(int index) {
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
