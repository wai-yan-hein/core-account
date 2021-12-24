/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.inventory.CvInventoryApplication;
import com.inventory.common.Global;
import com.inventory.common.PanelControl;
import com.inventory.common.ReturnObject;
import com.inventory.common.RoleDefault;
import com.inventory.common.Util1;
import com.inventory.model.Category;
import com.inventory.model.Currency;
import com.inventory.model.Location;
import com.inventory.model.Region;
import com.inventory.model.ReorderLevel;
import com.inventory.model.SaleMan;
import com.inventory.model.Stock;
import com.inventory.model.StockBrand;
import com.inventory.model.StockType;
import com.inventory.model.StockUnit;
import com.inventory.model.SysProperty;
import com.inventory.model.Trader;
import com.inventory.model.VRoleMenu;
import com.inventory.model.VUsrCompAssign;
import com.inventory.model.VouStatus;
import com.inventory.ui.entry.OtherSetup;
import com.inventory.ui.entry.Purchase;
import com.inventory.ui.entry.ReorderLevelEntry;
import com.inventory.ui.entry.ReturnIn;
import com.inventory.ui.entry.ReturnOut;
import com.inventory.ui.entry.RoleSetting;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.inventory.ui.entry.Reports;
import com.inventory.ui.setup.OpeningSetup;
import com.inventory.ui.setup.PatternSetup;
import com.inventory.ui.system.SystemProperty;
import java.text.DateFormat;
import java.util.ArrayList;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class ApplicationMainFrame extends javax.swing.JFrame {

    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

    @Autowired
    private WebClient webClient;
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
    private StockInOutEntry sotckInOut;
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
    private SystemProperty systemProperty;
    @Autowired
    private PatternSetup patternSetup;
    @Autowired
    private ReorderLevelEntry reorderLevel;
    private PanelControl control;
    private final HashMap<String, JLabel> hmTabLoading = new HashMap();
    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        log.info("selected menu : " + menuName);
        JPanel panel = getPanel(menuName);
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
                        }
                    }
                }
            }
            return false;
        });
    }

    private void addTabMain(JPanel panel, String menuName) {
        if (panel != null) {
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

    private JPanel getPanel(String menuName) {
        switch (menuName) {
            case "Sale" -> {
                sale.initMain();
                return sale;
            }
            case "Purchase" -> {
                purchase.initMain();
                return purchase;
            }
            case "Return In" -> {
                retIn.initMain();
                return retIn;
            }
            case "Return Out" -> {
                retOut.initMain();
                return retOut;
            }
            case "Stock In/Out" -> {
                sotckInOut.initMain();
                return sotckInOut;
            }
            case "Stock" -> {
                stockSetup.initMain();
                return stockSetup;
            }
            case "Opening" -> {
                openingSetup.initMain();
                return openingSetup;
            }
            case "Customer" -> {
                return customerSetup;
            }
            case "Supplier" -> {
                return supplierSetup;
            }
            case "Other Setup" -> {
                return otherSetup;
            }
            case "Role Setting" -> {
                return roleSetting;
            }
            case "Report" -> {
                report.initMain();
                return report;
            }
            case "System Propery" -> {
                systemProperty.initTable();
                return systemProperty;
            }
            case "Pattern Setup" -> {
                patternSetup.initMain();
                return patternSetup;
            }
            case "Reorder Level" -> {
                reorderLevel.initMain();
                return reorderLevel;
            }
        }
        return null;
    }

    public void companyUserRoleAssign() {
        Mono<ResponseEntity<List<VUsrCompAssign>>> result = webClient.get()
                .uri(builder -> builder.path("/user/get-assign-company")
                .queryParam("userCode", Global.loginUser.getAppUserCode())
                .build())
                .retrieve().toEntityList(VUsrCompAssign.class);
        result.subscribe((t) -> {
            List<VUsrCompAssign> listVUCA = t.getBody();
            if (listVUCA == null) {
                JOptionPane.showMessageDialog(new JFrame(),
                        "No company assign to the user",
                        "Invalid Compay Access", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            } else if (listVUCA.isEmpty()) {
                JOptionPane.showMessageDialog(new JFrame(),
                        "No company assign to the user",
                        "Invalid Compay Access", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            } else if (listVUCA.size() > 1) {
                CompanyDialog companyDialog = new CompanyDialog();
                companyDialog.setListCompany(listVUCA);
                companyDialog.initTable();
                companyDialog.setLocationRelativeTo(null);
                companyDialog.setVisible(true);
            } else {
                VUsrCompAssign vuca = listVUCA.get(0);
                Global.roleCode = vuca.getKey().getRoleCode();
                Global.compCode = vuca.getKey().getCompCode();
                Global.companyName = vuca.getCompName();
                log.info("Role Code : " + Global.roleCode);
                log.info("Company Code : " + Global.compCode);
            }
            initializeData();
            initMenu();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initMenu() {
        Mono<ResponseEntity<List<VRoleMenu>>> result = webClient.get()
                .uri(builder -> builder.path("/get-menu")
                .queryParam("roleCode", Global.roleCode)
                .queryParam("type", "Menu")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(VRoleMenu.class);
        result.subscribe((t) -> {
            if (t != null) {
                List<VRoleMenu> listVRM = t.getBody();
                if (listVRM != null) {
                    listVRM.forEach((menu) -> {
                        if (menu.getIsAllow()) {
                            if (menu.getChild() != null) {
                                if (!menu.getChild().isEmpty()) {
                                    JMenu parent = new JMenu();
                                    parent.setText(menu.getMenuName());
                                    parent.setFont(Global.menuFont);
                                    parent.setName(menu.getMenuClass() + ","
                                            + Util1.isNull(menu.getSoureAccCode(), "-") + ","
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
                                            + Util1.isNull(menu.getSoureAccCode(), "-") + ","
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
                                        + Util1.isNull(menu.getSoureAccCode(), "-") + ","
                                        + menu.getMenuName());                        //Need to add action listener
                                //====================================
                                menuBar.add(jmenu);
                            }
                        }
                    });
                }
            }
            revalidate();
            repaint();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
        log.info("init menu end.");
    }

    private void addChildMenu(JMenu parent, List<VRoleMenu> listVRM) {
        listVRM.forEach((vrMenu) -> {
            if (vrMenu.getIsAllow()) {
                if (vrMenu.getChild() != null) {
                    if (!vrMenu.getChild().isEmpty()) {
                        JMenu menu = new JMenu();
                        menu.setText(vrMenu.getMenuName());
                        menu.setFont(Global.menuFont);
                        menu.setName(vrMenu.getMenuClass() + ","
                                + Util1.isNull(vrMenu.getSoureAccCode(), "-") + ","
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
                                + Util1.isNull(vrMenu.getSoureAccCode(), "-") + "-"
                                + vrMenu.getMenuName());
                        //====================================
                        parent.add(menuItem);
                    }
                } else {  //No Child
                    JMenuItem menuItem = new JMenuItem();

                    menuItem.setText(vrMenu.getMenuName());
                    menuItem.setName(vrMenu.getMenuClass() + ","
                            + Util1.isNull(vrMenu.getSoureAccCode(), "-") + ","
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
        initDefault();
        getRoleProperty();
        initCategory();
        initBrand();
        initType();
        initUnit();
        initSaleMan();
        initCustomer();
        initSupplier();
        initTrader();
        initRegion();
        initLocation();
        initStock();
        initCurrency();
        initVoucherStatus();
    }

    private void initSysProperty() {
        Mono<ReturnObject> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-system-property")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.subscribe((t) -> {
            java.lang.reflect.Type listType = new TypeToken<ArrayList<SysProperty>>() {
            }.getType();
            List<SysProperty> listSys = gson.fromJson(gson.toJsonTree(t.getList()), listType);
            if (!listSys.isEmpty()) {
                listSys.forEach(p -> {
                    Global.hmRoleProperty.put(p.getPropKey(), p.getPropValue());
                });
            }

        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });

    }

    private void initCategory() {
        Mono<ResponseEntity<List<Category>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-category")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Category.class);
        result.subscribe((t) -> {
            Global.listCategory = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initSaleMan() {
        Mono<ResponseEntity<List<SaleMan>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-saleman")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(SaleMan.class);
        result.subscribe((t) -> {
            Global.listSaleMan = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initBrand() {
        Mono<ResponseEntity<List<StockBrand>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-brand")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(StockBrand.class);
        result.subscribe((t) -> {
            Global.listStockBrand = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initType() {
        Mono<ResponseEntity<List<StockType>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-type")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(StockType.class);
        result.subscribe((t) -> {
            Global.listStockType = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initUnit() {
        Mono<ResponseEntity<List<StockUnit>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-unit")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(StockUnit.class);
        result.subscribe((t) -> {
            Global.listStockUnit = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initCustomer() {
        Mono<ResponseEntity<List<Trader>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-customer")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Trader.class);
        result.subscribe((t) -> {
            Global.listCustomer = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initSupplier() {
        Mono<ResponseEntity<List<Trader>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-supplier")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Trader.class);
        result.subscribe((t) -> {
            Global.listSupplier = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initTrader() {

    }

    private void initRegion() {
        Mono<ResponseEntity<List<Region>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-region")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Region.class);
        result.subscribe((t) -> {
            Global.listRegion = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initLocation() {
        Mono<ResponseEntity<List<Location>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-location")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Location.class);
        result.subscribe((t) -> {
            Global.listLocation = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initStock() {
        Mono<ResponseEntity<List<Stock>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-stock")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Stock.class);
        result.subscribe((t) -> {
            Global.listStock = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initCurrency() {
        Mono<ResponseEntity<List<Currency>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-currency").build())
                .retrieve().toEntityList(Currency.class);
        result.subscribe((t) -> {
            Global.listCurrency = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initVoucherStatus() {
        Mono<ResponseEntity<List<VouStatus>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-voucher-status")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(VouStatus.class);
        result.subscribe((t) -> {
            Global.listVouStatus = t.getBody();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void getRoleProperty() {
        log.info("getRoleProperty.");
        Mono<Map<String, String>> result = webClient.get()
                .uri(builder -> builder.path("/user/get-role-property")
                .queryParam("roleCode", Global.roleCode)
                .build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                });
        result.subscribe((t) -> {
            Global.hmRoleProperty = t;
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initDefault() {
        Mono<ResponseEntity<RoleDefault>> result = webClient.get()
                .uri(builder -> builder.path("/user/role-default")
                .queryParam("roleCode", Global.roleCode)
                .build())
                .retrieve().toEntity(RoleDefault.class);
        result.subscribe((t) -> {
            RoleDefault rd = t.getBody();
            Global.defaultCurrency = rd.getDefaultCurrency();
            Global.defaultCustomer = rd.getDefaultCustomer();
            Global.defaultLocation = rd.getDefaultLocation();
            Global.defaultSaleMan = rd.getDefaultSaleMan();
            Global.defaultSupplier = rd.getDefaultSupplier();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void logout() {
        dispose();
        CvInventoryApplication.restart();
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
        btnDelete = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnHistory = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnNew = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnNew1 = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btnNew2 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        menuBar = new javax.swing.JMenuBar();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Core Inventory (V.1.0)");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tabMain.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jToolBar1.setFocusable(false);

        btnSave1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSave1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
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

        btnPrint.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/print.png"))); // NOI18N
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

        btnDelete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/trash_20px.png"))); // NOI18N
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
        jToolBar1.add(jSeparator5);

        btnHistory.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnHistory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/time_machine_20px.png"))); // NOI18N
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
        jToolBar1.add(jSeparator2);

        btnNew.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add_file_20px.png"))); // NOI18N
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

        btnNew1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnNew1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh_20px.png"))); // NOI18N
        btnNew1.setText("Refresh - F11");
        btnNew1.setFocusable(false);
        btnNew1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNew1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew1);
        jToolBar1.add(jSeparator7);

        btnNew2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnNew2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logout_rounded_down_20px.png"))); // NOI18N
        btnNew2.setText("Logout - F12");
        btnNew2.setFocusable(false);
        btnNew2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNew2ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew2);

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
                    .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jSeparator4)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
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

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNew1;
    private javax.swing.JButton btnNew2;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTabbedPane tabMain;
    // End of variables declaration//GEN-END:variables
}
