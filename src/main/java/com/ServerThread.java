/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.common.Global;
import com.common.ui.ApplicationMainFrame;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.usermodel.ObjectMetaData;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ServerThread extends Thread {
    
    private ServerSocket server;
    private int port;
    
    public ServerThread(int port) {
        this.port = port;
    }
    
    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started, listening for connections...");
            while (!isInterrupted()) {
                try (Socket client = server.accept(); OutputStream out = client.getOutputStream()) {
                    out.write("ok\n".getBytes());
                    out.flush();
                }
                System.out.println("Received request to bring main window to front and give it focus.");
                EventQueue.invokeLater(() -> {
                    log.info("hi");
                    log.info("dialog" + Global.dialog);
                    if (Global.dialog != null) {
                        Global.dialog.toFront();
                    }
                    if (Global.parentForm != null) {
                        Global.parentForm.toFront();
                        Global.parentForm.requestFocus();
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
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
