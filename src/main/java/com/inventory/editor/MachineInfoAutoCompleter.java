/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.editor;

import com.common.Global;
import com.common.IconUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.formdev.flatlaf.FlatClientProperties;
import com.user.model.MachineInfo;
import com.inventory.ui.common.MachineInfoCompleterTableModel;
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
import javax.swing.BorderFactory;
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
import org.slf4j.LoggerFactory;

/**
 *
 * @author wai yan
 */
public class MachineInfoAutoCompleter implements KeyListener, SelectionObserver {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MachineInfoAutoCompleter.class);
    private final JTable table = new JTable();
    private final JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER";
    private MachineInfoCompleterTableModel machTableModel;
    private MachineInfo machInto;
    public AbstractCellEditor editor;
    private TableRowSorter<TableModel> sorter;
    private int x = 0;
    private int y = 0;
    private SelectionObserver selectionObserver;

    public void setObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public MachineInfoAutoCompleter() {
    }

    public MachineInfoAutoCompleter(JTextComponent comp, List<MachineInfo> list,
            AbstractCellEditor editor) {
        this.textComp = comp;
        this.editor = editor;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, IconUtil.getIcon(IconUtil.FILTER_ICON_ALT));
        textComp.setFont(Global.textFont);
        textComp.addKeyListener(this);

        machTableModel = new MachineInfoCompleterTableModel(list);
        table.setModel(machTableModel);
        table.setSize(50, 50);
        table.setTableHeader(null);
        table.setFont(Global.lableFont); // NOI18N
        table.setRowHeight(Global.tblRowHeight);
        table.setDefaultRenderer(Object.class, new TableCellRender());
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.setSelectionBackground(UIManager.getDefaults().getColor("Table.selectionBackground"));
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);//Code

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    mouseSelect();
                }
            }
        });

        scroll.getVerticalScrollBar().setFocusable(false);
        scroll.getHorizontalScrollBar().setFocusable(false);

        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.setPopupSize(170, 300);

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

        if (list.size() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            machInto = machTableModel.getMachineInfo(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            textComp.setText(machInto.getMachineName());
            if (editor == null) {
                if (selectionObserver != null) {
                    selectionObserver.selected("MachineInfo", machInto.getMachineName());
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
            MachineInfoAutoCompleter completer = (MachineInfoAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            MachineInfoAutoCompleter completer = (MachineInfoAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            MachineInfoAutoCompleter completer = (MachineInfoAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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

    public MachineInfo getManchineInfo() {
        return machInto;
    }

    public void setMachineInfo(MachineInfo mach) {
        this.machInto = mach;
        this.textComp.setText(machInto.getMachineName());
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

    @Override
    public void keyReleased(KeyEvent e) {
        String filter = textComp.getText();

        if (filter.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            //String value = Util1.getPropValue("system.iac.filter");

            if ("N".equals("Y")) {
                sorter.setRowFilter(RowFilter.regexFilter(filter));
            } else {
                sorter.setRowFilter(startsWithFilter);
            }
            try {
                if (e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_UP) {
                    table.setRowSelectionInterval(0, 0);
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
    }
    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            //for (int i = entry.getValueCount() - 1; i >= 0; i--) {
            /*
             * if (NumberUtil.isNumber(textComp.getText())) { if
             * (entry.getStringValue(0).toUpperCase().startsWith(
             * textComp.getText().toUpperCase())) { return true; } } else {
             *
             * if (entry.getStringValue(1).toUpperCase().contains(
             * textComp.getText().toUpperCase())) { return true; } else if
             * (entry.getStringValue(2).toUpperCase().contains(
             * textComp.getText().toUpperCase())) { return true; }
             }
             */

            String tmp1 = entry.getStringValue(0).toUpperCase();
            String tmp2 = entry.getStringValue(1).toUpperCase();
            String tmp3 = entry.getStringValue(3).toUpperCase();
            String tmp4 = entry.getStringValue(4).toUpperCase();
            String text = textComp.getText().toUpperCase();

            return tmp1.startsWith(text) || tmp2.startsWith(text) || tmp3.startsWith(text) || tmp4.startsWith(text);
        }
    };

}
