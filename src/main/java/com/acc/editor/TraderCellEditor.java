/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.editor;

import com.repo.AccountRepo;
import com.acc.model.TraderA;
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

/**
 *
 * @author Lenovo
 */
public class TraderCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JComponent component = null;
    private TraderAAutoCompleter completer;
    private AccountRepo accountRepo;

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {
            try {
                JTextField jtf = (JTextField) e.getSource();
                String lastString = jtf.getText().substring(jtf.getText().length() - 1);
                jtf.setText("");
                jtf.setText(lastString);
            } catch (Exception ex) {

            }
        }

    };

    public TraderCellEditor(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();

                if ((keyEvent.isControlDown() && (keyCode == KeyEvent.VK_F8))
                        || (keyEvent.isShiftDown() && (keyCode == KeyEvent.VK_F8))
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
        jtf.addFocusListener(fa);
        component = jtf;
        if (value != null) {
            jtf.setText(value.toString());
            jtf.selectAll();
        }
        completer = new TraderAAutoCompleter(jtf, accountRepo, this, false);
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj;
        TraderA trader = completer.getTrader();

        if (trader != null) {
            obj = trader;
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
