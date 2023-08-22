/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.common.Global;
import com.ui.LoginDialog;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class Tray {

    private TrayIcon trayIcon;
    private LoginDialog loginDialog;
    private SystemTray tray;

    public void setLoginDialog(LoginDialog loginDialog) {
        this.loginDialog = loginDialog;
    }

    public SystemTray getTray() {
        return tray;
    }

    public void setTray(SystemTray tray) {
        this.tray = tray;
    }

    public void startup(Image icon) {
        if (SystemTray.isSupported()) {
            log.info("Tray started.");
            tray = SystemTray.getSystemTray();
            PopupMenu menu = new PopupMenu();
            MenuItem closeItem = new MenuItem("Exit");
            closeItem.addActionListener((e) -> {
                tray.remove(trayIcon);
                System.exit(0);
            });
            menu.add(closeItem);
            trayIcon = new TrayIcon(icon, "Core Account", menu);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() >= 1) {
                        openMF();
                    }
                }
            });
            try {
                tray.add(trayIcon);
                trayIcon.displayMessage("Core Value Notification.", "Welcome.", MessageType.INFO);
            } catch (AWTException e) {
                log.error(String.format("startup: %s", e.getMessage()));
            }
        }
    }

    public void openMF() {
        if (loginDialog.isVisible()) {
            loginDialog.toFront();
            loginDialog.focus();
        } else {
            if (Global.parentForm != null) {
                Global.parentForm.setExtendedState(JFrame.MAXIMIZED_BOTH);
                Global.parentForm.setVisible(true);
                Global.parentForm.toFront();
            }
        }
    }

    public void removeTray() {
        tray.remove(trayIcon);
        if (Global.parentForm != null) {
            Global.parentForm.setVisible(false);
        }
    }

    public void showMessage(String message) {
        trayIcon.displayMessage("Core Value Software Solution.", message, MessageType.INFO);
    }
}
