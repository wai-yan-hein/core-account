/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.editor;

import com.acc.common.DepartmentTableModel;
import com.acc.model.DepartmentA;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.OptionModel;
import com.inventory.ui.setup.dialog.OptionDialog;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
public final class DepartmentAutoCompleter implements KeyListener {

    private JTable table = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private DepartmentTableModel depTableModel = new DepartmentTableModel();
    private DepartmentA department;
    public AbstractCellEditor editor;
    private TableRowSorter<TableModel> sorter;
    private int x = 0;
    private int y = 0;
    private boolean popupOpen = false;
    private boolean custom = false;
    private List<String> listOption = new ArrayList<>();
    private OptionDialog optionDialog;
    private List<DepartmentA> listDepartment;
    private SelectionObserver observer;
    private boolean filter;

    public List<String> getListOption() {
        return listOption;
    }

    public void setListOption(List<String> listOption) {
        this.listOption = listOption;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    private void initOption() {
        listOption.clear();
        listDepartment.forEach(t -> {
            if (!Util1.isNullOrEmpty(t.getKey().getCompCode())) {
                listOption.add(t.getKey().getDeptCode());
            }
        });
    }

    public void setListDepartment(List<DepartmentA> list) {
        if (filter) {
            DepartmentA dep = new DepartmentA("-", "All");
            list.add(0, dep);
            setDepartment(dep);
        }
        if (custom) {
            list.add(1, new DepartmentA("C", "Custom"));
        }
        depTableModel.setListDep(list);
        if (!list.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }
        this.listDepartment = list;
        initOption();
    }

    public DepartmentAutoCompleter() {
    }

    public DepartmentAutoCompleter(JTextComponent comp,
            AbstractCellEditor editor, boolean filter, boolean custom) {
        this.textComp = comp;
        this.editor = editor;
        this.custom = custom;
        this.filter = filter;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.setFont(Global.textFont);
        depTableModel.setTable(table);
        table.setModel(depTableModel);
        table.getTableHeader().setFont(Global.textFont);
        table.setFont(Global.lableFont); // NOI18N
        table.setRowHeight(Global.tblRowHeight);
        table.getTableHeader().setFont(Global.lableFont);
        table.setDefaultRenderer(Object.class, new TableCellRender());
        table.setSelectionForeground(Color.WHITE);
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);//Code
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
        popup.setPopupSize(400, 200);

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
        }

        textComp.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                JComponent.WHEN_FOCUSED);
        textComp.registerKeyboardAction(hidePopupAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);
        textComp.registerKeyboardAction(unFoucsTable, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
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
            department = depTableModel.getDepatment(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            textComp.setText(department.getDeptName());
            if (custom) {
                switch (department.getKey().getDeptCode()) {
                    case "C" -> {
                        optionDialog = new OptionDialog(Global.parentForm, "Stock Category");
                        List<OptionModel> listOP = new ArrayList<>();
                        listDepartment.forEach(t -> {
                            listOP.add(new OptionModel(t.getKey().getDeptCode(), t.getDeptName()));
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
                        listOption.add(department.getKey().getDeptCode());
                    }
                }
            }
            if (observer != null) {
                observer.selected("Department", department.getKey().getDeptCode());
            }
            popup.setVisible(false);
            popupOpen = false;
            if (editor != null) {
                editor.stopCellEditing();
            }
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
            DepartmentAutoCompleter completer = (DepartmentAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            DepartmentAutoCompleter completer = (DepartmentAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            DepartmentAutoCompleter completer = (DepartmentAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                completer.popup.setVisible(false);
                popupOpen = false;
            }
        }
    };
    Action unFoucsTable = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.info("UnFoucs table...");
            table.requestFocus(false);
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

    public DepartmentA getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentA department) {
        this.department = department;
        if (department != null) {
            this.textComp.setText(this.department.getDeptName());
        } else {
            this.textComp.setText(null);
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
