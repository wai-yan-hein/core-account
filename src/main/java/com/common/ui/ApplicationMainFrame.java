/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common.ui;

import com.CoreAccountApplication;
import com.acc.common.AccountRepo;
import com.acc.entry.AllCash;
import com.acc.entry.DrCrVoucher;
import com.acc.entry.Journal;
import com.acc.entry.JournalClosingStock;
import com.acc.entry.TraderAdjustment;
import com.acc.report.AparReport;
import com.acc.report.FinancialReport;
import com.acc.report.GLReport;
import com.acc.setup.COAManagment;

import com.acc.setup.COAOpening;
import com.acc.setup.COASetup;
import com.acc.setup.DepartmentSetup;
import com.acc.setup.TraderSetup;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.user.common.UserRepo;
import com.common.Util1;
import com.h2.service.StockService;
import com.user.setup.MenuSetup;
import com.user.model.DepartmentUser;
import com.inventory.model.VRoleMenu;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.entry.GRNEntry;
import com.inventory.ui.entry.Manufacture;
import com.inventory.ui.entry.OrderEntry;
import com.user.model.VRoleCompany;
import com.inventory.ui.entry.OtherSetupMain;
import com.inventory.ui.entry.Purchase;
import com.inventory.ui.entry.PurchaseByWeight;
import com.inventory.ui.entry.RFID;
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
import org.springframework.web.reactive.function.client.WebClient;
import com.inventory.ui.entry.Reports;
import com.inventory.ui.entry.SaleByBatch;
import com.inventory.ui.entry.SaleByWeight;
import com.inventory.ui.entry.Transfer;
import com.inventory.ui.entry.WeightLossEntry;
import com.inventory.ui.setup.OpeningSetup;
import com.inventory.ui.setup.PatternSetup;
import com.user.dialog.CompanyOptionDialog;
import com.user.dialog.DepartmentDialog;
import com.user.setup.SystemProperty;
import com.user.setup.AppUserSetup;
import com.user.setup.CloudConfig;
import com.user.setup.CompanySetup;
import com.user.setup.CompanyTemplate;
import com.user.setup.CurrencyExchange;
import com.user.setup.ProjectSetup;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.springframework.core.task.TaskExecutor;

