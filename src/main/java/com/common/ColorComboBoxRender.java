/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ColorComboBoxRender extends DefaultListCellRenderer {

    public ColorComboBoxRender() {
        setOpaque(true); // Make the renderer opaque
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Call the superclass method to set up default rendering
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (cellHasFocus) {
            setBackground(UIManager.getDefaults().getColor("Table.selectionBackground"));
        }
        setBackground(index % 2 == 0 ? Global.BG_COLOR : Color.WHITE);
        return this;
    }
}
