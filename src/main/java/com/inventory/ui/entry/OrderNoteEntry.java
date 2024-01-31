/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.Global;
import com.common.ImageCache;
import com.common.PanelControl;
import com.common.RowHeader;
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
import com.inventory.ui.entry.dialog.OrderNoteHistoryDialog;
import com.repo.DMSRepo;
import com.repo.InventoryRepo;
import com.toedter.calendar.JTextFieldDateEditor;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.io.Opener;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
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
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class OrderNoteEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl, KeyListener {

    public static final int ORDERNOTE = 1;
    private DefaultMutableTreeNode treeRoot;
    @Setter
    private JProgressBar progress;
    @Setter
    private DMSRepo dmsRepo;
    @Setter
    private SelectionObserver observer;
    @Setter
    private TaskExecutor taskExecutor;
    @Setter
    private InventoryRepo inventoryRepo;
    private CVFileTableModel fileTableModel = new CVFileTableModel();
    private JPopupMenu newPopup;
    private JPopupMenu morePopup;
    private JPopupMenu trashPopup;
    private FileRenameDialog fileRenameDialog;
    private Map<String, ImageIcon> hmIcon = new HashMap<>();
    private CVFile lastFile;
    private OrderNote note = OrderNote.builder().build();
    private TraderAutoCompleter traderAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private OrderNoteHistoryDialog dialog;
    private final int type;

    /**
     * Creates new form CoreDrive
     */
    public OrderNoteEntry(int type) {
        this.type = type;
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
        initRowHeader();
        assignDefaultValue();
        getHeadFolder();
        getStorageInfo();
        txtOrderDate.setDate(Util1.getTodayDate());
        txtCus.requestFocus();
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblChild, 30);
        scroll.setRowHeaderView(list);
    }

    private void initDateListner() {
        txtOrderDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtOrderDate.getDateEditor().getUiComponent().addKeyListener(this);
        ComponentUtil.addFocusListener(this);
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
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, false);
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
        progress.setIndeterminate(true);
        taskExecutor.execute(() -> {
            Opener opener = new Opener();
            CVFile file = getSelectFile();
            if (file != null) {
                progress.setIndeterminate(false);
                String url = dmsRepo.getRootUrl() + "file/view/" + file.getFileId();

                // Check if the image is already in the cache
                ImagePlus cachedImage = ImageCache.getImage(url);
                if (cachedImage != null) {
                    // If cached, use the cached image
                    showImage(cachedImage);
                } else {
                    // If not cached, load the image
                    ImagePlus imagePlus = opener.openURL(url);
                    if (imagePlus != null) {
                        // Cache the image for future use
                        ImageCache.cacheImage(url, imagePlus);
                        showImage(imagePlus);
                    } else {
                        JOptionPane.showMessageDialog(this, "File not support viewing. Please download.", "Access Denied", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
    }

    private void showImage(ImagePlus imagePlus) {
        // Display the ImagePlus instance
        ImageWindow window = new ImageWindow(imagePlus);
        window.setSliderHeight(20);
        window.setIconImage(Global.parentForm.getIconImage());
        window.setLocationRelativeTo(null);
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
            proStorage.setMinimum(0);
            proStorage.setMaximum(total);
            proStorage.setValue(used);
            lblStorage.setText(Util1.bytesToSize(t.getUsedSpace()) + " of " + Util1.bytesToSize(t.getTotalSpace()));
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
        } else {
            lblFileIcon.setIcon(null);
            lblFileName.setText(null);
            lblFileType.setText(null);
            lblFileSize.setText(null);
            lblPath.setText(null);
            lblUpdate.setText(null);
            lblCreated.setText(null);
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
        d.setMultipleMode(true);
        d.setVisible(true);
        File[] listFile = d.getFiles();
        progress.setIndeterminate(true);
        observer.selected("save", false);
        Flux.fromArray(listFile)
                .flatMap(file -> {
                    String directory = file.getPath();
                    log.info(directory);
                    String id = "head";
                    return dmsRepo.createFile(id, Path.of(directory))
                            .doOnSuccess(t -> {
                                fileTableModel.addObject(t.getBody());
                            }).doOnError((e) -> {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        observer.selected("save", true);
                        progress.setIndeterminate(false);
                    }).doOnTerminate(() -> {
                        observer.selected("save", true);
                        progress.setIndeterminate(false);
                    });
                }).collectList().subscribe();
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
            inventoryRepo.save(note).doOnSuccess((t) -> {
                clear(true);
            }).doOnError((e) -> {
                observeMain();
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
            }).doOnTerminate(() -> {
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
        } else {
            if (lblStatus.getText().equals("NEW")) {
                note.setCreatedBy(Global.loginUser.getUserCode());
            } else {
                note.setUpdatedBy(Global.loginUser.getUserCode());
            }
            note.setVouNo(txtVouNo.getText());
            note.setVouDate(Util1.convertToLocalDateTime(txtOrderDate.getDate()));
            note.setCompCode(Global.compCode);
            note.setDeptId(Global.deptId);
            note.setMacId(Global.macId);
            note.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            note.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
            note.setOrderCode(txtOrderCode.getText());
            note.setOrderName(txtOrderName.getText());
            note.setDeleted(false);
            note.setDetailList(getOrderFileJoin());
        }
        return true;
    }

    private List<OrderFileJoin> getOrderFileJoin() {
        List<OrderFileJoin> listJoin = new ArrayList<>();
        List<CVFile> listFile = fileTableModel.getListDetail();
        listFile.forEach((t) -> {
            var join = OrderFileJoin.builder()
                    .vouNo(note.getVouNo())
                    .compCode(note.getCompCode())
                    .fileId(t.getFileId())
                    .build();
            listJoin.add(join);
        });
        return listJoin;
    }

    private void deleteOrderNote() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Save Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    note.setDeleted(true);
                    inventoryRepo.delete(note).doOnSuccess((t) -> {
                        clear(true);
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "OrderNote Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    note.setDeleted(false);
                    inventoryRepo.restore(note).doOnSuccess((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        disableForm(true);
                    }).subscribe();

                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
        }
    }

    private void clear(boolean foucs) {
        disableForm(true);
        assignDefaultValue();
        setFileDetail(null);
        fileTableModel.clear();
        note = OrderNote.builder().build();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtOrderCode.setText(null);
        txtOrderName.setText(null);
        txtVouNo.setText(null);
        txtOrderDate.setDate(Util1.getTodayDate());
        traderAutoCompleter.setTrader(null);
        stockAutoCompleter.setStock(null);
        if (foucs) {
            txtCus.requestFocus();
        }
        getStorageInfo();
    }

    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);
    }

    public void historySale() {
        if (dialog == null) {
            dialog = new OrderNoteHistoryDialog(Global.parentForm, 1);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
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
        jLabel8 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lblPath = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        scroll = new javax.swing.JScrollPane();
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
        jButton3 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        proStorage = new javax.swing.JProgressBar();
        lblStorage = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

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

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setName("txtCus"); // NOI18N
        txtVouNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouNoFocusGained(evt);
            }
        });
        txtVouNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtVouNoMouseExited(evt);
            }
        });
        txtVouNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouNoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtOrderDate, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCus, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtStock, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtOrderCode, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtOrderName, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addContainerGap())
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblPath.setText("-");

        jButton1.setBackground(Global.selectionColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
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
                    .addComponent(jButton1))
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
        scroll.setViewportView(tblChild);

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
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Download");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(Global.selectionColor);
        jButton3.setFont(Global.lableFont);
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("View");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
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
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Storage");

        lblStorage.setFont(Global.lableFont);
        lblStorage.setText("0 of 0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(proStorage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStorage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(proStorage, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStorage)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblStatus.setFont(Global.menuFont);
        lblStatus.setText("NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scroll)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)))
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

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoFocusGained

    private void txtVouNoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtVouNoMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoMouseExited

    private void txtVouNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        viewFile();
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCreated;
    private javax.swing.JLabel lblFileIcon;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblFileSize;
    private javax.swing.JLabel lblFileType;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblPath;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStorage;
    private javax.swing.JLabel lblUpdate;
    private javax.swing.JProgressBar proStorage;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblChild;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtOrderCode;
    private com.toedter.calendar.JDateChooser txtOrderDate;
    private javax.swing.JTextField txtOrderName;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "ORDER-NOTE-HISTORY" -> {
                if (selectObj instanceof OrderNote s) {
                    setSaleVoucher(s);
                }
            }
        }
    }

    public void setSaleVoucher(OrderNote sh) {
        if (sh != null) {
            progress.setIndeterminate(true);
            note = sh;
            setHeader(sh);
            setDetail(sh);
        }
    }

    private void setListDetail(List<CVFile> list) {
        switch (type) {
            case ORDERNOTE -> {
                fileTableModel.setListDetail(list);
            }
        }
    }

    private void setDetail(OrderNote sh) {
        String vouNo = sh.getVouNo();
        inventoryRepo.getOrderNoteDetail(vouNo)
                .flatMap((t) -> convertCVFile(t))
                .collectList()
                .doOnSuccess((t) -> {
                    setListDetail(t);
                }).doOnError(e -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }).doOnTerminate(() -> {
            focusTable();
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private Mono<CVFile> convertCVFile(OrderFileJoin od) {
        String fileId = od.getFileId();
        return dmsRepo.findFile(fileId);
    }

    private void focusTable() {
        int rc = tblChild.getRowCount();
        if (rc >= 1) {
            tblChild.setRowSelectionInterval(rc - 1, rc - 1);
            tblChild.setColumnSelectionInterval(0, 0);
            tblChild.requestFocus();
        } else {
            txtCus.requestFocus();
        }
    }

    private void setHeader(OrderNote on) {
        if (on.getDeleted()) {
            lblStatus.setText("DELETED");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
            observer.selected("delete", true);
        } else if (DateLockUtil.isLockDate(note.getVouDate())) {
            lblStatus.setText(DateLockUtil.MESSAGE);
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        } else {
            lblStatus.setText("EDIT");
            lblStatus.setForeground(Color.blue);
            disableForm(true);
        }
        txtOrderDate.setDate(Util1.convertToDate(on.getVouDate()));
        txtVouNo.setText(on.getVouNo());
        txtOrderCode.setText(on.getOrderCode());
        txtOrderName.setText(on.getOrderName());
        inventoryRepo.findTrader(on.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        inventoryRepo.findStock(on.getStockCode()).doOnSuccess((t) -> {
            stockAutoCompleter.setStock(t);
        }).subscribe();
    }

    @Override
    public void save() {
        saveOrder();
    }

    @Override
    public void delete() {
        deleteOrderNote();
    }

    @Override
    public void newForm() {
        clear(true);
    }

    @Override
    public void history() {
        historySale();
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
