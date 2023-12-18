/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dms.commom;

import com.common.Global;
import com.common.Util1;
import com.dms.model.CVFile;
import com.repo.DMSRepo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class CVFileTableModel extends AbstractTableModel {

    private List<CVFile> listDetail = new ArrayList();
    private final String[] columnNames = {"Icon", "Name", "Owner", "Last modified", "File Size"};
    private Map<String, ImageIcon> hmIcon = new HashMap<>();
    private DMSRepo dmsRepo;
    private JTable table;

    public void setTable(JTable table) {
        this.table = table;
    }

    public void setDmsRepo(DMSRepo dmsRepo) {
        this.dmsRepo = dmsRepo;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return Object.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                CVFile his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //date
                        if (his.isFile()) {
                            return getFileIcon(his.getFileExtension());
                        }
                        return getFileIcon("folder");
                    }
                    case 1 -> {
                        //date
                        return his.getFileDescription();
                    }
                    case 2 -> {
                        return Global.hmUser.get(his.getCreatedBy());
                    }
                    case 3 -> {
                        return Util1.toDateStr(his.getUpdatedDate(), "dd/MM/yyyy hh:mm:ss a");
                    }
                    case 4 -> {
                        return Util1.bytesToSize(his.getFileSize());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<CVFile> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<CVFile> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public CVFile getFile(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void setFile(CVFile file) {
        int row = table.getSelectedRow();
        listDetail.set(row, file);
        fireTableRowsUpdated(row, row);
        
    }

    public void deleteFile(CVFile file) {
        int row = table.getSelectedRow();
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void addObjectFirst(CVFile t) {
        listDetail.add(0, t);
        fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
    }

    public int getSize() {
        return listDetail.size();
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
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
                ImageIcon resizedIcon = new ImageIcon(originalIcon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH));
                hmIcon.put(extension, resizedIcon);
                return resizedIcon;
            } else {
                return null; // Handle the case where the icon is not available
            }
        }
    }
}
