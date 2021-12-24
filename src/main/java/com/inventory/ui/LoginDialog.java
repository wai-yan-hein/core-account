/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui;

import com.inventory.common.Global;
import com.inventory.common.ReturnObject;
import com.inventory.common.Util1;
import com.inventory.model.AppUser;
import com.inventory.model.MachineInfo;
import java.awt.HeadlessException;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author winswe
 */
@Component
@Slf4j
public class LoginDialog extends javax.swing.JDialog implements KeyListener {
    
    private boolean login = false;
    private int loginAttempt = 0;
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            Object sourceObj = evt.getSource();
            if (sourceObj instanceof JComboBox) {
                JComboBox jcb = (JComboBox) sourceObj;
                log.info("Control Name : " + jcb.getName());
            } else if (sourceObj instanceof JFormattedTextField) {
                JFormattedTextField jftf = (JFormattedTextField) sourceObj;
                jftf.selectAll();
                log.info("Control Name : " + jftf.getName());
            } else if (sourceObj instanceof JTextField) {
                JTextField jtf = (JTextField) sourceObj;
                jtf.selectAll();
                log.info("Control Name : " + jtf.getName());
            }
        }
        
        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            
        }
    };
    @Autowired
    private WebClient webClient;

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
            Mono<ReturnObject> result = webClient.get()
                    .uri(builder -> builder.path("/get-mac-id").queryParam("macName", Global.machineName).build())
                    .retrieve().bodyToMono(ReturnObject.class);
            result.subscribe((t) -> {
                Global.macId = Util1.getInteger(t.getObj());
                //Global.macId = machineInfoService.getMax(Global.machineName);
                if (Global.macId == 0) {
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
                }
            }, (e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
            });
            
        } catch (HeadlessException ex) {
            log.error("getMachineInfo Error : {}", ex.getMessage());
            JOptionPane.showMessageDialog(this, "Database not found.", "System Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void errorHandler(String msg) throws Exception {
        
    }
    
    private void register() {
        try {
            String machineName = Util1.getComputerName();
            String ipAddress = Util1.getIPAddress();
            MachineInfo machine = new MachineInfo();
            machine.setIpAddress(ipAddress);
            machine.setMachineName(machineName);
            Mono<MachineInfo> result = webClient.post()
                    .uri("/save-machine")
                    .body(Mono.just(machine), MachineInfo.class)
                    .retrieve()
                    .bodyToMono(MachineInfo.class);
            MachineInfo block = result.block();
            if (block != null) {
                Global.macId = block.getMachineId();
            }
        } catch (Exception ex) {
            log.error("Register Error : {}", ex.getMessage());
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
        
        if (sourceObj instanceof JComboBox) {
            ctrlName = ((JComboBox) sourceObj).getName();
        } else if (sourceObj instanceof JFormattedTextField) {
            ctrlName = ((JFormattedTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        }
        
        switch (ctrlName) {
            case "txtLoginName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtPassword.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    butClear.requestFocus();
                }
                break;
            case "txtPassword":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER: //Login
                        login();
                        break;
                    case KeyEvent.VK_DOWN:
                        butLogin.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtLoginName.requestFocus();
                        break;
                }
                break;
            case "butLogin":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        login();
                        break;
                    case KeyEvent.VK_DOWN:
                        butClear.requestFocus();
                        break;
                }
                break;
            case "butClear":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        clear();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtLoginName.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        butLogin.requestFocus();
                        break;
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
                Mono<AppUser> result = webClient.get()
                        .uri(builder -> builder.path("/user/login")
                        .queryParam("username", txtLoginName.getText())
                        .queryParam("password", Arrays.toString(txtPassword.getPassword()))
                        .build())
                        .retrieve().bodyToMono(AppUser.class);
                result.subscribe((t) -> {
                    if (Util1.isNull(t)) {
                        JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                                "Authentication error.", JOptionPane.ERROR_MESSAGE);
                        loginAttempt++;
                    } else {
                        Global.loginUser = t;
                        login = true;
                        this.dispose();
                    }
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });
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
        butClear.addKeyListener(this);
        butLogin.addKeyListener(this);
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtLoginName = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        butClear = new javax.swing.JButton();
        butLogin = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Login Core Inventory");
        setFont(Global.lableFont);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Login Name ");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Password");

        txtLoginName.setFont(Global.textFont);
        txtLoginName.setName("txtLoginName"); // NOI18N
        txtLoginName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLoginNameKeyReleased(evt);
            }
        });

        txtPassword.setFont(Global.lableFont);
        txtPassword.setName("txtPassword"); // NOI18N

        butClear.setFont(Global.lableFont);
        butClear.setText("Clear");
        butClear.setName("butClear"); // NOI18N

        butLogin.setBackground(Global.selectionColor);
        butLogin.setFont(Global.lableFont);
        butLogin.setForeground(new java.awt.Color(255, 255, 255));
        butLogin.setText("Login");
        butLogin.setName("butLogin"); // NOI18N
        butLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLoginActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 184, Short.MAX_VALUE)
                        .addComponent(butLogin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                            .addComponent(txtLoginName, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtLoginName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(butLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, txtLoginName, txtPassword});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLoginActionPerformed
        login();
    }//GEN-LAST:event_butLoginActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void txtLoginNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLoginNameKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLoginNameKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextField txtLoginName;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
