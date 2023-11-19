package com.common;

import lombok.Data;

@Data
public class UndoItem {

    private int row;
    private Object oldValue;

    public UndoItem(int row, Object oldValue) {
        this.row = row;
        this.oldValue = oldValue;
    }
}
