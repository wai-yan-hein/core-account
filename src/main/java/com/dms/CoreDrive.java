/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.dms;

import com.acc.dialog.FindDialog;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.Util1;
import com.dms.commom.CVFileTableModel;
import com.dms.commom.CustomRenderer;
import com.dms.commom.FileDropHandler;
import com.dms.commom.IconRenderer;
import com.dms.dialog.FileRenameDialog;
import com.dms.model.CVFile;
import com.dms.model.FileObject;
import com.repo.DMSRepo;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CoreDrive extends javax.swing.JPanel implements SelectionObserver, PanelControl {
    
    private DefaultMutableTreeNode treeRoot;
    private DefaultTreeModel treeModel;
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
    private FindDialog findDialog;
    
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
    public CoreDrive() {
        initComponents();
    }
    
    public void initMain() {
        initNewPopup();
        initMorePopup();
        initTrashPopup();
        initTree();
        initTable();
        getHeadFolder();
        getStorageInfo();
    }
    
    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblChild);
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
        treeModel = (DefaultTreeModel) tree.getModel();
        tree.setRowHeight(Global.tblRowHeight - 2);
        tree.setFont(Global.textFont);
        tree.setCellRenderer(new CustomRenderer());
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
        treeModel.reload();
        progress.setIndeterminate(true);
        dmsRepo.getFolder("head").doOnSuccess((t) -> {
            addChildMenu(treeRoot, t);
        }).doOnTerminate(() -> {
            treeModel.setRoot(treeRoot);
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
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selRow != -1 && e.getClickCount() == 1) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof CVFile file) {
                log.info("Clicked on: " + file.getFileName());
                progress.setIndeterminate(true);
                setFileDetail(file);
                getFile(file.getFileId());
            }
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
        if (lastFile != null) {
            FileDialog d = new FileDialog(Global.parentForm, "Choose CSV File", FileDialog.LOAD);
            d.setVisible(true);
            String directory = d.getDirectory() + d.getFile();
            log.info(directory);
            String id = lastFile.getFileId();
            if (!Util1.isNullOrEmpty(id)) {
                progress.setIndeterminate(true);
                dmsRepo.createFile(id, Path.of(directory)).doOnSuccess((t) -> {
                    fileTableModel.addObjectFirst(t.getBody());
                }).doOnTerminate(() -> {
                    progress.setIndeterminate(false);
                }).subscribe();
            }
        }
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
        newPopup.show(btnNew, e.getX(), e.getY());
    }
    
    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
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
        tree = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lblPath = new javax.swing.JLabel();
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
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        proStorage = new javax.swing.JProgressBar();
        lblStorage = new javax.swing.JLabel();
        btnNew = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tree.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        tree.setRowHeight(Global.tblRowHeight);
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tree);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Core Drive");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setText("Search");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblPath.setText("-");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPath, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Storage");

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
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStorage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(proStorage, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStorage)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnNew.setBackground(Global.selectionColor);
        btnNew.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnNew.setForeground(new java.awt.Color(255, 255, 255));
        btnNew.setText("NEW");
        btnNew.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNewMouseClicked(evt);
            }
        });

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Trash");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(btnNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMouseClicked
        // TODO add your handling code here:
        searchChild(evt);
    }//GEN-LAST:event_treeMouseClicked

    private void tblChildMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblChildMouseClicked
        // TODO add your handling code here:
        setFileInfo(evt);
    }//GEN-LAST:event_tblChildMouseClicked

    private void btnNewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNewMouseClicked
        // TODO add your handling code here:
        showPopup(evt);
    }//GEN-LAST:event_btnNewMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        getTrash();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNew;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblCreated;
    private javax.swing.JLabel lblFileIcon;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblFileSize;
    private javax.swing.JLabel lblFileType;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblPath;
    private javax.swing.JLabel lblStorage;
    private javax.swing.JLabel lblUpdate;
    private javax.swing.JProgressBar proStorage;
    private javax.swing.JTable tblChild;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
    }
    
    @Override
    public void save() {
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
        findDialog.setVisible(!findDialog.isVisible());
    }
    
    @Override
    public String panelName() {
        return this.getName();
    }
}
