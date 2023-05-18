package com;

import com.common.Global;
import com.common.Util1;
import com.formdev.flatlaf.FlatLightLaf;
import com.common.ui.ApplicationMainFrame;
import com.common.ui.LoginDialog;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
public class CoreAccountApplication {

    private static ConfigurableApplicationContext context;
    private static Tray tray;
    private static final Image appIcon = new ImageIcon(CoreAccountApplication.class.getResource("/images/applogo.png")).getImage();
    private static final String APP_NAME = "Core Account";
    private static ServerThread serverThread;

    public static void main(String[] args) {
        applayTheme();
        loadProperty();
        //splash
        Splash splash = new Splash();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        //create context
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CoreAccountApplication.class);
        builder.headless(false);
        builder.web(WebApplicationType.NONE);
        builder.bannerMode(Banner.Mode.OFF);
        context = builder.run(args);
        tray = context.getBean(Tray.class);
        tray.startup(appIcon);
        splash.dispose();
        LoginDialog lg = context.getBean(LoginDialog.class);
        URL imgUrl = CoreAccountApplication.class.getResource("/images/male_user_16px.png");
        lg.setIconImage(new ImageIcon(imgUrl).getImage());
        lg.checkMachineRegister();
        lg.setLocationRelativeTo(null);
        lg.setVisible(true);
        if (lg.isLogin()) {
            if (Global.macId != null) {
                ApplicationMainFrame appMain = context.getBean(ApplicationMainFrame.class);
                java.awt.EventQueue.invokeLater(() -> {
                    appMain.setName(APP_NAME);
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
            System.exit(0);
        }
    }

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);
        Thread thread = new Thread(() -> {
            context.close();
            tray.removeTray();
            serverThread.shutDown();
            main(args.getSourceArgs());
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

    private static void loadProperty() {
        try {
            FileReader reader = new FileReader("config" + File.separator + "application.properties");
            Properties p = new Properties();
            p.load(reader);
            initFont(Util1.getInteger(p.getProperty("font.size")));
            int port = getPort(p.get("program.id"));
            checkRun(port);
            serverThread = new ServerThread(port);
            serverThread.start();
        } catch (IOException e) {
            log.error("loadProperty : " + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, "Property file not found.");
        }
    }

    private static void checkRun(int port) {
        try {
            try (Socket client = new Socket("localhost", port); OutputStream out = client.getOutputStream()) {
                out.write("focus\n".getBytes());
                out.flush();
            }
            System.exit(0);
        } catch (IOException e) {
            // Server is not running, so we can start a new instance of the program
            System.out.println("Starting new instance of program...");
        }
    }

    private static int getPort(Object obj) {
        if (obj != null) {
            return Integer.parseInt(obj.toString());
        }
        return 100;
    }

    private static void applayTheme() {
        try {
            log.info("theme start.");
            UIManager.setLookAndFeel(new FlatLightLaf());
            Global.selectionColor = UIManager.getDefaults().getColor("Table.selectionBackground");
            System.setProperty("flatlaf.useWindowDecorations", "true");
            System.setProperty("flatlaf.menuBarEmbedded", "true");
            System.setProperty("flatlaf.animation", "true");
            System.setProperty("flatlaf.uiScale.enabled", "true");
            UIManager.put("Button.arc", 10);
            UIManager.put("Table.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TableHeader.background", Global.selectionColor);
            UIManager.put("TableHeader.foreground", Color.white);
            log.info("theme end.");
            //UIManager.put("TableHeader.separatorColor", Color.black);
            //UIManager.put("TableHeader.bottomSeparatorColor", Color.black);
            //UIManager.put("Table.gridColor", UIManager.getDefaults().getColor("Table.selectionBackground"));
            //UIManager.put("Table.gridColor", new Color(213, 235, 226));
            //FlatSolarizedLightIJTheme.setup();
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize LaF");
        }

    }

}
