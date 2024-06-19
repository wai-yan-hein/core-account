/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import java.text.DecimalFormat;
import javax.swing.RowFilter;
import javax.swing.text.JTextComponent;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author WSwe
 */
@Slf4j
public class StartWithRowFilter extends RowFilter<Object, Object> {

    private final JTextComponent jtf;

    public StartWithRowFilter(JTextComponent jtf) {
        this.jtf = jtf;
    }

    @Override
    public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
        DecimalFormat f = new DecimalFormat("#");
        for (int i = 0; i < entry.getValueCount(); i++) {
            Object valueObj = entry.getValue(i);
            String text = replaceAll(jtf.getText().toLowerCase());
            String value = "";
            if (valueObj instanceof Double d) {
                String v = f.format(d);
                value = replaceAll(Util1.getString(v));
            } else if (valueObj instanceof String s) {
                value = replaceAll(s.toLowerCase());
            }
            if (!Util1.isNullOrEmpty(value)) {
                if (value.startsWith(text)) {
                    return true;
                } else if (value.contains(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String replaceAll(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s", "");
    }
}
