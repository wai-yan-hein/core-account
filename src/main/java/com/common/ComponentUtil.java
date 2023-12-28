/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ComponentUtil {

    public static void setComponentHierarchyEnabled(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof JPanel panel) {
            Component[] childComponents = panel.getComponents();
            for (Component childComponent : childComponents) {
                setComponentHierarchyEnabled(childComponent, enabled);
            }
        }
        if (component instanceof JToolBar tb) {
            Component[] childComponents = tb.getComponents();
            for (Component childComponent : childComponents) {
                setComponentHierarchyEnabled(childComponent, enabled);
            }
        }
        if (component instanceof JMenuBar tb) {
            Component[] childComponents = tb.getComponents();
            for (Component childComponent : childComponents) {
                setComponentHierarchyEnabled(childComponent, enabled);
            }
        }

    }

    public static void addFocusListener(Component component) {
        if (component instanceof JTextField textField) {
            textField.addFocusListener(fa); // replace 'yourFocusListener' with your actual focus listener
        } else if (component instanceof JTextFieldDateEditor txt) {
            txt.addFocusListener(fa); // replace 'yourFocusListener' with your actual focus listener
            txt.selectAll();
        } else if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                addFocusListener(child);
            }
        }
    }

    public static void setTextProperty(Component component) {
        switch (component) {
            case JDateChooser dateChooser ->
                dateChooser.setFont(Global.textFont);
            case JFormattedTextField textField -> {
                textField.setFont(Global.amtFont);
                textField.setHorizontalAlignment(JTextField.RIGHT);
                textField.setFormatterFactory(ProUtil.getDecimalFormatter());
            }
            case Container container -> {
                for (Component child : container.getComponents()) {
                    setTextProperty(child);
                }
            }
            default -> {
            }
        }
    }

    public static void addFocusListener(JScrollPane pane) {
        for (Component component : pane.getComponents()) {
            if (component instanceof JPanel p) {
                addFocusListener(p);
            }
        }
    }

    public static void enableForm(Container container, boolean status) {
        for (Component component : container.getComponents()) {
            component.setEnabled(status);
            if (component instanceof Container) {
                enableForm((Container) component, status);
            }
        }
    }

    public static final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            } else if (e.getSource() instanceof JTextFieldDateEditor txt) {
                txt.selectAll();
            }
        }
    };

    public static boolean checkClear(String status) {
        if (status.equals("NEW")) {
            YNOptionPane optionPane = new YNOptionPane("Do your want new voucher?", JOptionPane.WARNING_MESSAGE);
            JDialog dialog = optionPane.createDialog("New Voucher Message");
            dialog.setVisible(true);
            int yn = (int) optionPane.getValue();
            return yn == JOptionPane.YES_OPTION;
        }
        return true;
    }

}
