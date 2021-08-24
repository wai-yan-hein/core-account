/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.common;

import javax.swing.JTextField;
import javax.swing.RowFilter;

/**
 *
 * @author WSwe
 */
public class StartWithRowFilter extends RowFilter<Object, Object> {

    private final JTextField jtf;

    public StartWithRowFilter(JTextField jtf) {
        this.jtf = jtf;
    }

    @Override
    public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
        for (int i = 0; i < entry.getValueCount(); i++) {
            if (entry.getStringValue(i) != null) {
                if (entry.getStringValue(i).toUpperCase().startsWith(
                        jtf.getText().toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}
