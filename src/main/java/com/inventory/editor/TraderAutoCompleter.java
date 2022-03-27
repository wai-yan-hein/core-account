/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.editor;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.inventory.model.OptionModel;
import com.inventory.model.Trader;
import com.inventory.ui.common.TraderTableModel;
import com.inventory.ui.setup.dialog.OptionDialog;
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
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
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
    private TableRowSorter<TableModel> sorter;
    private int x = 0;
    private int y = 0;
    private SelectionObserver selectionObserver;
    private List<String> listOption = new ArrayList<>();
    private OptionDialog optionDialog;
    private List<Trader> listTrader;
    private boolean custom;

    public List<String> getListOption() {
        return listOption;
    }

    public void setListOption(List<String> listOption) {
        this.listOption = listOption;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public TraderAutoCompleter() {
    }

    public TraderAutoCompleter(JTextComponent comp, List<Trader> list,
            AbstractCellEditor editor, boolean filter, int max, boolean custom) {
        this.textComp = comp;
        this.editor = editor;
        this.listTrader = list;
        this.custom = custom;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        initOption();
        if (filter) {
            Trader t = new Trader("-", "All");
            switch (max) {
                case 1 -> {
                    list = new ArrayList<>(list);
                    list.add(0, t);
                    setTrader(t);
                }
                case 2 -> {
                    list = new ArrayList<>(list);
                    list.add(0, t);
                    list.add(1, new Trader("C", "All Customer"));
                    list.add(2, new Trader("S", "All Supplier"));
                    setTrader(t);
                }
                default -> {
                    Trader t2 = new Trader(null, "Default Customer");
                    list = new ArrayList<>(list);
                    list.add(0, t2);
                    setTrader(t2);
                }
            }
        }
        if (custom) {
            list = new ArrayList<>(list);
            list.add(1, new Trader("C", "Custom"));
        }
        textComp.setFont(Global.textFont);
        textComp.addKeyListener(this);
        traderTableModel = new TraderTableModel(list);
        table.setModel(traderTableModel);
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.setFont(Global.textFont); // NOI18N
        table.setDefaultRenderer(Object.class, new TableCellRender());
        table.setRowHeight(Global.tblRowHeight);
        table.setSelectionBackground(UIManager.getDefaults().getColor("Table.selectionBackground"));
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        table.getColumnModel().getColumn(1).setPreferredWidth(150);//Name
        table.getColumnModel().getColumn(2).setPreferredWidth(100);//Region

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

        popup.setPopupSize(500, 200);

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

        if (list != null) {
            if (!list.isEmpty()) {
                table.setRowSelectionInterval(0, 0);
            }
        }
    }

    private void initOption() {
        listOption.clear();
        listTrader.forEach(t -> {
            listOption.add(t.getCode());
        });
    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            trader = traderTableModel.getTrader(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            textComp.setText(trader.getTraderName());
            listOption = new ArrayList<>();
            if (custom) {
                switch (trader.getCode()) {
                    case "C" -> {
                        optionDialog = new OptionDialog(Global.parentForm, "Trader");
                        List<OptionModel> listOP = new ArrayList<>();
                        listTrader.forEach(t -> {
                            listOP.add(new OptionModel(t.getCode(), t.getTraderName()));
                        });
                        optionDialog.setOptionList(listOP);
                        optionDialog.setLocationRelativeTo(null);
                        optionDialog.setVisible(true);
                        if (optionDialog.isSelect()) {
                            listOption = optionDialog.getOptionList();
                        }
                        //open
                    }
                    case "-" ->
                        initOption();
                    default -> {
                        listOption.clear();
                        listOption.add(trader.getCode());
                    }
                }
                if (editor == null) {
                    if (selectionObserver != null) {
                        selectionObserver.selected("Select", trader);
                    }
                }
            }
        }

        popup.setVisible(false);
        if (editor != null) {
            editor.stopCellEditing();
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
        String filter = textComp.getText();
        if (filter.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
            try {
                if (!containKey(e)) {
                    if (table.getRowCount() >= 0) {
                        table.setRowSelectionInterval(0, 0);
                    }
                }
            } catch (Exception ex) {
            }

        }
    }
    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String tmp1 = entry.getStringValue(0).toUpperCase().replace(" ", "");
            String tmp2 = entry.getStringValue(1).toUpperCase().replace(" ", "");
            String tmp3 = entry.getStringValue(3).toUpperCase().replace(" ", "");
            String tmp4 = entry.getStringValue(4).toUpperCase().replace(" ", "");
            String tmp5 = entry.getStringValue(4).toUpperCase().replace(" ", "");
            String text = textComp.getText().toUpperCase().replace(" ", "");
            return tmp1.startsWith(text) || tmp2.startsWith(text)
                    || tmp3.startsWith(text) || tmp4.startsWith(text) || tmp5.startsWith(text);
        }
    };

    private boolean containKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP;
    }
}
