package com.common;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import javax.swing.table.DefaultTableModel;

public class JTableDataSource implements JRDataSource {

    private DefaultTableModel tableModel;
    private int currentIndex = -1;

    public JTableDataSource(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    @Override
    public boolean next() throws JRException {
        currentIndex++;
        return currentIndex < tableModel.getRowCount();
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        String fieldName = jrField.getName();
        return tableModel.getValueAt(currentIndex, tableModel.findColumn(fieldName));
    }
}
