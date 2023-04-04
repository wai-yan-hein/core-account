/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.setup;

import com.acc.common.AccountRepo;
import com.acc.model.Department;
import com.acc.model.DepartmentKey;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.Util1;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class DepartmentSetup extends javax.swing.JPanel implements TreeSelectionListener, MouseListener, KeyListener,
        PanelControl {

    DefaultMutableTreeNode treeRoot;
    DefaultMutableTreeNode child;
    DefaultTreeModel treeModel;
    DefaultMutableTreeNode selectedNode;
    private final String parentRootName = "Department";
    private SelectionObserver observer;
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

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private AccountRepo accountRepo;

    JPopupMenu popupmenu;
    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        switch (menuName) {
            case "New" ->
                newDepartment();
        }

    };

    /**
     * Creates new form DepartmentSetup
     */
    public DepartmentSetup() {
        initComponents();
        initKeyListener();
        initPopup();
    }

    private void batchLock(boolean lock) {
        txtName.setEnabled(lock);
        txtUserCode.setEnabled(lock);
        txtSystemCode.setEnabled(lock);
        observer.selected("save", lock);
        observer.selected("delete", lock);
    }

    public void initMain() {
        batchLock(!Global.batchLock);
        initTree();
    }

    private void initTree() {
        treeModel = (DefaultTreeModel) treeDep.getModel();
        treeModel.setRoot(null);
        treeRoot = new DefaultMutableTreeNode(parentRootName);
        progress.setIndeterminate(true);
        taskExecutor.execute(() -> {
            createTreeNode(treeRoot);
            treeModel.setRoot(treeRoot);
        });

    }

    private void createTreeNode(DefaultMutableTreeNode treeRoot) {
        accountRepo.getDepartment().subscribe((t) -> {
            t.forEach((menu) -> {
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
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }, () -> {
            treeModel.setRoot(treeRoot);
            treeModel.reload(treeRoot);
            progress.setIndeterminate(false);
        });
    }

    private void addChildMenu(DefaultMutableTreeNode parent, List<Department> departments) {
        departments.forEach((dep) -> {
            if (dep.getChild() != null) {
                if (!dep.getChild().isEmpty()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(dep);
                    parent.add(node);
                    addChildMenu(node, dep.getChild());
                } else {  //No Child
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(dep);
                    parent.add(node);
                }
            } else {  //No Child
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(dep);
                parent.add(node);
            }
        });
    }

    private void setEnabledControl(boolean status) {
        txtUserCode.setEnabled(status);
        txtName.setEnabled(status);
        chkActive.setEnabled(status);
    }

    private void setDepartment(Department dep) {
        txtSystemCode.setText(dep.getKey().getDeptCode());
        txtUserCode.setText(dep.getUserCode());
        txtName.setText(dep.getDeptName());
        chkActive.setSelected(Util1.getBoolean(dep.isActive()));
        labelStatus.setText("EDIT");
    }

    public void clear() {
        txtSystemCode.setText(null);
        txtUserCode.setText(null);
        txtName.setText(null);
        chkActive.setSelected(true);
        labelStatus.setText("NEW");
        txtUserCode.requestFocus();
    }

    private void initPopup() {
        popupmenu = new JPopupMenu();
        JMenuItem cut = new JMenuItem("New");
        JMenuItem copy = new JMenuItem("Delete");
        cut.addActionListener(menuListener);
        copy.addActionListener(menuListener);
        popupmenu.add(cut);
        popupmenu.add(copy);

    }

    private void initKeyListener() {
        txtName.addKeyListener(this);
        txtSystemCode.addKeyListener(this);
        txtUserCode.addKeyListener(this);
        chkActive.addKeyListener(this);

        txtUserCode.requestFocus();
        treeDep.addMouseListener(this);
        treeDep.addTreeSelectionListener(this);
        treeDep.addKeyListener(this);

    }

    private void newDepartment() {
        Department dep = new Department();
        DepartmentKey key = new DepartmentKey();
        key.setCompCode(Global.compCode);
        dep.setKey(key);
        dep.setDeptName("New Department");
        DefaultTreeModel model = (DefaultTreeModel) treeDep.getModel();
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(dep);
        DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) treeDep.getLastSelectedPathComponent();
        model.insertNodeInto(newNode, selectNode, selectNode.getChildCount());
        TreePath path = new TreePath(newNode.getPath());
        treeDep.expandPath(new TreePath(selectNode.getPath()));
        treeDep.setSelectionPath(path);
        txtUserCode.requestFocus();
    }

    private void saveDepartment() {
        Department dep = new Department();
        DepartmentKey key = new DepartmentKey();
        key.setCompCode(Global.compCode);
        key.setDeptCode(txtSystemCode.getText());
        dep.setKey(key);
        dep.setDeptName(txtName.getText());
        dep.setUserCode(txtUserCode.getText());
        dep.setActive(chkActive.isSelected());
        dep.setMacId(Global.macId);
        if (isValidDepartment(dep)) {
            accountRepo.saveDepartment(dep).subscribe((t) -> {
                if (t != null) {
                    selectedNode.setUserObject(t);
                    TreePath path = treeDep.getSelectionPath();
                    DefaultTreeModel model = (DefaultTreeModel) treeDep.getModel();
                    model.nodeChanged(selectedNode);
                    treeDep.setSelectionPath(path);
                    setEnabledControl(false);
                    clear();
                }
            });

        }
    }

    private boolean isValidDepartment(Department dept) {
        if (Util1.isNull(dept.getDeptName(), "-").equals("-")) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid department code.");
            return false;
        } else {
            dept.setUpdatedBy(Global.loginUser.getUserCode());
            dept.setUpdatedDt(Util1.getTodayDate());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedNode.getParent();
            Object userObject = node.getUserObject();
            if (userObject.toString().equals(parentRootName)) {
                dept.setParentDept("#");
            } else {
                Department dep = (Department) userObject;
                dept.setParentDept(dep.getKey().getDeptCode());
            }
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSystemCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        labelStatus = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeDep = new javax.swing.JTree();

        jLabel4.setText("jLabel4");

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont        );
        jLabel1.setText("System Code");

        txtSystemCode.setEditable(false);
        txtSystemCode.setFont(Global.textFont);
        txtSystemCode.setName("txtSystemCode"); // NOI18N

        jLabel2.setFont(Global.lableFont        );
        jLabel2.setText("User Code");

        txtUserCode.setFont(Global.textFont);
        txtUserCode.setEnabled(false);
        txtUserCode.setName("txtUserCode"); // NOI18N

        jLabel3.setFont(Global.lableFont        );
        jLabel3.setText("Name");

        txtName.setFont(Global.textFont);
        txtName.setEnabled(false);
        txtName.setName("txtName"); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });

        chkActive.setFont(Global.lableFont        );
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setEnabled(false);
        chkActive.setName("chkActive"); // NOI18N

        labelStatus.setFont(Global.menuFont        );
        labelStatus.setText("NEW");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addComponent(txtUserCode)
                    .addComponent(txtSystemCode)
                    .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtSystemCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        treeDep.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Department");
        treeDep.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeDep.setName("treeDep"); // NOI18N
        jScrollPane2.setViewportView(treeDep);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
        txtUserCode.requestFocus();

    }//GEN-LAST:event_formComponentShown

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        // TODO add your handling code here:
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JTree treeDep;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSystemCode;
    private javax.swing.JTextField txtUserCode;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (DefaultMutableTreeNode) treeDep.getLastSelectedPathComponent();
        if (selectedNode != null) {
            if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                Department dep = (Department) selectedNode.getUserObject();
                setDepartment(dep);
                setEnabledControl(true);
            } else {
                clear();
                setEnabledControl(false);
            }
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            popupmenu.show(this, e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JTree jTree) {
            ctrlName = jTree.getName();
        } else if (sourceObj instanceof JCheckBox jCheckBox) {
            ctrlName = jCheckBox.getName();
        } else if (sourceObj instanceof JTextField jTextField) {
            ctrlName = jTextField.getName();
        } else if (sourceObj instanceof JButton jButton) {
            ctrlName = jButton.getName();
        }
        switch (ctrlName) {
            case "txtSystemCode" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtUserCode.requestFocus();
                }
            }
            case "txtUserCode" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtSystemCode.requestFocus();
                }
                tabToTree(e);
            }
            case "txtName" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtUserCode.requestFocus();
                }
                tabToTree(e);
            }
            case "chkActive" -> {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtName.requestFocus();
                }
                tabToTree(e);
            }
            case "btnSave" -> {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    chkActive.requestFocus();
                }
                tabToTree(e);
            }
            case "btnClear" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtUserCode.requestFocus();
                }

                tabToTree(e);
            }
            case "treeDept" -> {
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    if (selectedNode != null) {
                        if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                            Department dep = (Department) selectedNode.getUserObject();
                            setDepartment(dep);
                            setEnabledControl(true);
                        } else {
                            clear();
                            setEnabledControl(false);
                        }
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtUserCode.requestFocus();
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtUserCode.requestFocus();
                }
            }

        }
    }

    private void tabToTree(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            treeDep.requestFocus();
        }
    }

    @Override
    public void save() {
        saveDepartment();
    }

    @Override
    public void delete() {
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
