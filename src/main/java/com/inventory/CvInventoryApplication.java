package com.inventory;

import com.formdev.flatlaf.FlatLightLaf;
import com.inventory.common.Global;
import com.inventory.ui.ApplicationMainFrame;
import com.inventory.ui.LoginDialog;
import java.util.TimeZone;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class CvInventoryApplication {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize LaF");
        }
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CvInventoryApplication.class);
        builder.headless(false);
        builder.web(WebApplicationType.NONE);
        builder.bannerMode(Banner.Mode.OFF);
        context = builder.run(args);
        LoginDialog loginDialog = context.getBean(LoginDialog.class);
        loginDialog.checkMachineRegister();
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setVisible(true);
        if (loginDialog.isLogin()) {
            if (Global.machineId != null) {
                ApplicationMainFrame appMain = context.getBean(ApplicationMainFrame.class);
                java.awt.EventQueue.invokeLater(() -> {
                    appMain.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    appMain.companyUserRoleAssign();
                    appMain.setVisible(true);
                });
                log.info("login sucess.");
            } else {
                JOptionPane.showMessageDialog(Global.parentForm, "Your machine is not register.", "Security Alert", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }
}
