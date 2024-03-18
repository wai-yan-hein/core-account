package com.common;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class OfflineOptionPane extends JOptionPane {

    public OfflineOptionPane() {
        super("Internet Offline. Try Again?", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
        setupKeyBindings();
        setInitialValue(JOptionPane.NO_OPTION); // Set initial value to NO_OPTION
    }

    private void setupKeyBindings() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValue(JOptionPane.YES_OPTION);
            }
        });
    }

}
