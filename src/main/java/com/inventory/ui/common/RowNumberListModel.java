package com.inventory.ui.common;

import javax.swing.AbstractListModel;

public class RowNumberListModel extends AbstractListModel<Integer> {

    private int rowCount;

    @Override
    public int getSize() {
        return rowCount;
    }

    @Override
    public Integer getElementAt(int index) {
        // Adding 1 to convert from 0-based index to 1-based row numbers
        return index + 1;
    }

    // Method to update the row count dynamically
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        fireContentsChanged(this, 0, rowCount - 1);
    }
}
