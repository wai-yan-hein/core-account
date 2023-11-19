/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

/**
 *
 * @author Lenovo
 */
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

    public static void addFocusListener(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JTextField) {
                JTextField textField = (JTextField) component;
                textField.addFocusListener(fa);
            } else if (component instanceof JTextFieldDateEditor txt) {
                txt.selectAll();
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
