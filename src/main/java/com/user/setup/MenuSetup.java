/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.repo.AccountRepo;
import com.acc.editor.COA3AutoCompleter;
import com.acc.model.ChartOfAccount;
import com.common.ComponentUtil;
import com.common.Global;
import com.common.OfflineOptionPane;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.entity.MessageType;
import com.repo.UserRepo;
import com.user.common.MenuTreeTrasnferHandler;
import com.user.model.Menu;
import com.user.model.MenuKey;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DropMode;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class MenuSetup extends javax.swing.JPanel implements TreeSelectionListener, PanelControl, SelectionObserver {

    private DefaultMutableTreeNode treeRoot;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode selectedNode;
    private COA3AutoCompleter cOA3AutoCompleter;
    private UserRepo userRepo;
    private AccountRepo accountRepo;

    private SelectionObserver observer;
    private final String parentRootName = "Core Account";
    private JProgressBar progress;

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

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
    private final ActionListener menuListener = (ActionEvent evt) -> {
        if (evt.getSource() instanceof JMenuItem actionMenu) {
            String menuName = actionMenu.getText();
            switch (menuName) {
                case "New Menu" ->
                    newMenu("Menu");
                case "Delete" ->
                    deleteMenu();
                case "New Function" ->
                    newMenu("Function");
                case "New Report" ->
                    newMenu("Report");
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
        initFocusListener();
    }

    private void initCombo() {
        cOA3AutoCompleter = new COA3AutoCompleter(txtAccount, accountRepo, null, false, 3);
        cOA3AutoCompleter.setObserver(this);
    }

    public void initMain() {
        initTree();
        initCombo();
    }

    private void deleteMenu() {
        if (selectedNode.getUserObject() != null) {
            Menu menu = (Menu) selectedNode.getUserObject();
            userRepo.delete(menu.getKey()).doOnSuccess((t) -> {
                if (t) {
                    treeModel.removeNodeFromParent(selectedNode);
                    observer.selected("menu", "menu");
                }
            }).doOnError((e) -> {
                if (e instanceof WebClientRequestException) {
                    OfflineOptionPane pane = new OfflineOptionPane();
                    JDialog dialog = pane.createDialog("Offline");
                    dialog.setVisible(true);
                    int yn = (int) pane.getValue();
                    if (yn == JOptionPane.YES_OPTION) {
                        deleteMenu();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }).subscribe();
        } else {
            JOptionPane.showMessageDialog(this, "Select Menu.");
        }
    }

    private void getMenu() {
        progress.setIndeterminate(true);
        userRepo.getMenuTree().doOnSuccess((menus) -> {
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
            progress.setIndeterminate(false);
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progress.setIndeterminate(false);
        }).subscribe();

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
        treeCOA.setDragEnabled(true);
        treeCOA.setDropMode(DropMode.ON_OR_INSERT);
        treeCOA.setTransferHandler(new MenuTreeTrasnferHandler(userRepo));
        treeCOA.getSelectionModel().setSelectionMode(
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        treeModel.setRoot(null);
        treeRoot = new DefaultMutableTreeNode(parentRootName);
        getMenu();

    }

    private void initPopup() {
        popupmenu = new JPopupMenu("Edit");
        JMenuItem newMenu = new JMenuItem("New Menu");
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem newReport = new JMenuItem("New Report");
        newMenu.addActionListener(menuListener);
        delete.addActionListener(menuListener);
        newReport.addActionListener(menuListener);
        popupmenu.add(newMenu);
        popupmenu.add(newReport);
        popupmenu.add(delete);
        ComponentUtil.setFont(popupmenu, Global.textFont);
    }

    private void newMenu(String type) {
        if (selectedNode.getUserObject() instanceof Menu obj) {
            Menu menu = new Menu();
            MenuKey key = new MenuKey();
            key.setCompCode(Global.compCode);
            menu.setKey(key);
            menu.setMenuClass(obj.getMenuClass());
            switch (type) {
                case "Menu" -> {
                    menu.setMenuName("New Menu");
                    menu.setMenuType("Menu");

                }
                case "Report" -> {
                    menu.setMenuName("New Report");
                    menu.setMenuType("Report");
                }
            }
            DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) treeCOA.getLastSelectedPathComponent();
            int count = selectNode.getChildCount();
            menu.setOrderBy(count);
            menu.setMenuVersion(Util1.getInteger(txtVersion.getText()));
            DefaultTreeModel model = (DefaultTreeModel) treeCOA.getModel();
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(menu);
            model.insertNodeInto(newNode, selectNode, count);
            TreePath path = new TreePath(newNode.getPath());
            treeCOA.expandPath(new TreePath(selectNode.getPath()));
            treeCOA.setSelectionPath(path);
            txtMenuName.requestFocus();
        }

    }

    private void saveMenu() {
        String parentCode = "";
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
        if (parentNode != null) {
            Object userObject = parentNode.getUserObject();
            if (userObject.toString().equals(parentRootName)) {
                parentCode = "#";
            } else {
                Menu menu = (Menu) parentNode.getUserObject();
                parentCode = menu.getKey().getMenuCode();
            }
        }
        String menuName = txtMenuName.getText();
        if (!menuName.isEmpty()) {
            if (selectedNode.getUserObject() instanceof Menu menu) {
                menu.setMenuName(menuName);
                menu.setMenuNameMM(txtMenuMM.getText());
                menu.setParentMenuCode(parentCode);
                menu.setMenuUrl(txtMenuUrl.getText());
                ChartOfAccount coa = cOA3AutoCompleter.getCOA();
                menu.setAccount(coa == null ? null : coa.getKey().getCoaCode());
                menu.setMenuType(txtMenuType.getText().trim());
                menu.setMenuClass(txtMenuClass.getText());
                menu.setOrderBy(Integer.valueOf(Util1.isNull(txtOrder.getText(), "0")));
                menu.setMenuVersion(Util1.getInteger(txtVersion.getText()));
                userRepo.save(menu).doOnSuccess((t) -> {
                    selectedNode.setUserObject(t);
                    TreePath path = treeCOA.getSelectionPath();
                    DefaultTreeModel model = (DefaultTreeModel) treeCOA.getModel();
                    model.nodeChanged(selectedNode);
                    treeCOA.setSelectionPath(path);
                    sendMessage(t.getMenuName());
                }).doOnError((e) -> {
                    JOptionPane.showConfirmDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }).doOnTerminate(() -> {
                    observer.selected("menu", "menu");
                    clear();
                }).subscribe();
            }

        }
    }

    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.MENU, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void setMenu(Menu menu) {
        txtMenuName.setText(menu.getMenuName());
        txtMenuMM.setText(menu.getMenuNameMM());
        txtMenuUrl.setText(menu.getMenuUrl());
        txtOrder.setText(menu.getOrderBy() == null ? null : menu.getOrderBy().toString());
        accountRepo.findCOA(menu.getAccount()).doOnSuccess((t) -> {
            cOA3AutoCompleter.setCoa(t);
        }).subscribe();
        txtMenuType.setText(menu.getMenuType());
        txtMenuClass.setText(menu.getMenuClass());
        txtVersion.setText(Util1.getString(menu.getMenuVersion()));
        enableControl(true);
    }

    private void clear() {
        txtMenuName.setText(null);
        txtMenuMM.setText(null);
        txtMenuUrl.setText(null);
        txtOrder.setText(null);
        txtAccount.setText(null);
        txtMenuType.setText(null);
        txtMenuClass.setText(null);
        txtMenuMM.setText(null);
        txtVersion.setText(null);
        enableControl(false);
    }

    private void enableControl(boolean status) {
        ComponentUtil.enableForm(panelEntry, status);
    }

    private void removeSpace() {
        txtMenuUrl.setText(txtMenuUrl.getText().replaceAll(" ", ""));
    }

    private void initFocusListener() {
        ComponentUtil.addFocusListener(this);
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", false);
        observer.selected("history", false);
        observer.selected("delete", false);
        observer.selected("refresh", true);
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
        panelEntry = new javax.swing.JPanel();
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
        txtMenuClass = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtMenuMM = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtVersion = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        treeCOA.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Menu");
        treeCOA.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(treeCOA);

        panelEntry.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelEntry.setFont(Global.textFont);

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
        txtMenuUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMenuUrlActionPerformed(evt);
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

        txtMenuType.setEditable(false);
        txtMenuType.setFont(Global.textFont);
        txtMenuType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuTypeFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Menu Class");

        txtMenuClass.setFont(Global.textFont);
        txtMenuClass.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuClassFocusGained(evt);
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

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Version");

        txtVersion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtVersion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVersionFocusGained(evt);
            }
        });

        javax.swing.GroupLayout panelEntryLayout = new javax.swing.GroupLayout(panelEntry);
        panelEntry.setLayout(panelEntryLayout);
        panelEntryLayout.setHorizontalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMenuUrl)
                    .addComponent(txtMenuName)
                    .addComponent(txtAccount)
                    .addComponent(txtMenuType)
                    .addComponent(txtMenuClass)
                    .addComponent(txtMenuMM)
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addComponent(txtOrder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVersion)))
                .addContainerGap())
        );
        panelEntryLayout.setVerticalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtMenuName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtMenuMM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMenuUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtMenuType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtMenuClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(txtVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtMenuNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuNameFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMenuNameFocusGained

    private void txtMenuUrlFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuUrlFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMenuUrlFocusGained

    private void txtAccountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAccountFocusGained

    private void txtOrderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOrderFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOrderFocusGained

    private void txtMenuTypeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuTypeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMenuTypeFocusGained

    private void txtMenuClassFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuClassFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMenuClassFocusGained

    private void txtMenuMMFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuMMFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMenuMMFocusGained

    private void txtMenuUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMenuUrlActionPerformed
        // TODO add your handling code here:
        removeSpace();
    }//GEN-LAST:event_txtMenuUrlActionPerformed

    private void txtVersionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVersionFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVersionFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelEntry;
    private javax.swing.JTree treeCOA;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtMenuClass;
    private javax.swing.JTextField txtMenuMM;
    private javax.swing.JTextField txtMenuName;
    private javax.swing.JTextField txtMenuType;
    private javax.swing.JTextField txtMenuUrl;
    private javax.swing.JFormattedTextField txtOrder;
    private javax.swing.JFormattedTextField txtVersion;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (DefaultMutableTreeNode) treeCOA.getLastSelectedPathComponent();
        if (selectedNode != null) {
            if (selectedNode.getUserObject() != null) {
                if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                    if (selectedNode.getUserObject() instanceof Menu menu) {
                        setMenu(menu);
                    }
                } else {
                    clear();
                }
            }
        }
    }

    @Override
    public void save() {
        saveMenu();
    }

    @Override
    public void newForm() {
        clear();
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

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("COA_TF")) {
            txtMenuName.setText(cOA3AutoCompleter.getCOA().getCoaNameEng());

        }
    }
}
