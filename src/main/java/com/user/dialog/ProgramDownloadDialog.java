/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.user.dialog;

import com.common.Global;
import com.common.Util1;
import com.repo.UserRepo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ProgramDownloadDialog extends javax.swing.JDialog {

    private UserRepo userRepo;
    private TaskScheduler taskScheduler;
    private final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * Creates new form ProgramDownloadDialog
     *
     * @param frame
     */
    public ProgramDownloadDialog(JFrame frame) {
        super(frame, false);
        initComponents();
    }

    public void start() {
        String program = "core-account.jar";
        taskScheduler.scheduleAtFixedRate(() -> {
            userRepo.getUpdatedProgramDate(program).doOnSuccess((t) -> {
                log.info("updatedDate : " + t);
                String updatedDate = t.replace("\"", "");
                String localTimeStr = getProgramDateTime(program);
                if (localTimeStr != null) {
                    log.info("localDate : " + localTimeStr);
                    if (Util1.compareDate(localTimeStr, updatedDate, dateFormat)) {
                        log.info("program need to update.");
                        userRepo.downloadProgram(program).subscribe((byteArray) -> {
                            String filePath = "core-account.jar"; // File path where you want to save the byte array
                            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                                log.info("byteArray : "+byteArray.length);
                                outputStream.write(byteArray);
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                LocalDateTime customDateTime = LocalDateTime.parse(updatedDate, formatter);
                                // Convert LocalDateTime to milliseconds since the epocs
                                long timestamp = customDateTime.toEpochSecond(java.time.ZoneOffset.UTC) * 1000; // Convert to milliseconds
                                log.info("timestamp : "+timestamp);
                                FileTime customTimestamp = FileTime.fromMillis(timestamp);
                                // Set the modification time for the saved file
                                Path path = Path.of(filePath);
                                Files.setLastModifiedTime(path, customTimestamp);
                            } catch (IOException e) {
                                log.error(e.getMessage());
                            }
                        }, (e) -> {

                        }, () -> {
                            setLocationRelativeTo(null);
                            setVisible(true);
                        });
                    }
                } else {
                    log.info("local file not found.");
                }
            }).subscribe();
        }, Duration.ofMinutes(60));
    }

    private String getProgramDateTime(String program) {
        File file = new File(program);
        if (file.exists()) {
            SimpleDateFormat df = new SimpleDateFormat(dateFormat);
            return df.format(new Date(file.lastModified()));
        }
        return null;
    }

    private void restart() {
        try {
            log.info("program is restarting.");
            String javaHome = System.getProperty("java.home");
            String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
            String classpath = System.getProperty("java.class.path");
            String className = "CoreAccountApplication"; // Replace with your main class name
            ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
            builder.start();
            System.exit(0);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Program Update");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setForeground(new java.awt.Color(153, 0, 0));
        jLabel1.setText("Could you please exit the program and then reopen it? Thank you.     ");

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Exit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Cancel");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("An important update is ready for Core Account. Please update to access new features, improvements, and security enhancements.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jSeparator1))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(78, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        restart();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
