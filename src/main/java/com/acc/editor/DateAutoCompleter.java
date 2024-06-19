/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.editor;

import com.acc.common.DateTableModel;
import com.acc.dialog.DatePickerDialog;
import com.acc.model.DateModel;
import com.common.Global;
import com.common.IconUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public final class DateAutoCompleter implements KeyListener, SelectionObserver {

    private final JTable table = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private DateTableModel dateTableModel = new DateTableModel();
    private DateModel dateModel;
    public AbstractCellEditor editor;
    private int x = 0;
    private boolean popupOpen = true;
    private final DatePickerDialog dialog = new DatePickerDialog();
    @Setter
    private SelectionObserver observer;

    public DateAutoCompleter() {
    }

    public DateAutoCompleter(JTextComponent comp) {
        this.textComp = comp;
        initDateDialog();
        setTodayDate();
        setData();
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, IconUtil.getIcon(IconUtil.CALENDER_ICON));
        textComp.setFont(Global.textFont);
        table.setModel(dateTableModel);
        table.setFont(Global.textFont); // NOI18N
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.setRowHeight(Global.tblRowHeight);
        table.setDefaultRenderer(Object.class, new TableCellRender());
        table.setSelectionForeground(Color.white);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);//Code

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
        popup.setPopupSize(200, 500);

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
                textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
                popupOpen = false;
                if (!popupOpen) {
                    if (editor != null) {
                        editor.stopCellEditing();
                    }
                }

            }
        });

        table.setRequestFocusEnabled(false);

        if (Global.listDate != null) {
            if (!Global.listDate.isEmpty()) {
                table.setRowSelectionInterval(0, 0);
            }
        }
    }
    private void setData(){
      if(Global.listDate==null){
          Global.listDate = generateDate(Util1.toDateStr(Global.startDate, Global.dateFormat, "yyyy-MM-dd"));
      }
      dateTableModel.setListDate(Global.listDate);
    }

    public void mouseSelect() {
        int selectRow = table.getSelectedRow();
        if (selectRow != -1) {
            int row = table.convertRowIndexToModel(selectRow);
            dateModel = dateTableModel.getDate(row);
            ((JTextField) textComp).setText(dateModel.getDescription());
            generateDate(dateModel);
        }
        popupOpen = false;
        popup.setVisible(false);
        if (editor != null) {
            editor.stopCellEditing();

        }
    }

    private void initDateDialog() {
        dialog.setIconImage(IconUtil.getImage(IconUtil.CALENDER_ICON));
        dialog.setLocationRelativeTo(null);
    }

    private void generateDate(DateModel date) {
        if (date.getDescription().equals("Custom")) {
            closePopup();
            dialog.setVisible(true);
            String startDate = dialog.getStartDate();
            String endDate = dialog.getEndDate();
            date.setStartDate(startDate);
            date.setEndDate(endDate);
            this.textComp.setText(
                    String.format("%s%s%s", Util1.toDateStr(startDate, "yyyy-MM-dd", Global.dateFormat),
                            " to ", Util1.toDateStr(endDate, "yyyy-MM-dd", Global.dateFormat)));
        }
        if (observer != null) {
            observer.selected("Date", "Date");
        }
    }

    private final Action acceptAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mouseSelect();
        }
    };
    private final DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {

        }

        @Override
        public void removeUpdate(DocumentEvent e) {

        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };

    public void closePopup() {
        popup.setVisible(false);
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
                        x = textComp.getCaretPosition();
                    }

                    popup.show(textComp, x, textComp.getHeight());
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
            DateAutoCompleter completer = (DateAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectNextPossibleValue();
                } else {
                    popupOpen = true;
                    completer.showPopup();
                }
            }
        }
    };
    Action upAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            DateAutoCompleter completer = (DateAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            DateAutoCompleter completer = (DateAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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

    public DateModel getDateModel() {
        return dateModel;
    }

    public void setDateModel(DateModel dateModel) {
        this.dateModel = dateModel;
        textComp.setText(dateModel == null ? null : dateModel.getDescription());
    }

    public void setTodayDate() {
        LocalDate todayDate = LocalDate.now();
        DateModel today = new DateModel();
        String todayDateStr = todayDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        today.setDescription("Today");
        today.setMonth(todayDate.getMonthValue());
        today.setYear(todayDate.getYear());
        today.setStartDate(todayDateStr);
        today.setEndDate(todayDateStr);
        setDateModel(today);
    }
     public static List<DateModel> generateDate(String fromDate) {
        List<DateModel> listFix = new ArrayList<>();
        List<DateModel> list = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(fromDate).withDayOfMonth(1);
        LocalDate todayDate = LocalDate.now();
        while (!startDate.isAfter(todayDate.plusMonths(0))) {
            String monthName = startDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDateStr = startDate.withDayOfMonth(startDate.lengthOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int month = startDate.getMonthValue();
            int year = startDate.getYear();
            DateModel m = new DateModel();
            m.setMonthName(monthName);
            m.setStartDate(startDateStr);
            m.setEndDate(endDateStr);
            m.setMonth(month);
            m.setYear(year);
            m.setDescription(monthName + "/" + year);
            list.add(m);
            startDate = startDate.plusMonths(1);
        }
        String todayDateStr = todayDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //today
        DateModel today = new DateModel();
        today.setDescription("Today");
        today.setMonth(todayDate.getMonthValue());
        today.setYear(todayDate.getYear());
        today.setStartDate(todayDateStr);
        today.setEndDate(todayDateStr);
        listFix.add(0, today);
        //yesterday
        DateModel yesterday = new DateModel();
        LocalDate yesDate = LocalDate.now().minusDays(1);
        String yesterdayStr = yesDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        yesterday.setDescription("Yesterday");
        yesterday.setMonth(yesDate.getMonthValue());
        yesterday.setYear(yesDate.getYear());
        yesterday.setStartDate(yesterdayStr);
        yesterday.setEndDate(yesterdayStr);
        listFix.add(1, yesterday);
        DateModel custom = new DateModel();
        custom.setDescription("Custom");
        listFix.add(2, custom);
        List<DateModel> combinedList = new ArrayList<>(listFix);
        combinedList.addAll(list.reversed());
        return combinedList;
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
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            if (source.toString().equals("DatePickerDialog")) {
                ((JTextField) textComp).setText(selectObj.toString());
                if (observer != null) {
                    observer.selected("Date", selectObj);
                    popup.setVisible(false);
                    if (editor != null) {
                        editor.stopCellEditing();

                    }
                }

            }
        }
    }

    public void clear() {
        setTodayDate();
    }

}
