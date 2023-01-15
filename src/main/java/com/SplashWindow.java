/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class SplashWindow extends JWindow implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SplashWindow.class);
    ImageIcon imageIcon;

    // A simple little method to show a title screen in the center
    // of the screen for the amount of time given in the constructor
    public void showSplash() {
        log.info("Splash Screen Started.");
        URL url = getClass().getResource("/images/splash.png");
        // Create ImageIcon from Image
        imageIcon = new ImageIcon(url);
        // Set JWindow size from image size
        setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        // Get current screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Get x coordinate on screen for make JWindow locate at center
        int x = (screenSize.width - getSize().width) / 2;
        // Get y coordinate on screen for make JWindow locate at center
        int y = (screenSize.height - getSize().height) / 2;
        // Set new location for JWindow
        setLocation(x, y);
        // Make JWindow visible
        setVisible(true);
    }
    // Paint image onto JWindow

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(imageIcon.getImage(), 0, 0, this);
    }

    public void stopSplah() {
        log.info("Splash Screen End.");
        setVisible(false);
        dispose();
    }

    @Override
    public void run() {
        showSplash();
    }

}
