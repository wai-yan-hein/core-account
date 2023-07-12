/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import java.awt.Component;
import javax.swing.JPanel;
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
    }
}
