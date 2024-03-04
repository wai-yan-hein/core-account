/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.editor;

import com.common.Global;
import com.inventory.entity.VouDiscount;
import com.repo.InventoryRepo;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Lenovo
 */
public class DiscountDescriptionEditor extends AbstractCellEditor implements TableCellEditor {

    private JComponent component = null;
    private DiscountDescriptionCompleter completer;
    private final InventoryRepo inventoryRepo;

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

    public DiscountDescriptionEditor(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
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
        completer = new DiscountDescriptionCompleter(jtf, inventoryRepo, this);
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj;
        VouDiscount d = completer.getObject();

        if (d != null) {
            obj = d;
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
