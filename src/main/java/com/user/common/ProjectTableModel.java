/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.user.model.Project;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ProjectTableModel extends AbstractTableModel {

    private List<Project> listProject = new ArrayList();
    private final String[] columnNames = {"Project No", "Project Name", "Status"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            Project p = listProject.get(row);
            switch (column) {
                case 0 -> {
                    return p.getKey().getProjectCode();
                }
                case 1 -> {
                    return p.getProjectName();
                }
                case 2 -> {
                    return p.getProjectStatus();
                }
            }
        } catch (Exception e) {
            log.error(String.format("getValueAt : %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        return listProject.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void add(Project info) {
        listProject.add(info);
        fireTableRowsInserted(listProject.size() - 1, listProject.size() - 1);
    }

    public void set(int row, Project user) {
        if (!listProject.isEmpty()) {
            listProject.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }

    public List<Project> getListProject() {
        return listProject;
    }

    public void setListProject(List<Project> listProject) {
        this.listProject = listProject;
        fireTableDataChanged();
    }

    public Project get(int row) {
        return listProject.get(row);
    }

    public void addNewRow() {
        listProject.add(new Project());
        fireTableRowsInserted(listProject.size() - 1, listProject.size() - 1);
    }

    public void delete(int row) {
        listProject.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
