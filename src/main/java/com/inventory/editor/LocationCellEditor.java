/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.editor;

import com.common.Global;
import com.inventory.model.Location;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Lenovo
 */
public class LocationCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JComponent component = null;
    private LocationAutoCompleter completer;
    private List<Location> listLocation;

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {
            JTextField jtf = (JTextField) e.getSource();
            String lastString = jtf.getText().substring(jtf.getText().length() - 1);
            jtf.setText("");
            jtf.setText(lastString);
        }

    };

    public LocationCellEditor(List<Location> listLocation) {
        this.listLocation = listLocation;
    }

    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        jtf.setFont(Global.textFont);
        jtf.addFocusListener(fa);
        component = jtf;
        if (value != null) {
            jtf.setText(value.toString());
        }
        completer = new LocationAutoCompleter(jtf, listLocation, this, false, false);
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj;
        Location stock = completer.getLocation();

        if (stock != null) {
            obj = stock;
        } else {
            obj = ((JTextField) component).getText();
        }

        return obj;

    }

    /*
     * To prevent mouse click cell editing and 
     * function key press
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return false;
        } else if (anEvent instanceof KeyEvent ke) {

            return !ke.isActionKey(); //Function key
        } else {
            return true;
        }
    }

}
