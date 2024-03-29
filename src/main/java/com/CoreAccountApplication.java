package com;

import com.common.Global;
import com.common.IconUtil;
import com.common.Util1;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.ui.LoginDialog;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperReportsContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "com")
@Slf4j
public class CoreAccountApplication {

    private static ApplicationContext context;
    public static Tray tray;
    private static final Image appIcon = new ImageIcon(CoreAccountApplication.class.getResource("/images/applogo.jpg")).getImage();
    private static ServerThread serverThread;
    private static Splash splash;

    public static void main(String[] args) {
        initialize();
        loadSplash();
        loadProperty();
        setupContext(args);
        initUI();
    }

    private static void initialize() {
        System.setProperty("spring.main.lazy-initialization", "true");
    }

    private static void initUI() {
        setupTray();
        endSplash();
        startLogin();
    }

    private static void loadSplash() {
        splash = new Splash(appIcon);
        splash.setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> {
            splash.setVisible(true);
        });
    }

    private static void endSplash() {
        splash.setVisible(false);
    }

    private static void setupContext(String[] args) {
        log.info("start context.");
        context = new SpringApplicationBuilder()
                .headless(false)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .sources(CoreAccountApplication.class)
                .run(args);
        log.info("end context.");
    }

    private static void setupTray() {
        tray = new Tray();
        tray.startup(appIcon);
    }

    private static void startLogin() {
        LoginDialog lg = context.getBean(LoginDialog.class);
        tray.setLoginDialog(lg);
        lg.setIconImage(IconUtil.getImage(IconUtil.USER));
        lg.setAppIcon(appIcon);
        lg.checkMachineRegister();
    }

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);
        Thread thread = new Thread(() -> {
            Global.clear();
            SpringApplication.exit(context, () -> 0);
            tray.removeTray();
            serverThread.shutDown();
            main(args.getSourceArgs());
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static void initFont(int fontSize) {
        try {
            List<File> files;
            try (Stream<Path> pathStream = Files.list(Paths.get("font"))) {
                files = pathStream
                        .map(Path::toFile)
                        .filter(File::isFile)
                        .toList();
            }
            if (!files.isEmpty()) {
                File file = files.getFirst();
                InputStream inputStream = new BufferedInputStream(
                        new FileInputStream(file.getPath()));
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
                initJasper(font, file.getPath());
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
            applayTheme();
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
            log.info("Starting new instance of program...");
        }
    }

    private static int getPort(Object obj) {
        if (obj != null) {
            return Integer.parseInt(obj.toString());
        }
        return 100;
    }

    private static void applayTheme() {
        log.info("theme start.");
        System.setProperty("flatlaf.useWindowDecorations", "true");
        System.setProperty("flatlaf.menuBarEmbedded", "true");
        System.setProperty("flatlaf.animation", "true");
        System.setProperty("flatlaf.uiScale.enabled", "true");
        FlatLaf.registerCustomDefaultsSource("com.theme");
        Util1.DARK_MODE = isDarkModeEnabled();
        if (Util1.DARK_MODE) {
            FlatMacDarkLaf.setup();
        } else {
            FlatMacLightLaf.setup();
        }
        initUIManager();
        initIcon();
        initDateChooser();
        log.info("theme end.");
    }

    public static void initUIManager() {
        Global.selectionColor = UIManager.getColor("MenuItem.selectionBackground");
        UIManager.put("TableHeader.background", Global.selectionColor);
        UIManager.put("Table.selectionBackground", Global.selectionColor);
        UIManager.put("Button.arc", 20);
        UIManager.put("Table.arc", 20);
        UIManager.put("Component.arc", 20);
        UIManager.put("ProgressBar.arc", 20);
        UIManager.put("CheckBox.arc", 20);
        UIManager.put("TextComponent.arc", 20);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TabbedPane.tabSeparatorsFullHeight", true);
    }

    private static void initIcon() {
        FlatSVGIcon i = new FlatSVGIcon("svg/search.svg");

        UIManager.put(IconUtil.SEARCH_ICON, i);
    }

    private static void initDateChooser() {
        UIManager.put("JDateChooser.foreground", Color.RED);

    }

    private static boolean isDarkModeEnabled() {
        boolean isDarkModeEnabled = false;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) { // Windows
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("reg", "query", "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize", "/v", "AppsUseLightTheme");
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("AppsUseLightTheme")) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 3) {
                            String value = parts[parts.length - 1];
                            log.info(value);
                            return value.equals("0x0");
                        }
                    }
                }
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                log.error("win : " + e.getMessage());
            }
        } else if (os.contains("mac")) { // macOS
            isDarkModeEnabled = "dark".equals(System.getProperty("apple.awt.application.appearance"));
        } else if (os.contains("nix") || os.contains("nux") || os.contains("bsd")) { // Unix/Linux/BSD
            String desktopSession = System.getenv("DESKTOP_SESSION");
            if (desktopSession != null && desktopSession.toLowerCase().contains("gnome")) {
                isDarkModeEnabled = "dark".equals(System.getenv("GTK_THEME"));
            } else if (desktopSession != null && desktopSession.toLowerCase().contains("kde")) {
                isDarkModeEnabled = "breeze-dark".equals(System.getenv("KDE_FULL_SESSION"));
            }
        }
        return isDarkModeEnabled;
    }

    public static void initJasper(Font font, String path) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);

        // Set JasperReports default font
        JasperReportsContext jc = DefaultJasperReportsContext.getInstance();
        jc.setProperty("net.sf.jasperreports.default.font.name", path); // Set font name, not the string "Pyidaungsu"
        jc.setProperty("net.sf.jasperreports.default.pdf.font.name", path); // Set PDF font name
        jc.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
        jc.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
        jc.setProperty("net.sf.jasperreports.viewer.zoom", "1");
        jc.setProperty("net.sf.jasperreports.export.xlsx.detect.cell.type", "true");
        jc.setProperty("net.sf.jasperreports.export.xlsx.white.page.background", "false");
        jc.setProperty("net.sf.jasperreports.export.xlsx.auto.fit.page.width", "true");
        jc.setProperty("net.sf.jasperreports.export.xlsx.ignore.graphics", "false");
    }
}
