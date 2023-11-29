/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dms.commom;

import com.common.Global;
import com.dms.model.CVFile;
import com.repo.DMSRepo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
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
                        return getFileIcon(his.getFileExtension());
                    }
                    case 1 -> {
                        //date
                        return his.getFileDescription();
                    }
                    case 2 -> {
                        return Global.hmUser.get(his.getCreatedBy());
                    }
                    case 3 -> {
                        return null;
                    }
                    case 4 -> {
                        return his.getFileSize();
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

    public CVFile getSelectVou(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(CVFile t) {
        listDetail.add(t);
    }

    public int getSize() {
        return listDetail.size();
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }

    private ImageIcon getFileIcon(String extension) {
        if (extension == null) {
            return (ImageIcon) UIManager.getIcon("Tree.leafIcon");

        }
        if (hmIcon.containsKey(extension)) {
            return hmIcon.get(extension);
        } else {
            ImageIcon icon = dmsRepo.getIcon(extension).block();
            hmIcon.put(extension, icon);
            return icon;
        }
    }
}
