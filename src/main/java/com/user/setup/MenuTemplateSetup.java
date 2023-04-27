/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.acc.model.BusinessType;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.Util1;
import com.user.common.UserRepo;
import com.user.model.MenuTemplate;
import com.user.model.MenuTemplateKey;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JComboBox;
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

/**
 *
 * @author Lenovo
 */
public class MenuTemplateSetup extends javax.swing.JPanel implements TreeSelectionListener, PanelControl {

    private DefaultMutableTreeNode treeRoot;
    private UserRepo userRepo;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode selectedNode;
    private SelectionObserver observer;
    private final String rootName = "Core Value";
    private JProgressBar progress;
    private JComboBox<BusinessType> busType;
    private JPopupMenu popupmenu;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public JComboBox<BusinessType> getBusType() {
        return busType;
    }

    public void setBusType(JComboBox<BusinessType> busType) {
        this.busType = busType;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private final ActionListener menuListener = (ActionEvent evt) -> {
        if (evt.getSource() instanceof JMenuItem actionMenu) {
            String menuName = actionMenu.getText();
            switch (menuName) {
                case "New Menu" ->
                    newMenu("Menu");
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
    public MenuTemplateSetup() {
        initComponents();
        initKeyListener();
        initPopup();
    }

    public void initMain() {
        initTree();
    }

    private void searchMenu() {
        if (busType.getSelectedItem() instanceof BusinessType type) {
            progress.setIndeterminate(true);
            userRepo.getMenuTemplate(type.getBusId())
                    .subscribe((menus) -> {
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
                    }, (e) -> {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                        progress.setIndeterminate(false);
                    });
        }

    }

    private void addChildMenu(DefaultMutableTreeNode parent, List<MenuTemplate> listVRM) {
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
        MenuTemplate root = new MenuTemplate();
        root.setMenuName(rootName);
        treeRoot = new DefaultMutableTreeNode(root);
        searchMenu();
    }

    private void initPopup() {
        popupmenu = new JPopupMenu("Edit");
        popupmenu.setFont(Global.textFont);
        JMenuItem newMenu = new JMenuItem("New Menu");
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem newReport = new JMenuItem("New Report");
        newMenu.addActionListener(menuListener);
        delete.addActionListener(menuListener);
        newReport.addActionListener(menuListener);
        popupmenu.add(newMenu);
        popupmenu.add(newReport);
        popupmenu.add(delete);
    }

    private void newMenu(String menuType) {
        if (selectedNode.getUserObject() instanceof MenuTemplate obj) {
            if (busType.getSelectedItem() instanceof BusinessType type) {
                MenuTemplate menu = new MenuTemplate();
                MenuTemplateKey key = new MenuTemplateKey();
                key.setBusId(type.getBusId());
                menu.setKey(key);
                menu.setMenuClass(obj.getMenuClass());
                switch (menuType) {
                    case "Menu" -> {
                        menu.setMenuName("New Menu");
                        menu.setMenuType("Menu");

                    }
                    case "Report" -> {
                        menu.setMenuName("New Report");
                        menu.setMenuType("Report");
                    }
                }
                DefaultTreeModel model = (DefaultTreeModel) treeCOA.getModel();
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(menu);
                DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) treeCOA.getLastSelectedPathComponent();
                model.insertNodeInto(newNode, selectNode, selectNode.getChildCount());
                TreePath path = new TreePath(newNode.getPath());
                treeCOA.expandPath(new TreePath(selectNode.getPath()));
                treeCOA.setSelectionPath(path);
                txtMenuName.requestFocus();
            }
        }

    }

    private void saveMenu() {
        Integer parentId = 0;
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
        if (parentNode.getUserObject() instanceof MenuTemplate tmp) {
            if (!tmp.getMenuName().equals(rootName)) {
                MenuTemplate menu = (MenuTemplate) parentNode.getUserObject();
                parentId = menu.getKey().getMenuId();
            }
        }
        String menuName = txtMenuName.getText();
        if (!menuName.isEmpty()) {
            MenuTemplate vMenu = (MenuTemplate) selectedNode.getUserObject();
            MenuTemplate menu = new MenuTemplate();
            menu.setKey(vMenu.getKey());
            menu.setMenuName(menuName);
            menu.setParentMenuId(parentId);
            menu.setMenuUrl(txtMenuUrl.getText());
            menu.setAccount(txtAccount.getText());
            menu.setMenuType(txtMenuType.getText().trim());
            menu.setMenuClass(txtClass.getText());
            menu.setOrderBy(Integer.valueOf(Util1.isNull(txtOrder.getText(), "0")));
            if (txtOrder.getValue() != null) {
                menu.setOrderBy(Util1.getInteger(txtOrder.getText()));
            }
            userRepo.save(menu).subscribe((t) -> {
                selectedNode.setUserObject(t);
                TreePath path = treeCOA.getSelectionPath();
                DefaultTreeModel model = (DefaultTreeModel) treeCOA.getModel();
                model.nodeChanged(selectedNode);
                treeCOA.setSelectionPath(path);
                clear();
                observer.selected("menu", "menu");
            });
        }
    }

    private void setMenu(MenuTemplate menu) {
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

    private void removeSpace() {
        txtMenuUrl.setText(txtMenuUrl.getText().replaceAll(" ", ""));
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
        txtMenuName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtMenuUrl = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtOrder = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        txtMenuType = new javax.swing.JTextField();
        txtClass = new javax.swing.JTextField();
        txtMenuMM = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        treeCOA.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Menu");
        treeCOA.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(treeCOA);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setFont(Global.textFont);

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

        txtMenuType.setEditable(false);
        txtMenuType.setFont(Global.textFont);
        txtMenuType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuTypeFocusGained(evt);
            }
        });

        txtClass.setFont(Global.textFont);
        txtClass.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClassFocusGained(evt);
            }
        });

        txtMenuMM.setFont(Global.textFont);
        txtMenuMM.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuMMFocusGained(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Menu Name");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Menu Name MM");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Menu Type");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Menu Class");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMenuUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                    .addComponent(txtMenuName)
                    .addComponent(txtOrder)
                    .addComponent(txtAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                    .addComponent(txtMenuType, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                    .addComponent(txtClass, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                    .addComponent(txtMenuMM))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMenuName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMenuMM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
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
                    .addComponent(txtMenuType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
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

    private void txtMenuUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMenuUrlActionPerformed
        // TODO add your handling code here:
        removeSpace();
    }//GEN-LAST:event_txtMenuUrlActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
            if (selectedNode.getUserObject() instanceof MenuTemplate tmp) {
                if (!tmp.getMenuName().equals(rootName)) {
                    setMenu(tmp);
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
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void delete() {
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
