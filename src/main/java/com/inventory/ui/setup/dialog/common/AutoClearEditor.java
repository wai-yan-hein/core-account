/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;


import com.common.Global;
import com.common.KeyPropagate;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Lenovo
 */
public class AutoClearEditor extends javax.swing.AbstractCellEditor implements TableCellEditor {
    
    private JComponent component = null;
    private Object oldValue;
    private KeyPropagate kp;
    
    public AutoClearEditor() {
    }
    
    public AutoClearEditor(KeyPropagate kp) {
        this.kp = kp;
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        oldValue = value;
        JTextField jtf = new JTextField();
        Color color = UIManager.getColor("Component.focusedBorderColor");
        jtf.setBorder(BorderFactory.createLineBorder(color, 3, true));
        jtf.setFont(Global.textFont);
        if (value != null) {
            jtf.setText(value.toString());
        }
        if (isSelected) {
            jtf.selectAll();
        }
        jtf.selectAll();
        if (kp != null) {
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
                        kp.keyEvent(keyEvent);
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
        }
        
        if (value != null) {
            jtf.setText(value.toString());
            jtf.selectAll();
        }
        component = jtf;
        return component;
    }
    
    @Override
    public Object getCellEditorValue() {
        Object obj = ((JTextField) component).getText();
        
        if (obj == null) {
            obj = oldValue;
        }
        
        return obj;
    }

    /*
     * To prevent mouse click cell editing
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return !(anEvent instanceof MouseEvent);
    }
}
