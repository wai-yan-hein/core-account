/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.editor;

import com.common.Global;
import com.inventory.entity.Stock;
import com.repo.InventoryRepo;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JComponent component = null;
    private StockAutoCompleter1 completer;
    private InventoryRepo inventoryRepo;
    private boolean barcode;
    private JTextField jtf;

    public StockCellEditor(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public StockCellEditor(InventoryRepo inventoryRepo, boolean barcode) {
        this.inventoryRepo = inventoryRepo;
        this.barcode = barcode;
    }

    public StockCellEditor() {

    }

    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        jtf = new JTextField();
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
                String barCode = jtf.getText();
            }
        };

        jtf.addKeyListener(keyListener);
        component = jtf;
        if (value != null) {
            jtf.setText(value.toString());
            jtf.selectAll();
        }
        if (barcode) {
            return component;
        }
        completer = new StockAutoCompleter1(jtf, inventoryRepo, this, false);
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        if (barcode) {
            String barCode = jtf.getText();
            log.info("find Barcode : " + barCode);
            Stock s = inventoryRepo.findStockByBarcode(barCode).block();
            if (s != null) {
                s.setBarcode(barCode);
                return s;
            }
            return null;
        }
        return completer.getStock();

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
