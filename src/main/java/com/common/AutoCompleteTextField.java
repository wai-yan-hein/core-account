/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JTextField;

/**
 *
 * @author Lenovo
 */
public class AutoCompleteTextField extends JTextField {

    private Icon prefixIcon;
    private Icon suffixIcon;

    public Icon getPrefixIcon() {
        return prefixIcon;
    }

    public void setPrefixIcon(Icon prefixIcon) {
        this.prefixIcon = prefixIcon;
    }

    public Icon getSuffixIcon() {
        return suffixIcon;
    }

    public void setSuffixIcon(Icon suffixIcon) {
        this.suffixIcon = suffixIcon;
        initBorder();
    }

    public AutoCompleteTextField() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paint(g);
        paintIcon(g);
    }

    private void initBorder() {
        int left = 5;
        int right = 5;
        if (prefixIcon != null) {
            left = prefixIcon.getIconWidth();
        }
        if (suffixIcon != null) {
            right = suffixIcon.getIconWidth();
        }
        BorderFactory.createEmptyBorder(5, left, 5, right);
    }

    private void paintIcon(Graphics g) {
        /*Graphics2D g2 = (Graphics2D) g;
        if (prefixIcon != null) {
        Image prefix = ((ImageIcon) prefixIcon).getImage();
        int y = (getHeight() - prefixIcon.getIconHeight()) / 2;
        g2.drawImage(prefix, 0, y, this);
        }
        if (suffixIcon != null) {
        Image prefix = ((ImageIcon) suffixIcon).getImage();
        int y = (getHeight() - suffixIcon.getIconHeight()) / 2;
        g2.drawImage(prefix, getWidth() - suffixIcon.getIconWidth(), y, this);
        }*/
    }

}
