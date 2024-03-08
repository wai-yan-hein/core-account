/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ui;

import com.CloudIntegration;
import com.CoreAccountApplication;
import com.H2Repo;
import com.IconManager;
import com.SSEListener;
import com.repo.AccountRepo;
import com.acc.entry.AllCash;
import com.acc.entry.DrCrVoucher;
import com.acc.entry.Journal;
import com.acc.entry.JournalClosingStock;
import com.acc.entry.TraderAdjustment;
import com.acc.report.AparReport;
import com.acc.report.FinancialReport;
import com.acc.report.GLReport;
import com.acc.report.excel.ExcelReport;
import com.acc.setup.COAManagment;
import com.acc.setup.COAOpening;
import com.acc.setup.COASetup;
import com.acc.setup.DepartmentSetup;
import com.acc.setup.TraderSetup;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.Global;
import com.common.IconUtil;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.repo.UserRepo;
import com.common.Util1;
import com.common.YNOptionPane;
import com.dms.CoreDrive;
import com.h2.dao.DateFilterRepo;
import com.user.setup.MenuSetup;
import com.user.model.DepartmentUser;
import com.inventory.entity.VRoleMenu;
import com.inventory.ui.entry.GRNEntry;
import com.inventory.ui.entry.LabourPaymentEntry;
import com.repo.InventoryRepo;
import com.inventory.ui.entry.LandingEntry;
import com.inventory.ui.entry.Manufacture;
import com.user.model.CompanyInfo;
import com.inventory.ui.entry.OtherSetupMain;
import com.inventory.ui.entry.Purchase;
import com.inventory.ui.entry.PurchaseDynamic;
import com.inventory.ui.entry.RFID;
import com.inventory.ui.entry.PaymentEntry;
import com.inventory.ui.entry.ReorderLevelEntry;
import com.inventory.ui.entry.ReturnIn;
import com.inventory.ui.entry.ReturnOut;
import com.user.setup.RoleSetting;
import com.inventory.ui.entry.Sale;
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
import com.inventory.ui.entry.Reports;
import com.inventory.ui.entry.MillingEntry;
import com.inventory.ui.entry.OrderDynamic;
import com.inventory.ui.entry.OrderNoteEntry;
import com.inventory.ui.entry.PurOrderHisEntry;
import com.inventory.ui.entry.SaleDynamic;
import com.inventory.ui.entry.StockInOutEntry;
import com.inventory.ui.entry.ConsignEntry;
import com.inventory.ui.entry.SaleOrderEntry;
import com.inventory.ui.entry.StockPaymentEntry;
import com.inventory.ui.entry.Transfer;
import com.inventory.ui.entry.WeightEntry;
import com.inventory.ui.entry.WeightLossEntry;
import com.inventory.ui.entry.dialog.StockBalanceFrame;
import com.inventory.ui.setup.EmployeeSetup;
import com.inventory.ui.setup.JobSetup;
import com.inventory.ui.setup.LanguageSetup;
import com.inventory.ui.setup.OpeningDynamic;
import com.inventory.ui.setup.OutputCostSetup;
import com.inventory.ui.setup.PatternSetup;
import com.inventory.ui.setup.StockFormulaSetup;
import com.repo.DMSRepo;
import com.repo.HMSRepo;
import com.ui.management.StockBalance;
import com.ui.management.StockPayable;
import com.user.dialog.CompanyOptionDialog;
import com.user.dialog.DepartmentDialog;
import com.user.dialog.ProgramDownloadDialog;
import com.user.setup.SystemProperty;
import com.user.setup.AppUserSetup;
import com.user.setup.CompanySetup;
import com.user.setup.CompanyTemplate;
import com.user.setup.CurrencyExchange;
import com.user.setup.HMSIntegration;
import com.user.setup.ProjectSetup;
import java.awt.KeyEventDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Slf4j
@Component
public class ApplicationMainFrame extends javax.swing.JFrame implements SelectionObserver {

