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

    public static final String SEARCH_ICON = "search.svg";
    public static final String FILTER_ICON_ALT = "filter_alt.svg";
    public static final String CALENDER = "calender.svg";
    public static final Icon getIcon(String name) {
        FlatSVGIcon icon = new FlatSVGIcon("svg/" + name);
        icon.setColorFilter(colorFilter);
        return icon;
    }
    private static final FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter(color -> {
        if (Util1.DARK_MODE) {
            return Color.WHITE;
        }
        return Color.black;
    });
}
