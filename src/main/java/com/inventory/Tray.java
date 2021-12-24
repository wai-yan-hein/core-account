/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import javax.swing.ImageIcon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Slf4j
@Component
public class Tray {

    private TrayIcon trayIcon;

    public void startup() {
        if (SystemTray.isSupported()) {
            log.info("Tray started.");
            SystemTray tray = SystemTray.getSystemTray();
            ImageIcon icon = new ImageIcon("images/icon.png");
            PopupMenu menu = new PopupMenu();
            MenuItem closeItem = new MenuItem("Exit");
            closeItem.addActionListener(e -> System.exit(0));
            menu.add(closeItem);
            trayIcon = new TrayIcon(icon.getImage(), "Core Inventory", menu);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
                trayIcon.displayMessage("Core Value Notification.", "Welcome.", MessageType.INFO);
            } catch (AWTException e) {
                log.error(String.format("startup: %s", e.getMessage()));
            }
        }
    }

    public void showMessage(String message) {
        trayIcon.displayMessage("Core Value Notification.", message, MessageType.INFO);
    }
}
