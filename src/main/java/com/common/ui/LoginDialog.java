/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common.ui;

import com.common.Global;
import com.user.common.UserRepo;
import com.common.Util1;
import com.inventory.model.AppUser;
import com.inventory.model.MachineInfo;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author winswe
 */
@PropertySource(value = {"file:config/application.properties"})
@Component
@Slf4j
public class LoginDialog extends javax.swing.JDialog implements KeyListener {

    @Value("${src.path}")
    private String srcPath;
    private boolean login = false;
    private int loginAttempt = 0;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TaskExecutor taskExecutor;
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
    @Autowired
    private WebClient userApi;

    /**
     * Creates new form LoginDialog
     */
    public LoginDialog() {
        super(new javax.swing.JFrame(), true);
        initComponents();
        //Init
        initKeyListener();
        initFocusListener();
    }

    public void checkMachineRegister() {
        try {
            Global.machineName = Util1.getComputerName();
            MachineInfo mac = userRepo.register(Global.machineName);
            if (mac == null) {
                JOptionPane.showMessageDialog(this, "Core User Api is not running.", "Machine", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            int macId = mac.getMacId();
            if (macId == 0) {
                SecurityDialog dialog = new SecurityDialog();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                String inputKey = dialog.getKey();
                String toDayKey = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
                toDayKey = toDayKey.replaceAll("-", "");
                if (inputKey.equals(toDayKey)) {
                    register();
                } else {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Security Key.");
                    System.exit(1);
                }
            } else {
                if (!mac.isProUpdate()) {
                    formEnable(false);
                    lblStatus.setForeground(Color.blue);
                    lblStatus.setText("Please wait program is updating...");
                    taskExecutor.execute(() -> {
                        String replacStr = srcPath.replaceAll("\"", "//");
                        log.info(replacStr);
                        try {
                            String distPath = "core-inventory.jar";
                            FileChannel src = new FileInputStream(
                                    replacStr)
                                    .getChannel();
                            FileChannel dest
                                    = new FileOutputStream(
                                            distPath)
                                            .getChannel();
                            dest.transferFrom(src, 0, src.size());
                            lblStatus.setForeground(Color.BLACK);
                            lblStatus.setText("Program is updated. Exit program and open.");
                            mac.setProUpdate(true);
                            userRepo.register(mac);
                        } catch (IOException ex) {
                            formEnable(true);
                            lblStatus.setForeground(Color.red);
                            lblStatus.setText(String.format("Program updating error :%s", ex.getMessage()));
                        }
                    });
                    //JOptionPane.showMessageDialog(new JFrame(), "Program is updating... Please Wait..", "Update Alert", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    lblStatus.setText("Latest version.");
                }
                Global.macId = macId;
            }
        } catch (HeadlessException ex) {
            log.error("getMachineInfo Error : {}", ex.getMessage());
            JOptionPane.showMessageDialog(this, "Database not found.", "System Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void formEnable(boolean status) {
        btnLogin.setEnabled(status);
        txtLoginName.setEnabled(status);
        txtPassword.setEnabled(status);

    }

    private void register() {
        String machineName = Util1.getComputerName();
        String ipAddress = Util1.getIPAddress();
        MachineInfo machine = new MachineInfo();
        machine.setMachineIp(ipAddress);
        machine.setMachineName(machineName);
        machine.setProUpdate(true);
        MachineInfo mac = userRepo.register(machine);
        if (mac.getMacId() != null) {
            Global.macId = mac.getMacId();
        } else {
            System.exit(0);
        }

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
    //======End KeyListener implementation ======

    public boolean isLogin() {
        return login;
    }

    private void login() {
        //register();
        if (txtLoginName.getText().isEmpty() || txtPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                    "Authentication error.", JOptionPane.ERROR_MESSAGE);
            loginAttempt++;
        } else {
            try {
                Mono<AppUser> result = userApi.get()
                        .uri(builder -> builder.path("/user/login")
                        .queryParam("userName", txtLoginName.getText())
                        .queryParam("password", String.valueOf(txtPassword.getPassword()))
                        .build())
                        .retrieve().bodyToMono(AppUser.class);
                AppUser user = result.block();
                if (Util1.isNull(user)) {
                    JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                            "Authentication error.", JOptionPane.ERROR_MESSAGE);
                    loginAttempt++;
                } else {
                    Global.loginUser = user;
                    login = true;
                    this.dispose();
                }
            } catch (HeadlessException ex) {
                log.error("login : " + ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Authentication error.", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (loginAttempt >= 3) {
            this.dispose();
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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLoginName, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(txtLoginName, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        //System.exit(0);
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
    private javax.swing.JTextField txtLoginName;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
