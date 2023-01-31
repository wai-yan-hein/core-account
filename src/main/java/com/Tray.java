/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.common.ui.ApplicationMainFrame;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Slf4j
@Component
public class Tray {

    private TrayIcon trayIcon;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private SystemTray tray;

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
        mainFrame.setVisible(true);
        mainFrame.toFront();
        mainFrame.requestFocus();
    }

    public void removeTray() {
        mainFrame.setVisible(false);
        tray.remove(trayIcon);
    }

    public void showMessage(String message) {
        trayIcon.displayMessage("Core Value Notification.", message, MessageType.INFO);
    }
}
