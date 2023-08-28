/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.user.dialog;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.user.common.VRoleCompanyTableModel;
import com.user.model.VRoleCompany;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author DELL
 */
public class CompanyOptionDialog extends javax.swing.JDialog {

    private final VRoleCompanyTableModel companyTableModel = new VRoleCompanyTableModel();
    private SelectionObserver observer;
    private VRoleCompany companyInfo;
    private List<VRoleCompany> listCompany;
    private static final Color SELECTED_ROW_COLOR = new Color(173, 216, 249); // Light blue color, you can change it
    private JPanel selectedPanel = null;

    public List<VRoleCompany> getListCompany() {
        return listCompany;
    }

    public void setListCompany(List<VRoleCompany> listCompany) {
        this.listCompany = listCompany;
    }

    public JButton getjButton1() {
        return jButton1;
    }

    public void setjButton1(JButton jButton1) {
        this.jButton1 = jButton1;
    }

    public VRoleCompany getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(VRoleCompany companyInfo) {
        this.companyInfo = companyInfo;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form RoleSetupDialog
     *
     * @param parent
     */
    public CompanyOptionDialog(JFrame parent) {
        super(parent, true);
        initComponents();
        actionMapping();

    }

    private void actionMapping() {
        String solve = "enter";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        listPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        listPanel.getActionMap().put(solve, new EnterAction());

    }

    private class EnterAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            select();
        }
    }

    public void initMain() {
//        tblCompany.setModel(companyTableModel);
//        tblCompany.getTableHeader().setFont(Global.tblHeaderFont);
//        tblCompany.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        tblCompany.setRowHeight(Global.tblRowHeight);
//        tblCompany.setDefaultRenderer(Object.class, new TableCellRender());
//        tblCompany.getColumnModel().getColumn(0).setPreferredWidth(1);
//        tblCompany.getColumnModel().getColumn(1).setPreferredWidth(20);
//        tblCompany.getColumnModel().getColumn(2).setPreferredWidth(50);
        companyTableModel.setListCompany(listCompany);
        int index = 0;  // Initialize the index
        for (VRoleCompany companyInfo : listCompany) {
            companyInfoPanel(companyInfo, index);
            index++;
        }
    }

    private void select() {
//        int row = tblCompany.convertRowIndexToModel(tblCompany.getSelectedRow());
        int row = companyTableModel.getSelectedIndex();
        if (row >= 0) {
            companyInfo = companyTableModel.getCompany(row);
            this.dispose();
        }
    }

    private void companyInfoPanel(VRoleCompany info, int index) {
        // Create a panel for drawing the icon
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage image = null;
                try {
                    image = ImageIO.read(getClass().getResource("/images/applogo.jpg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Draw the image
                if (image != null) {
                    int iconSize = 30;
                    int x = (getWidth() - iconSize) / 2;
                    int y = (getHeight() - iconSize) / 2;
                    g.drawImage(image, x, y, iconSize, iconSize, this);
                }
            }
        };
        iconPanel.setPreferredSize(new Dimension(40, 40)); // Set a fixed size for the icon panel

        // Create labels
        JLabel label1 = new JLabel(info.getCompName());
        JLabel label2 = new JLabel(Util1.toDateStr(info.getStartDate(), "dd/MM/yyyy")
                + " to "
                + Util1.toDateStr(info.getEndDate(), "dd/MM/yyyy"));

        // Create a container for labels
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.add(label1);
        Font labelFont = label1.getFont(); // Get the default font
        label1.setFont(labelFont.deriveFont(Font.BOLD, 15));
        labelPanel.add(label2);

        // Create a container for the whole company info panel
        JPanel companyInfoPanel = new JPanel(new BorderLayout());
        companyInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding
        companyInfoPanel.setBackground(Color.white); // Set background color
        companyInfoPanel.add(iconPanel, BorderLayout.WEST);
        companyInfoPanel.add(labelPanel, BorderLayout.CENTER);

        // Set layout for the main frame
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.add(companyInfoPanel);

        companyInfoPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Handle the click event here
                if (selectedPanel != null) {
                    selectedPanel.setBackground(Color.white);
                }

                // Set the background color of the clicked panel
                companyInfoPanel.setBackground(SELECTED_ROW_COLOR);
                selectedPanel = companyInfoPanel;

                if (index >= 0) {
                    companyTableModel.setSelectedIndex(index);
                }

                if (e.getClickCount() > 1) {
                    select();
                }
                System.out.println("Selected index: " + index);
            }

        });
        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        listPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Choose Company Dialog");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.menuFont);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Company List");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setBackground(Global.selectionColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Select");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        listPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout listPanelLayout = new javax.swing.GroupLayout(listPanel);
        listPanel.setLayout(listPanelLayout);
        listPanelLayout.setHorizontalGroup(
            listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        listPanelLayout.setVerticalGroup(
            listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 361, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(listPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(listPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        select();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel listPanel;
    // End of variables declaration//GEN-END:variables
}
