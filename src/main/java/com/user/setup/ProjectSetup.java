/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.user.setup;

import com.common.DateLockUtil;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.MessageType;
import com.toedter.calendar.JTextFieldDateEditor;
import com.user.common.ProjectTableModel;
import com.repo.UserRepo;
import com.user.model.Project;
import com.user.model.ProjectKey;
import com.user.model.ProjectStatus;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ProjectSetup extends javax.swing.JPanel implements SelectionObserver, PanelControl, KeyListener {

    private UserRepo userRepo;
    private SelectionObserver observer;
    private JProgressBar progress;
    private final ProjectTableModel projectTableModel = new ProjectTableModel();
    private Project p = new Project();

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Creates new form ProjectSetup
     */
    public ProjectSetup() {
        initComponents();
        initFoucsAdapter();
        initDate();
        initFormat();
        initKeyListener();
    }

    public void initMain() {
        initTable();
        searchProject();
    }

    private void initFormat() {
        txtBudget.setFormatterFactory(Util1.getDecimalFormat());
        txtBudget.setHorizontalAlignment(JTextField.RIGHT);
    }

    private void initKeyListener() {
        txtProjectNo.addKeyListener(this);
        txtProjectName.addKeyListener(this);
        txtBudget.addKeyListener(this);
        txtStartDate.getDateEditor().getUiComponent().setName("txtStartDate");
        txtStartDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtEndDate.getDateEditor().getUiComponent().setName("txtEndDate");
        txtEndDate.getDateEditor().getUiComponent().addKeyListener(this);

    }

    private void initTable() {
        tblProject.setModel(projectTableModel);
        tblProject.getTableHeader().setFont(Global.textFont);
        tblProject.setFont(Global.textFont);
        tblProject.setRowHeight(Global.tblRowHeight);
        tblProject.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProject.setDefaultRenderer(Object.class, new TableCellRender());
        tblProject.getSelectionModel().addListSelectionListener((e) -> {
            int row = tblProject.convertRowIndexToModel(tblProject.getSelectedRow());
            setProject(projectTableModel.get(row));
        });
    }

    private void initDate() {
        txtStartDate.setDateFormatString(Global.dateFormat);
        txtEndDate.setDateFormatString(Global.dateFormat);
        txtStartDate.setDate(Util1.getTodayDate());
        txtEndDate.setDate(Util1.getTodayDate());
    }

    private void initFoucsAdapter() {
        txtProjectNo.addFocusListener(fa);
        txtProjectName.addFocusListener(fa);
        txtBudget.addFocusListener(fa);
        txtStartDate.addFocusListener(fa);
        txtEndDate.addFocusListener(fa);
        cboStatus.addFocusListener(fa);
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            Object s = e.getSource();
            if (s instanceof JTextField txt) {
                txt.selectAll();
            } else if (s instanceof JFormattedTextField txt) {
                txt.selectAll();
            } else if (s instanceof JTextFieldDateEditor txt) {
                txt.selectAll();
            }
        }
    };

    private void setProject(Project p) {
        this.p = p;
        txtProjectNo.setText(p.getKey().getProjectNo());
        txtProjectName.setText(p.getProjectName());
        txtBudget.setValue(p.getBudget());
        txtStartDate.setDate(p.getStartDate());
        txtEndDate.setDate(p.getEndDate());
        cboStatus.setSelectedIndex(ProjectStatus.valueOf(p.getProjectStatus()).ordinal());
        if (DateLockUtil.isLockDate(p.getStartDate()) || DateLockUtil.isLockDate(p.getStartDate())) {
            lblStatus.setText(DateLockUtil.MESSAGE);
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        }
        lblStatus.setText("EDIT");
    }

    private void disableForm(boolean status) {
        txtProjectNo.setEnabled(status);
        txtProjectName.setEnabled(status);
        txtBudget.setEnabled(status);
        txtStartDate.setEnabled(status);
        txtEndDate.setEnabled(status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);

    }

    private void saveProject() {
        if (isValidEntry()) {
            if (DateLockUtil.isLockDate(txtStartDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtStartDate.requestFocus();
                return;
            } else if (DateLockUtil.isLockDate(txtEndDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtEndDate.requestFocus();
                return;
            }
            progress.setIndeterminate(true);
            userRepo.save(p).doOnSuccess((t) -> {
                if (lblStatus.getText().equals("NEW")) {
                    projectTableModel.add(t);
                } else {
                    projectTableModel.set(tblProject.getSelectedRow(), t);
                }
                clear();
                sendMessage(t.getProjectName());
            }).subscribe();
        }
    }

    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.PROJECT, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void clear() {
        txtProjectNo.setText(null);
        txtProjectName.setText(null);
        txtBudget.setValue(null);
        lblStatus.setText("NEW");
        p = new Project();
        progress.setIndeterminate(false);
        txtProjectNo.requestFocus();
    }

    private boolean isValidEntry() {
        String projectNo = txtProjectNo.getText();
        if (projectNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid Project No.");
            return false;
        } else if (txtProjectName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid Project Name.");
            return false;
        } else if (!Util1.isDateBetween(txtStartDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Start Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtStartDate.requestFocus();
            return false;
        } else if (!Util1.isDateBetween(txtEndDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid End Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtEndDate.requestFocus();
            return false;
        } else {
            ProjectKey key = new ProjectKey();
            key.setCompCode(Global.compCode);
            key.setProjectNo(txtProjectNo.getText());
            if (lblStatus.getText().equals("NEW")) {
                if (userRepo.find(key).block() != null) {
                    JOptionPane.showMessageDialog(this, "Duplicate Project No.");
                    return false;
                }
            }
            p.setKey(key);
            ProjectStatus s = (ProjectStatus) cboStatus.getSelectedItem();
            p.setProjectStatus(s.name());
            p.setProjectName(txtProjectName.getText());
            p.setStartDate(txtStartDate.getDate());
            p.setEndDate(txtEndDate.getDate());
            p.setBudget(Util1.getDouble(txtBudget.getValue()));
        }
        return true;
    }

    private void searchProject() {
        progress.setIndeterminate(true);
        userRepo.searchProject().subscribe((t) -> {
            projectTableModel.setListProject(t);
            progress.setIndeterminate(false);
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblProject = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtProjectName = new javax.swing.JTextField();
        txtStartDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        txtBudget = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        cboStatus = new javax.swing.JComboBox<>(ProjectStatus.values());
        lblStatus = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblProject.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblProject);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Project No");

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setName("txtProjectNo"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Project Name");

        txtProjectName.setFont(Global.textFont);
        txtProjectName.setName("txtProjectName"); // NOI18N

        txtStartDate.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Start Date");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("End Date");

        txtEndDate.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Budget");

        txtBudget.setFont(Global.textFont);
        txtBudget.setName("txtBudget"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Status");

        cboStatus.setFont(Global.textFont);

        lblStatus.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProjectName, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                            .addComponent(txtProjectNo)
                            .addComponent(txtStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtBudget))
                        .addContainerGap())
                    .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtProjectNo)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtProjectName)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBudget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(200, 200, 200))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel1, jScrollPane1});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<ProjectStatus> cboStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblProject;
    private javax.swing.JFormattedTextField txtBudget;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private javax.swing.JTextField txtProjectName;
    private javax.swing.JTextField txtProjectNo;
    private com.toedter.calendar.JDateChooser txtStartDate;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
    }

    @Override
    public void save() {
        saveProject();
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
        searchProject();
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
        Object sourceObj = e.getSource();
        String ctrlName = "-";
        if (sourceObj instanceof JTextField txt) {
            ctrlName = txt.getName();
        } else if (sourceObj instanceof JTextFieldDateEditor txt) {
            ctrlName = txt.getName();
        } else if (sourceObj instanceof JFormattedTextField txt) {
            ctrlName = txt.getName();
        }
        switch (ctrlName) {
            case "txtProjectNo" -> {
                if (isEnter(e)) {
                    txtProjectName.requestFocus();
                }
            }
            case "txtProjectName" -> {
                if (isEnter(e)) {
                    txtStartDate.getDateEditor().getUiComponent().requestFocus();
                }
            }
            case "txtStartDate" -> {
                if (isEnter(e)) {
                    txtEndDate.getDateEditor().getUiComponent().requestFocus();
                }
            }
        }
    }

    private boolean isEnter(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_ENTER;
    }

}
