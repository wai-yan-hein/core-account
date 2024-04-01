/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.awt.Image;
import javax.swing.Icon;

/**
 *
 * @author Lenovo
 */
public class IconUtil {

    public static final String SEARCH_ICON = "search.svg";
    public static final String FILTER_ICON_ALT = "filter_alt.svg";
    public static final String CALENDER_ICON = "calender.svg";
    public static final String LIST_ICON = "list.svg";
    public static final String CURRENCY = "currency.svg";
    public static final String USER = "user.svg";
    public static final String LOCATION = "location.svg";
    public static final String WIFI = "wifi.svg";
    public static final String WIFI_OFF = "wifi_off.svg";
    public static final String COMPANY = "company.svg";
    public static final String COMPUTER = "computer.svg";
    public static final String PRINTER = "print.svg";
    public static final String WARE_HOUSE = "warehouse.svg";
    public static final String STOCK = "stock.svg";
    public static final String GROUP = "group.svg";
    public static final Icon getIcon(String name) {
        FlatSVGIcon icon = new FlatSVGIcon("svg/" + name, 0.8f);
        icon.setColorFilter(colorFilter);
        return icon;
    }

    public static final Icon getIconSmall(String name) {
        FlatSVGIcon icon = new FlatSVGIcon("svg/" + name, 0.8f);
        icon.setColorFilter(colorFilter);
        return icon;
    }

    public static final Icon getIcon(String name, Color iconColor) {
        FlatSVGIcon icon = new FlatSVGIcon("svg/" + name, 0.8f);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> iconColor));
        return icon;
    }

    public static final Icon getIconSmall(String name, Color iconColor) {
        FlatSVGIcon icon = new FlatSVGIcon("svg/" + name, 0.6f);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> iconColor));
        return icon;
    }
    private static final FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter(color -> {
        if (Util1.DARK_MODE) {
            return Color.WHITE;
        }
        return Color.black;
    });

    public static final Icon getIcon(String name, float size) {
        FlatSVGIcon icon = new FlatSVGIcon("svg/" + name, size);
        icon.setColorFilter(colorFilter);
        return icon;
    }

    public static final Image getImage(String name) {
        FlatSVGIcon icon = new FlatSVGIcon("svg/" + name);
        icon.setColorFilter(colorFilter);
        return icon.getImage();
    }

}
