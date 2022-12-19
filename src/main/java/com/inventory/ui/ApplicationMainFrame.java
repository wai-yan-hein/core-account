/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui;

import com.CvInventoryApplication;
import com.acc.common.AccountRepo;
import com.acc.entry.AllCash;
import com.acc.entry.Journal;
import com.acc.report.AparReport;
import com.acc.report.FinancialReport;
import com.acc.report.GLReport;
import com.acc.setup.COAManagment;

import com.acc.setup.COAOpening;
import com.acc.setup.COASetup;
import com.acc.setup.DepartmentSetup;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.user.common.UserRepo;
import com.common.Util1;
import com.user.setup.MenuSetup;
import com.user.model.DepartmentUser;
import com.inventory.model.VRoleMenu;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.entry.Manufacture;
import com.user.model.VRoleCompany;
import com.inventory.ui.entry.OtherSetup;
import com.inventory.ui.entry.Purchase;
import com.inventory.ui.entry.ReorderLevelEntry;
import com.inventory.ui.entry.ReturnIn;
import com.inventory.ui.entry.ReturnOut;
import com.user.setup.RoleSetting;
import com.inventory.ui.entry.Sale;
import com.inventory.ui.entry.StockInOutEntry;
import com.inventory.ui.setup.CustomerSetup;
import com.inventory.ui.setup.StockSetup;
import com.inventory.ui.setup.SupplierSetup;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.inventory.ui.entry.Reports;
import com.inventory.ui.entry.Transfer;
import com.inventory.ui.entry.WeightLossEntry;
import com.inventory.ui.setup.OpeningSetup;
import com.inventory.ui.setup.PatternSetup;
import com.user.dialog.DepartmentDialog;
import com.user.setup.SystemProperty;
import com.user.setup.AppUserSetup;
import com.user.setup.CloudConfig;
import com.user.setup.CompanySetup;
import java.time.Duration;
import java.util.HashMap;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperReportsContext;
import org.springframework.core.task.TaskExecutor;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class ApplicationMainFrame extends javax.swing.JFrame implements SelectionObserver {

    @Autowired
    private AccountRepo accounRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private WebClient userApi;
    @Autowired
    private StockSetup stockSetup;
    @Autowired
    private OpeningSetup openingSetup;
    @Autowired
    private Sale sale;
    @Autowired
    private Purchase purchase;
    @Autowired
    private ReturnIn retIn;
    @Autowired
    private ReturnOut retOut;
    @Autowired
    private StockInOutEntry stockInOut;
    @Autowired
    private CustomerSetup customerSetup;
    @Autowired
    private SupplierSetup supplierSetup;
    @Autowired
    private OtherSetup otherSetup;
    @Autowired
    private RoleSetting roleSetting;
    @Autowired
    private Reports report;
    @Autowired
    private PatternSetup patternSetup;
    @Autowired
    private ReorderLevelEntry reorderLevel;
    @Autowired
    private Transfer transfer;
    @Autowired
    private Manufacture manufacture;
    @Autowired
    private WeightLossEntry weightLoss;
//account
    @Autowired
    private DepartmentSetup departmentSetup;
    @Autowired
    private COAManagment cOAManagment;
    @Autowired
    private COASetup cOASetup;
    @Autowired
    private GLReport gLReport;
    @Autowired
    private AparReport aparReport;

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private WebClient accountApi;
    @Autowired
    private FinancialReport financialReport;
    @Autowired
    private Journal journal;
    @Autowired
    private COAOpening coaOpening;
//user
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private CloudConfig cloudConfig;
    @Autowired
    private AppUserSetup userSetup;
    @Autowired
    private MenuSetup menuSetup;
    @Autowired
    private CompanySetup companySetup;
    private PanelControl control;
    private final HashMap<String, JPanel> hmPanel = new HashMap<>();
    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        String className = actionMenu.getName();
        JPanel panel = getPanel(className);
        addTabMain(panel, menuName);
    };

    public PanelControl getControl() {
        return control;
    }

    public void setControl(PanelControl control) {
        this.control = control;
    }

    /**
     * Creates new form ApplicationMainFrame
     */
    public ApplicationMainFrame() {
        initComponents();
        initKeyFoucsManager();
    }

    private void initKeyFoucsManager() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((KeyEvent ke) -> {
            if (ke.isAltDown()) {
                if (ke.getKeyCode() == KeyEvent.VK_F4) {
                    System.exit(0);
                }
            }
            switch (ke.getID()) {
                case KeyEvent.KEY_PRESSED -> {
                    if (control != null) {
                        switch (ke.getKeyCode()) {
                            case KeyEvent.VK_F5 ->
                                control.save();
                            case KeyEvent.VK_F6 ->
                                control.print();
                            case KeyEvent.VK_F7 ->
                                control.refresh();
                            case KeyEvent.VK_F8 ->
                                control.delete();
                            case KeyEvent.VK_F9 ->
                                control.history();
                            case KeyEvent.VK_F10 ->
                                control.newForm();
                            case KeyEvent.VK_F11 ->
                                logout();
                            case KeyEvent.VK_F12 ->
                                control.filter();

                        }
                    }
                }
            }
            return false;
        });
    }

    private void addTabMain(JPanel panel, String menuName) {
        if (panel != null) {
            hmPanel.put(menuName, panel);
            tabMain.add(panel);
            tabMain.setTabComponentAt(tabMain.indexOfComponent(panel), setTitlePanel(tabMain, panel, menuName));
            tabMain.setSelectedComponent(panel);
        }
    }

    private JPanel setTitlePanel(final JTabbedPane tabbedPane, final JPanel panel, String title) {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        // title button
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(Global.menuFont);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        titlePanel.add(titleLbl);

        // close button
        JLabel closeButton = new JLabel("x", SwingConstants.RIGHT);
        closeButton.setFont(Global.menuFont);
        closeButton.setToolTipText("Click to close");
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hmPanel.remove(title);
                tabbedPane.remove(panel);
                if (control != null) {
                    control.newForm();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.BLACK);
            }
        });

        titlePanel.setName(title);
        titlePanel.add(closeButton);
        return titlePanel;
    }

    private void assignWindoInfo() {
        Global.x = this.getX();
        Global.y = this.getY();
        Global.height = this.getHeight();
        Global.width = this.getWidth();
    }

    private JPanel getPanel(String className) {
        String[] split = className.split(",");
        String cName = split[0]; // group name
        String srcAcc = split[1];
        String menuName = split[2];
        if (hmPanel.containsKey(menuName)) {
            return hmPanel.get(menuName);
        }
        switch (menuName) {
            case "Sale" -> {
                sale.setName(menuName);
                sale.setObserver(this);
                sale.setProgress(progress);
                sale.initMain();
                return sale;
            }
            case "Purchase" -> {
                purchase.setName(menuName);
                purchase.setObserver(this);
                purchase.setProgress(progress);
                purchase.initMain();
                return purchase;
            }
            case "Return In" -> {
                retIn.setName(menuName);
                retIn.setObserver(this);
                retIn.setProgress(progress);
                retIn.initMain();
                return retIn;
            }
            case "Return Out" -> {
                retOut.setName(menuName);
                retOut.setObserver(this);
                retOut.setProgress(progress);
                retOut.initMain();
                return retOut;
            }
            case "Stock In/Out" -> {
                stockInOut.setName(menuName);
                stockInOut.setObserver(this);
                stockInOut.setProgress(progress);
                stockInOut.initMain();
                return stockInOut;
            }
            case "Stock" -> {
                stockSetup.setName(menuName);
                stockSetup.setObserver(this);
                stockSetup.setProgress(progress);
                stockSetup.initMain();
                return stockSetup;
            }
            case "Opening" -> {
                openingSetup.setName(menuName);
                openingSetup.setObserver(this);
                openingSetup.setProgress(progress);
                openingSetup.initMain();
                return openingSetup;
            }
            case "Customer" -> {
                customerSetup.setName(menuName);
                customerSetup.setObserver(this);
                customerSetup.setProgress(progress);
                customerSetup.initMain();
                return customerSetup;
            }
            case "Supplier" -> {
                customerSetup.setName(menuName);
                supplierSetup.setObserver(this);
                supplierSetup.setProgress(progress);
                supplierSetup.initMain();
                return supplierSetup;
            }
            case "Other Setup" -> {
                otherSetup.setName(menuName);
                return otherSetup;
            }
            case "Role Setting" -> {
                roleSetting.setName(menuName);
                roleSetting.setObserver(this);
                roleSetting.setProgress(progress);
                roleSetting.initMain();
                return roleSetting;
            }
            case "Report" -> {
                report.setName(menuName);
                report.setObserver(this);
                report.setProgress(progress);
                report.initMain();
                return report;
            }
            case "System Property" -> {
                SystemProperty systemProperty = new SystemProperty();
                systemProperty.setUserRepo(userRepo);
                systemProperty.setInventoryRepo(inventoryRepo);
                systemProperty.setAccountRepo(accounRepo);
                systemProperty.setName(menuName);
                systemProperty.setObserver(this);
                systemProperty.setProgress(progress);
                systemProperty.setProperyType("System");
                systemProperty.initMain();
                return systemProperty;
            }
            case "Machine Property" -> {
                SystemProperty systemProperty = new SystemProperty();
                systemProperty.setUserRepo(userRepo);
                systemProperty.setInventoryRepo(inventoryRepo);
                systemProperty.setName(menuName);
                systemProperty.setObserver(this);
                systemProperty.setProgress(progress);
                systemProperty.setProperyType("Machine");
                systemProperty.initMain();
                return systemProperty;
            }
            case "Cloud Config" -> {
                cloudConfig.setName(menuName);
                cloudConfig.setObserver(this);
                cloudConfig.initMain();
                return cloudConfig;
            }
            case "Pattern Setup" -> {
                patternSetup.setName(menuName);
                patternSetup.setObserver(this);
                patternSetup.setProgress(progress);
                patternSetup.initMain();
                return patternSetup;
            }
            case "Reorder Level" -> {
                reorderLevel.setName(menuName);
                reorderLevel.setObserver(this);
                reorderLevel.setProgress(progress);
                reorderLevel.initMain();
                return reorderLevel;
            }
            case "Transfer" -> {
                transfer.setName(menuName);
                transfer.setObserver(this);
                transfer.setProgress(progress);
                transfer.initMain();
                return transfer;
            }
            case "Manufacture" -> {
                manufacture.setName(menuName);
                manufacture.setObserver(this);
                manufacture.setProgress(progress);
                manufacture.initMain();
                return manufacture;
            }
            case "Weight Loss" -> {
                weightLoss.setName(menuName);
                weightLoss.setObserver(this);
                weightLoss.setProgress(progress);
                weightLoss.initMain();
                return weightLoss;
            }
            case "Menu" -> {
                menuSetup.setName(menuName);
                menuSetup.setObserver(this);
                menuSetup.setProgress(progress);
                menuSetup.initMain();
                return menuSetup;
            }
            case "Department" -> {
                departmentSetup.setName(menuName);
                departmentSetup.setObserver(this);
                departmentSetup.setProgress(progress);
                departmentSetup.initMain();
                return departmentSetup;
            }
            case "COA Managment" -> {
                cOAManagment.setName(menuName);
                cOAManagment.setObserver(this);
                cOAManagment.setProgress(progress);
                cOAManagment.initMain();
                return cOAManagment;
            }
            case "User Setup" -> {
                userSetup.setName(menuName);
                userSetup.setObserver(this);
                userSetup.setProgress(progress);
                userSetup.initMain();
                return userSetup;
            }
            case "Company" -> {
                companySetup.setName(menuName);
                companySetup.setObserver(this);
                companySetup.setProgress(progress);
                companySetup.initMain();
                return companySetup;
            }
            case "G/L Listing" -> {
                gLReport.setName(menuName);
                gLReport.setObserver(this);
                gLReport.setProgress(progress);
                gLReport.initMain();
                return gLReport;
            }
            case "AR / AP" -> {
                aparReport.setName(menuName);
                aparReport.setObserver(this);
                aparReport.setProgress(progress);
                aparReport.initMain();
                return aparReport;
            }
            case "Financial Report" -> {
                financialReport.setName(menuName);
                financialReport.setObserver(this);
                financialReport.setProgress(progress);
                financialReport.initMain();
                return financialReport;
            }
            case "Chart Of Account" -> {
                cOASetup.setName(menuName);
                cOASetup.setObserver(this);
                cOASetup.setProgress(progress);
                cOASetup.initMain();
                return cOASetup;
            }
            case "Opening Balance" -> {
                coaOpening.setName(menuName);
                coaOpening.setObservaer(this);
                coaOpening.setProgress(progress);
                coaOpening.initMain();
                return coaOpening;
            }
            case "Journal Voucher" -> {
                journal.setName(menuName);
                journal.setObserver(this);
                journal.setProgress(progress);
                journal.initMain();
                return journal;
            }
            default -> {
                switch (cName) {
                    case "AllCash" -> {
                        AllCash cash = new AllCash();
                        cash.setName(menuName);
                        cash.setObserver(this);
                        cash.setProgress(progress);
                        cash.setTaskExecutor(taskExecutor);
                        cash.setAccountApi(accountApi);
                        cash.setSourceAccId(srcAcc);
                        cash.setAccounRepo(accounRepo);
                        cash.setUserRepo(userRepo);
                        cash.initMain();
                        return cash;
                    }
                }
            }
        }
        return null;
    }

    private void companyUserRoleAssign() {
        Mono<ResponseEntity<List<VRoleCompany>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-privilege-role-company")
                .queryParam("roleCode", Global.loginUser.getRole().getRoleCode())
                .build())
                .retrieve().toEntityList(VRoleCompany.class);
        List<VRoleCompany> listVUCA = result.block(Duration.ofMinutes(1)).getBody();
        if (listVUCA.isEmpty()) {
            JOptionPane.showMessageDialog(new JFrame(),
                    "No company assign to the user",
                    "Invalid Compay Access", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else if (listVUCA.size() > 1) {
            CompanyDialog companyDialog = new CompanyDialog();
            companyDialog.setListCompany(listVUCA);
            companyDialog.initTable();
            companyDialog.setLocationRelativeTo(null);
            companyDialog.setVisible(true);
        } else {
            VRoleCompany vuca = listVUCA.get(0);
            Global.roleCode = vuca.getRoleCode();
            Global.compCode = vuca.getCompCode();
            Global.companyName = vuca.getCompName();
            Global.companyAddress = vuca.getCompAddress();
            Global.companyPhone = vuca.getCompPhone();
            Global.currency = vuca.getCurrency();
            Global.startDate = Util1.toDateStr(vuca.getStartDate(), "dd/MM/yyyy");
            Global.endate = Util1.toDateStr(vuca.getEndDate(), "dd/MM/yyyy");
        }
    }

    public void initMain() {
        companyUserRoleAssign();
        initializeData();
        departmentAssign();
        initMenu();
    }

    private void departmentAssign() {
        DepartmentUser dep = null;
        List<DepartmentUser> listDep = userRepo.getDeparment();
        if (Util1.getBoolean(ProUtil.getProperty("department.option"))) {
            if (listDep.size() > 1) {
                DepartmentDialog dialog = new DepartmentDialog(listDep);
                dialog.initMain();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                dep = dialog.getDeparment();
            }
        } else {
            dep = listDep.get(0);
        }
        lblDep.setText(dep.getDeptName());
        Global.deptId = dep.getDeptId();
    }

    public void initMenu() {
        log.info("init menu.");
        menuBar.removeAll();
        Mono<ResponseEntity<List<VRoleMenu>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-privilege-role-menu-tree")
                .queryParam("roleCode", Global.roleCode)
                .build())
                .retrieve().toEntityList(VRoleMenu.class);
        result.subscribe((t) -> {
            createMenu(t.getBody());
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void createMenu(List<VRoleMenu> listVRM) {
        if (!listVRM.isEmpty()) {
            listVRM.forEach((menu) -> {
                if (menu.isAllow()) {
                    if (menu.getChild() != null) {
                        if (!menu.getChild().isEmpty()) {
                            JMenu parent = new JMenu();
                            parent.setText(menu.getMenuName());
                            parent.setFont(Global.menuFont);
                            parent.setName(menu.getMenuClass() + ","
                                    + Util1.isNull(menu.getAccount(), "-") + ","
                                    + menu.getMenuName());
                            //Need to add action listener
                            //====================================
                            menuBar.add(parent);
                            addChildMenu(parent, menu.getChild());
                        } else {  //No Child
                            JMenu jmenu = new JMenu();
                            jmenu.setText(menu.getMenuName());
                            jmenu.setFont(Global.menuFont);
                            jmenu.setName(menu.getMenuClass() + ","
                                    + Util1.isNull(menu.getAccount(), "-") + ","
                                    + menu.getMenuName());
                            //Need to add action listener
                            //====================================
                            menuBar.add(jmenu);
                        }
                    } else {  //No Child
                        JMenu jmenu = new JMenu();
                        jmenu.setText(menu.getMenuName());
                        jmenu.setFont(Global.menuFont);
                        jmenu.setName(menu.getMenuClass() + ","
                                + Util1.isNull(menu.getAccount(), "-") + ","
                                + menu.getMenuName());
                        //Need to add action listener
                        //====================================
                        menuBar.add(jmenu);
                    }
                }
            });
        }
        revalidate();
        repaint();
    }

    private void addChildMenu(JMenu parent, List<VRoleMenu> listVRM) {
        listVRM.forEach((vrMenu) -> {
            if (vrMenu.isAllow()) {
                if (vrMenu.getChild() != null) {
                    if (!vrMenu.getChild().isEmpty()) {
                        JMenu menu = new JMenu();
                        menu.setText(vrMenu.getMenuName());
                        menu.setFont(Global.menuFont);
                        menu.setName(vrMenu.getMenuClass() + ","
                                + Util1.isNull(vrMenu.getAccount(), "-") + ","
                                + vrMenu.getMenuName());
                        //Need to add action listener
                        //====================================
                        parent.add(menu);
                        addChildMenu(menu, vrMenu.getChild());
                    } else {  //No Child
                        JMenuItem menuItem = new JMenuItem();
                        menuItem.setText(vrMenu.getMenuName());
                        menuItem.addActionListener(menuListener);
                        menuItem.setFont(Global.menuFont);
                        menuItem.setName(vrMenu.getMenuClass() + ","
                                + Util1.isNull(vrMenu.getAccount(), "-") + ","
                                + vrMenu.getMenuName());
                        //====================================
                        parent.add(menuItem);
                    }
                } else {  //No Child
                    JMenuItem menuItem = new JMenuItem();

                    menuItem.setText(vrMenu.getMenuName());
                    menuItem.setName(vrMenu.getMenuClass() + ","
                            + Util1.isNull(vrMenu.getAccount(), "-") + ","
                            + vrMenu.getMenuName());                    //Need to add action listener
                    menuItem.addActionListener(menuListener);
                    menuItem.setFont(Global.menuFont);
                    //====================================
                    parent.add(menuItem);
                }
            }
        });
    }

    private void initializeData() {
        Global.parentForm = this;
        lblCompName.setText(Global.companyName);
        lblUserName.setText(Global.loginUser.getUserName());
        userRepo.setupProperty(Global.roleCode, Global.compCode, Global.macId);
    }

    private void logout() {
        dispose();
        CvInventoryApplication.restart();
    }

    @Override
    public void selected(Object source, Object selectObj) {
        String type = source.toString();
        switch (type) {
            case "control" -> {
                control = (PanelControl) selectObj;
                lblPanelName.setText(control.panelName());
            }
            case "menu" -> {
                initMenu();
            }
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

        jMenu1 = new javax.swing.JMenu();
        tabMain = new javax.swing.JTabbedPane();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPrint = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnNew1 = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnDelete = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnHistory = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btnNew = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnNew2 = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        btnFilter = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        btnFilter2 = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jSeparator4 = new javax.swing.JSeparator();
        progress = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        lblUserName = new javax.swing.JLabel();
        lblDep = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblCompName = new javax.swing.JLabel();
        lblPanelName = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Core Account  Cloud (V.2.0)");
        setAutoRequestFocus(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tabMain.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jToolBar1.setFocusable(false);

        btnSave1.setFont(Global.lableFont);
        btnSave1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save_18px.png"))); // NOI18N
        btnSave1.setText("Save - F5");
        btnSave1.setFocusable(false);
        btnSave1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave1);
        jToolBar1.add(jSeparator1);

        btnPrint.setFont(Global.lableFont);
        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/print_18px.png"))); // NOI18N
        btnPrint.setText("Print - F6");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);
        jToolBar1.add(jSeparator6);

        btnNew1.setFont(Global.lableFont);
        btnNew1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh_18px.png"))); // NOI18N
        btnNew1.setText("Refresh - F7");
        btnNew1.setFocusable(false);
        btnNew1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNew1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew1);
        jToolBar1.add(jSeparator5);

        btnDelete.setFont(Global.lableFont);
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/trash_18px.png"))); // NOI18N
        btnDelete.setText("Delete - F8");
        btnDelete.setFocusable(false);
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDelete);
        jToolBar1.add(jSeparator2);

        btnHistory.setFont(Global.lableFont);
        btnHistory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/time_machine_18px.png"))); // NOI18N
        btnHistory.setText("History - F9");
        btnHistory.setFocusable(false);
        btnHistory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHistory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoryActionPerformed(evt);
            }
        });
        jToolBar1.add(btnHistory);
        jToolBar1.add(jSeparator7);

        btnNew.setFont(Global.lableFont);
        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new_copy_18px.png"))); // NOI18N
        btnNew.setText("New - F10");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);
        jToolBar1.add(jSeparator3);

        btnNew2.setFont(Global.lableFont);
        btnNew2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logout_rounded_down_18px.png"))); // NOI18N
        btnNew2.setText("Logout - F11");
        btnNew2.setFocusable(false);
        btnNew2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNew2ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew2);
        jToolBar1.add(jSeparator8);

        btnFilter.setFont(Global.lableFont);
        btnFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/slider_18px.png"))); // NOI18N
        btnFilter.setText("Filter-F12");
        btnFilter.setToolTipText("Filter Bar");
        btnFilter.setFocusable(false);
        btnFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });
        jToolBar1.add(btnFilter);
        jToolBar1.add(jSeparator11);

        btnFilter2.setFont(Global.lableFont);
        btnFilter2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel_18px.png"))); // NOI18N
        btnFilter2.setText("Exit - Alt+F4");
        btnFilter2.setToolTipText("Filter Bar");
        btnFilter2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFilter2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFilter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter2ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnFilter2);
        jToolBar1.add(jSeparator9);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblUserName.setFont(Global.lableFont);
        lblUserName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUserName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/male_user_16px.png"))); // NOI18N
        lblUserName.setText("-");
        lblUserName.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblDep.setFont(Global.lableFont);
        lblDep.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/shop_16px.png"))); // NOI18N
        lblDep.setText("-");
        lblDep.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDep, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblUserName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDep))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblCompName.setFont(Global.companyFont);
        lblCompName.setForeground(Global.selectionColor);
        lblCompName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCompName.setText("-");

        lblPanelName.setFont(Global.companyFont);
        lblPanelName.setForeground(Global.selectionColor);
        lblPanelName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPanelName.setText("-");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPanelName, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCompName, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPanelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCompName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        menuBar.setFont(Global.menuFont);
        menuBar.setMargin(new java.awt.Insets(5, 5, 5, 5));
        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabMain)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jSeparator4)
            .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        assignWindoInfo();
    }//GEN-LAST:event_formComponentShown

    private void btnSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave1ActionPerformed
        // TODO add your handling code here:
        if (control != null)
            control.save();
    }//GEN-LAST:event_btnSave1ActionPerformed

    private void btnHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoryActionPerformed
        // TODO add your handling code here:
        if (control != null)
            control.history();
    }//GEN-LAST:event_btnHistoryActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        if (control != null)
            control.delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        if (control != null)

            control.newForm();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        if (control != null)
            control.print();
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnNew1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNew1ActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.refresh();
        }
    }//GEN-LAST:event_btnNew1ActionPerformed

    private void btnNew2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNew2ActionPerformed
        // TODO add your handling code here:
        logout();
    }//GEN-LAST:event_btnNew2ActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.filter();
        }
    }//GEN-LAST:event_btnFilterActionPerformed

    private void btnFilter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter2ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btnFilter2ActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnFilter2;
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNew1;
    private javax.swing.JButton btnNew2;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblCompName;
    private javax.swing.JLabel lblDep;
    private javax.swing.JLabel lblPanelName;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTabbedPane tabMain;
    // End of variables declaration//GEN-END:variables
}
