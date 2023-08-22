/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ui;

import com.CloudIntegration;
import com.MessageDialog;
import com.CoreAccountApplication;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.TokenFile;
import com.repo.UserRepo;
import com.common.Util1;
import com.user.model.AppUser;
import com.inventory.model.MachineInfo;
import com.user.model.AuthenticationResponse;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
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
    private int enableCount = 0;
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
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            Object sourceObj = evt.getSource();
            if (sourceObj instanceof JTextField txt) {
                txt.selectAll();
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {

        }
    };

    /**
     * Creates new form LoginDialog
     */
    public LoginDialog() {
        super(new javax.swing.JFrame(), true);
        initComponents();
        initKeyListener();
        initFocusListener();
    }

    public void focus() {
        txtLoginName.requestFocus();
    }

    public void checkMachineRegister() {
        String serialNo = Util1.getBaseboardSerialNumber();
        if (serialNo == null) {
            JOptionPane.showMessageDialog(this, "Something went wrong.");
            System.exit(0);
        }
        Global.dialog = this;
        Global.machineName = Util1.getComputerName();
        MessageDialog d = new MessageDialog(this, "Connecting to Server.");
        taskExecutor.execute(() -> {
            d.setLocationRelativeTo(null);
            d.setVisible(true);
        });
        MachineInfo t = userRepo.checkSerialNo(serialNo).block();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Please Check Internet Connection.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        d.setVisible(false);
        if (t.getMacId() == null) {
            SecurityDialog dialog = new SecurityDialog();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            String inputKey = dialog.getKey();
            String toDayKey = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
            toDayKey = toDayKey.replaceAll("-", "");
            if (inputKey.equals(toDayKey)) {
                register(serialNo).subscribe((response) -> {
                    file.write(response);
                    JOptionPane.showMessageDialog(this, "Logout");
                    logout();
                });

            } else {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Security Key.");
                System.exit(0);
            }
        } else {
            Global.macId = t.getMacId();
            enableForm(false);
            taskExecutor.execute(() -> {
                setLocationRelativeTo(null);
                setVisible(true);
            });
            lblStatus.setText("downloading...");
            integration.setObserver(this);
            integration.start();

        }
    }

    private void enableForm(boolean status) {
        txtLoginName.setEnabled(status);
        txtPassword.setEnabled(status);
        btnLogin.setEnabled(status);
        progress.setIndeterminate(!status);
        lblStatus.setText(status ? "Latest Version." : "");
    }

    private void logout() {
        CoreAccountApplication.restart();
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
                        txtLoginName.requestFocus();
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
                        txtLoginName.requestFocus();
                    case KeyEvent.VK_UP ->
                        btnLogin.requestFocus();
                }
            }
        }
    }

    private void login() {
        String userName = txtLoginName.getText();
        String password = String.valueOf(txtPassword.getPassword());
        if (userName.isEmpty() || password.length() == 0) {
            JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                    "Authentication error.", JOptionPane.ERROR_MESSAGE);
            loginAttempt++;
        } else {
            Mono<AppUser> user = userRepo.login(userName, password);
            user.hasElement().subscribe((status) -> {
                if (status) {
                    userRepo.login(userName, password).subscribe((t) -> {
                        Global.loginUser = t;
                        Global.roleCode = t.getRoleCode();
                        taskExecutor.execute(() -> {
                            mainFrame.setName(APP_NAME);
                            mainFrame.setIconImage(appIcon);
                            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                            mainFrame.initMain();
                            mainFrame.setVisible(true);
                        });
                        setVisible(false);
                    }, (e) -> {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                            "Authentication error.", JOptionPane.ERROR_MESSAGE);
                    loginAttempt++;
                }
                if (loginAttempt >= 3) {
                    this.dispose();
                }
            });

        }

    }

    public void clear() {
        txtLoginName.setText(null);
        txtPassword.setText(null);
        txtLoginName.requestFocus();
    }

    private void initKeyListener() {
        txtLoginName.addKeyListener(this);
        txtPassword.addKeyListener(this);
        btnLogin.addKeyListener(this);
    }

    private void initFocusListener() {
        txtLoginName.addFocusListener(fa);
        txtPassword.addFocusListener(fa);
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
        txtLoginName = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();

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

        txtLoginName.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txtLoginName.setText("Username");
        txtLoginName.setName("txtLoginName"); // NOI18N
        txtLoginName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoginNameActionPerformed(evt);
            }
        });

        txtPassword.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txtPassword.setText("Password");
        txtPassword.setName("txtPassword"); // NOI18N
        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Login Your Account");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(txtLoginName, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(txtLoginName, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
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

    private void txtLoginNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoginNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLoginNameActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPasswordActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTextField txtLoginName;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        String type = source.toString();
        if (type.equals("download")) {
            enableCount += 1;
            lblStatus.setText(selectObj.toString());
            if (enableCount > 14) {
                enableForm(true);
            }
        } else if (type.equals("enable")) {
            if (selectObj instanceof Boolean enable) {
                enableForm(enable);
            }
        }
    }
}
