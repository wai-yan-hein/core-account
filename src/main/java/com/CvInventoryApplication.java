package com;

import com.common.Global;
import com.common.Util1;
import com.formdev.flatlaf.FlatLightLaf;
import com.inventory.ui.ApplicationMainFrame;
import com.inventory.ui.LoginDialog;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
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
    private static Tray tray;
    private static final Image appIcon = new ImageIcon(CvInventoryApplication.class.getResource("/images/applogo.png")).getImage();
    private static final SplashWindow SPLASH_WINDOW = new SplashWindow();

    public static void main(String[] args) throws IOException {
        SPLASH_WINDOW.run();
        Properties loadProperty = loadProperty();
        initFont(Util1.getInteger(loadProperty.getProperty("font.size")));
        SystemTray systemTray = SystemTray.getSystemTray();
        TrayIcon[] icons = systemTray.getTrayIcons();
        log.info("icon" + icons.length);
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize LaF");
        }
        try {
            int port = 100;
            Object programId = loadProperty.get("program.id");
            if (programId != null) {
                port = Integer.parseInt(programId.toString());
            }
            log.info("progarm id : " + port);
            Global.sock = new ServerSocket(port);
        } catch (IOException e) {
            if (tray != null) {
                tray.openMF();
            }
            JOptionPane.showMessageDialog(new JFrame(), "Core Account is already running.", "Duplicate Program", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        Global.selectionColor = UIManager.getDefaults().getColor("Table.selectionBackground");
        System.setProperty("flatlaf.useWindowDecorations", "true");
        System.setProperty("flatlaf.menuBarEmbedded", "false");
        System.setProperty("flatlaf.animation", "true");
        System.setProperty("flatlaf.uiScale.enabled", "true");
        UIManager.put("Button.arc", 10);
        UIManager.put("Table.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        //UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("TableHeader.background", Global.selectionColor);
        UIManager.put("TableHeader.foreground", Color.white);
        //UIManager.put("TableHeader.separatorColor", Color.black);
        //UIManager.put("TableHeader.bottomSeparatorColor", Color.black);
        //UIManager.put("Table.gridColor", UIManager.getDefaults().getColor("Table.selectionBackground"));
        //UIManager.put("Table.gridColor", new Color(213, 235, 226));
        //FlatSolarizedLightIJTheme.setup();
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CvInventoryApplication.class);
        builder.headless(false);
        builder.web(WebApplicationType.NONE);
        builder.bannerMode(Banner.Mode.OFF);
        context = builder.run(args);
        tray = context.getBean(Tray.class);
        tray.startup(appIcon);
        SPLASH_WINDOW.stopSplah();
        LoginDialog lg = context.getBean(LoginDialog.class);
        URL imgUrl = CvInventoryApplication.class.getResource("/images/male_user_16px.png");
        lg.setIconImage(new ImageIcon(imgUrl).getImage());
        lg.checkMachineRegister();
        lg.setLocationRelativeTo(null);
        lg.setVisible(true);
        if (lg.isLogin()) {
            if (Global.macId != null) {
                ApplicationMainFrame appMain = context.getBean(ApplicationMainFrame.class);
                java.awt.EventQueue.invokeLater(() -> {
                    appMain.setIconImage(appIcon);
                    appMain.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    appMain.initMain();
                    appMain.setVisible(true);
                });
                log.info("login sucess.");
            } else {
                JOptionPane.showMessageDialog(Global.parentForm, "Your machine is not register.", "Security Alert", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } else {
            log.info("exit");
            Global.sock.close();
            System.exit(0);
        }
    }

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);
        Thread thread = new Thread(() -> {
            try {
                context.close();
                tray.removeTray();
                Global.sock.close();
                main(args.getSourceArgs());
            } catch (IOException e) {
                log.error("restart :" + e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static void initFont(int fontSize) {
        try {
            List<File> files = Files.list(Paths.get("font")).map(Path::toFile).filter(File::isFile).collect(Collectors.toList());
            if (!files.isEmpty()) {
                InputStream inputStream = new BufferedInputStream(
                        new FileInputStream(files.get(0).getPath()));
                Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
                Global.textFont = font.deriveFont(Font.PLAIN, fontSize);
                Global.menuFont = font.deriveFont(Font.BOLD, fontSize + 2);
                Global.lableFont = font.deriveFont(Font.BOLD, fontSize);
                Global.amtFont = font.deriveFont(Font.BOLD, fontSize + 1);
                Global.companyFont = font.deriveFont(Font.BOLD, fontSize + 3);
                Global.shortCutFont = font.deriveFont(Font.BOLD, fontSize + 2);
                Global.tblHeaderFont = font.deriveFont(Font.BOLD, fontSize + 1);
                Global.tblRowHeight = fontSize + 15;
                Global.fontName = "font" + File.separator + font.getName();
                log.info(Global.fontName);
            }

        } catch (FontFormatException | IOException ex) {
            log.error("initFont: " + ex.getMessage());
        }
    }

    private static Properties loadProperty() {
        Properties p = null;
        try {
            FileReader reader = new FileReader("config" + File.separator + "application.properties");
            p = new Properties();
            p.load(reader);
        } catch (IOException e) {
            log.error("loadProperty : " + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, "Property file not found.");
        }
        return p;
    }
}
