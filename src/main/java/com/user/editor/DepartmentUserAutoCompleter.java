/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.editor;

import com.user.model.Branch;
import com.common.Global;
import com.common.IconUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.formdev.flatlaf.FlatClientProperties;
import com.user.common.BranchTableModel;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.BorderFactory;
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
 * @author wai yan
 */
public class DepartmentUserAutoCompleter implements KeyListener, SelectionObserver {

    private final JTable table = new JTable();
    private final JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER";
    private BranchTableModel departmentTableModel = new BranchTableModel();
    private Branch department;
    public AbstractCellEditor editor;
    private TableRowSorter<TableModel> sorter;
    private int x = 0;
    private int y = 0;
    private SelectionObserver observer;
    private boolean filter;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setListDepartment(List<Branch> list) {
        if (filter) {
            Branch sb = allDepartment();
            list.add(0, sb);
            setDepartment(sb);
        }
        departmentTableModel.setListDepartment(list);
        if (!list.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    private Branch allDepartment() {
        return new Branch(0, "All");
    }

    public DepartmentUserAutoCompleter() {
    }

    public DepartmentUserAutoCompleter(JTextComponent comp, AbstractCellEditor editor, boolean filter) {
        this.textComp = comp;
        this.editor = editor;
        this.filter = filter;
        setDepartment(allDepartment());
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, IconUtil.getIcon(IconUtil.LOCATION));
        textComp.setFont(Global.textFont);
        textComp.addKeyListener(this);
        textComp.getDocument().addDocumentListener(documentListener);
        table.setModel(departmentTableModel);
        table.setSize(50, 50);
        table.setFont(Global.textFont); // NOI18N
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.setDefaultRenderer(Object.class, new TableCellRender());
        table.setRowHeight(Global.tblRowHeight);
        table.setSelectionForeground(Color.WHITE);
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(10);//Code
        table.getColumnModel().getColumn(1).setPreferredWidth(200);//Code
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

        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.setPopupSize(400, 200);

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

    public Branch getDepartment() {
        return department;
    }

    public final void setDepartment(Branch department) {
        this.department = department;
        if (this.department != null) {
            this.textComp.setText(department.getDeptName());
        } else {
            this.textComp.setText(null);
        }
    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            department = departmentTableModel.getDepartment(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            textComp.setText(department.getDeptName());
            if (editor == null) {
                if (observer != null) {
                    observer.selected("Department", department.getKey().getDeptId());
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
            DepartmentUserAutoCompleter completer = (DepartmentUserAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            DepartmentUserAutoCompleter completer = (DepartmentUserAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            DepartmentUserAutoCompleter completer = (DepartmentUserAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
