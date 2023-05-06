/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.editor;

import com.common.Global;
import com.common.Util1;
import com.inventory.model.PriceOption;
import com.inventory.model.Stock;
import com.inventory.ui.common.OrderTableModel;
import com.inventory.ui.common.SaleTableModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
 * @author DELL
 */
public class OrderPriceCellEditor extends AbstractCellEditor implements TableCellEditor{
    private JComponent component = null;
    private SalePriceAutoCompleter completer;
    private List<PriceOption> listOption;
    private OrderTableModel orderTableModel;

    public OrderTableModel getOrderTableModel() {
        return orderTableModel;
    }

    public void setOrderTableModel(OrderTableModel orderTableModel) {
        this.orderTableModel = orderTableModel;
    }

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {
            JTextField jtf = (JTextField) e.getSource();
            int length = jtf.getText().length();
            if (length > 0) {
                String lastString = jtf.getText().substring(length - 1);
                jtf.setText("");
                jtf.setText(lastString);
            }
        }
    };

    public OrderPriceCellEditor(List<PriceOption> listOption) {
        this.listOption = listOption;
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
        }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        if (row >= 0) {
            listOption = getPriceOption(row);
        }
        completer = new SalePriceAutoCompleter(jtf, listOption, this);
        return component;
    }

    public List<PriceOption> getPriceOption(int row) {
        Stock s = orderTableModel.getOrderEntry(row).getStock();
        if (!listOption.isEmpty()) {
            for (PriceOption op : listOption) {
                switch (Util1.isNull(op.getKey().getPriceType(), "N")) {
                    case "A" ->
                        op.setPrice(s.getSalePriceA());
                    case "B" ->
                        op.setPrice(s.getSalePriceB());
                    case "C" ->
                        op.setPrice(s.getSalePriceC());
                    case "D" ->
                        op.setPrice(s.getSalePriceD());
                    case "E" ->
                        op.setPrice(s.getSalePriceE());
                    case "N" ->
                        op.setPrice(s.getSalePriceN());
                    default -> {
                        break;
                    }
                }
            }
        }
        return listOption;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj;
        PriceOption price = completer.getPrice();
        if (price != null) {
            obj = price.getPrice();
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
