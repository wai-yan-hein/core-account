/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import java.util.HashMap;
import javax.swing.Icon;

/**
 *
 * @author Lenovo
 */
public class IconManager {

    private static HashMap<String, Icon> hmIcon = new HashMap<>();

    public static void put(String key, Icon value) {
        hmIcon.put(key, value);
    }

    public static Icon get(String key) {
        return hmIcon.get(key);
    }
}
