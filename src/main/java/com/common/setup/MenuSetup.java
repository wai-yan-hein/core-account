/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common.setup;

import com.common.Global;
import com.common.PanelControl;
import com.common.ReturnObject;
import com.common.SelectionObserver;
import com.common.Util1;
import com.common.model.Menu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.List;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class MenuSetup extends javax.swing.JPanel implements TreeSelectionListener, PanelControl {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MenuSetup.class);
    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private DefaultMutableTreeNode treeRoot;
    DefaultTreeModel treeModel;
    DefaultMutableTreeNode selectedNode;
    @Autowired
    private WebClient userApi;
    @Autowired
    private TaskExecutor taskExecutor;

    private SelectionObserver observer;
    private final String parentRootName = "Root Menu";
    private JProgressBar progress;

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    JPopupMenu popupmenu;
    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        log.info("Selected Menu : " + menuName);
        switch (menuName) {
            case "New Menu" ->
                newMenu("Menu");
            case "Delete" ->
                deleteMenu();//deleteCOA();
            case "New Function" ->
                newMenu("Function");
            case "New Report" ->
                newMenu("Report");
            default -> {
            }
        }

    };

    /**
     * Creates new form COASetup
     */
    public MenuSetup() {
        initComponents();
        initKeyListener();
        initPopup();
    }

    public void initMain() {
        initTree();
    }

    private void deleteMenu() {
        if (selectedNode.getUserObject() != null) {
            Menu menu = (Menu) selectedNode.getUserObject();
            Mono<ReturnObject> result = userApi.post()
                    .uri("/user/delete-menu")
                    .body(Mono.just(menu), Menu.class)
                    .retrieve()
                    .bodyToMono(ReturnObject.class
                    );
            ReturnObject block = result.block();
            JOptionPane.showMessageDialog(this, block.getMessage());
            treeModel.removeNodeFromParent(selectedNode);
        } else {
            JOptionPane.showMessageDialog(this, "Select Menu.");
        }
    }

    private void getMenu() {
        Mono<ResponseEntity<List<Menu>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-menu-tree")
                .build())
                .retrieve().toEntityList(Menu.class);
        result.subscribe((t) -> {
            List<Menu> menus = t.getBody();
            if (!menus.isEmpty()) {
                menus.forEach((menu) -> {
                    if (menu.getChild() != null) {
                        if (!menu.getChild().isEmpty()) {
                            DefaultMutableTreeNode parent = new DefaultMutableTreeNode(menu);
                            treeRoot.add(parent);
                            addChildMenu(parent, menu.getChild());
                        } else {  //No Child
                            DefaultMutableTreeNode parent = new DefaultMutableTreeNode(menu);
                            treeRoot.add(parent);
                        }
                    } else {  //No Child
                        DefaultMutableTreeNode parent = new DefaultMutableTreeNode(menu);
                        treeRoot.add(parent);
                    }

                });
            }
            treeModel.setRoot(treeRoot);
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void addChildMenu(DefaultMutableTreeNode parent, List<Menu> listVRM) {
        listVRM.forEach((menu) -> {
            if (menu.getChild() != null) {
                if (!menu.getChild().isEmpty()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(menu);
                    parent.add(node);
                    addChildMenu(node, menu.getChild());
                } else {  //No Child
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(menu);
                    parent.add(node);
                }
            } else {  //No Child
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(menu);
                parent.add(node);
            }
        });
    }

    private void initKeyListener() {
        treeCOA.addTreeSelectionListener(this);
        treeCOA.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupmenu.show(treeCOA, e.getX(), e.getY());
                }
            }

        });
    }

    private void initTree() {
        treeModel = (DefaultTreeModel) treeCOA.getModel();
        treeModel.setRoot(null);
        treeRoot = new DefaultMutableTreeNode(parentRootName);
        progress.setIndeterminate(true);
        taskExecutor.execute(() -> {
            getMenu();
            treeModel.setRoot(treeRoot);
            progress.setIndeterminate(false);
        });
    }

    private void initPopup() {
        popupmenu = new JPopupMenu("Edit");
        JMenuItem newMenu = new JMenuItem("New Menu");
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem newFun = new JMenuItem("New Function");
        JMenuItem newReport = new JMenuItem("New Report");
        newMenu.addActionListener(menuListener);
        delete.addActionListener(menuListener);
        newReport.addActionListener(menuListener);
        newFun.addActionListener(menuListener);
        popupmenu.add(newMenu);
        popupmenu.add(newFun);
        popupmenu.add(newReport);
        popupmenu.add(delete);
    }

    private void newMenu(String type) {

        Menu menu = new Menu();
        switch (type) {
            case "Menu" -> {
                menu.setMenuName("New Menu");
                menu.setMenuType("Menu");
            }
            case "Function" -> {
                menu.setMenuName("New Function");
                menu.setMenuType("Function");
                menu.setMenuClass("Report");
            }
            case "Report" -> {
                menu.setMenuName("New Report");
                menu.setMenuType("Report");
                menu.setMenuClass("Report");
            }
            default -> {
            }
        }
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(menu);
        if (selectedNode != null) {
            selectedNode.add(child);
            treeModel.insertNodeInto(child, selectedNode, selectedNode.getChildCount() - 1);
            treeCOA.setSelectionInterval(selectedNode.getChildCount(), selectedNode.getChildCount());
        }
    }

    private void saveMenu() {
        String parentCode = "";
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
        if (parentNode != null) {
            Object userObject = parentNode.getUserObject();
            if (userObject.toString().equals(parentRootName)) {
                parentCode = "1";
            } else {
                Menu menu = (Menu) parentNode.getUserObject();
                parentCode = menu.getMenuCode();
            }
        }
        String menuName = txtMenuName.getText();
        if (!menuName.isEmpty()) {
            Menu vMenu = (Menu) selectedNode.getUserObject();
            Menu menu = new Menu();
            menu.setMenuCode(vMenu.getMenuCode());
            menu.setMenuName(menuName);
            menu.setParentMenuCode(parentCode);
            menu.setMenuUrl(txtMenuUrl.getText());
            menu.setAccount(txtAccount.getText());
            menu.setMenuType(txtMenuType.getText().trim());
            menu.setMenuClass(txtClass.getText());
            menu.setOrderBy(Integer.parseInt(Util1.isNull(txtOrder.getText(), "0")));
            if (txtOrder.getValue() != null) {
                menu.setOrderBy(Util1.getInteger(txtOrder.getText()));
            }
            Menu saveMenu = saveMenu(menu);
            if (saveMenu != null) {
                selectedNode.setUserObject(saveMenu);
                treeModel.reload(selectedNode);
                clear();
            }
        }
    }

    private Menu saveMenu(Menu menu) {
        Mono<ReturnObject> result = userApi.post()
                .uri("/user/save-menu")
                .body(Mono.just(menu), Menu.class
                )
                .retrieve()
                .bodyToMono(ReturnObject.class
                );
        ReturnObject block = result.block();
        if (!Objects.isNull(block)) {
            menu = gson.fromJson(gson.toJson(block.getData()), Menu.class);
        }
        return menu;
    }

    private void setMenu(Menu menu) {
        txtMenuName.setText(menu.getMenuName());
        txtMenuUrl.setText(menu.getMenuUrl());
        txtOrder.setText(menu.getOrderBy() == null ? null : menu.getOrderBy().toString());
        txtAccount.setText(menu.getAccount());
        txtMenuType.setText(menu.getMenuType());
        txtClass.setText(menu.getMenuClass());
        enableControl(true);
    }

    private void clear() {
        txtMenuName.setText(null);
        txtMenuUrl.setText(null);
        txtOrder.setText(null);
        txtAccount.setText(null);
        txtMenuType.setText(null);
        txtClass.setText(null);
        txtMenuMM.setText(null);
        enableControl(false);

    }

    private void enableControl(boolean status) {
        txtMenuName.setEditable(status);
        txtMenuUrl.setEditable(status);
        txtOrder.setEditable(status);
        txtAccount.setEditable(status);
        txtMenuType.setEditable(status);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        treeCOA = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtMenuName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtMenuUrl = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtOrder = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtMenuType = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtClass = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtMenuMM = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        treeCOA.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Menu");
        treeCOA.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(treeCOA);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Menu Name");

        txtMenuName.setFont(Global.textFont);
        txtMenuName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuNameFocusGained(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Url");

        txtMenuUrl.setFont(Global.textFont);
        txtMenuUrl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuUrlFocusGained(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Order");

        txtOrder.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtOrder.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOrderFocusGained(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Account ");

        txtAccount.setFont(Global.textFont);
        txtAccount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAccountFocusGained(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Menu Type");

        txtMenuType.setFont(Global.textFont);
        txtMenuType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuTypeFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Menu Class");

        txtClass.setFont(Global.textFont);
        txtClass.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClassFocusGained(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Menu MM");

        txtMenuMM.setFont(Global.textFont);
        txtMenuMM.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuMMFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMenuUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addComponent(txtMenuName)
                    .addComponent(txtOrder)
                    .addComponent(txtAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addComponent(txtMenuType, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addComponent(txtClass, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addComponent(txtMenuMM))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtMenuName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtMenuMM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMenuUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtMenuType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void txtMenuNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuNameFocusGained
        // TODO add your handling code here:
        txtMenuName.selectAll();
    }//GEN-LAST:event_txtMenuNameFocusGained

    private void txtMenuUrlFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuUrlFocusGained
        // TODO add your handling code here:
        txtMenuUrl.selectAll();
    }//GEN-LAST:event_txtMenuUrlFocusGained

    private void txtAccountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusGained
        // TODO add your handling code here:
        txtAccount.selectAll();
    }//GEN-LAST:event_txtAccountFocusGained

    private void txtOrderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOrderFocusGained
        // TODO add your handling code here:
        txtOrder.selectAll();
    }//GEN-LAST:event_txtOrderFocusGained

    private void txtMenuTypeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuTypeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMenuTypeFocusGained

    private void txtClassFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClassFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClassFocusGained

    private void txtMenuMMFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuMMFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMenuMMFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree treeCOA;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtClass;
    private javax.swing.JTextField txtMenuMM;
    private javax.swing.JTextField txtMenuName;
    private javax.swing.JTextField txtMenuType;
    private javax.swing.JTextField txtMenuUrl;
    private javax.swing.JFormattedTextField txtOrder;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (DefaultMutableTreeNode) treeCOA.getLastSelectedPathComponent();
        if (selectedNode != null) {
            if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                if (selectedNode.getUserObject() instanceof Menu) {
                    Menu menu = (Menu) selectedNode.getUserObject();
                    setMenu(menu);
                } else if (selectedNode.getUserObject() instanceof Menu) {
                    Menu menu = (Menu) selectedNode.getUserObject();
                    txtMenuName.setText(menu.getMenuName());
                    txtMenuUrl.setText(menu.getMenuUrl());
                }
            } else {
                clear();
                //setEnabledControl(false);
            }
        }
    }

    @Override
    public void save() {
        saveMenu();
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void delete() {
        deleteMenu();
    }

    @Override
    public void refresh() {
        initTree();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
