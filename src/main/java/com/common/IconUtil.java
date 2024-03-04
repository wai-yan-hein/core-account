/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import javax.swing.Icon;

/**
 *
 * @author Lenovo
 */
public class IconUtil {

    public static final String SEARCH_ICON = "icon.search";

    public static final Icon getIcon(String name) {
        FlatSVGIcon icon = new FlatSVGIcon("svg/" + name);
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.white, Color.black);
        icon.setColorFilter(f);
        return icon;
    }
}
