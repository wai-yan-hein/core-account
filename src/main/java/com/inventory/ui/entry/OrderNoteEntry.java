/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.ComponentUtil;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.Util1;
import com.dms.commom.CVFileTableModel;
import com.dms.commom.FileDropHandler;
import com.dms.commom.IconRenderer;
import com.dms.dialog.FileRenameDialog;
import com.dms.model.CVFile;
import com.dms.model.FileObject;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.OrderFileJoin;
import com.inventory.model.OrderNote;
import com.repo.DMSRepo;
import com.repo.InventoryRepo;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class OrderNoteEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl, KeyListener {
    
    private DefaultMutableTreeNode treeRoot;
    private JProgressBar progress;
    private DMSRepo dmsRepo;
    private SelectionObserver observer;
    private CVFileTableModel fileTableModel = new CVFileTableModel();
    private JPopupMenu newPopup;
    private JPopupMenu morePopup;
    private JPopupMenu trashPopup;
    private FileRenameDialog fileRenameDialog;
    private Map<String, ImageIcon> hmIcon = new HashMap<>();
    private CVFile lastFile;
    private OrderNote orderNote;
    private List<OrderFileJoin> orderDetail = new ArrayList<>();
    
    private TraderAutoCompleter traderAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private InventoryRepo inventoryRepo;
    
    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }
    
    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }
    
    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }
    
    public void setDmsRepo(DMSRepo dmsRepo) {
        this.dmsRepo = dmsRepo;
    }

    /**
     * Creates new form CoreDrive
     */
    public OrderNoteEntry() {
        initComponents();
        initKeyListener();
        initDateListner();
    }
    
    public void initMain() {
        initCombo();
        initNewPopup();
        initMorePopup();
        initTrashPopup();
        initTree();
        initTable();
        assignDefaultValue();
        getHeadFolder();
        getStorageInfo();
        txtOrderDate.setDate(Util1.getTodayDate());
        txtCus.requestFocus();
    }
    
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            } else if (e.getSource() instanceof JTextFieldDateEditor txt) {
                txt.selectAll();
            }
        }
    };
    
    private void initDateListner() {
        txtOrderDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtOrderDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtCus.addFocusListener(fa);
        txtStock.addFocusListener(fa);
        txtOrderCode.addFocusListener(fa);
        txtOrderName.addFocusListener(fa);
    }
    
    private void initKeyListener() {
        txtOrderDate.getDateEditor().getUiComponent().setName("txtOrderDate");
        txtOrderDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtCus.addKeyListener(this);
        txtStock.addKeyListener(this);
    }
    
    private void assignDefaultValue() {
        progress.setIndeterminate(false);
        if (!lblStatus.getText().equals("NEW")) {
            txtOrderDate.setDate(Util1.getTodayDate());
        }
    }
    
    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true);
        stockAutoCompleter.setObserver(this);
        
        inventoryRepo.getDefaultCustomer().doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
    }
    
    private void initNewPopup() {
        newPopup = new JPopupMenu("Edit");
        newPopup.setFont(Global.textFont);
        JMenuItem folder = new JMenuItem("New Folder");
        JMenuItem file = new JMenuItem("File Upload");
        JMenuItem fu = new JMenuItem("Folder Upload");
        folder.addActionListener(menuListener);
        file.addActionListener(menuListener);
        fu.addActionListener(menuListener);
        newPopup.add(folder);
        newPopup.addSeparator();
        newPopup.add(file);
        newPopup.add(fu);
    }
    
    private void initMorePopup() {
        morePopup = new JPopupMenu("More");
        morePopup.setFont(Global.textFont);
        JMenuItem copyLink = new JMenuItem("Copy Link");
        JMenuItem view = new JMenuItem("View");
        JMenuItem download = new JMenuItem("Download");
        JMenuItem rename = new JMenuItem("Rename");
        JMenuItem trash = new JMenuItem("Move to trash");
        copyLink.addActionListener(menuListener);
        view.addActionListener(menuListener);
        download.addActionListener(menuListener);
        rename.addActionListener(menuListener);
        trash.addActionListener(menuListener);
        morePopup.add(copyLink);
        morePopup.add(view);
        morePopup.add(download);
        morePopup.add(rename);
        morePopup.addSeparator();
        morePopup.add(trash);
    }
    
    private void initTrashPopup() {
        trashPopup = new JPopupMenu("Edit");
        trashPopup.setFont(Global.textFont);
        JMenuItem restore = new JMenuItem("Restore");
        JMenuItem delete = new JMenuItem("Delete Forever");
        restore.addActionListener(menuListener);
        delete.addActionListener(menuListener);
        trashPopup.add(restore);
        trashPopup.addSeparator();
        trashPopup.add(delete);
    }
    private final ActionListener menuListener = (ActionEvent evt) -> {
        if (evt.getSource() instanceof JMenuItem actionMenu) {
            String menuName = actionMenu.getText();
            switch (menuName) {
                case "New Folder" ->
                    createFolder();
                case "File Upload" ->
                    createFile();
                case "Folder Upload" ->
                    createFolder();
                case "View" ->
                    viewFile();
                case "Rename" ->
                    renameFile();
                case "Copy Link" ->
                    copyLink();
                case "Move to trash" ->
                    moveToTrash();
                case "Restore" ->
                    restore();
                case "Delete Forever" ->
                    deleteForever();
                case "Download" ->
                    downloadFile();
                
            }
        }
    };
    
    private void viewFile() {
        CVFile file = getSelectFile();
        if (file != null) {
            String url = dmsRepo.getRootUrl() + "file/view/" + file.getFileId();
            openWebBrowser(url);
        }
    }
    
    private void downloadFile() {
        CVFile file = getSelectFile();
        if (file != null) {
            String url = dmsRepo.getRootUrl() + "file/download/" + file.getFileId();
            openWebBrowser(url);
        }
    }
    
    private CVFile getSelectFile() {
        int row = tblChild.convertRowIndexToModel(tblChild.getSelectedRow());
        if (row >= 0) {
            return fileTableModel.getFile(row);
        }
        return null;
    }
    
    private void copyLink() {
        CVFile file = getSelectFile();
        if (file != null) {
            String url = dmsRepo.getRootUrl() + "file/view/" + file.getFileId();
            Util1.copyToClipboard(url);
        }
    }
    
    private void renameFile() {
        if (fileRenameDialog == null) {
            fileRenameDialog = new FileRenameDialog(Global.parentForm);
            fileRenameDialog.setLocationRelativeTo(null);
        }
        CVFile file = getSelectFile();
        if (file != null) {
            String oldName = file.getFileDescription();
            fileRenameDialog.setFileName(oldName);
            fileRenameDialog.setVisible(true);
            if (fileRenameDialog.isSelect()) {
                String newName = fileRenameDialog.getFileName();
                if (!oldName.equals(newName)) {
                    file.setFileDescription(newName);
                    dmsRepo.updateFile(file).doOnSuccess((t) -> {
                        fileTableModel.setFile(t);
                    }).subscribe();
                }
            }
        }
    }
    
    private void moveToTrash() {
        CVFile file = getSelectFile();
        if (file != null) {
            file.setDeleted(true);
            dmsRepo.updateFile(file).doOnSuccess((t) -> {
                fileTableModel.deleteFile(t);
            }).subscribe();
        }
    }
    
    private void restore() {
        progress.setIndeterminate(true);
        CVFile file = getSelectFile();
        if (file != null) {
            file.setDeleted(false);
            dmsRepo.updateFile(file).doOnSuccess((t) -> {
                fileTableModel.deleteFile(t);
            }).doOnTerminate(() -> {
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }
    
    private void deleteForever() {
        progress.setIndeterminate(true);
        CVFile file = getSelectFile();
        if (file != null) {
            file.setDeleted(false);
            dmsRepo.deleteForever(file.getFileId()).doOnSuccess((t) -> {
                fileTableModel.deleteFile(file);
            }).doOnTerminate(() -> {
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }
    
    private void openWebBrowser(String url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                URI uri = new URI(url);
                desktop.browse(uri);
            } catch (IOException | URISyntaxException ex) {
                log.error("openWebBrowser : " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Error opening web browser: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Desktop is not supported on this platform.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initTree() {
        CVFile head = new CVFile();
        head.setFileId("head");
        head.setFileDescription("Drive");
        treeRoot = new DefaultMutableTreeNode(head);
    }
    
    private void initTable() {
        fileTableModel.setDmsRepo(dmsRepo);
        fileTableModel.setTable(tblChild);
        tblChild.setModel(fileTableModel);
        tblChild.getTableHeader().setFont(Global.tblHeaderFont);
        tblChild.setRowHeight(Global.tblRowHeight + 6);
        tblChild.setFont(Global.textFont);
        tblChild.setDragEnabled(true);
        tblChild.setDropMode(DropMode.INSERT_ROWS);
        tblChild.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblChild.setShowHorizontalLines(true);
        tblChild.setShowVerticalLines(false);
        tblChild.getColumnModel().getColumn(0).setCellRenderer(new IconRenderer());
        tblChild.getColumnModel().getColumn(0).setPreferredWidth(1);
        tblChild.getColumnModel().getColumn(1).setPreferredWidth(400);
        tblChild.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblChild.getColumnModel().getColumn(3).setPreferredWidth(70);
        tblChild.setTransferHandler(new FileDropHandler());
    }
    
    private void getHeadFolder() {
        treeRoot.removeAllChildren();
        progress.setIndeterminate(true);
        dmsRepo.getFolder("head").doOnSuccess((t) -> {
            addChildMenu(treeRoot, t);
        }).doOnTerminate(() -> {
            progress.setIndeterminate(false);
        }).subscribe();
    }
    
    private void getStorageInfo() {
        dmsRepo.getStorageInfo().doOnSuccess((t) -> {
            int total = Util1.getInteger(t.getTotalSpace() / 1024 / 1024);
            int used = Util1.getInteger(t.getUsedSpace() / 1024 / 1024);
        }).subscribe();
    }
    
    private void addChildMenu(DefaultMutableTreeNode parent, List<CVFile> listVRM) {
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
    
    private void searchChild(MouseEvent e) {
        if (e.getClickCount() == 1) {
//            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
//            Object userObject = selectedNode.getUserObject();
//            if (userObject instanceof CVFile file) {
//                log.info("Clicked on: " + file.getFileName());
//                progress.setIndeterminate(true);
//                setFileDetail(file);
//                getFile(file.getFileId());
//            }
        }
    }
    
    private void getFile(String parentId) {
        dmsRepo.getFile(parentId).doOnSuccess((t) -> {
            log.info(t.size() + "");
            fileTableModel.setListDetail(t);
        }).doOnTerminate(() -> {
            progress.setIndeterminate(false);
        }).subscribe();
    }
    
    private void getTrash() {
        progress.setIndeterminate(true);
        dmsRepo.getTrash().doOnSuccess((t) -> {
            log.info(t.size() + "");
            fileTableModel.setListDetail(t);
        }).doOnTerminate(() -> {
            lblPath.setText("Trash");
            progress.setIndeterminate(false);
        }).subscribe();
    }
    
    private void setFileDetail(CVFile file) {
        if (file != null) {
            lastFile = file;
            lblFileIcon.setIcon(getFileIcon(Util1.isNull(file.getFileExtension(), "folder")));
            lblFileName.setText(file.getFileDescription());
            lblFileType.setText(file.isFile() ? file.getFileContent() : "Folder");
            lblFileSize.setText(Util1.bytesToSize(file.getFileSize()));
            lblPath.setText(file.getFilePath());
            lblUpdate.setText(Util1.toDateStr(file.getUpdatedDate(), "dd/MM/yyyy hh:mm:ss a"));
            lblCreated.setText(Util1.toDateStr(file.getCreatedDate(), "dd/MM/yyyy hh:mm:ss a"));
        }
    }
    
    public ImageIcon getFileIcon(String extension) {
        if (extension == null) {
            return null;
        }
        if (hmIcon.containsKey(extension)) {
            return hmIcon.get(extension);
        } else {
            ImageIcon originalIcon = dmsRepo.getIcon(extension).block();
            if (originalIcon != null) {
                ImageIcon resizedIcon = new ImageIcon(originalIcon.getImage().getScaledInstance(96, 96, java.awt.Image.SCALE_SMOOTH));
                hmIcon.put(extension, resizedIcon);
                return resizedIcon;
            } else {
                return null; // Handle the case where the icon is not available
            }
        }
    }
    
    private void createFolder() {
        String input = JOptionPane.showInputDialog(this, "New Folder");
        if (!Util1.isNullOrEmpty(input)) {
            String id = lastFile == null ? null : lastFile.getFileId();
            FileObject obj = new FileObject();
            obj.setFolderName(input);
            obj.setParentId(id);
            progress.setIndeterminate(true);
            dmsRepo.createFolder(obj).doOnSuccess((t) -> {
                if (t != null) {
                    fileTableModel.addObjectFirst(t.getBody());
                }
            }).doOnTerminate(() -> {
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }
    
    private void createFile() {
        FileDialog d = new FileDialog(Global.parentForm, "Choose CSV File", FileDialog.LOAD);
        d.setVisible(true);
        String directory = d.getDirectory() + d.getFile();
        log.info(directory);
        String id = "head";
        progress.setIndeterminate(true);
        dmsRepo.createFile(id, Path.of(directory)).doOnSuccess((t) -> {
            fileTableModel.addObjectFirst(t.getBody());
            addOrderFileJoin(t.getBody());
        }).doOnTerminate(() -> {
            progress.setIndeterminate(false);
        }).subscribe();
    }
    
    private void addOrderFileJoin(CVFile file) {
        OrderFileJoin fJ = OrderFileJoin.builder().build();
        fJ.setFileId(file.getFileId());
        fJ.setCompCode(Global.compCode);
        orderDetail.add(fJ);
    }
    
    private void setFileInfo(MouseEvent e) {
        int count = e.getClickCount();
        int row = tblChild.convertRowIndexToModel(tblChild.getSelectedRow());
        CVFile f = fileTableModel.getFile(row);
        if (SwingUtilities.isRightMouseButton(e)) {
            if (f.isDeleted()) {
                trashPopup.show(tblChild, e.getX(), e.getY());
                return;
            } else {
                morePopup.show(tblChild, e.getX(), e.getY());
                return;
            }
        }
        if (count == 1) {
            setFileDetail(f);
        } else {
            if (!f.isFile()) {
                getFile(f.getFileId());
            }
        }
    }
    
    private void showPopup(MouseEvent e) {
//        newPopup.show(btnNew, e.getX(), e.getY());
    }
    
    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", false);
        observer.selected("history", true);
        observer.selected("delete", true);
        observer.selected("refresh", true);
    }
    
    private void saveOrder() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
            orderNote = OrderNote.builder().build();
            if (orderNote.getVouNo() == null) {
                lblStatus.setText("New");
                lblStatus.setForeground(Color.green);
                orderNote.setVouNo(null);
                orderNote.setCreatedBy(Global.loginUser.getUserCode());                
                orderNote.setUpdatedBy(Global.loginUser.getUserCode());
                orderNote.setCreatedDate(Util1.getTodayLocalDateTime());
            } else {
                lblStatus.setText("Edit");
                orderNote.setUpdatedBy(Global.loginUser.getUserCode());
            }
            orderNote.setVouDate(Util1.convertToLocalDateTime(txtOrderDate.getDate()));
            orderNote.setCompCode(Global.compCode);
            orderNote.setDeptId(Global.deptId);
            orderNote.setMacId(Global.macId);
            orderNote.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            orderNote.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
            orderNote.setOrderCode(txtOrderCode.getText());
            orderNote.setOrderName(txtOrderName.getText());
            orderNote.setDeleted(false);
            orderNote.setDetailList(orderDetail);
            inventoryRepo.save(orderNote).doOnSuccess((t) -> {
                progress.setIndeterminate(false);
//                if (print) {
//                    printVoucher(t);
//                } else {
                clear(true);
//                }
            }).doOnError((e) -> {
                observeMain();
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }
    
    private boolean isValidEntry() {
        if (lblStatus.getText().equals("DELETED")) {
            clear(true);
            return false;
        } else if (traderAutoCompleter.getTrader() == null) {
            JOptionPane.showMessageDialog(this, "Choose Trader.",
                    "No Trader.", JOptionPane.ERROR_MESSAGE);
            txtCus.requestFocus();
            return false;
        } else if (stockAutoCompleter.getStock() == null) {
            JOptionPane.showMessageDialog(this, "Choose Stock.",
                    "No Trader.", JOptionPane.ERROR_MESSAGE);
            txtCus.requestFocus();
            return false;
        }
        return true;
    }
    
    private void clear(boolean foucs) {
        disableForm(true);
        assignDefaultValue();
        fileTableModel.clear();
        orderNote = OrderNote.builder().build();
        orderDetail = new ArrayList<>();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtOrderCode.setText(null);
        txtOrderName.setText(null);
        if (foucs) {
            txtCus.requestFocus();
        }
    }
    
    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtOrderDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtOrderCode = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtOrderName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lblPath = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblChild = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        lblFileName = new javax.swing.JLabel();
        lblFileIcon = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblFileType = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblFileSize = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblLocation = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblUpdate = new javax.swing.JLabel();
        lblCreated = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Date");

        txtOrderDate.setDateFormatString("dd/MM/yyyy");
        txtOrderDate.setFont(Global.textFont);
        txtOrderDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOrderDateFocusGained(evt);
            }
        });
        txtOrderDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtOrderDatePropertyChange(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Customer");

        txtCus.setFont(Global.textFont);
        txtCus.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCus.setName("txtCus"); // NOI18N
        txtCus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusFocusGained(evt);
            }
        });
        txtCus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtCusMouseExited(evt);
            }
        });
        txtCus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusActionPerformed(evt);
            }
        });

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Order Code");

        txtOrderCode.setFont(Global.textFont);
        txtOrderCode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtOrderCode.setName("txtOrderCode"); // NOI18N
        txtOrderCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOrderCodeFocusGained(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Order Name");

        txtOrderName.setFont(Global.textFont);
        txtOrderName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtOrderName.setName("txtRemark"); // NOI18N
        txtOrderName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOrderNameFocusGained(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Stock");

        txtStock.setFont(Global.textFont);
        txtStock.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtStock.setName("txtCus"); // NOI18N
        txtStock.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockFocusGained(evt);
            }
        });
        txtStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtStockMouseExited(evt);
            }
        });
        txtStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtOrderDate, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtStock)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtOrderCode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtOrderName)
                .addGap(230, 230, 230))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(txtOrderCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21)
                        .addComponent(txtOrderName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtOrderDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblPath.setText("-");

        jButton1.setText("Choose File");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPath, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(lblStatus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblChild.setModel(new javax.swing.table.DefaultTableModel(
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
        tblChild.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblChildMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblChild);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblFileName.setFont(Global.textFont);
        lblFileName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFileName.setText("File Name");

        lblFileIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFileIcon.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(Global.menuFont);
        jLabel4.setText("File Detail");

        jLabel5.setText("File Type");

        lblFileType.setText("-");

        jLabel7.setText("File Size");

        lblFileSize.setText("-");

        jLabel9.setText("Location");

        lblLocation.setText("-");

        jLabel11.setText("Updated");

        lblUpdate.setText("-");

        lblCreated.setText("-");

        jLabel12.setText("Created");

        jButton2.setBackground(Global.selectionColor);
        jButton2.setFont(Global.lableFont);
        jButton2.setText("Download");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator1)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblFileSize, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFileType, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFileIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFileName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFileIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblFileType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblFileSize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(lblLocation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lblUpdate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(lblCreated))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblChildMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblChildMouseClicked
        // TODO add your handling code here:
        setFileInfo(evt);
    }//GEN-LAST:event_tblChildMouseClicked

    private void txtOrderDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOrderDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOrderDateFocusGained

    private void txtOrderDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtOrderDatePropertyChange

    }//GEN-LAST:event_txtOrderDatePropertyChange

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        txtCus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    private void txtCusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusMouseExited

    private void txtCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusActionPerformed
        //inventoryRepo.getCustomer().subscribe()
    }//GEN-LAST:event_txtCusActionPerformed

    private void txtOrderCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOrderCodeFocusGained

        // TODO add your handling code here:
    }//GEN-LAST:event_txtOrderCodeFocusGained

    private void txtOrderNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOrderNameFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOrderNameFocusGained

    private void txtStockFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockFocusGained

    private void txtStockMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtStockMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockMouseExited

    private void txtStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        createFile();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        downloadFile();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCreated;
    private javax.swing.JLabel lblFileIcon;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblFileSize;
    private javax.swing.JLabel lblFileType;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblPath;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblUpdate;
    private javax.swing.JTable tblChild;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtOrderCode;
    private com.toedter.calendar.JDateChooser txtOrderDate;
    private javax.swing.JTextField txtOrderName;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
    }
    
    @Override
    public void save() {
        saveOrder();
    }
    
    @Override
    public void delete() {
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
    public void refresh() {
        getHeadFolder();
        getStorageInfo();
    }
    
    @Override
    public void filter() {
    }
    
    @Override
    public String panelName() {
        return this.getName();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObject = e.getSource();
        String controlName = "-";
        if (sourceObject instanceof JTextField jTextField) {
            controlName = jTextField.getName();
        } else if (sourceObject instanceof JTextFieldDateEditor jTextFieldDateEditor) {
            controlName = jTextFieldDateEditor.getName();
        }
        
        switch (controlName) {
            case "txtOrderDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObject).getText();
                    txtOrderDate.setDate(Util1.formatDate(date));
                    txtCus.requestFocus();
                }
            }
            case "txtCus" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtStock.requestFocus();
                }
            }
            case "txtStock" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtOrderCode.requestFocus();
                }
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtOrderName.requestFocus();
                }
            }
            
        }
    }
}
