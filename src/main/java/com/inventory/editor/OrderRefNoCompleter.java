/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.editor;

import com.common.Global;
import com.common.IconUtil;
import com.common.Resolution;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.formdev.flatlaf.FlatClientProperties;
import com.inventory.entity.OrderHis;
import com.inventory.ui.common.OrderRefNoTableModel;
import com.repo.InventoryRepo;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class OrderRefNoCompleter implements KeyListener, SelectionObserver {

    private final JTable table = new JTable();
    private final JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER";
    private OrderRefNoTableModel tableModel;
    private OrderHis object;
    public AbstractCellEditor editor;
    private int x = 0;
    private int y = 0;
    @Setter
    private SelectionObserver observer;
    private InventoryRepo inventoryRepo;

    public OrderRefNoCompleter() {
    }

    public OrderRefNoCompleter(JTextComponent comp, InventoryRepo inventoryRepo,
            AbstractCellEditor editor) {
        this.textComp = comp;
        this.editor = editor;
        this.inventoryRepo = inventoryRepo;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, IconUtil.getIcon(IconUtil.STOCK));
        textComp.setFont(Global.textFont);
        textComp.addKeyListener(this);
        textComp.getDocument().addDocumentListener(documentListener);
        tableModel = new OrderRefNoTableModel();
        table.setModel(tableModel);
        table.setFont(Global.textFont); // NOI18N
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.setDefaultRenderer(Object.class, new TableCellRender());
        table.setRowHeight(Global.tblRowHeight);
        table.setSelectionForeground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(table);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);//Ref No
        table.getColumnModel().getColumn(1).setPreferredWidth(250);//Customer Name 
        table.getColumnModel().getColumn(2).setPreferredWidth(50);//Order Date
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    mouseSelect();
                }
            }
        });

        scroll.getVerticalScrollBar().setFocusable(false);
        scroll.getHorizontalScrollBar().setFocusable(false);

        Resolution r = Util1.getPopSize();
        popup.setPopupSize(r.getWidth(), r.getHeight());
        popup.add(scroll);

        if (textComp instanceof JTextField) {
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                    JComponent.WHEN_FOCUSED);
            textComp.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showPopup();
                }

            });
        } else {
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
                    InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_FOCUSED);
        }

        textComp.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                JComponent.WHEN_FOCUSED);
        textComp.registerKeyboardAction(hidePopupAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);

        popup.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        table.setRequestFocusEnabled(false);
    }

    public OrderHis getObject() {
        return object;
    }

    public void setStock(OrderHis stock) {
        this.object = stock;
        this.textComp.setText(this.object == null ? null : this.object.getRefNo());
    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            object = tableModel.getObject(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            try {
                textComp.setText(object.getRefNo());
            } catch (Exception e) {
            }
            popup.setVisible(false);
            if (observer != null) {
                observer.selected("OrderHis", "OrderHis");
            }
            if (editor != null) {
                editor.stopCellEditing();

            }

        }
    }

    private final Action acceptAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mouseSelect();
        }
    };

    public void closePopup() {
        popup.setVisible(false);
    }

    public void showPopup() {
        try {
            if (!popup.isVisible()) {
                if (textComp.isEnabled()) {
                    textComp.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                            JComponent.WHEN_FOCUSED);
                    if (x == 0) {
                        x = textComp.getWidth();
                        y = textComp.getHeight();
                    }

                    popup.show(textComp, x, y);
                } else {
                    popup.setVisible(false);
                }
            }
            textComp.requestFocus();
        } catch (Exception e) {
            log.error("showPopup : " + e.getMessage());
        }
    }

    Action showAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            OrderRefNoCompleter completer = (OrderRefNoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectNextPossibleValue();
                } else {
                    completer.showPopup();
                }
            }
        }
    };
    Action upAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            OrderRefNoCompleter completer = (OrderRefNoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectPreviousPossibleValue();
                }
            }
        }
    };
    Action hidePopupAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            OrderRefNoCompleter completer = (OrderRefNoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                completer.popup.setVisible(false);
            }
        }
    };

    protected void selectNextPossibleValue() {
        int si = table.getSelectedRow();

        if (si < table.getRowCount() - 1) {
            try {
                table.setRowSelectionInterval(si + 1, si + 1);
            } catch (Exception ex) {

            }
        }

        Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, true);
        table.scrollRectToVisible(rect);
    }

    /**
     * Selects the previous item in the list. It won't change the selection if
     * the currently selected item is already the first item.
     */
    protected void selectPreviousPossibleValue() {
        int si = table.getSelectedRow();

        if (si > 0) {
            try {
                table.setRowSelectionInterval(si - 1, si - 1);
            } catch (Exception ex) {

            }
        }

        Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, true);
        table.scrollRectToVisible(rect);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != 10) {
            showPopup();
        }

    }
    private final DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            if (editor != null) {
                showPopup();
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (editor != null) {
                showPopup();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };

    @Override
    public void selected(Object source, Object selectObj) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        String refNo = textComp.getText();
        if (!refNo.isEmpty()) {
            if (!containKey(e)) {
                inventoryRepo.searchByRefNo(refNo)
                        .doFirst(() -> {
                            tableModel.clear();
                        })
                        .doOnNext((t) -> {
                            tableModel.addObject(t);
                        }).doOnComplete(() -> {
                    if (tableModel.getSize() > 0) {
                        table.setRowSelectionInterval(0, 0);
                    }
                }).subscribe();
            }
        }
    }

    private boolean containKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP;
    }

}
