/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.setup.common;

import com.inventory.entity.Location;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Lenovo
 */
public class LocationComboModel extends AbstractListModel<Location> implements ComboBoxModel<Location> {

    private List<Location> data;
    private Location selected;

    public List<Location> getData() {
        return data;
    }

    public void setData(List<Location> data) {
        this.data = data;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem == null) {
            this.selected = null;
            fireContentsChanged(this, -1, -1);
        } else if (anItem instanceof Location coa) {
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
        return data.size();
    }

    @Override
    public Location getElementAt(int index) {
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
