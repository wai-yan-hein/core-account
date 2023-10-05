/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.editor;

import com.common.Global;
import com.inventory.model.StockCriteria;
import com.repo.InventoryRepo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class StockCriteriaEditor extends AbstractCellEditor implements TableCellEditor {

    private JComponent component = null;
    private StockCriteriaCompleter completer;
    private InventoryRepo inventoryRepo;

    public StockCriteriaEditor(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public StockCriteriaEditor() {
    }

    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        jtf.setFont(Global.textFont);
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();

                if ((keyEvent.isControlDown() && (keyCode == KeyEvent.VK_DELETE))
                        || (keyEvent.isShiftDown() && (keyCode == KeyEvent.VK_DELETE))
                        || (keyCode == KeyEvent.VK_F5)
                        || (keyCode == KeyEvent.VK_F7)
                        || (keyCode == KeyEvent.VK_F9)
                        || (keyCode == KeyEvent.VK_F10)
                        || (keyCode == KeyEvent.VK_ESCAPE)) {
                    stopCellEditing();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }

            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }
        };

        jtf.addKeyListener(keyListener);
        component = jtf;
        if (value != null) {
            jtf.setText(value.toString());
            jtf.selectAll();
        }
        completer = new StockCriteriaCompleter(jtf, inventoryRepo, this);
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj;
        StockCriteria stock = completer.getStockUnit();
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
