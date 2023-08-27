/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.common.Global;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ServerThread extends Thread {

    private ServerSocket server;
    private final int port;

    public ServerThread(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            while (!isInterrupted()) {
                log.info("inter : " + isInterrupted());
                try (Socket client = server.accept(); OutputStream out = client.getOutputStream()) {
                    out.write("ok\n".getBytes());
                    out.flush();
                }
                log.info("Received request to bring main window to front and give it focus.");
                if (Global.dialog != null) {
                    if (Global.dialog.isVisible()) {
                        Global.dialog.toFront();
                    }
                } else if (Global.parentForm != null) {
                    Global.parentForm.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    Global.parentForm.setVisible(true);
                    Global.parentForm.toFront();
                } else {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            log.error("Error starting server: " + e.getMessage());
        }
    }

    public void shutDown() {
        try {
            server.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            server.close();
        } catch (IOException e) {
            // Ignore
        }
    }
}
