/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.editor;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.Trader;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.TraderTableModel;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Lenovo
 */
public class TraderAutoCompleter implements KeyListener {

    private final JTable table = new JTable();
    private final JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private TraderTableModel traderTableModel;
    private Trader trader;
    public AbstractCellEditor editor;
    private int x = 0;
    private int y = 0;
    private SelectionObserver observer;
    private List<String> listOption = new ArrayList<>();
    private InventoryRepo inventoryRepo;
    private boolean filter;
    private String traderType;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public List<String> getListOption() {
        return listOption;
    }

    public void setListOption(List<String> listOption) {
        this.listOption = listOption;
    }

    public TraderAutoCompleter() {
    }

    public TraderAutoCompleter(JTextComponent comp, InventoryRepo inventoryRepo,
            AbstractCellEditor editor, boolean filter, String traderType) {
        this.textComp = comp;
        this.editor = editor;
        this.filter = filter;
        this.traderType = traderType;
        this.inventoryRepo = inventoryRepo;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        if (filter) {
            Trader t = new Trader("-", "All");
            setTrader(t);
        }
        textComp.setFont(Global.textFont);
        textComp.addKeyListener(this);
        traderTableModel = new TraderTableModel();
        table.setModel(traderTableModel);
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.setFont(Global.textFont); // NOI18N
        table.setDefaultRenderer(Object.class, new TableCellRender());
        table.setRowHeight(Global.tblRowHeight);
        table.setSelectionForeground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(table);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        table.getColumnModel().getColumn(1).setPreferredWidth(150);//Name
        table.getColumnModel().getColumn(2).setPreferredWidth(150);//Region

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

        popup.setPopupSize(600, 200);

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
                    KeyEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
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
                if (editor != null) {
                    editor.stopCellEditing();
                }
            }
        });

        table.setRequestFocusEnabled(false);
    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            trader = traderTableModel.getTrader(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            textComp.setText(trader.getTraderName());
            listOption = new ArrayList<>();
        }
        popup.setVisible(false);
        if (editor != null) {
            editor.stopCellEditing();
        }
        if (observer != null) {
            if (trader != null) {
                observer.selected("TRADER", "TRADER");
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
    }
    Action showAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TraderAutoCompleter completer = (TraderAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectNextPossibleValue();
                } else {
                    completer.showPopup();

                }
            }
        }
    };
    static Action upAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TraderAutoCompleter completer = (TraderAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            TraderAutoCompleter completer = (TraderAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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

    public Trader getTrader() {
        return trader;
    }

    public final void setTrader(Trader trader) {
        this.trader = trader;
        this.textComp.setText(trader == null ? null : trader.getTraderName());
    }

    public void setTrader(Trader t, int row) {
        traderTableModel.setTrader(t, row);
    }

    public void addTrader(Trader t) {
        traderTableModel.addTrader(t);
    }

    /*
     * KeyListener implementation
     */
    /**
     * Handle the key typed event from the text field.
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Handle the key-pressed event from the text field.
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != 0) {
            showPopup();
        }
    }

    /**
     * Handle the key-released event from the text field.
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        String str = Util1.cleanString(textComp.getText());
        if (!str.isEmpty()) {
            if (!containKey(e)) {
                inventoryRepo.getTraderList(str, traderType).subscribe((t) -> {
                    if (this.filter) {
                        Trader s = new Trader("-", "All");
                        t.add(s);
                    }
                    traderTableModel.setListTrader(t);
                    if (!t.isEmpty()) {
                        table.setRowSelectionInterval(0, 0);
                    }
                });
            }
        }
    }

    private boolean containKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP;
    }
}
