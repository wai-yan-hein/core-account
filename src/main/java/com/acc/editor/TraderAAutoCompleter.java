/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.editor;

import com.repo.AccountRepo;
import com.acc.common.TraderATableModel;
import com.acc.model.TraderA;
import com.acc.model.TraderAKey;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
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
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public final class TraderAAutoCompleter implements KeyListener {

    private final JTable table = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private TraderATableModel traderTableModel;
    private TraderA trader;
    public AbstractCellEditor editor;
    private int x = 0;
    private int y = 0;
    private boolean popupOpen = false;
    private SelectionObserver observer;
    private AccountRepo accountRepo;
    private boolean filter;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public TraderAAutoCompleter() {
    }

    public TraderAAutoCompleter(JTextComponent comp, AccountRepo accountRepo,
            AbstractCellEditor editor, boolean filter) {
        this.textComp = comp;
        this.editor = editor;
        this.accountRepo = accountRepo;
        this.filter = filter;
        if (this.filter) {
            setTrader(new TraderA(new TraderAKey("-", Global.compCode), "All"));
        }
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.setFont(Global.textFont);
        traderTableModel = new TraderATableModel();
        table.setModel(traderTableModel);
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.setFont(Global.textFont); // NOI18N
        table.setRowHeight(Global.tblRowHeight);
        table.setDefaultRenderer(Object.class, new TableCellRender());
        table.setSelectionForeground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(table);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);//Code
        table.getColumnModel().getColumn(1).setPreferredWidth(200);//Name
        table.getColumnModel().getColumn(1).setPreferredWidth(100);//Name
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

        popup.setPopupSize(600, 300);

        popup.add(scroll);

        if (textComp instanceof JTextField) {
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                    JComponent.WHEN_FOCUSED);
            textComp.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    popupOpen = true;
                    showPopup();
                }

            });
            textComp.getDocument().addDocumentListener(documentListener);
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
                popupOpen = false;

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                if (!popupOpen) {
                    if (editor != null) {
                        editor.stopCellEditing();
                    }
                }
            }
        });

        table.setRequestFocusEnabled(false);

    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            trader = traderTableModel.getTrader(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            ((JTextField) textComp).setText(trader.getTraderName());
            if (editor == null) {
                if (observer != null) {
                    observer.selected("TRADER", trader);
                }
            }
        }

        popup.setVisible(false);
        popupOpen = false;
        if (editor != null) {
            editor.stopCellEditing();
        }
    }

    private Action acceptAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mouseSelect();

        }
    };
    DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            if (editor != null) {
                popupOpen = true;
                showPopup();
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (editor != null) {
                popupOpen = true;
                showPopup();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };

    public void closePopup() {
        popup.setVisible(false);
        popupOpen = false;
    }

    public void showPopup() {
        if (popupOpen) {
            if (!popup.isVisible()) {
                textComp.addKeyListener(this);
                //popup.setVisible(false); 
                if (textComp.isEnabled()) {
                    if (textComp instanceof JTextField) {
                        textComp.getDocument().addDocumentListener(documentListener);
                    }

                    textComp.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                            JComponent.WHEN_FOCUSED);
                 // Calculate the preferred x and y positions for the popup
                    if (x == 0) {
                        x = textComp.getWidth();
                        y = -200;
                    }
                    popup.show(textComp, x, y);
                    popupOpen = false;

                } else {
                    popup.setVisible(false);
                    popupOpen = false;
                }
            }
        }
        textComp.requestFocus();
    }
    Action showAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TraderAAutoCompleter completer = (TraderAAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectNextPossibleValue();
                } else {
                    if (!popupOpen) {
                        popupOpen = true;
                        completer.showPopup();
                    }

                }
            }
        }
    };
    static Action upAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TraderAAutoCompleter completer = (TraderAAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            TraderAAutoCompleter completer = (TraderAAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                completer.popup.setVisible(false);
                popupOpen = false;
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

    public TraderA getTrader() {
        return trader;
    }

    public void setTrader(TraderA trader) {
        this.trader = trader;
        if (trader != null) {
            this.textComp.setText(trader.getTraderName());
        } else {
            this.textComp.setText(null);
        }
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
    }

    /**
     * Handle the key-released event from the text field.
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        String str = textComp.getText();
        if (!str.isEmpty()) {
            if (!containKey(e)) {
                traderTableModel.clear();
                accountRepo.getTrader(str).subscribe((t) -> {
                    if (filter) {
                        TraderA s = new TraderA(new TraderAKey("-", Global.compCode), "All");
                        t.add(s);
                    }
                    traderTableModel.setListTrader(t);
                    if (!t.isEmpty()) {
                        table.setRowSelectionInterval(0, 0);
                    }
                }, (er) -> {
                    log.error(er.getMessage());
                });

            }

        }
    }

    private boolean containKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP;
    }

    public void clear() {
        TraderAKey key = new TraderAKey("-", Global.compCode);
        setTrader(new TraderA(key, "All"));
    }
}
