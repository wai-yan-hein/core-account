/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

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
        for (int i = 0; i < entry.getValueCount(); i++) {
            String value =entry.getStringValue(i);
            if (!Util1.isNullOrEmpty(value)) {
                if (value.toUpperCase().startsWith(jtf.getText().toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}
