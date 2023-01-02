/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.setup;

import com.acc.common.AccountRepo;
import com.acc.common.COAViewTableModel;
import com.acc.common.StandardCOATableModel;
import com.acc.dialog.COAUnusedDailog;
import com.acc.dialog.ChartOfAccountImportDialog;
import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReturnObject;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.TreeTransferHandler;
import com.common.Util1;
import com.common.model.Menu;
import com.inventory.editor.MenuAutoCompleter;
import com.inventory.model.CFont;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import java.awt.FileDialog;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
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
public class COAManagment extends javax.swing.JPanel implements
        MouseListener,
        TreeSelectionListener, KeyListener,
        PanelControl {

    private final HashMap<Integer, Integer> hmIntToZw = new HashMap<>();
    private DefaultMutableTreeNode selectedNode;
    DefaultTreeModel treeModel;
    private final String parentRootName = "Core Account";
    private DefaultMutableTreeNode treeRoot;
    private final StandardCOATableModel standardCOATableModel = new StandardCOATableModel();
    private final COAUnusedDailog unusedDailog = new COAUnusedDailog();
    private final COAViewTableModel cOAViewTableModel = new COAViewTableModel();
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private WebClient accountApi;
    @Autowired
    private WebClient userApi;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private InventoryRepo inventoryRepo;
    private MenuAutoCompleter completer;
    private TableRowSorter<TableModel> sorter;
    private JPopupMenu popupmenu;
    private SelectionObserver observer;
    private final HashMap<String, Menu> hmMenu = new HashMap<>();
    private ChartOfAccount chartOfAccount;
    private boolean isNew = false;
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

    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        log.info("Selected Menu : " + menuName);
        switch (menuName) {
            case "New" ->
                newCOA();
            case "Delete" ->
                deleteCOA();
            case "Import" ->
                importCOA();
            default -> {
            }
        }

    };

    /**
     * Creates new form COASetup
     */
    public COAManagment() {
        initComponents();
        initKeyListener();
        initPopup();

    }

    public void initMain() {
        multiCurrency();
        initTree();
        initCombo();
    }

    private void initCombo() {
        Mono<ResponseEntity<List<Menu>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-menu-parent")
                .build())
                .retrieve().toEntityList(Menu.class);
        result.subscribe((t) -> {
            completer = new MenuAutoCompleter(txtMenu, t.getBody(), null);
            completer.setObserver(observer);
        }, (e) -> {
        });
    }

    private void initCOATable() {
        log.info("initCOATable");
        tblCOA.setModel(cOAViewTableModel);
        tblCOA.getTableHeader().setFont(Global.tblHeaderFont);
        tblCOA.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblCOA.getColumnModel().getColumn(1).setPreferredWidth(20);
        tblCOA.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblCOA.getColumnModel().getColumn(3).setPreferredWidth(20);
        tblCOA.getColumnModel().getColumn(4).setPreferredWidth(20);
        tblCOA.getColumnModel().getColumn(5).setPreferredWidth(200);
        tblCOA.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblCOA.getModel());
        tblCOA.setRowSorter(sorter);
        searchCOAStandard();
    }

    private void newCOA() {
        ChartOfAccount coa = new ChartOfAccount();
        COAKey key = new COAKey();
        key.setCompCode(Global.compCode);
        key.setCoaCode(txtSysCode.getText());
        coa.setKey(key);
        coa.setCoaNameEng("New Chart of Account");
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(coa);
        if (selectedNode != null) {
            selectedNode.add(child);
            treeModel.insertNodeInto(child, selectedNode, selectedNode.getChildCount() - 1);
            treeCOA.setSelectionInterval(selectedNode.getChildCount(), selectedNode.getChildCount());
        }
        isNew = true;
    }

    private void saveChartAcc() {
        String parentCode;
        String option;
        if (selectedNode.getParent() != null) {
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            try {
                Object userObject = parentNode.getUserObject();
                if (userObject.toString().equals(parentRootName)) {
                    parentCode = "#";
                    option = "SYS";
                } else {
                    ChartOfAccount pCoa = (ChartOfAccount) parentNode.getUserObject();
                    parentCode = pCoa.getKey().getCoaCode();
                    option = "USR";
                }
                if (isNew) {
                    chartOfAccount.setCreatedBy(Global.loginUser.getUserCode());
                    chartOfAccount.setCreatedDate(Util1.getTodayDate());
                }
                int level = selectedNode.getLevel();
                chartOfAccount.setCoaNameEng(txtName.getText());
                chartOfAccount.setCoaCodeUsr(txtUsrCode.getText());
                chartOfAccount.setCoaParent(parentCode);
                chartOfAccount.setCoaLevel(level);
                chartOfAccount.setModifiedBy(Global.loginUser.getUserCode());
                chartOfAccount.setModifiedDate(Util1.getTodayDate());
                chartOfAccount.setOption(option);
                chartOfAccount.setActive(chkActive.isSelected());
                chartOfAccount.setCurCode(Util1.isNull(txtCurrency.getText(), null));
                chartOfAccount.setMacId(Global.macId);
                ChartOfAccount coaSave = accountRepo.saveCOA(chartOfAccount);
                if (coaSave != null) {
                    if (lblStatus.getText().equals("EDIT")) {
                        selectedNode.setUserObject(coaSave);
                        treeModel.reload(selectedNode);
                        setEnabledControl(false);
                        clear();
                    }
                }
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Parent Tree.");
        }

    }

    private void deleteCOA() {
        try {
            if (selectedNode != null) {
                ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                if (coa != null) {
                    String code = coa.getKey().getCoaCode();
                    int status = deleteCOA(code);
                    if (status == 10) {
                        JOptionPane.showMessageDialog(Global.parentForm, "Can't delete this account is already used.");
                    } else {
                        treeModel.removeNodeFromParent(selectedNode);
                        treeModel.reload(selectedNode);
                    }
                }
            }
        } catch (HeadlessException e) {
            log.error("Delete ChartOfAccount :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Delete ChartOfAccount", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int deleteCOA(String coaCode) {
        return 1;
    }

    private void initPopup() {
        popupmenu = new JPopupMenu("Edit");
        JMenuItem cut = new JMenuItem("New");
        JMenuItem copy = new JMenuItem("Delete");
        JMenuItem importCOA = new JMenuItem("Import");
        cut.addActionListener(menuListener);
        copy.addActionListener(menuListener);
        importCOA.addActionListener(menuListener);
        popupmenu.add(cut);
        popupmenu.add(copy);
        popupmenu.add(importCOA);
    }

    private void multiCurrency() {
        if (ProUtil.isMultiCur()) {
            txtCurrency.setVisible(true);
            lblCurrency.setVisible(true);
        } else {
            txtCurrency.setVisible(false);
            lblCurrency.setVisible(false);
        }
    }

    private void initTree() {
        log.info("initTree");
        progress.setIndeterminate(true);
        treeModel = (DefaultTreeModel) treeCOA.getModel();
        treeCOA.setDragEnabled(true);
        treeCOA.setDropMode(DropMode.ON_OR_INSERT);
        treeCOA.setTransferHandler(new TreeTransferHandler(inventoryApi));
        treeCOA.getSelectionModel().setSelectionMode(
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        treeRoot = new DefaultMutableTreeNode(parentRootName);
        createTreeNode(treeRoot);
    }

    private void expandTree(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), expand);
    }

    private void expandAll(JTree tree, TreePath path, boolean expand) {
        TreeNode node = (TreeNode) path.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            Enumeration<? extends TreeNode> enumeration = node.children();
            while (enumeration.hasMoreElements()) {
                TreeNode treeNode = enumeration.nextElement();
                TreePath treePath = path.pathByAddingChild(treeNode);

                expandAll(tree, treePath, expand);
            }
        }

        if (expand) {
            tree.expandPath(path);
        } else {
            tree.collapsePath(path);
        }
    }

    private void createTreeNode(DefaultMutableTreeNode treeRoot) {
        Mono<ResponseEntity<List<ChartOfAccount>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-coa-tree")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(ChartOfAccount.class);
        result.subscribe((t) -> {
            List<ChartOfAccount> chart = t.getBody();
            if (!chart.isEmpty()) {
                chart.forEach((menu) -> {
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
                treeModel.setRoot(treeRoot);
                treeModel.reload(treeRoot);
                progress.setIndeterminate(false);
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });

    }

    private void addChildMenu(DefaultMutableTreeNode parent, List<ChartOfAccount> chart) {
        chart.forEach((dep) -> {
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

    private void setCOA(ChartOfAccount coa) {
        chartOfAccount = coa;
        setEnabledControl(true);
        txtSysCode.setText(chartOfAccount.getKey().getCoaCode());
        txtName.setText(chartOfAccount.getCoaNameEng());
        txtUsrCode.setText(chartOfAccount.getCoaCodeUsr());
        chkActive.setSelected(Util1.getBoolean(chartOfAccount.isActive()));
        txtCurrency.setText(coa.getCurCode());
        lblStatus.setText("EDIT");
        if (chartOfAccount.getCoaLevel() != null) {
            if (chartOfAccount.getCoaLevel() == 3) {
                btnCreate.setEnabled(true);
                Menu menu = hmMenu.get(chartOfAccount.getKey().getCoaCode());
                completer.setMenu(menu);
            } else {
                btnCreate.setEnabled(false);
            }
        }
        txtUsrCode.requestFocus();
    }

    public void clear() {
        txtSysCode.setText(null);
        txtName.setText(null);
        txtUsrCode.setText(null);
        chkActive.setSelected(false);
        txtCurrency.setText(null);
        treeCOA.requestFocus();
        chartOfAccount = new ChartOfAccount();
        isNew = false;
    }

    private void initKeyListener() {
        txtName.addKeyListener(this);
        txtSysCode.addKeyListener(this);
        txtUsrCode.addKeyListener(this);
        chkActive.addKeyListener(this);
        treeCOA.addKeyListener(this);
        treeCOA.addMouseListener(this);
        treeCOA.addTreeSelectionListener(this);

    }

    private void setEnabledControl(boolean status) {
        txtUsrCode.setEnabled(status);
        txtName.setEnabled(status);
        chkActive.setEnabled(status);
        btnImport.setEnabled(status);
        txtCurrency.setEnabled(status);

    }

    private void saveMenu() {
        if (completer.getMenu() != null) {
            ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
            Menu menu = new Menu();
            menu.setMenuName(coa.getCoaNameEng());
            menu.setMenuClass(completer.getMenu().getMenuClass());
            menu.setParentMenuCode(completer.getMenu().getMenuCode());
            menu.setAccount(coa.getKey().getCoaCode());
            menu.setMenuType("Menu");
            try {
                saveMenu(menu);
            } catch (HeadlessException e) {
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
                log.info("Save Menu :" + e.getMessage());
            }
        }
    }

    private void saveMenu(Menu menu) {
        Mono<ReturnObject> result = userApi.post()
                .uri("/user/save-menu")
                .body(Mono.just(menu), Menu.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block();
        clear();
        observer.selected("menu", "menu");
    }

    private void importCOA() {
        ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
        Integer cLevel = coa == null ? 0 : coa.getCoaLevel();
        String cCode = cLevel == 2 ? coa.getKey().getCoaCode() : null;
        if (cCode != null) {
            FileDialog dialog = new FileDialog(Global.parentForm, "Choose CSV File", FileDialog.LOAD);
            dialog.setDirectory("D:\\");
            dialog.setFile(".csv");
            dialog.setVisible(true);
            String directory = dialog.getFile();
            log.info("File Path :" + directory);
            String path = dialog.getDirectory() != null ? dialog.getDirectory() + "\\" + directory : "";
            readFile(path, cCode);
        }

    }

    private void readFile(String path, String parentCode) {
        List<CFont> listFont = inventoryRepo.getFont();
        if (listFont != null) {
            listFont.forEach(f -> {
                hmIntToZw.put(f.getIntCode(), f.getFontKey().getZwKeyCode());
            });
        }
        String line;
        int lineCount = 0;
        try ( FileInputStream fis = new FileInputStream(path);  InputStreamReader isr = new InputStreamReader(fis);  BufferedReader reader = new BufferedReader(isr)) {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String userCode = data[0];
                String coaName = data[1];
                ChartOfAccount coa = new ChartOfAccount();
                COAKey key = new COAKey();
                key.setCompCode(Global.compCode);
                coa.setKey(key);
                coa.setCoaCodeUsr(userCode);
                coa.setOption("USR");
                coa.setCoaLevel(3);
                coa.setCoaParent(parentCode);
                coa.setCoaNameEng(getZawgyiText(coaName));
                coa.setActive(Boolean.TRUE);
                coa.setCreatedDate(Util1.getTodayDate());
                coa.setCreatedBy(Global.loginUser.getUserCode());
                coa.setMacId(Global.macId);
                accountRepo.saveCOA(coa);
            }
            log.info("Import Sucess : " + lineCount);
        } catch (Exception ex) {
            log.error("readFile : " + ex.getMessage());
        }
    }

    private String getZawgyiText(String text) {
        String tmpStr = "";
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                String tmpS = Character.toString(text.charAt(i));
                int tmpChar = (int) text.charAt(i);
                if (hmIntToZw.containsKey(tmpChar)) {
                    char tmpc = (char) hmIntToZw.get(tmpChar).intValue();
                    if (tmpStr.isEmpty()) {
                        tmpStr = Character.toString(tmpc);
                    } else {
                        tmpStr = tmpStr + Character.toString(tmpc);
                    }
                } else if (tmpS.equals("ƒ")) {
                    if (tmpStr.isEmpty()) {
                        tmpStr = "ႏ";
                    } else {
                        tmpStr = tmpStr + "ႏ";
                    }
                } else if (tmpStr.isEmpty()) {
                    tmpStr = tmpS;
                } else {
                    tmpStr = tmpStr + tmpS;
                }
            }
        }

        return tmpStr;
    }

    private void tblCOAStandard() {
        log.info("tblCOAStandard");
        tblStandCOA.setCellSelectionEnabled(true);
        tblStandCOA.getTableHeader().setFont(Global.tblHeaderFont);
        tblStandCOA.setModel(standardCOATableModel);
        tblStandCOA.getTableHeader().setFont(Global.textFont);
        tblStandCOA.setRowHeight(Global.tblRowHeight);
        tblStandCOA.setFont(Global.textFont);
        standardCOATableModel.setParent(tblStandCOA);
        tblStandCOA.getColumnModel().getColumn(0).setPreferredWidth(100);// Sys Code
        tblStandCOA.getColumnModel().getColumn(1).setPreferredWidth(100);// Usr Code
        tblStandCOA.getColumnModel().getColumn(2).setPreferredWidth(400);// Name
        tblStandCOA.getColumnModel().getColumn(3).setPreferredWidth(100);// Name
        tblStandCOA.getColumnModel().getColumn(3).setPreferredWidth(100);// Active
        tblStandCOA.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblStandCOA.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //tblStandCOA.setDefaultRenderer(Boolean.class, new TableCellRender());
        //tblStandCOA.setDefaultRenderer(Object.class, new TableCellRender());
        tblStandCOA.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        searchCOAStandard();
    }

    private void searchCOAStandard() {
        log.info("searchCOAStandard");
        Mono<ResponseEntity<List<ChartOfAccount>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/account/get-coa")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(ChartOfAccount.class);

        result.subscribe((t) -> {
            standardCOATableModel.setListCOA(t.getBody());
        }, (e) -> {
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

        tabMain = new javax.swing.JTabbedPane();
        panel1 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSysCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtUsrCode = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        panelMenu = new javax.swing.JPanel();
        btnCreate = new javax.swing.JButton();
        txtMenu = new javax.swing.JTextField();
        btnImport = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        lblCurrency = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeCOA = new javax.swing.JTree();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCOA = new javax.swing.JTable();
        txtCOASearch = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblStandCOA = new javax.swing.JTable();

        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                formComponentRemoved(evt);
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tabMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabMainStateChanged(evt);
            }
        });
        tabMain.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabMainComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("System Code");

        txtSysCode.setEditable(false);
        txtSysCode.setFont(Global.textFont);
        txtSysCode.setName("txtSysCode"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("User Code");

        txtUsrCode.setFont(Global.textFont);
        txtUsrCode.setEnabled(false);
        txtUsrCode.setName("txtUsrCode"); // NOI18N

        txtName.setFont(Global.textFont);
        txtName.setEnabled(false);
        txtName.setName("txtName"); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Name");

        chkActive.setFont(Global.lableFont);
        chkActive.setText("Active");
        chkActive.setEnabled(false);
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        panelMenu.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Menu Group Mapping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        btnCreate.setFont(Global.lableFont);
        btnCreate.setText("Create");
        btnCreate.setEnabled(false);
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        txtMenu.setFont(Global.textFont);
        txtMenu.setName("txtName"); // NOI18N
        txtMenu.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuFocusGained(evt);
            }
        });

        javax.swing.GroupLayout panelMenuLayout = new javax.swing.GroupLayout(panelMenu);
        panelMenu.setLayout(panelMenuLayout);
        panelMenuLayout.setHorizontalGroup(
            panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCreate)
                .addContainerGap())
        );
        panelMenuLayout.setVerticalGroup(
            panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate)
                    .addComponent(txtMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnImport.setBackground(Global.selectionColor);
        btnImport.setFont(Global.textFont);
        btnImport.setForeground(new java.awt.Color(255, 255, 255));
        btnImport.setText("Import");
        btnImport.setName("btnSave"); // NOI18N
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Unused COA");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblCurrency.setFont(Global.lableFont);
        lblCurrency.setText("Currency");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtName"); // NOI18N
        txtCurrency.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCurrencyFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName)
                            .addComponent(txtUsrCode)
                            .addComponent(txtSysCode)
                            .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCurrency)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnImport)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtSysCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUsrCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCurrency)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkActive)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnImport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(95, Short.MAX_VALUE))
        );

        treeCOA.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("color");
        treeNode1.add(treeNode2);
        treeCOA.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeCOA.setName("treeCOA"); // NOI18N
        jScrollPane1.setViewportView(treeCOA);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabMain.addTab("COA Managment", panel1);

        tblCOA.setAutoCreateRowSorter(true);
        tblCOA.setFont(Global.textFont);
        tblCOA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCOA.setCellSelectionEnabled(true);
        tblCOA.setOpaque(false);
        tblCOA.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblCOA);

        txtCOASearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCOASearchFocusGained(evt);
            }
        });
        txtCOASearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCOASearchKeyReleased(evt);
            }
        });

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Clear");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(txtCOASearch, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCOASearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE))
        );

        tabMain.addTab("COA View", jPanel2);

        tblStandCOA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblStandCOA.setShowHorizontalLines(true);
        tblStandCOA.setShowVerticalLines(true);
        tblStandCOA.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tblStandCOAComponentShown(evt);
            }
        });
        jScrollPane3.setViewportView(tblStandCOA);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        tabMain.addTab("Standard COA", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
        txtUsrCode.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusLost

    private void formComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentRemoved
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentRemoved

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:

    }//GEN-LAST:event_formComponentResized

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        // TODO add your handling code here:

    }//GEN-LAST:event_formComponentMoved

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        // TODO add your handling code here:
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        saveMenu();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        // TODO add your handling code here:
        ChartOfAccountImportDialog importDialog = new ChartOfAccountImportDialog();
        importDialog.setInventoryRepo(inventoryRepo);
        importDialog.setAccountRepo(accountRepo);
        importDialog.setSize(Global.width - 400, Global.height - 400);
        importDialog.setLocationRelativeTo(null);
        importDialog.setVisible(true);

    }//GEN-LAST:event_btnImportActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        unusedDailog.initTable();
        unusedDailog.setSize(Global.width - 400, Global.height - 400);
        unusedDailog.setLocationRelativeTo(null);
        unusedDailog.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtCOASearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCOASearchKeyReleased
        // TODO add your handling code here:
        if (txtCOASearch.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(txtCOASearch.getText()));
        }
    }//GEN-LAST:event_txtCOASearchKeyReleased

    private void txtCOASearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCOASearchFocusGained
        // TODO add your handling code here:
        txtCOASearch.selectAll();
    }//GEN-LAST:event_txtCOASearchFocusGained

    private void txtCurrencyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyFocusGained

    private void tblStandCOAComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tblStandCOAComponentShown
        // TODO add your handling code here:

    }//GEN-LAST:event_tblStandCOAComponentShown

    private void tabMainComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabMainComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_tabMainComponentShown

    private void tabMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabMainStateChanged
        // TODO add your handling code here:
        log.info(String.format("Selected %s", tabMain.getSelectedIndex()));
        int index = tabMain.getSelectedIndex();
        switch (index) {
            case 1 ->
                initCOATable();
            case 2 ->
                tblCOAStandard();
            default -> {
            }
        }

    }//GEN-LAST:event_tabMainStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        txtCOASearch.setText(null);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtMenuFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMenuFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnImport;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCurrency;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panelMenu;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblCOA;
    private javax.swing.JTable tblStandCOA;
    private javax.swing.JTree treeCOA;
    private javax.swing.JTextField txtCOASearch;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtMenu;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSysCode;
    private javax.swing.JTextField txtUsrCode;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            popupmenu.show(this, e.getX(), e.getY());
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (DefaultMutableTreeNode) treeCOA.getLastSelectedPathComponent();
        if (selectedNode != null) {
            if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                setCOA(coa);
            } else {
                clear();
                setEnabledControl(false);
            }
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

        if (sourceObj instanceof JTree) {
            ctrlName = ((JTree) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        }
        //log.info("Control Name Key Released:" + ctrlName);
        switch (ctrlName) {
            case "txtUsrCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtName.requestFocus();
                }
                tabToTree(e);
                break;
            case "txtName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtUsrCode.requestFocus();
                }
                tabToTree(e);

                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtName.requestFocus();
                }
                tabToTree(e);

                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    chkActive.requestFocus();
                }
                tabToTree(e);

                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtUsrCode.requestFocus();
                }
                tabToTree(e);
                break;
            case "treeCOA":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtUsrCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    if (selectedNode != null) {
                        if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                            ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                            setCOA(coa);
                        } else {
                            clear();
                            setEnabledControl(false);

                        }
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtUsrCode.requestFocus();
                }
        }
    }

    private void tabToTree(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            treeCOA.requestFocus();
        }
    }

    @Override
    public void delete() {
        deleteCOA();
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
    public void save() {
        saveChartAcc();
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void filter() {
    }

    @Override
    public void refresh() {
        int index = tabMain.getSelectedIndex();
        switch (index) {
            case 0 ->
                initTree();
            case 1 ->
                searchCOAStandard();
            case 2 ->
                searchCOAStandard();
        }
    }

}
