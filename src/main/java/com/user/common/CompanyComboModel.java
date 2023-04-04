/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.common;

import com.user.model.CompanyInfo;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Lenovo
 */
public class CompanyComboModel implements ComboBoxModel<CompanyInfo> {

    private List<CompanyInfo> data;
    private CompanyInfo selected;

    public List<CompanyInfo> getData() {
        return data;
    }

    public void setData(List<CompanyInfo> data) {
        this.data = data;
    }

    public CompanyComboModel(List<CompanyInfo> data) {
        this.data = data;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (CompanyInfo) anItem;
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
    public CompanyInfo getElementAt(int index) {
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
