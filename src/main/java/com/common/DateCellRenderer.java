package com.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableCellRenderer;

public class DateCellRenderer extends DefaultTableCellRenderer {

    private SimpleDateFormat dateFormat;

    public DateCellRenderer(String dateFormatPattern) {
        this.dateFormat = new SimpleDateFormat(dateFormatPattern);
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof Date) {
            setText(dateFormat.format(value));
        } else {
            super.setValue(value);
        }
    }
}
