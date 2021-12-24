package com.inventory;

import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;
import com.inventory.common.Global;
import com.inventory.ui.ApplicationMainFrame;
import com.inventory.ui.LoginDialog;
import java.awt.Color;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
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
            UIManager.setLookAndFeel(new FlatCyanLightIJTheme());
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize LaF");
        }
        /* try {
            Global.sock = new ServerSocket(10004);//Pharmacy
        } catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Core Inventory is already running.", "Duplicate Program", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }*/
        System.setProperty("flatlaf.useWindowDecorations", "false");
        System.setProperty("flatlaf.menuBarEmbedded", "false");
        System.setProperty("flatlaf.animation", "true");
        System.setProperty("flatlaf.uiScale.enabled", "true");
        UIManager.put("Button.arc", 10);
        UIManager.put("Table.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("TabbedPane.showTabSeparators", true);
        //UIManager.put("Table.gridColor", UIManager.getDefaults().getColor("Table.selectionBackground"));
        UIManager.put("Table.gridColor", new Color(213, 235, 226));
        Global.selectionColor = UIManager.getDefaults().getColor("Table.selectionBackground");
        //FlatSolarizedLightIJTheme.setup();
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CvInventoryApplication.class);
        builder.headless(false);
        builder.web(WebApplicationType.NONE);
        builder.bannerMode(Banner.Mode.OFF);
        context = builder.run(args);
        Tray tray = context.getBean(Tray.class);
        tray.startup();
        LoginDialog lg = context.getBean(LoginDialog.class);
        URL imgUrl = CvInventoryApplication.class.getResource("/images/male_user.png");
        lg.setIconImage(new ImageIcon(imgUrl).getImage());
        lg.checkMachineRegister();
        lg.setLocationRelativeTo(null);
        lg.setVisible(true);
        if (lg.isLogin()) {
            if (Global.macId != null) {
                ApplicationMainFrame appMain = context.getBean(ApplicationMainFrame.class);
                java.awt.EventQueue.invokeLater(() -> {
                    URL appUrl = CvInventoryApplication.class.getResource("/images/warehouse.png");
                    appMain.setIconImage(new ImageIcon(appUrl).getImage());
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

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);
        Thread thread = new Thread(() -> {
            context.close();
            main(args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }
}
