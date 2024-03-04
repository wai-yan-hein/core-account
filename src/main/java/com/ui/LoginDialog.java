/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ui;

import com.CloudIntegration;
import com.MessageDialog;
import com.CoreAccountApplication;
import com.common.ComponentUtil;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.TokenFile;
import com.repo.UserRepo;
import com.common.Util1;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.user.model.MachineInfo;
import com.user.model.AuthenticationResponse;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 *
 * @author wai yan
 */
@PropertySource(value = {"file:config/application.properties"})
@Component
@Slf4j
public class LoginDialog extends javax.swing.JDialog implements KeyListener, SelectionObserver {

    private int loginAttempt = 0;
    private String APP_NAME = "Core Account";
    private Image appIcon;
    private final TokenFile<AuthenticationResponse> file = new TokenFile<>(AuthenticationResponse.class);
    @Autowired
    private CloudIntegration integration;

    public Image getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Image appIcon) {
        this.appIcon = appIcon;
    }

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ApplicationMainFrame mainFrame;

    /**
     * Creates new form LoginDialog
     */
    public LoginDialog() {
        super(new javax.swing.JFrame(), true);
        initComponents();
        initKeyListener();
        initFocusListener();
        initClientProperty();
    }

    private void initClientProperty() {
        txtUserName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        txtUserName.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true;" + "showCapsLock:true");
    }

    public void focus() {
        txtUserName.requestFocus();
    }

    public void checkMachineRegister() {
        chkDark.setSelected(Util1.DARK_MODE);
        String serialNo = Util1.getBaseboardSerialNumber();
        log.info("serialNo : " + serialNo);
        if (serialNo == null) {
            JOptionPane.showMessageDialog(this, "Something went wrong.");
            System.exit(0);
        }
        Global.dialog = this;
        Global.machineName = Util1.getComputerName();
        MessageDialog d = new MessageDialog(this, "Connecting to Server.");
        SwingUtilities.invokeLater(() -> {
            d.setLocationRelativeTo(null);
            d.setVisible(true);
        });
        userRepo.checkLocalSerialNo(serialNo).doOnSuccess((t) -> {
            if (t == null) {
                JOptionPane.showMessageDialog(this, "Please Check Internet Connection.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            d.setVisible(false);
            if (t.getMacId() == null || t.getMacId() == 0) {
                JFrame frame = new JFrame();
                frame.setIconImage(appIcon);
                SecurityDialog dialog = new SecurityDialog(frame);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                String inputKey = dialog.getKey();
                String toDayKey = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
                toDayKey = toDayKey.replaceAll("-", "");
                if (inputKey == null) {
                    System.exit(0);
                }
                if (inputKey.equals(toDayKey)) {
                    register(serialNo).doOnSuccess((response) -> {
                        file.write(response);
                    }).doOnTerminate(() -> {
                        JOptionPane.showMessageDialog(this, "Logout");
                        logout();
                    }).subscribe();
                } else {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Security Key.");
                    System.exit(0);
                }
            } else {
                Global.macId = t.getMacId();
                log.info("mac id :" + Global.macId.toString());
                checkAndDeleteData(t);
                saveMachine(t);
                enableForm(false);
                SwingUtilities.invokeLater(() -> {
                    setLocationRelativeTo(null);
                    focus();
                    setVisible(true);
                });
                lblStatus.setText("downloading...");
                integration.setObserver(this);
                integration.start();
            }
        }).doOnError((e) -> {
            d.setVisible(false);
            int yn = JOptionPane.showConfirmDialog(this, "Internet Offline. Try Again?", "Offline", JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
            if (yn == JOptionPane.YES_OPTION) {
                checkMachineRegister();
            } else {
                System.exit(0);
            }
        }).subscribe();

    }

    private void checkAndDeleteData(MachineInfo info) {
        if (info.isProUpdate()) {
            log.info("need to delete local data.");
            File f = new File("data/database.mv.db");
            boolean delete = f.delete();
            if (delete) {
                log.info("local data delete success.");
                info.setProUpdate(false);
            }
        }
    }

    private void enableForm(boolean status) {
        txtUserName.setEnabled(status);
        txtPassword.setEnabled(status);
        btnLogin.setEnabled(status);
        progress.setIndeterminate(!status);
        lblStatus.setText(status ? "Latest Version." : "");
    }

    private void logout() {
        CoreAccountApplication.restart();
    }

    private void saveMachine(MachineInfo info) {
        String machineName = Util1.getComputerName();
        String ipAddress = Util1.getIPAddress();
        String macAddress = Util1.getMacAddress();
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        info.setMachineIp(ipAddress);
        info.setMachineName(machineName);
        info.setMacAddress(macAddress);
        info.setOsName(osName);
        info.setOsVersion(osVersion);
        info.setOsArch(osArch);
        userRepo.saveMachine(info).doOnSuccess((t) -> {
            log.info("machine info update.");
        }).doOnError((e) -> {
            log.error("saveMachine : " + e.getMessage());
        }).subscribe();
    }

    private Mono<AuthenticationResponse> register(String serialNo) {
        String machineName = Util1.getComputerName();
        String ipAddress = Util1.getIPAddress();
        String macAddress = Util1.getMacAddress();
        MachineInfo machine = new MachineInfo();
        machine.setMachineIp(ipAddress);
        machine.setMachineName(machineName);
        machine.setMacAddress(macAddress);
        machine.setProUpdate(true);
        machine.setSerialNo(serialNo);
        return userRepo.register(machine);

    }

    //KeyListener implementation
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JTextField txt) {
            ctrlName = txt.getName();
        }

        switch (ctrlName) {
            case "txtLoginName" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtPassword.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
            }
            case "txtPassword" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> //Login
                        login();
                    case KeyEvent.VK_DOWN ->
                        btnLogin.requestFocus();
                    case KeyEvent.VK_UP ->
                        txtUserName.requestFocus();
                }
            }
            case "butLogin" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        login();
                    case KeyEvent.VK_DOWN -> {
                    }
                }
            }
            case "butClear" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        clear();
                    case KeyEvent.VK_DOWN ->
                        txtUserName.requestFocus();
                    case KeyEvent.VK_UP ->
                        btnLogin.requestFocus();
                }
            }
        }
    }

    private void login() {
        String userName = txtUserName.getText();
        String password = String.valueOf(txtPassword.getPassword());
        if (userName.isEmpty() || password.length() == 0) {
            JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                    "Authentication error.", JOptionPane.ERROR_MESSAGE);
            loginAttempt++;
        } else {
            userRepo.login(userName, password).doOnSuccess((t) -> {
                if (t != null) {
                    Global.loginUser = t;
                    Global.roleCode = t.getRoleCode();
                    SwingUtilities.invokeLater(() -> {
                        mainFrame.setName(APP_NAME);
                        mainFrame.setIconImage(appIcon);
                        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        mainFrame.initMain();
                        mainFrame.setVisible(true);
                    });
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                            "Authentication error.", JOptionPane.ERROR_MESSAGE);
                    loginAttempt++;
                    lblStatus.setText(String.format("Incorrect : %d of 3", loginAttempt));
                    txtUserName.putClientProperty("JComponent.outline", "warning");
                    txtPassword.putClientProperty("JComponent.outline", "warning");
                }
                if (loginAttempt >= 3) {
                    this.dispose();
                }
            }).subscribe();
        }
    }

    public void clear() {
        txtUserName.setText(null);
        txtPassword.setText(null);
        txtUserName.requestFocus();
    }

    private void initKeyListener() {
        txtUserName.addKeyListener(this);
        txtPassword.addKeyListener(this);
        btnLogin.addKeyListener(this);
    }

    private void initFocusListener() {
        ComponentUtil.addFocusListener(this);
    }

    private void enableDarkMode(boolean isDark) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (isDark) {
                    UIManager.setLookAndFeel(new FlatMacDarkLaf());
                } else {
                    UIManager.setLookAndFeel(new FlatMacLightLaf());
                }
                CoreAccountApplication.initUIManager();
                Util1.DARK_MODE = isDark;
                for (Window w : Window.getWindows()) {
                    SwingUtilities.updateComponentTreeUI(w);
                }
            } catch (UnsupportedLookAndFeelException e) {
                log.error("enableDarkMode : " + e.getMessage());
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        chkDark = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Login Core Account Cloud");
        setFont(Global.lableFont);
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("LOGIN");

        txtUserName.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txtUserName.setName("txtUserName"); // NOI18N
        txtUserName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserNameActionPerformed(evt);
            }
        });

        txtPassword.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txtPassword.setName("txtPassword"); // NOI18N
        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Login Your Core Account");

        btnLogin.setBackground(UIManager.getDefaults().getColor("Table.selectionBackground"));
        btnLogin.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Login");
        btnLogin.setName("btnLogin"); // NOI18N
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        lblStatus.setBackground(new java.awt.Color(255, 255, 255));
        lblStatus.setFont(Global.lableFont);
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        chkDark.setFont(Global.lableFont);
        chkDark.setText("Dark Mode");
        chkDark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDarkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(txtUserName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkDark)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDark)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_formWindowClosed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        login();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtUserNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserNameActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void chkDarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDarkActionPerformed
        // TODO add your handling code here:
        enableDarkMode(chkDark.isSelected());
    }//GEN-LAST:event_chkDarkActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JRadioButton chkDark;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        String type = source.toString();
        if (type.equals("download")) {
            lblStatus.setText(selectObj.toString());
            enableForm(true);
        } else if (type.equals("enable")) {
            if (selectObj instanceof Boolean enable) {
                enableForm(enable);
            }
        }
    }
}
