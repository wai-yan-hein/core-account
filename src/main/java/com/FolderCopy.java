/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import java.io.File;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class FolderCopy {

    public static void main(String[] args) {
        String remoteHost = "localhost";
        String remoteUsername = "username";
        String remotePassword = "password";
        String sourceFolder = "/path/to/source/folder";
        String destinationFolder = System.getProperty("user.dir");
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(remoteHost);
            ftpClient.login(remoteUsername, remotePassword);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            copyFolder(ftpClient, sourceFolder, destinationFolder);

            ftpClient.logout();
            ftpClient.disconnect();

            System.out.println("Folder copied successfully.");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private static void copyFolder(FTPClient ftpClient, String sourceFolder, String destinationFolder)
            throws IOException {
        ftpClient.changeWorkingDirectory(sourceFolder);
        FTPFile[] files = ftpClient.listFiles();

        for (FTPFile file : files) {
            if (file.isDirectory()) {
                String subSourceFolder = sourceFolder + "/" + file.getName();
                String subDestinationFolder = destinationFolder + "/" + file.getName();
                new File(subDestinationFolder).mkdirs();
                copyFolder(ftpClient, subSourceFolder, subDestinationFolder);
            } else {
                String sourceFile = sourceFolder + "/" + file.getName();
                String destinationFile = destinationFolder + "/" + file.getName();
                try (OutputStream outputStream = new FileOutputStream(destinationFile)) {
                    ftpClient.retrieveFile(sourceFile, outputStream);
                }
            }
        }
    }
}
