/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.editor;

import com.acc.common.COATableModel;
import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.IconUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Lenovo
 */
public final class COAAutoCompleter implements KeyListener {

    private JTable table = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private COATableModel cOATableModel = new COATableModel();
    private ChartOfAccount coa;
    public AbstractCellEditor editor;
    private TableRowSorter<TableModel> sorter;
    private int x = 0;
    private int y = 0;
    boolean popupOpen = false;
    private SelectionObserver observer;
    private boolean filter;
    private List<ChartOfAccount> listCOA;

    public void setListCOA(List<ChartOfAccount> listCOA) {
        if (filter) {
            ChartOfAccount c = getAll();
            listCOA.add(0, c);
            setCoa(c);
        }
        cOATableModel.setListCOA(listCOA);
        if (!listCOA.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }
        this.listCOA = listCOA;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    private ChartOfAccount getAll() {
        return new ChartOfAccount(new COAKey("-", Global.compCode), "All");
    }

    public COAAutoCompleter() {
    }

    public COAAutoCompleter(JTextComponent comp, AbstractCellEditor editor, boolean filter) {
        this.textComp = comp;
        this.editor = editor;
        this.filter = filter;
        if (this.filter) {
            setCoa(getAll());
        }
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, IconUtil.getIcon(IconUtil.FILTER_ICON_ALT));
        textComp.setFont(Global.textFont);
        table.setModel(cOATableModel);
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.setFont(Global.textFont); // NOI18N
        table.setRowHeight(Global.tblRowHeight);
        table.setDefaultRenderer(Object.class, new TableCellRender());
        table.setSelectionForeground(Color.WHITE);
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(10);//Code
        table.getColumnModel().getColumn(1).setPreferredWidth(200);//Name
        table.setFocusable(false);
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

    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            coa = cOATableModel.getCOA(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            ((JTextField) textComp).setText(coa.getCoaNameEng());
            if (editor == null) {
                if (observer != null) {
                    observer.selected("Selected", coa.getKey().getCoaCode());
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
                    if (x == 0) {
                        x = textComp.getWidth();
                        y = textComp.getHeight();
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
            COAAutoCompleter completer = (COAAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
    Action upAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            COAAutoCompleter completer = (COAAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            COAAutoCompleter completer = (COAAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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

    public ChartOfAccount getCOA() {
        return coa;
    }

    public void setCoa(ChartOfAccount coa) {
        this.coa = coa;
        if (this.coa != null) {
            textComp.setText(coa.getCoaNameEng());
        } else {
            textComp.setText(null);
        }
    }

    /*
     * KeyListener implementation
     */
    /**
     * Handle the key typed event from the text field.
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Handle the key-pressed event from the text field.
     */
    @Override
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Handle the key-released event from the text field.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        String str = textComp.getText();
        if (str.length() == 0) {
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