    @Autowired
    private H2Repo h2Repo;
    @Autowired
    private CloudIntegration integration;
    @Autowired
    private AccountRepo accounRepo;
    @Autowired
    private HMSRepo hmsRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private DMSRepo dmsRepo;
    @Autowired
    private String getToken;
    @Autowired
    private Environment environment;
//account
    @Autowired
    private TaskExecutor taskExecutor;

//user
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private String hostName;
    @Autowired
    private DateFilterRepo dateFilterRepo;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private SSEListener sseListener;
    @Autowired
    private DateLockUtil dateLockUtil;
    private PanelControl control;
    private ProgramDownloadDialog pdDialog;
    private StockBalanceFrame stockBalanceFrame;
    private Timer inactivityTimer;
    private long lastActivityTime;
    private final HashMap<String, JPanel> hmPanel = new HashMap<>();
    private KeyEventDispatcher keyEventDispatcher; // Maintain a reference to the dispatcher
    private JDialog ynDialog;
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
        initIcon();
        //addMouseAndKeyboardListeners();
        //startInactivityTimer();
    }

    private void initIcon() {
        File file = new File("images/icons");
        File[] listFile = file.listFiles();

        if (listFile != null) {
            for (File f : listFile) {
                if (f.isFile() && isImageFile(f)) {
                    String[] name = f.getName().split("\\.");
                    String filePath = f.getAbsolutePath();
                    ImageIcon icon = new ImageIcon(filePath);
                    IconManager.put(name[0], icon);
                }
            }
        }
    }

    private boolean isImageFile(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf(".");
        if (lastDotIndex > 0) {
            String extension = name.substring(lastDotIndex + 1).toLowerCase();
            return extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif");
        }
        return false;
    }

    private void exitProgram() {
        int confirmed = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to exit?", "Exit Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmed == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void initToolBarIcon() {
        SwingUtilities.invokeLater(() -> {
            btnSave.setIcon(IconUtil.getIcon("save.svg"));
            btnPrint.setIcon(IconUtil.getIcon("print.svg"));
            btnRefresh.setIcon(IconUtil.getIcon("refresh.svg"));
            btnDelete.setIcon(IconUtil.getIcon("delete.svg"));
            btnHistory.setIcon(IconUtil.getIcon("history.svg"));
            btnNew.setIcon(IconUtil.getIcon("new.svg"));
            btnLogout.setIcon(IconUtil.getIcon("logout.svg"));
            btnFilter.setIcon(IconUtil.getIcon("search.svg"));
            btnExit.setIcon(IconUtil.getIcon("exit.svg"));
        });
    }

// Apply the color filter to the icon
    private void initKeyFoucsManager() {
        keyEventDispatcher = (KeyEvent ke) -> {
            if (ke.isAltDown()) {
                if (ke.getKeyCode() == KeyEvent.VK_F4) {
                    System.exit(0);
                }
            }
            switch (ke.getID()) {
                case KeyEvent.KEY_PRESSED -> {
                    if (control != null) {
                        setKeyEventDispatcherEnabled(false);
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
                        setKeyEventDispatcherEnabled(true);
                    }
                }
            }
            return false;
        };

        // Add the dispatcher when initializing
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
    }

    private void setKeyEventDispatcherEnabled(boolean enabled) {
        if (enabled) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
        } else {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
        }
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
        int version = Util1.getInteger(split[3]);
        if (hmPanel.containsKey(menuName)) {
            return hmPanel.get(menuName);
        }
        if (Global.deptId == null || Global.deptId == 0) {
            JOptionPane.showMessageDialog(this, "No deapartment assign. Please logout.");
            return null;
        }
        enableToolBar(true);
        switch (menuName) {
            case "Sale" -> {
                Sale sale = new Sale();
                sale.setName(menuName);
                sale.setObserver(this);
                sale.setProgress(progress);
                sale.setStockBalanceDialog(stockBalanceFrame);
                sale.setUserRepo(userRepo);
                sale.setInventoryRepo(inventoryRepo);
                sale.setAccountRepo(accounRepo);
                sale.setTaskExecutor(taskExecutor);
                sale.setIntegration(integration);
                sale.setH2Repo(h2Repo);
                sale.initMain();
                return sale;
            }
            case "Order", "Purchase Order" -> {
                int type = getOrderType(menuName);
                OrderDynamic orderDynamic = new OrderDynamic(type);
                orderDynamic.setName(menuName);
                orderDynamic.setInventoryRepo(inventoryRepo);
                orderDynamic.setUserRepo(userRepo);
                orderDynamic.setObserver(this);
                orderDynamic.setProgress(progress);
                orderDynamic.initMain();
                return orderDynamic;
            }
            case "Sale Order" -> {
                SaleOrderEntry saleOrderEntry = new SaleOrderEntry();
                saleOrderEntry.setName(menuName);
                saleOrderEntry.setInventoryRepo(inventoryRepo);
                saleOrderEntry.setUserRepo(userRepo);
                saleOrderEntry.setObserver(this);
                saleOrderEntry.setProgress(progress);
                saleOrderEntry.initMain();
                return saleOrderEntry;
            }
            case "Sale By Weight", "Sale Export", "Sale Rice", "Sale Paddy", "Sale By Batch", "POS" -> {
                int type = getSaleType(menuName);
                SaleDynamic s = new SaleDynamic(type);
                s.setUserRepo(userRepo);
                s.setInventoryRepo(inventoryRepo);
                s.setAccountRepo(accounRepo);
                s.setName(menuName);
                s.setObserver(this);
                s.setProgress(progress);
                s.setStockBalanceDialog(stockBalanceFrame);
                s.initMain();
                return s;
            }
            case "RFID" -> {
                RFID rfid = new RFID();
                rfid.setName(menuName);
                rfid.setObserver(this);
                rfid.setProgress(progress);
                rfid.setUserRepo(userRepo);
                rfid.setInventoryRepo(inventoryRepo);
                rfid.initMain();
                return rfid;
            }
            case "Purchase" -> {
                Purchase purchase = new Purchase();
                purchase.setName(menuName);
                purchase.setObserver(this);
                purchase.setProgress(progress);
                purchase.setUserRepo(userRepo);
                purchase.setInventoryRepo(inventoryRepo);
                purchase.setAccountRepo(accounRepo);
                purchase.setIntegration(integration);
                purchase.initMain();
                return purchase;
            }
            case "Purchase By Weight", "Purchase Rice", "Purchase Export", "Purchase Paddy", "Purchase Other" -> {
                int type = getPurType(menuName, version);
                PurchaseDynamic p = new PurchaseDynamic(type);
                p.setName(menuName);
                p.setUserRepo(userRepo);
                p.setInventoryRepo(inventoryRepo);
                p.setAccountRepo(accounRepo);
                p.setObserver(this);
                p.setProgress(progress);
                p.initMain();
                return p;
            }
            case "Return In" -> {
                ReturnIn retIn = new ReturnIn();
                retIn.setName(menuName);
                retIn.setObserver(this);
                retIn.setProgress(progress);
                retIn.setUserRepo(userRepo);
                retIn.setInventoryRepo(inventoryRepo);
                retIn.setIntegration(integration);
                retIn.initMain();
                return retIn;
            }
            case "Return Out" -> {
                ReturnOut retOut = new ReturnOut();
                retOut.setName(menuName);
                retOut.setObserver(this);
                retOut.setProgress(progress);
                retOut.setUserRepo(userRepo);
                retOut.setInventoryRepo(inventoryRepo);
                retOut.setIntegration(integration);
                retOut.initMain();
                return retOut;
            }
            case "Stock In/Out", "Stock In/Out By Weight", "Stock In/Out Paddy" -> {
                int type = getStockIOType(menuName);
                StockInOutEntry io = new StockInOutEntry(type);
                io.setName(menuName);
                io.setObserver(this);
                io.setProgress(progress);
                io.setUserRepo(userRepo);
                io.setInventoryRepo(inventoryRepo);
                io.initMain();
                return io;
            }
            case "Stock" -> {
                StockSetup setup = new StockSetup(ProUtil.isStockNoUnit());
                setup.setName(menuName);
                setup.setUserRepo(userRepo);
                setup.setInventoryRepo(inventoryRepo);
                setup.setObserver(this);
                setup.setProgress(progress);
                setup.initMain();
                return setup;
            }
            case "Language" -> {
                LanguageSetup setup = new LanguageSetup();
                setup.setName(menuName);
                setup.setUserRepo(userRepo);
                setup.setInventoryRepo(inventoryRepo);
                setup.setObserver(this);
                setup.setProgress(progress);
                setup.initMain();
                return setup;
            }
            case "Stock Formula" -> {
                StockFormulaSetup setup = new StockFormulaSetup();
                setup.setName(menuName);
                setup.setInventoryRepo(inventoryRepo);
                setup.setObserver(this);
                setup.setProgress(progress);
                setup.initMain();
                return setup;
            }
            case "Stock Opening", "Stock Opening Payable", "Stock Opening Paddy", "Consign Opening" -> {
                int type = getOpeningType(menuName);
                OpeningDynamic openingSetup = new OpeningDynamic(type);
                openingSetup.setInventoryRepo(inventoryRepo);
                openingSetup.setUserRepo(userRepo);
                openingSetup.setName(menuName);
                openingSetup.setObserver(this);
                openingSetup.setProgress(progress);
                openingSetup.setTaskExecutor(taskExecutor);
                openingSetup.initMain();
                return openingSetup;
            }
            case "Customer" -> {
                CustomerSetup customerSetup = new CustomerSetup();
                customerSetup.setName(menuName);
                customerSetup.setObserver(this);
                customerSetup.setProgress(progress);
                customerSetup.setUserRepo(userRepo);
                customerSetup.setInventoryRepo(inventoryRepo);
                customerSetup.setAccountRepo(accounRepo);
                customerSetup.setTaskExecutor(taskExecutor);
                customerSetup.initMain();
                return customerSetup;
            }
            case "Supplier" -> {
                SupplierSetup supplierSetup = new SupplierSetup();
                supplierSetup.setName(menuName);
                supplierSetup.setObserver(this);
                supplierSetup.setProgress(progress);
                supplierSetup.setUserRepo(userRepo);
                supplierSetup.setInventoryRepo(inventoryRepo);
                supplierSetup.setAccountRepo(accounRepo);
                supplierSetup.initMain();
                return supplierSetup;
            }
            case "Employee" -> {
                EmployeeSetup employeeSetup = new EmployeeSetup();
                employeeSetup.setName(menuName);
                employeeSetup.setObserver(this);
                employeeSetup.setProgress(progress);
                employeeSetup.setUserRepo(userRepo);
                employeeSetup.setInventoryRepo(inventoryRepo);
                employeeSetup.setAccountRepo(accounRepo);
                employeeSetup.setTaskExecutor(taskExecutor);
                employeeSetup.initMain();
                return employeeSetup;
            }
            case "Output Cost" -> {
                OutputCostSetup outputCostSetup = new OutputCostSetup();
                outputCostSetup.setName(menuName);
                outputCostSetup.setObserver(this);
                outputCostSetup.setProgress(progress);
                outputCostSetup.setInventoryRepo(inventoryRepo);
                outputCostSetup.setAccountRepo(accounRepo);
                outputCostSetup.initMain();
                return outputCostSetup;
            }
            case "Job" -> {
                JobSetup setup = new JobSetup();
                setup.setName(menuName);
                setup.setUserRepo(userRepo);
                setup.setInventoryRepo(inventoryRepo);
                setup.setObserver(this);
                setup.setProgress(progress);
                setup.initMain();
                return setup;
            }
            case "Other Setup" -> {
                OtherSetupMain otherSetupMain = new OtherSetupMain();
                otherSetupMain.setName(menuName);
                otherSetupMain.setObserver(this);
                otherSetupMain.setProgress(progress);
                otherSetupMain.setUserRepo(userRepo);
                otherSetupMain.setInventoryRepo(inventoryRepo);
                otherSetupMain.setAccountRepo(accounRepo);
                otherSetupMain.initMain();
                return otherSetupMain;
            }
            case "Role Setting" -> {
                RoleSetting roleSetting = new RoleSetting();
                roleSetting.setName(menuName);
                roleSetting.setObserver(this);
                roleSetting.setProgress(progress);
                roleSetting.setUserRepo(userRepo);
                roleSetting.setInventoryRepo(inventoryRepo);
                roleSetting.setAccountRepo(accounRepo);
                roleSetting.initMain();
                return roleSetting;
            }
            case "Report" -> {
                Reports report = new Reports();
                report.setName(menuName);
                report.setObserver(this);
                report.setProgress(progress);
                report.setInventoryRepo(inventoryRepo);
                report.setUserRepo(userRepo);
                report.setTaskExecutor(taskExecutor);
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
                systemProperty.setAccountRepo(accounRepo);
                systemProperty.setName(menuName);
                systemProperty.setObserver(this);
                systemProperty.setProgress(progress);
                systemProperty.setProperyType("Machine");
                systemProperty.initMain();
                return systemProperty;
            }
            case "Project" -> {
                ProjectSetup projectSetup = new ProjectSetup();
                projectSetup.setName(menuName);
                projectSetup.setObserver(this);
                projectSetup.setProgress(progress);
                projectSetup.setUserRepo(userRepo);
                projectSetup.initMain();
                return projectSetup;
            }
            case "Core Drive" -> {
                CoreDrive drive = new CoreDrive();
                drive.setProgress(progress);
                drive.setObserver(this);
                drive.setDmsRepo(dmsRepo);
                drive.initMain();
                return drive;
            }
            case "Order Note" -> {
                int type = getOrderNoteType(menuName);
                OrderNoteEntry entry = new OrderNoteEntry(type);
                entry.setProgress(progress);
                entry.setObserver(this);
                entry.setDmsRepo(dmsRepo);
                entry.setInventoryRepo(inventoryRepo);
                entry.setTaskExecutor(taskExecutor);
                entry.initMain();
                return entry;
            }
            case "Pattern Setup" -> {
                PatternSetup patternSetup = new PatternSetup();
                patternSetup.setName(menuName);
                patternSetup.setObserver(this);
                patternSetup.setProgress(progress);
                patternSetup.setInventoryRepo(inventoryRepo);
                patternSetup.initMain();
                return patternSetup;
            }
            case "Reorder Level" -> {
                ReorderLevelEntry reorderLevel = new ReorderLevelEntry();
                reorderLevel.setName(menuName);
                reorderLevel.setObserver(this);
                reorderLevel.setProgress(progress);
                reorderLevel.setInventoryRepo(inventoryRepo);
                reorderLevel.initMain();
                return reorderLevel;
            }
            case "Transfer", "Transfer Paddy" -> {
                int type = getTransferType(menuName);
                Transfer transfer = new Transfer(type);
                transfer.setName(menuName);
                transfer.setObserver(this);
                transfer.setProgress(progress);
                transfer.setUserRepo(userRepo);
                transfer.setInventoryRepo(inventoryRepo);
                transfer.initMain();
                return transfer;
            }
            case "Manufacture" -> {
                Manufacture manufacture = new Manufacture();
                manufacture.setName(menuName);
                manufacture.setObserver(this);
                manufacture.setProgress(progress);
                manufacture.setUserRepo(userRepo);
                manufacture.setInventoryRepo(inventoryRepo);
                manufacture.setIntegration(integration);
                manufacture.initMain();
                return manufacture;
            }
            case "Weight Loss" -> {
                WeightLossEntry weightLoss = new WeightLossEntry();
                weightLoss.setName(menuName);
                weightLoss.setObserver(this);
                weightLoss.setProgress(progress);
                weightLoss.setUserRepo(userRepo);
                weightLoss.setInventoryRepo(inventoryRepo);
                weightLoss.initMain();
                return weightLoss;
            }
            case "GRN" -> {
                GRNEntry entry = new GRNEntry();
                entry.setName(menuName);
                entry.setObserver(this);
                entry.setProgress(progress);
                entry.setInventoryRepo(inventoryRepo);
                entry.setUserRepo(userRepo);
                entry.initMain();
                return entry;
            }
            case "Customer Payment" -> {
                PaymentEntry payment = new PaymentEntry("C");
                payment.setUserRepo(userRepo);
                payment.setInventoryRepo(inventoryRepo);
                payment.setAccountRepo(accounRepo);
                payment.setName(menuName);
                payment.setObserver(this);
                payment.setProgress(progress);
                payment.initMain();
                return payment;
            }
            case "Supplier Payment" -> {
                PaymentEntry payment = new PaymentEntry("S");
                payment.setUserRepo(userRepo);
                payment.setInventoryRepo(inventoryRepo);
                payment.setAccountRepo(accounRepo);
                payment.setName(menuName);
                payment.setObserver(this);
                payment.setProgress(progress);
                payment.initMain();
                return payment;
            }
            case "Labour Payment" -> {
                LabourPaymentEntry payment = new LabourPaymentEntry();
                payment.setUserRepo(userRepo);
                payment.setInventoryRepo(inventoryRepo);
                payment.setAccountRepo(accounRepo);
                payment.setName(menuName);
                payment.setObserver(this);
                payment.setProgress(progress);
                payment.initMain();
                return payment;
            }
            case "Paddy Issue", "Rice Issue" -> {
                int type = 0;
                switch (menuName) {
                    case "Paddy Issue" ->
                        type = StockPaymentEntry.QTY;
                    case "Rice Issue" ->
                        type = StockPaymentEntry.BAG;
                }
                StockPaymentEntry payment = new StockPaymentEntry("C", type);
                payment.setInventoryRepo(inventoryRepo);
                payment.setName(menuName);
                payment.setObserver(this);
                payment.setProgress(progress);
                payment.initMain();
                return payment;
            }
            case "Paddy Receive", "Rice Receive" -> {
                int type = 0;
                switch (menuName) {
                    case "Paddy Receive" ->
                        type = StockPaymentEntry.QTY;
                    case "Rice Receive" ->
                        type = StockPaymentEntry.BAG;
                }
                StockPaymentEntry payment = new StockPaymentEntry("S", type);
                payment.setInventoryRepo(inventoryRepo);
                payment.setName(menuName);
                payment.setObserver(this);
                payment.setProgress(progress);
                payment.initMain();
                return payment;
            }
            case "Consign Issue" -> {
                ConsignEntry stockIss = new ConsignEntry("I");
                stockIss.setUserRepo(userRepo);
                stockIss.setInventoryRepo(inventoryRepo);
                stockIss.setName(menuName);
                stockIss.setObserver(this);
                stockIss.setProgress(progress);
                stockIss.initMain();
                return stockIss;
            }
            case "Consign Receive" -> {
                ConsignEntry stockRec = new ConsignEntry("R");
                stockRec.setUserRepo(userRepo);
                stockRec.setInventoryRepo(inventoryRepo);
                stockRec.setName(menuName);
                stockRec.setObserver(this);
                stockRec.setProgress(progress);
                stockRec.initMain();
                return stockRec;
            }
            case "Purchase Order Entry" -> {
                PurOrderHisEntry entry = new PurOrderHisEntry();
                entry.setName(menuName);
                entry.setObserver(this);
                entry.setProgress(progress);
                entry.setInventoryRepo(inventoryRepo);
                entry.setUserRepo(userRepo);
                entry.initMain();
                return entry;
            }
            case "Milling Entry", "Milling" -> {
                MillingEntry millingEntry = new MillingEntry();
                millingEntry.setName(menuName);
                millingEntry.setObserver(this);
                millingEntry.setProgress(progress);
                millingEntry.setUserRepo(userRepo);
                millingEntry.setAccountRepo(accounRepo);
                millingEntry.setInventoryRepo(inventoryRepo);
                millingEntry.initMain();
                return millingEntry;
            }
            case "Landing" -> {
                LandingEntry g = new LandingEntry();
                g.setInventoryRepo(inventoryRepo);
                g.setUserRepo(userRepo);
                g.setProgress(progress);
                g.setObserver(this);
                g.initMain();
                return g;
            }
            case "Stock Balance" -> {
                StockBalance b = new StockBalance();
                b.setInventoryRepo(inventoryRepo);
                b.setProgress(progress);
                b.setObserver(this);
                b.initMain();
                return b;
            }
            case "Stock Customer Payable" -> {
                StockPayable b = new StockPayable(StockPayable.SPCUS);
                b.setInventoryRepo(inventoryRepo);
                b.setProgress(progress);
                b.setObserver(this);
                b.initMain();
                return b;
            }
            case "Stock Consignor Payable" -> {
                StockPayable b = new StockPayable(StockPayable.SPCON);
                b.setInventoryRepo(inventoryRepo);
                b.setProgress(progress);
                b.setObserver(this);
                b.initMain();
                return b;
            }

            case "Weight Entry" -> {
                WeightEntry e = new WeightEntry();
                e.setUserRepo(userRepo);
                e.setInventoryRepo(inventoryRepo);
                e.setProgress(progress);
                e.setObserver(this);
                e.initMain();
                return e;
            }
            case "Menu" -> {
                MenuSetup menuSetup = new MenuSetup();
                menuSetup.setName(menuName);
                menuSetup.setObserver(this);
                menuSetup.setProgress(progress);
                menuSetup.setAccountRepo(accounRepo);
                menuSetup.setUserRepo(userRepo);
                menuSetup.initMain();
                return menuSetup;
            }
            case "Department" -> {
                DepartmentSetup departmentSetup = new DepartmentSetup();
                departmentSetup.setName(menuName);
                departmentSetup.setObserver(this);
                departmentSetup.setProgress(progress);
                departmentSetup.setAccountRepo(accounRepo);
                departmentSetup.setTaskExecutor(taskExecutor);
                departmentSetup.initMain();
                return departmentSetup;
            }
            case "COA Management" -> {
                COAManagment cOAManagment = new COAManagment();
                cOAManagment.setName(menuName);
                cOAManagment.setObserver(this);
                cOAManagment.setProgress(progress);
                cOAManagment.setUserRepo(userRepo);
                cOAManagment.setAccountRepo(accounRepo);
                cOAManagment.initMain();
                return cOAManagment;
            }
            case "User Setup" -> {
                AppUserSetup userSetup = new AppUserSetup();
                userSetup.setName(menuName);
                userSetup.setObserver(this);
                userSetup.setProgress(progress);
                userSetup.setAccountRepo(accounRepo);
                userSetup.setInventoryRepo(inventoryRepo);
                userSetup.setUserRepo(userRepo);
                userSetup.initMain();
                return userSetup;
            }
            case "Company" -> {
                CompanySetup companySetup = new CompanySetup();
                companySetup.setName(menuName);
                companySetup.setObserver(this);
                companySetup.setProgress(progress);
                companySetup.setUserRepo(userRepo);
                companySetup.setAccountRepo(accounRepo);
                companySetup.setEnvironment(environment);
                companySetup.setToken(getToken);
                companySetup.initMain();
                return companySetup;
            }
            case "HMS Integration" -> {
                HMSIntegration hmsIntegration = new HMSIntegration();
                hmsIntegration.setName(menuName);
                hmsIntegration.setObserver(this);
                hmsIntegration.setProgress(progress);
                hmsIntegration.setAccountRepo(accounRepo);
                hmsIntegration.setHmsRepo(hmsRepo);
                hmsIntegration.initMain();
                return hmsIntegration;
            }
            case "Company Template" -> {
                CompanyTemplate companyTemplate = new CompanyTemplate();
                companyTemplate.setName(menuName);
                companyTemplate.setObserver(this);
                companyTemplate.setProgress(progress);
                companyTemplate.setAccountRepo(accounRepo);
                companyTemplate.setUserRepo(userRepo);
                companyTemplate.setTaskExecutor(taskExecutor);
                companyTemplate.intTabMain();
                return companyTemplate;
            }
            case "Currency" -> {
                CurrencyExchange currencyExchange = new CurrencyExchange();
                currencyExchange.setName(menuName);
                currencyExchange.setObserver(this);
                currencyExchange.setProgress(progress);
                currencyExchange.setUserRepo(userRepo);
                currencyExchange.initMain();
                return currencyExchange;
            }
            case "G/L Listing" -> {
                GLReport gLReport = new GLReport();
                gLReport.setName(menuName);
                gLReport.setObserver(this);
                gLReport.setProgress(progress);
                gLReport.setAccountRepo(accounRepo);
                gLReport.setUserRepo(userRepo);
                gLReport.setTaskExecutor(taskExecutor);
                gLReport.initMain();
                return gLReport;
            }
            case "AR / AP" -> {
                AparReport apar = new AparReport();
                apar.setName(menuName);
                apar.setObserver(this);
                apar.setProgress(progress);
                apar.setAccountRepo(accounRepo);
                apar.setUserRepo(userRepo);
                apar.setInventoryRepo(inventoryRepo);
                apar.setTaskExecutor(taskExecutor);
                apar.initMain();
                return apar;
            }
            case "Financial Report" -> {
                FinancialReport financialReport = new FinancialReport();
                financialReport.setName(menuName);
                financialReport.setObserver(this);
                financialReport.setProgress(progress);
                financialReport.setAccountRepo(accounRepo);
                financialReport.setUserRepo(userRepo);
                financialReport.initMain();
                return financialReport;
            }
            case "Chart Of Account" -> {
                COASetup cOASetup = new COASetup();
                cOASetup.setName(menuName);
                cOASetup.setObserver(this);
                cOASetup.setProgress(progress);
                cOASetup.setAccountRepo(accounRepo);
                cOASetup.initMain();
                return cOASetup;
            }
            case "Opening Balance" -> {
                COAOpening coaOpening = new COAOpening();
                coaOpening.setName(menuName);
                coaOpening.setObservaer(this);
                coaOpening.setProgress(progress);
                coaOpening.setUserRepo(userRepo);
                coaOpening.setAccountRepo(accounRepo);
                coaOpening.initMain();
                return coaOpening;
            }
            case "Journal Voucher" -> {
                Journal journal = new Journal();
                journal.setName(menuName);
                journal.setObserver(this);
                journal.setProgress(progress);
                journal.setAccountRepo(accounRepo);
                journal.setUserRepo(userRepo);
                journal.initMain();
                return journal;
            }
            case "Dr & Cr Voucher" -> {
                DrCrVoucher drCrVoucher = new DrCrVoucher();
                drCrVoucher.setName(menuName);
                drCrVoucher.setObserver(this);
                drCrVoucher.setProgress(progress);
                drCrVoucher.setAccountRepo(accounRepo);
                drCrVoucher.setUserRepo(userRepo);
                drCrVoucher.initMain();
                return drCrVoucher;
            }
            case "Journal Stock Closing" -> {
                JournalClosingStock journalClosingStock = new JournalClosingStock();
                journalClosingStock.setName(menuName);
                journalClosingStock.setObserver(this);
                journalClosingStock.setProgress(progress);
                journalClosingStock.setAccountRepo(accounRepo);
                journalClosingStock.setUserRepo(userRepo);
                journalClosingStock.initMain();
                return journalClosingStock;
            }
            case "Trader" -> {
                TraderSetup traderSetup = new TraderSetup();
                traderSetup.setName(menuName);
                traderSetup.setObserver(this);
                traderSetup.setProgress(progress);
                traderSetup.setAccountRepo(accounRepo);
                traderSetup.initMain();
                return traderSetup;
            }
            case "Trader Adjustment" -> {
                TraderAdjustment adj = new TraderAdjustment();
                adj.setName(menuName);
                adj.setObserver(this);
                adj.setProgress(progress);
                adj.setTaskExecutor(taskExecutor);
                adj.setAccounRepo(accounRepo);
                adj.setUserRepo(userRepo);
                adj.initMain();
                return adj;
            }
            case "Excel Report" -> {
                ExcelReport excelReport = new ExcelReport();
                excelReport.setName(menuName);
                excelReport.setObserver(this);
                excelReport.setProgress(progress);
                excelReport.setUserRepo(userRepo);
                excelReport.setAccountRepo(accounRepo);
                excelReport.setTaskExecutor(taskExecutor);
                excelReport.initMain();
                return excelReport;
            }
            default -> {
                switch (cName) {
                    case "AllCash" -> {
                        AllCash cash = new AllCash(false);
                        cash.setName(menuName);
                        cash.setObserver(this);
                        cash.setProgress(progress);
                        cash.setTaskExecutor(taskExecutor);
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
                        db.setSourceAccId(srcAcc);
                        db.setAccounRepo(accounRepo);
                        db.setUserRepo(userRepo);
                        db.initMain();
                        return db;
                    }
                    default -> {
                        JOptionPane.showMessageDialog(this, "Invalid Menu.");
                    }
                }

            }
        }
        enableToolBar(false);
        return null;
    }

    private int getSaleType(String menuName) {
        return switch (menuName) {
            case "Sale By Weight" ->
                SaleDynamic.WEIGHT;
            case "Sale Export" ->
                SaleDynamic.EXPORT;
            case "Sale Rice" ->
                SaleDynamic.RICE;
            case "Sale Paddy" ->
                SaleDynamic.PADDY;
            case "Sale By Batch" ->
                SaleDynamic.BATCH;
            case "POS" ->
                SaleDynamic.QTY;
            default ->
                0;
        };
    }

    private int getOrderNoteType(String menuName) {
        return switch (menuName) {
            case "Order Note" ->
                OrderNoteEntry.ORDERNOTE;
            default ->
                0;
        };
    }

    private int getOrderType(String menuName) {
        return switch (menuName) {
            case "Order" ->
                OrderDynamic.ORDER;
            case "Purchase Order" ->
                OrderDynamic.PUR_ORDER;
            default ->
                0;
        };
    }

    private int getOpeningType(String menuName) {
        return switch (menuName) {
            case "Stock Opening" ->
                OpeningDynamic.OPENING;
            case "Stock Opening Payable" ->
                OpeningDynamic.PAYABLE;
            case "Stock Opening Paddy" ->
                OpeningDynamic.PADDY;
            case "Consign Opening" ->
                OpeningDynamic.CONSIGN;
            default ->
                0;
        };
    }

    private int getTransferType(String menuName) {
        return switch (menuName) {
            case "Transfer" ->
                Transfer.TRAN;
            case "Transfer Paddy" ->
                Transfer.TRAN_PADDY;
            default ->
                0;
        };
    }

    private int getPurType(String menuName, int version) {
        //"Purchase By Weight", "Purchase Rice", "Purchase Export"
        int purchaseType;
        switch (menuName) {
            case "Purchase By Weight" ->
                purchaseType = PurchaseDynamic.WEIGHT;
            case "Purchase Rice" ->
                purchaseType = (version == 1) ? PurchaseDynamic.RICE_BAG : PurchaseDynamic.RICE;
            case "Purchase Export" ->
                purchaseType = PurchaseDynamic.EXPORT;
            case "Purchase Paddy" ->
                purchaseType = PurchaseDynamic.PADDY;
            case "Purchase Other" ->
                purchaseType = PurchaseDynamic.OTHER;
            default ->
                purchaseType = 0;
        }

        return purchaseType;
    }

    private int getStockIOType(String menuName) {
        return switch (menuName) {
            case "Stock In/Out" ->
                StockInOutEntry.IO;
            case "Stock In/Out By Weight" ->
                StockInOutEntry.IO_W;
            case "Stock In/Out Paddy" ->
                StockInOutEntry.IO_PADDY;
            default ->
                0;
        };
    }

    private void companyUserRoleAssign() {
        userRepo.getPrivilegeRoleCompany(Global.roleCode).doOnSuccess((t) -> {
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
                    exitProgram();
                }
                assignCompany(d.getCompanyInfo());
            } else {
                assignCompany(t.get(0));
            }
            initDate();
            departmentAssign();
            initMenu();
            sseListener();
            lblCompName.setText(Global.companyName);
            lblUserName.setText(Global.loginUser.getUserLongName());
            userRepo.setupProperty().doOnSuccess((u) -> {
                Global.hmRoleProperty = u;
                scheduleProgramUpdate();
            }).doOnTerminate(() -> {
                initAccSetting();
            }).subscribe();
        }).subscribe();
    }

    private void sseListener() {
        sseListener.setObserver(this);
        sseListener.start();
        dateLockUtil.initLockDate();
    }

    private void initDate() {
        if (Global.listDate == null || Global.listDate.isEmpty()) {
            accounRepo.getDate().doOnSuccess((t) -> {
                if (t != null) {
                    dateFilterRepo.saveAll(t);
                    Global.listDate = t;
                }
            }).subscribe();
        }
    }

    private void assignCompany(CompanyInfo vuca) {
        Global.roleCode = vuca.getRoleCode();
        Global.compCode = vuca.getCompCode();
        Global.companyName = vuca.getCompName();
        Global.companyAddress = vuca.getCompAddress();
        Global.companyPhone = vuca.getCompPhone();
        Global.currency = vuca.getCurCode();
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
        initToolBarIcon();
        Global.parentForm = this;
        setTitle("Core Account Cloud : " + Global.version);
        initStockBalanceFrame();
        scheduleNetwork();
        initUser();
        companyUserRoleAssign();
    }

    private void initStockBalanceFrame() {
        stockBalanceFrame = new StockBalanceFrame();
        stockBalanceFrame.setInventoryRepo(inventoryRepo);
    }

    private void initUser() {
        userRepo.getAppUser().doOnSuccess((list) -> {
            if (list != null) {
                list.forEach((t) -> {
                    Global.hmUser.put(t.getUserCode(), t.getUserLongName());
                });
            }
        }).subscribe();
    }

    private void initAccSetting() {
        inventoryRepo.getAccSetting().doOnSuccess((list) -> {
            inventoryRepo.getDefaultLocation().doOnSuccess((d) -> {
                if (list != null) {
                    list.forEach((acc) -> {
                        if (d != null) {
                            acc.setPayAcc(Util1.isNull(d.getCashAcc(), acc.getPayAcc()));
                            acc.setDeptCode(Util1.isNull(d.getDeptCode(), acc.getDeptCode()));
                        }
                        Global.hmAcc.put(acc.getKey().getType(), acc);
                    });
                }
            }).subscribe();

        }).subscribe();
    }

    private void departmentAssign() {
        Integer deptId = Global.loginUser.getDeptId();
        if (Util1.isNullOrEmpty(deptId)) {
            userRepo.getDeparment(true).subscribe((t) -> {
                if (!t.isEmpty()) {
                    DepartmentUser dep;
                    if (t.size() > 1) {
                        DepartmentDialog dialog = new DepartmentDialog(t);
                        dialog.initMain();
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                        dep = dialog.getDeparment();
                    } else {
                        dep = t.get(0);
                    }
                    lblDep.setText(dep.getDeptName());
                    Global.deptId = dep.getKey().getDeptId();
                } else {
                    JOptionPane.showMessageDialog(this, "No Active Department.");
                    menuBar.setEnabled(false);
                }
            }, (e) -> {
                log.error(e.getMessage());
            });
        } else {
            Global.deptId = deptId;
            userRepo.findDepartment(deptId).subscribe((dep) -> {
                if (dep != null) {
                    String address = dep.getAddress();
                    String phoneNo = dep.getPhoneNo();
                    if (!Util1.isNullOrEmpty(address)) {
                        Global.companyAddress = address;
                    }
                    if (!Util1.isNullOrEmpty(phoneNo)) {
                        Global.companyPhone = phoneNo;
                    }
                    lblDep.setText(dep.getDeptName());
                } else {
                    JOptionPane.showMessageDialog(this, "Department not found.");
                    System.exit(0);
                }
            });
        }

    }

    public void initMenu() {
        progress.setIndeterminate(true);
        menuBar.removeAll();
        userRepo.getPrivilegeRoleMenuTree(Global.roleCode).doOnSuccess((t) -> {
            createMenu(t);
        }).doOnError((e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        }).subscribe();
    }

    private void createMenu(List<VRoleMenu> list) {
        if (list != null && !list.isEmpty()) {
            list.forEach((menu) -> {
                if (menu.getChild() != null) {
                    if (!menu.getChild().isEmpty()) {
                        JMenu parent = new JMenu();
                        parent.setFont(Global.menuFont);
                        parent.setName(getMenuName(menu));
                        parent.setText(Util1.isNull(menu.getMenuNameMM(), menu.getMenuName()));
                        //Need to add action listener
                        //====================================
                        menuBar.add(parent);
                        addChildMenu(parent, menu.getChild());
                    } else {  //No Child

                        JMenu jmenu = new JMenu();
                        jmenu.setFont(Global.menuFont);
                        jmenu.setName(getMenuName(menu));
                        jmenu.setText(Util1.isNull(menu.getMenuNameMM(), menu.getMenuName()));
                        menuBar.add(jmenu);
                    }
                } else {  //No Child
                    JMenu jmenu = new JMenu();
                    jmenu.setFont(Global.menuFont);
                    jmenu.setName(getMenuName(menu));
                    jmenu.setText(Util1.isNull(menu.getMenuNameMM(), menu.getMenuName()));
                    menuBar.add(jmenu);
                }
            });
        }
        progress.setIndeterminate(false);
        revalidate();
        repaint();
    }

    private String getMenuName(VRoleMenu menu) {
        return menu.getMenuClass() + ","
                + Util1.isNull(menu.getAccount(), "-") + ","
                + menu.getMenuName() + ","
                + menu.getMenuVersion();
    }

    private void addChildMenu(JMenu parent, List<VRoleMenu> listVRM) {
        listVRM.forEach((vrMenu) -> {
            if (vrMenu.isAllow()) {
                if (vrMenu.getChild() != null) {
                    if (!vrMenu.getChild().isEmpty()) {
                        JMenu menu = new JMenu();
                        menu.setFont(Global.menuFont);
                        menu.setName(getMenuName(vrMenu));
                        menu.setText(Util1.isNull(vrMenu.getMenuNameMM(), vrMenu.getMenuName()));
                        //Need to add action listener
                        //====================================
                        parent.add(menu);
                        addChildMenu(menu, vrMenu.getChild());
                    } else {  //No Child
                        JMenuItem menuItem = new JMenuItem();
                        menuItem.addActionListener(menuListener);
                        menuItem.setFont(Global.menuFont);
                        menuItem.setName(getMenuName(vrMenu));
                        menuItem.setText(Util1.isNull(vrMenu.getMenuNameMM(), vrMenu.getMenuName()));
                        //====================================
                        parent.add(menuItem);
                    }
                } else {  //No Child
                    JMenuItem menuItem = new JMenuItem();
                    menuItem.setName(getMenuName(vrMenu));                    //Need to add action listener
                    menuItem.addActionListener(menuListener);
                    menuItem.setFont(Global.menuFont);
                    menuItem.setText(Util1.isNull(vrMenu.getMenuNameMM(), vrMenu.getMenuName()));
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

    private void scheduleProgramUpdate() {
        boolean update = Util1.getBoolean(ProUtil.getProperty(ProUtil.AUTO_UPDATE));
        if (update) {
            log.info("auto update on.");
            pdDialog = new ProgramDownloadDialog(Global.parentForm);
            pdDialog.setTaskScheduler(taskScheduler);
            pdDialog.setUserRepo(userRepo);
            pdDialog.setObserver(this);
            pdDialog.start();
        }
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
                    setNetwork(-1);
                }
            }
        }, 0, Duration.ofSeconds(5).toMillis());
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
            case "refresh" -> {
                btnRefresh.setEnabled(Util1.getBoolean(selectObj.toString()));
            }
            case "enableToolBar" -> {
                enableToolBar(Util1.getBoolean(selectObj.toString()));
            }
            case "change-name" -> {
                lblCompName.setText(Global.companyName);
            }
            case "message" -> {
                lblLock.setForeground(Color.red);
                lblLock.setText(selectObj.toString());
            }
            case "PROGRAM_UPDATE" -> {
                pdDialog.start();
            }
        }
    }

    private void enableToolBar(boolean status) {
        ComponentUtil.setComponentHierarchyEnabled(toolBar, status);
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

    private void changeCompany() {
        companyUserRoleAssign();
    }

    private void addMouseAndKeyboardListeners() {
        // Add mouse listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                resetInactivityTimer();
            }
        });

        // Add keyboard listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                resetInactivityTimer();
            }
        });

        // Set focusable to true for the frame to receive keyboard events
        setFocusable(true);
    }

    private void resetInactivityTimer() {
        // Reset the inactivity timer and update the last activity time
        lastActivityTime = System.currentTimeMillis();
        if (inactivityTimer != null) {
            inactivityTimer.cancel();
        }

        inactivityTimer = new Timer();
        inactivityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                showExitConfirmation();
            }
        }, 5 * 60 * 1000); // 5 minutes in milliseconds
    }

    private void showExitConfirmation() {
        if (ynDialog != null) {
            logoutProgram();
        }
        YNOptionPane optionPane = new YNOptionPane("Do you want to exit the program due to inactivity? Program will logout within 10 minutes.", JOptionPane.WARNING_MESSAGE);
        ynDialog = optionPane.createDialog("Edit");
        ynDialog.setVisible(true);
        int yn = (int) optionPane.getValue();
        if (yn == JOptionPane.YES_OPTION) {
            logoutProgram();
        }
    }

    private void logoutProgram() {
        log.info("logout due to inactivity.");
        if (inactivityTimer != null) {
            inactivityTimer.cancel();
        }
        dispose(); // Close the JFrame
        System.exit(0);
    }

    private void startInactivityTimer() {
        // Set the initial last activity time
        lastActivityTime = System.currentTimeMillis();

        // Start a timer to check for inactivity
        Timer checkInactivityTimer = new Timer(true);
        checkInactivityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkInactivity();
            }
        }, 5, 60 * 1000); // Check every minute
    }

    private void checkInactivity() {
        long currentTime = System.currentTimeMillis();
        long inactiveDuration = currentTime - lastActivityTime;

        if (inactiveDuration >= 10 * 60 * 1000) { // 5 minutes in milliseconds
            // Trigger exit due to inactivity
            SwingUtilities.invokeLater(() -> showExitConfirmation());
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
        toolBar = new javax.swing.JToolBar();
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
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
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

        toolBar.setFocusable(false);

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save - F5");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        toolBar.add(btnSave);
        toolBar.add(jSeparator1);

        btnPrint.setFont(Global.lableFont);
        btnPrint.setText("Print - F6");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        toolBar.add(btnPrint);
        toolBar.add(jSeparator6);

        btnRefresh.setFont(Global.lableFont);
        btnRefresh.setText("Refresh - F7");
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        toolBar.add(btnRefresh);
        toolBar.add(jSeparator5);

        btnDelete.setFont(Global.lableFont);
        btnDelete.setText("Delete - F8");
        btnDelete.setFocusable(false);
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        toolBar.add(btnDelete);
        toolBar.add(jSeparator2);

        btnHistory.setFont(Global.lableFont);
        btnHistory.setText("History - F9");
        btnHistory.setFocusable(false);
        btnHistory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHistory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoryActionPerformed(evt);
            }
        });
        toolBar.add(btnHistory);
        toolBar.add(jSeparator7);

        btnNew.setFont(Global.lableFont);
        btnNew.setText("New - F10");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        toolBar.add(btnNew);
        toolBar.add(jSeparator3);

        btnLogout.setFont(Global.lableFont);
        btnLogout.setText("Logout - F11");
        btnLogout.setFocusable(false);
        btnLogout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLogout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        toolBar.add(btnLogout);
        toolBar.add(jSeparator8);

        btnFilter.setFont(Global.lableFont);
        btnFilter.setText("Search-F12");
        btnFilter.setToolTipText("Filter Bar");
        btnFilter.setFocusable(false);
        btnFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });
        toolBar.add(btnFilter);
        toolBar.add(jSeparator11);

        btnExit.setFont(Global.lableFont);
        btnExit.setText("Exit - Alt+F4");
        btnExit.setToolTipText("Filter Bar");
        btnExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        toolBar.add(btnExit);
        toolBar.add(jSeparator9);

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
        lblCompName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCompNameMouseClicked(evt);
            }
        });

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
                .addComponent(lblPanelName, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLock, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCompName, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
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
                .addComponent(lblNetwork, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        if (control != null) {
            control.save();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoryActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.history();
        }
    }//GEN-LAST:event_btnHistoryActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.delete();
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.newForm();
        }
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.print();
        }
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
        exitProgram();
    }//GEN-LAST:event_formWindowClosing

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentResized

    private void lblCompNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCompNameMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() > 1) {
            changeCompany();
        }
    }//GEN-LAST:event_lblCompNameMouseClicked

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
    private javax.swing.JLabel lblCompName;
    private javax.swing.JLabel lblDep;
    private javax.swing.JLabel lblLock;
    private javax.swing.JLabel lblNetwork;
    private javax.swing.JLabel lblPanelName;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