/**
 *
 * @author Lenovo
 */
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
    private OrderEntry order;
    @Autowired
    private SaleByBatch saleByBatch;
    @Autowired
    private SaleByWeight saleByWeight;
    @Autowired
    private RFID rfid;
    @Autowired
    private Purchase purchase;
    @Autowired
    private PurchaseByWeight purchaseByWeight;
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
    private OtherSetupMain otherSetupMain;
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
    @Autowired
    private GRNEntry grnEntry;
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
    private TraderSetup traderSetup;

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private WebClient accountApi;
    @Autowired
    private FinancialReport financialReport;
    @Autowired
    private Journal journal;
    @Autowired
    private JournalClosingStock journalClosingStock;
    @Autowired
    private COAOpening coaOpening;
    @Autowired
    private DrCrVoucher drcrVoucher;

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
    @Autowired
    private CompanyTemplate companyTemplate;
    @Autowired
    private ProjectSetup projectSetup;
    @Autowired
    private CurrencyExchange currencyExchange;
    @Autowired
    private String hostName;
    @Autowired
    private StockService stockService;
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
     *
     */
    public ApplicationMainFrame() {
        initComponents();
        initKeyFoucsManager();
        initPopup();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit?", "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmed == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
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
                            case KeyEvent.VK_F5 -> {
                                if (btnSave.isEnabled()) {
                                    control.save();
                                }
                            }
                            case KeyEvent.VK_F6 -> {
                                if (btnPrint.isEnabled()) {
                                    control.print();
                                }
                            }
                            case KeyEvent.VK_F7 -> {
                                if (btnRefresh.isEnabled()) {
                                    control.refresh();
                                }
                            }
                            case KeyEvent.VK_F8 -> {
                                if (btnDelete.isEnabled()) {
                                    control.delete();
                                }
                            }
                            case KeyEvent.VK_F9 -> {
                                if (btnHistory.isEnabled()) {
                                    control.history();
                                }
                            }
                            case KeyEvent.VK_F10 -> {
                                if (btnNew.isEnabled()) {
                                    control.newForm();
                                }
                            }
                            case KeyEvent.VK_F11 -> {
                                if (btnLogout.isEnabled()) {
                                    logout();
                                }
                            }
                            case KeyEvent.VK_F12 -> {
                                if (btnFilter.isEnabled()) {
                                    control.filter();
                                }
                            }

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

    private void initPopup() {
        tabMain.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popupmenu = new JPopupMenu("Edit");
                    JMenuItem closeAll = new JMenuItem("Close All");
                    closeAll.addActionListener((ActionEvent ee) -> {
                        tabMain.removeAll();
                    });
                    popupmenu.add(closeAll);
                    popupmenu.show(tabMain, e.getX(), e.getY());
                }
            }

        });

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
            case "Order" -> {
                order.setName(menuName);
                order.setObserver(this);
                order.setProgress(progress);
                order.initMain();
                return order;
            }
            case "Sale By Batch" -> {
                saleByBatch.setName(menuName);
                saleByBatch.setObserver(this);
                saleByBatch.setProgress(progress);
                saleByBatch.initMain();
                return saleByBatch;
            }
            case "Sale By Weight" -> {
                saleByWeight.setName(menuName);
                saleByWeight.setObserver(this);
                saleByWeight.setProgress(progress);
                saleByWeight.initMain();
                return saleByWeight;
            }
            case "RFID" -> {
                rfid.setName(menuName);
                rfid.setObserver(this);
                rfid.setProgress(progress);
                rfid.initMain();
                return rfid;
            }
            case "Purchase" -> {
                purchase.setName(menuName);
                purchase.setObserver(this);
                purchase.setProgress(progress);
                purchase.initMain();
                return purchase;
            }
            case "Purchase By Weight" -> {
                purchaseByWeight.setName(menuName);
                purchaseByWeight.setObserver(this);
                purchaseByWeight.setProgress(progress);
                purchaseByWeight.initMain();
                return purchaseByWeight;
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
                otherSetupMain.setName(menuName);
                otherSetupMain.setObserver(this);
                otherSetupMain.setProgress(progress);
                otherSetupMain.initMain();
                return otherSetupMain;
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
                systemProperty.setAccountApi(accountApi);
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
                systemProperty.setAccountRepo(accounRepo);
                systemProperty.setAccountApi(accountApi);
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
            case "Project" -> {
                projectSetup.setName(menuName);
                projectSetup.setObserver(this);
                projectSetup.setProgress(progress);
                projectSetup.initMain();
                return projectSetup;
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
            case "GRN" -> {
                grnEntry.setName(menuName);
                grnEntry.setObserver(this);
                grnEntry.setProgress(progress);
                grnEntry.initMain();
                return grnEntry;
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
            case "Company Template" -> {
                companyTemplate.setName(menuName);
                companyTemplate.setObserver(this);
                companyTemplate.setProgress(progress);
                companyTemplate.intTabMain();
                return companyTemplate;
            }
            case "Currency" -> {
                currencyExchange.setName(menuName);
                currencyExchange.setObserver(this);
                currencyExchange.setProgress(progress);
                currencyExchange.initMain();
                return currencyExchange;
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
            case "Dr & Cr Voucher" -> {
                drcrVoucher.setName(menuName);
                drcrVoucher.setObserver(this);
                drcrVoucher.setProgress(progress);
                drcrVoucher.initMain();
                return drcrVoucher;
            }
            case "Journal Stock Closing" -> {
                journalClosingStock.setName(menuName);
                journalClosingStock.setObserver(this);
                journalClosingStock.setProgress(progress);
                journalClosingStock.initMain();
                return journalClosingStock;
            }
            case "Trader" -> {
                traderSetup.setName(menuName);
                traderSetup.setObserver(this);
                traderSetup.setProgress(progress);
                traderSetup.initMain();
                return traderSetup;
            }
            case "Trader Adjustment" -> {
                TraderAdjustment adj = new TraderAdjustment();
                adj.setName(menuName);
                adj.setObserver(this);
                adj.setProgress(progress);
                adj.setTaskExecutor(taskExecutor);
                adj.setAccountApi(accountApi);
                adj.setAccounRepo(accounRepo);
                adj.setUserRepo(userRepo);
                adj.initMain();
                return adj;
            }
            default -> {
                switch (cName) {
                    case "AllCash" -> {
                        AllCash cash = new AllCash(false);
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
                    case "DayBook" -> {
                        AllCash db = new AllCash(true);
                        db.setName(menuName);
                        db.setObserver(this);
                        db.setProgress(progress);
                        db.setTaskExecutor(taskExecutor);
                        db.setAccountApi(accountApi);
                        db.setSourceAccId(srcAcc);
                        db.setAccounRepo(accounRepo);
                        db.setUserRepo(userRepo);
                        db.initMain();
                        return db;
                    }
                }

            }
        }
        return null;
    }

    private void companyUserRoleAssign() {
        userApi.get()
                .uri(builder -> builder.path("/user/get-privilege-role-company")
                .queryParam("roleCode", Global.loginUser.getRoleCode())
                .build())
                .retrieve()
                .bodyToFlux(VRoleCompany.class)
                .collectList()
                .subscribe((t) -> {
                    if (t.isEmpty()) {
                        JOptionPane.showMessageDialog(new JFrame(),
                                "No company assign to the user",
                                "Invalid Compay Access", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    } else if (t.size() > 1) {
                        CompanyOptionDialog d = new CompanyOptionDialog(Global.parentForm);
                        d.setListCompany(t);
                        d.initMain();
                        d.setLocationRelativeTo(null);
                        d.setVisible(true);
                        if (d.getCompanyInfo() == null) {
                            int confirmed = JOptionPane.showConfirmDialog(null,
                                    "Are you sure you want to exit?", "Exit Confirmation",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (confirmed == JOptionPane.YES_OPTION) {
                                dispose();
                                System.exit(0);
                            }
                        }
                        assignCompany(d.getCompanyInfo());
                    } else {
                        assignCompany(t.get(0));
                    }
                    departmentAssign();
                    initMenu();
                    lblCompName.setText(Global.companyName);
                    lblUserName.setText(Global.loginUser.getUserName());
                    userRepo.setupProperty();
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });

    }

    private void assignCompany(VRoleCompany vuca) {
        Global.roleCode = vuca.getRoleCode();
        Global.compCode = vuca.getCompCode();
        Global.companyName = vuca.getCompName();
        Global.companyAddress = vuca.getCompAddress();
        Global.companyPhone = vuca.getCompPhone();
        Global.currency = vuca.getCurrency();
        Global.startDate = Util1.toDateStr(vuca.getStartDate(), "dd/MM/yyyy");
        Global.endate = Util1.toDateStr(vuca.getEndDate(), "dd/MM/yyyy");
        Global.batchLock = vuca.isBatchLock();
        if (Global.batchLock) {
            lblLock.setText("Batch Lock.");
            lblLock.setForeground(Color.red);
        } else {
            lblLock.setText(null);
        }
    }

    public void initMain() {
        Global.parentForm = this;
        scheduleDownload();
        scheduleNetwork();
        scheduleExit();
        initUser();
        companyUserRoleAssign();
    }

    private void initUser() {
        userRepo.getAppUser().subscribe((list) -> {
            list.forEach((t) -> {
                Global.hmUser.put(t.getUserCode(), t.getUserShortName());
            });
        });
    }

    private void departmentAssign() {
        userRepo.getDeparment().subscribe((t) -> {
            DepartmentUser dep;
            if (Util1.getBoolean(ProUtil.getProperty("department.option"))) {
                if (t.size() > 1) {
                    DepartmentDialog dialog = new DepartmentDialog(t);
                    dialog.initMain();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                    dep = dialog.getDeparment();
                } else {
                    dep = t.get(0);
                }
            } else {
                dep = t.get(0);
            }
            lblDep.setText(dep.getDeptName());
            Global.deptId = dep.getDeptId();
        }, (e) -> {
            log.error(e.getMessage());
        });

    }

    public void initMenu() {
        menuBar.removeAll();
        String url = "/user/get-privilege-role-menu-tree?roleCode=" + Global.roleCode + "&compCode=" + Global.compCode;
        userApi.get().uri(url)
                .retrieve().bodyToFlux(VRoleMenu.class)
                .collectList()
                .subscribe((t) -> {
                    createMenu(t);
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

    private void logout() {
        dispose();
        CoreAccountApplication.restart();
    }

    private void scheduleDownload() {
        downloadInventory();
    }

    private void downloadUser() {

    }

    private void downloadInventory() {
        downloadStock();
    }

    private void downloadAccount() {

    }

    private void downloadStock() {
        inventoryRepo.getStock(false).subscribe((t) -> {
            t.forEach((s) -> {
                stockService.save(s);
            });
            log.info("stock download done.");
        }, (e) -> {
            log.info(e.getMessage());
        });
    }

    private void scheduleNetwork() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    InetAddress inet = InetAddress.getByName(hostName);
                    long start = new GregorianCalendar().getTimeInMillis();
                    if (inet.isReachable(5000)) {
                        long finish = new GregorianCalendar().getTimeInMillis();
                        long time = finish - start;
                        setNetwork(time);
                    } else {
                        setNetwork(-1);
                    }
                } catch (IOException e) {
                    log.error("scheduleNetwork : " + e.getMessage());
                }
            }
        }, 0, Duration.ofSeconds(5).toMillis());
    }

    private void scheduleExit() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        log.info("exit");
                        System.exit(0);
                    }
                }, Duration.ofMinutes(5).toMillis());
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Do you want to exit the program due to inactivity? Program will exit within 5 minutes.", "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmed == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    timer.cancel();
                }
            }
        }, Duration.ofDays(8).toMillis(), Duration.ofMinutes(8).toMillis());
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
            case "save" -> {
                btnSave.setEnabled(Util1.getBoolean(selectObj.toString()));
            }
            case "delete" -> {
                btnDelete.setEnabled(Util1.getBoolean(selectObj.toString()));
            }
            case "print" -> {
                btnPrint.setEnabled(Util1.getBoolean(selectObj.toString()));
            }
            case "history" -> {
                btnHistory.setEnabled(Util1.getBoolean(selectObj.toString()));
            }
            case "change-name" -> {
                lblCompName.setText(Global.companyName);
            }
        }
    }

    private void setNetwork(long time) {
//        log.info(time + "ms");
        if (time < 0) {
            lblNetwork.setText("Offline");
            lblNetwork.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/wifi_no_internet.png")));
        } else if (time < 100) {
            lblNetwork.setText(time + " ms");
            lblNetwork.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/wi-fi_green.png")));
        } else if (time >= 100 && time < 150) {
            lblNetwork.setText(time + " ms");
            lblNetwork.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/wifi_yellow.png")));
        } else {
            lblNetwork.setText(time + " ms");
            lblNetwork.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/wi-fi_red.png")));
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
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPrint = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnRefresh = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnDelete = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnHistory = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btnNew = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnLogout = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        btnFilter = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        btnExit = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jSeparator4 = new javax.swing.JSeparator();
        progress = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        lblUserName = new javax.swing.JLabel();
        lblDep = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblCompName = new javax.swing.JLabel();
        lblPanelName = new javax.swing.JLabel();
        lblLock = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblNetwork = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Core Account  Cloud (V.2.0)");
        setAutoRequestFocus(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabMain.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jToolBar1.setFocusable(false);

        btnSave.setFont(Global.lableFont);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save_18px.png"))); // NOI18N
        btnSave.setText("Save - F5");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);
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

        btnRefresh.setFont(Global.lableFont);
        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh_18px.png"))); // NOI18N
        btnRefresh.setText("Refresh - F7");
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRefresh);
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

        btnLogout.setFont(Global.lableFont);
        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logout_rounded_down_18px.png"))); // NOI18N
        btnLogout.setText("Logout - F11");
        btnLogout.setFocusable(false);
        btnLogout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLogout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        jToolBar1.add(btnLogout);
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

        btnExit.setFont(Global.lableFont);
        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel_18px.png"))); // NOI18N
        btnExit.setText("Exit - Alt+F4");
        btnExit.setToolTipText("Filter Bar");
        btnExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        jToolBar1.add(btnExit);
        jToolBar1.add(jSeparator9);

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
                    .addComponent(lblUserName, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(lblDep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblUserName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDep)
                .addContainerGap())
        );

        lblCompName.setFont(Global.companyFont);
        lblCompName.setForeground(Global.selectionColor);
        lblCompName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCompName.setText("-");

        lblPanelName.setFont(Global.companyFont);
        lblPanelName.setForeground(Global.selectionColor);
        lblPanelName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPanelName.setText("-");

        lblLock.setFont(Global.companyFont);
        lblLock.setForeground(Global.selectionColor);
        lblLock.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblLock.setText("-");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPanelName, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLock, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCompName, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPanelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCompName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        lblNetwork.setFont(Global.lableFont);
        lblNetwork.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNetwork.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/wifi_no_internet.png"))); // NOI18N
        lblNetwork.setText("Offline ");
        lblNetwork.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNetwork, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNetwork, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        assignWindoInfo();
    }//GEN-LAST:event_formComponentShown

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        if (control != null)
            control.save();
    }//GEN-LAST:event_btnSaveActionPerformed

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

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.refresh();
        }
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        logout();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.filter();
        }
    }//GEN-LAST:event_btnFilterActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
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
    private javax.swing.JLabel lblLock;
    private javax.swing.JLabel lblNetwork;
    private javax.swing.JLabel lblPanelName;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTabbedPane tabMain;
    // End of variables declaration//GEN-END:variables
}
