/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.acc.editor.DepartmentAutoCompleter;
import com.acc.model.DepartmentA;
import com.common.ComponentUtil;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.repo.UserRepo;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.MessageType;
import com.repo.AccountRepo;
import com.repo.InventoryRepo;
import com.user.model.AppUser;
import com.user.common.UserTableModel;
import com.user.editor.DepartmentUserAutoCompleter;
import com.user.editor.RoleAutoCompleter;
import com.user.model.DepartmentUser;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class AppUserSetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private int selectRow = -1;
    private AppUser appUser = new AppUser();
    private final UserTableModel userTableModel = new UserTableModel();
    private DepartmentUserAutoCompleter departmentUserAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;

    private UserRepo userRepo;
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private RoleAutoCompleter roleAutoCompleter;
    private SelectionObserver observer;
    private JProgressBar progress;

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
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

    /**
     * Creates new form UserSetup
     */
    public AppUserSetup() {
        initComponents();
    }

    private void initCombo() {
        departmentUserAutoCompleter = new DepartmentUserAutoCompleter(txtDep, null, false);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDepAcc, null, false, false);
        locationAutoCompleter = new LocationAutoCompleter(txtLoc, null, false, false);
        userRepo.getAppRole().subscribe((t) -> {
            roleAutoCompleter = new RoleAutoCompleter(txtRole, t, null, false);
            roleAutoCompleter.setAppRole(null);
        });
        userRepo.getDeparment(true).doOnSuccess((t) -> {
            departmentUserAutoCompleter.setListDepartment(t);
        }).subscribe();
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).subscribe();

    }

    private void initTable() {
        tblUser.setDefaultRenderer(Object.class, new TableCellRender());
        tblUser.setModel(userTableModel);
        tblUser.getTableHeader().setFont(Global.textFont);
        tblUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUser.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblUser.getSelectedRow() >= 0) {
                    selectRow = tblUser.convertRowIndexToModel(tblUser.getSelectedRow());
                    appUser = userTableModel.getUser(selectRow);
                    setUser(appUser);
                }

            }
        });
    }

    public void initMain() {
        ComponentUtil.addFocusListener(panelEntry);
        initCombo();
        initTable();
        searchUser();
    }

    private void searchUser() {
        userRepo.getAppUser().subscribe((t) -> {
            userTableModel.setListUser(t);
        });
    }

    private void setUser(AppUser user) {
        appUser = user;
        txtUserCode.setText(appUser.getUserCode());
        txtUserName.setText(appUser.getUserLongName());
        txtUserShort.setText(appUser.getUserShortName());
        txtEmail.setText(appUser.getEmail());
        txtPassword.setText(appUser.getPassword());
        chkAtive.setSelected(Util1.getBoolean(appUser.isActive()));
        userRepo.finRole(appUser.getRoleCode()).doOnSuccess((t) -> {
            roleAutoCompleter.setAppRole(t);
        }).subscribe();
        Integer deptId = user.getDeptId();
        userRepo.findDepartment(deptId).doOnSuccess((t) -> {
            departmentUserAutoCompleter.setDepartment(t);
        }).subscribe();
        accountRepo.findDepartment(user.getDeptCode()).doOnSuccess((t) -> {
            departmentAutoCompleter.setDepartment(t);
        }).subscribe();
        String locCode = user.getLocCode();
        inventoryRepo.findLocation(locCode).doOnSuccess((t) -> {
            locationAutoCompleter.setLocation(t);
        }).subscribe();
        lblStatus.setText("EDIT");
    }

    private void saveUser() {
        if (isValidEntry()) {
            userRepo.saveUser(appUser).subscribe((t) -> {
                if (lblStatus.getText().equals("NEW")) {
                    userTableModel.addUser(t);
                } else {
                    userTableModel.setUser(selectRow, t);
                }
                clear();
                sendMessage(t.getUserLongName());
            });

        }
    }

    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.USER, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private boolean isValidEntry() {
        if (txtUserName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "User Name can't empty");
            txtUserName.requestFocus();
            return false;
        } else if (txtUserShort.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "User Name can't empty");
            txtUserShort.requestFocus();
            return false;
        } else if (txtPassword.getPassword().length < 1) {
            JOptionPane.showMessageDialog(this, "Password can't empty");
            txtPassword.requestFocus();
            return false;
        } else if (roleAutoCompleter.getAppRole() == null) {
            JOptionPane.showMessageDialog(this, "Select Role");
            txtRole.requestFocus();
            return false;
        } else {
            DepartmentUser dep = departmentUserAutoCompleter.getDepartment();
            DepartmentA depAcc = departmentAutoCompleter.getDepartment();
            Location location = locationAutoCompleter.getLocation();
            appUser.setDeptId(dep == null ? null : dep.getKey().getDeptId());
            appUser.setDeptCode(depAcc == null ? null : depAcc.getKey().getDeptCode());
            appUser.setLocCode(location == null ? null : location.getKey().getLocCode());
            appUser.setUserCode(txtUserCode.getText());
            appUser.setUserLongName(txtUserName.getText());
            appUser.setUserShortName(txtUserShort.getText());
            appUser.setEmail(txtEmail.getText());
            appUser.setPassword(String.valueOf(txtPassword.getPassword()));
            appUser.setActive(chkAtive.isSelected());
            appUser.setRoleCode(roleAutoCompleter.getAppRole().getRoleCode());
            appUser.setUpdatedDate(LocalDateTime.now());
            appUser.setPhoneNo("");
            appUser.setDoctorId("");
            appUser.setImageLink("");
            return true;
        }
    }

    public void clear() {
        txtEmail.setText(null);
        txtPassword.setText(null);
        txtUserCode.setText(null);
        txtUserShort.setText(null);
        txtUserName.setText(null);
        chkAtive.setSelected(true);
        lblStatus.setText("NEW");
        txtUserName.requestFocus();
        appUser = new AppUser();
        roleAutoCompleter.setAppRole(null);
        departmentAutoCompleter.setDepartment(null);
        departmentUserAutoCompleter.setDepartment(null);
        locationAutoCompleter.setLocation(null);
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
        tblUser = new javax.swing.JTable();
        panelEntry = new javax.swing.JPanel();
        chkAtive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        txtUserShort = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtRole = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        txtDepAcc = new javax.swing.JTextField();
        txtLoc = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblUser.setFont(Global.textFont);
        tblUser.setModel(new javax.swing.table.DefaultTableModel(
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
        tblUser.setName("tblUser"); // NOI18N
        tblUser.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblUser);

        panelEntry.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkAtive.setFont(Global.lableFont);
        chkAtive.setText("Active");
        chkAtive.setName("chkAtive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("User Code");

        txtUserCode.setEditable(false);
        txtUserCode.setFont(Global.textFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("User Name");

        txtUserName.setFont(Global.textFont);

        txtUserShort.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("User Short");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Email");

        txtEmail.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Role");

        txtRole.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Password");

        txtPassword.setFont(Global.lableFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Department");

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Location");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Department Account");

        txtDep.setFont(Global.textFont);

        txtDepAcc.setFont(Global.textFont);

        txtLoc.setFont(Global.textFont);

        javax.swing.GroupLayout panelEntryLayout = new javax.swing.GroupLayout(panelEntry);
        panelEntry.setLayout(panelEntryLayout);
        panelEntryLayout.setHorizontalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtUserName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtUserCode, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtUserShort, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtRole, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAtive, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDepAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        panelEntryLayout.setVerticalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtUserShort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDepAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkAtive)
                    .addComponent(lblStatus))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkAtive;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelEntry;
    private javax.swing.JTable tblUser;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtDepAcc;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtLoc;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtRole;
    private javax.swing.JTextField txtUserCode;
    private javax.swing.JTextField txtUserName;
    private javax.swing.JTextField txtUserShort;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void save() {
        saveUser();
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
        searchUser();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
