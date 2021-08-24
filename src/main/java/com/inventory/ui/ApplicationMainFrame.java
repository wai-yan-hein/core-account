/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui;

import com.inventory.common.Global;
import com.inventory.common.PanelControl;
import com.inventory.common.SystemSetting;
import com.inventory.common.Util1;
import com.inventory.model.Category;
import com.inventory.model.Currency;
import com.inventory.model.Location;
import com.inventory.model.Region;
import com.inventory.model.SaleMan;
import com.inventory.model.Stock;
import com.inventory.model.StockBrand;
import com.inventory.model.StockType;
import com.inventory.model.StockUnit;
import com.inventory.model.Trader;
import com.inventory.model.VRoleMenu;
import com.inventory.model.VUsrCompAssign;
import com.inventory.ui.entry.OtherSetup;
import com.inventory.ui.entry.RoleSetting;
import com.inventory.ui.entry.Sale;
import com.inventory.ui.setup.CustomerSetup;
import com.inventory.ui.setup.StockSetup;
import com.inventory.ui.setup.SupplierSetup;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class ApplicationMainFrame extends javax.swing.JFrame {

    @Autowired
    private WebClient webClient;
    @Autowired
    private StockSetup stockSetup;
    @Autowired
    private Sale sale;
    @Autowired
    private CustomerSetup customerSetup;
    @Autowired
    private SupplierSetup supplierSetup;
    @Autowired
    private OtherSetup otherSetup;
    @Autowired
    private RoleSetting roleSetting;
    private PanelControl control;
    private final HashMap<String, JLabel> hmTabLoading = new HashMap();
    private final ImageIcon loadingIcon = new ImageIcon(this.getClass().getResource("/images/dual-loading.gif"));
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
        //loading
        JLabel loading = new JLabel(loadingIcon);
        titlePanel.add(loading);
        loading.setVisible(false);
        hmTabLoading.put(panel.getName(), loading);

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
            case "Stock" -> {
                return stockSetup;
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
        loadSS();
        initCategory();
        initBrand();
        initType();
        initUnit();
        initSaleMan();
        initCustomer();
        initSupplier();
        initRegion();
        initLocation();
        initStock();
        initCurrency();
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

    private void loadSS() {
        Mono<SystemSetting> result = webClient.get()
                .uri(builder -> builder.path("/user/get-role-setting")
                .queryParam("roleCode", Global.roleCode)
                .build())
                .retrieve().bodyToMono(SystemSetting.class);
        result.subscribe((t) -> {
            Global.setting = t;
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
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
        menuBar = new javax.swing.JMenuBar();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        assignWindoInfo();
    }//GEN-LAST:event_formComponentShown

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTabbedPane tabMain;
    // End of variables declaration//GEN-END:variables
}
