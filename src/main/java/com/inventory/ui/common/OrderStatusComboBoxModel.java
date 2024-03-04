/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.inventory.entity.OrderStatus;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author Athu Sint
 */
public class OrderStatusComboBoxModel extends AbstractListModel<OrderStatus> implements ComboBoxModel<OrderStatus> {

    private List<OrderStatus> list;
    private OrderStatus ordStatus;

    public List<OrderStatus> getList() {
        return list;
    }

    public void setList(List<OrderStatus> list) {
        this.list = list;
    }

    @Override
    public int getSize() {
        return this.list.size();
    }

    @Override
    public OrderStatus getElementAt(int index) {
        return this.list.get(index);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem == null) {
            this.ordStatus = null;
            fireContentsChanged(this, -1, -1);
        } else if (anItem instanceof OrderStatus ord) {
            this.ordStatus = ord;
            fireContentsChanged(this, -1, -1);
        }
    }

    @Override
    public Object getSelectedItem() {
        return this.ordStatus;
    }

}
