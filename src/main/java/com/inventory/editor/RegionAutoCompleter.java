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
import com.inventory.model.Region;
import com.inventory.ui.setup.dialog.OptionDialog;
import com.inventory.ui.setup.dialog.common.RegionTableModel;
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
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public final class RegionAutoCompleter implements KeyListener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RegionAutoCompleter.class);
    private final JTable table = new JTable();
    private final JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private RegionTableModel regionTableModel;
    private Region region;
    public AbstractCellEditor editor;
    private TableRowSorter<TableModel> sorter;
    private int x = 0;
    private int y = 0;
    private boolean popupOpen = false;
    private SelectionObserver selectionObserver;
    private List<String> listOption = new ArrayList<>();
    private OptionDialog optionDialog;
    private List<Region> listRegion;
    private boolean custom;

    public List<String> getListOption() {
        return listOption;
    }

    public void setListOption(List<String> listOption) {
        this.listOption = listOption;
    }

    private void initOption() {
        listOption.clear();
        listRegion.forEach(t -> {
            listOption.add(t.getKey().getRegCode());
        });
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public RegionAutoCompleter() {
    }

    public RegionAutoCompleter(JTextComponent comp, List<Region> list,
            AbstractCellEditor editor, boolean filter, boolean custom) {
        this.textComp = comp;
        this.editor = editor;
        this.listRegion = list;
        this.textComp.addKeyListener(this);
        this.custom = custom;
        initOption();
        if (filter) {
            Region reg = new Region("-", "All");
            list = new ArrayList<>(list);
            list.add(0, reg);
            setRegion(reg);
        }
        if (custom) {
            list = new ArrayList<>(list);
            list.add(1, new Region("C", "Custom"));
        }
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.setFont(Global.textFont);
        regionTableModel = new RegionTableModel(list);
        table.setModel(regionTableModel);
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.setFont(Global.textFont); // NOI18N
        table.setRowHeight(Global.tblRowHeight);
        table.setSelectionBackground(UIManager.getDefaults().getColor("Table.selectionBackground"));
        table.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(5);//Code
        table.getColumnModel().getColumn(1).setPreferredWidth(200);//Name

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

        if (list != null) {
            if (!list.isEmpty()) {
                table.setRowSelectionInterval(0, 0);
            }
        }
    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            region = regionTableModel.getRegion(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            textComp.setText(region.getRegionName());
            if (custom) {
                switch (region.getKey().getRegCode()) {
                    case "C" -> {
                        optionDialog = new OptionDialog(Global.parentForm, "Region");
                        List<OptionModel> listOP = new ArrayList<>();
                        listRegion.forEach(t -> {
                            listOP.add(new OptionModel(t.getKey().getRegCode(), t.getRegionName()));
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
                        listOption.add(region.getKey().getRegCode());
                    }
                }
            }
            if (editor == null) {
                if (selectionObserver != null) {
                    selectionObserver.selected("RegionList", region.getKey().getRegCode());
                }
            }
        }

        popup.setVisible(false);
        popupOpen = false;
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
        popupOpen = false;
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
               popupOpen = false;

            } else {
                popup.setVisible(false);
                popupOpen = false;
            }
        }
        textComp.requestFocus();
    }
    Action showAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            RegionAutoCompleter completer = (RegionAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            RegionAutoCompleter completer = (RegionAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            RegionAutoCompleter completer = (RegionAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
        this.textComp.setText(this.region == null ? null : this.region.getRegionName());
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
        if (e.getKeyCode() != 10) {
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
