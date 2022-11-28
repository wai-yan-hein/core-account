/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.editor;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.inventory.model.UnitRelation;
import com.inventory.ui.setup.dialog.common.RelationTableModel;
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
import javax.swing.RowFilter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public final class UnitRelationAutoCompleter implements KeyListener {

    private final JTable table = new JTable();
    private final JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private RelationTableModel relationTableModel;
    private UnitRelation relation;
    public AbstractCellEditor editor;
    private TableRowSorter<TableModel> sorter;
    private int x = 0;
    private int y = 0;
    private List<String> listOption = new ArrayList<>();
    private SelectionObserver observer;
    private List<UnitRelation> listRelation;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public List<String> getListOption() {
        return listOption;
    }

    public void setListOption(List<String> listOption) {
        this.listOption = listOption;
    }

    public UnitRelationAutoCompleter() {
    }

    public UnitRelationAutoCompleter(JTextComponent comp, List<UnitRelation> list,
            AbstractCellEditor editor, boolean filter, boolean custom) {
        this.textComp = comp;
        this.editor = editor;
        this.listRelation = list;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.setFont(Global.textFont);
        textComp.addKeyListener(this);
        relationTableModel = new RelationTableModel();
        relationTableModel.setListRelation(listRelation);
        table.setModel(relationTableModel);
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.setFont(Global.textFont); // NOI18N
        table.setRowHeight(Global.tblRowHeight);
        table.setSelectionForeground(Color.WHITE);
        table.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);//Code

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

        popup.setPopupSize(300, 300);

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

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            relation = relationTableModel.getRelation(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            textComp.setText(relation.getRelName());
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
            UnitRelationAutoCompleter completer = (UnitRelationAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            UnitRelationAutoCompleter completer = (UnitRelationAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            UnitRelationAutoCompleter completer = (UnitRelationAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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

    public UnitRelation getRelation() {
        return relation;

    }

    public void setRelation(UnitRelation relation) {
        this.relation = relation;
        textComp.setText(relation == null ? null : relation.getRelName());
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
        if (e.getKeyCode() != 10) {
            showPopup();
        }
    }

    /**
     * Handle the key-released event from the text field.
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
