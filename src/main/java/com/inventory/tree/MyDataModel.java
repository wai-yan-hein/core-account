/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.tree;

import com.common.SelectionObserver;
import com.inventory.entity.MessageType;
import com.user.model.PrivilegeMenu;
import com.user.model.PMKey;
import com.inventory.entity.VRoleMenu;
import com.repo.UserRepo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class MyDataModel extends MyAbstractTreeTableModel {

    // Spalten Name.
    static protected String[] columnNames = {"Name", "Type", "Allow"};
    private final UserRepo userRepo;
    private final SelectionObserver observer;
    // Spalten Typen.
    static protected Class<?>[] columnTypes = {MyTreeTableModel.class, String.class, Boolean.class};

    public MyDataModel(VRoleMenu rootNode, UserRepo userRepo, SelectionObserver observer) {
        super(rootNode);
        root = rootNode;
        this.userRepo = userRepo;
        this.observer = observer;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((VRoleMenu) parent).getChild().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        List<VRoleMenu> child = ((VRoleMenu) parent).getChild();
        return (child == null) ? 0 : child.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return columnTypes[column];
    }

    @Override
    public Object getValueAt(Object node, int column) {
        switch (column) {
            case 0 -> {
                return ((VRoleMenu) node).getMenuName();
            }
            case 1 -> {
                return ((VRoleMenu) node).getMenuType();
            }
            case 2 -> {
                return ((VRoleMenu) node).isAllow();
            }
            default -> {
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return column != 1; // Important to activate TreeExpandListener
    }

    @Override
    public void setValueAt(Object aValue, Object node, int column) {
        switch (column) {
            case 2 -> {
                if (aValue != null) {
                    if (node instanceof VRoleMenu roleMenu) {
                        roleMenu.setAllow((Boolean) aValue);
                        savePrivilege(roleMenu, (Boolean) aValue);

                    }
                }
            }

        }
    }

    private void setAllow(List<VRoleMenu> parent, boolean allow) {
        parent.forEach(child -> {
            if (child.getChild() != null) {
                child.setAllow(allow);
                savePrivilege(child, allow);
                setAllow(child.getChild(), allow);
            } else {
                child.setAllow(allow);
            }
        });
    }

    private void savePrivilege(VRoleMenu roleMenu, boolean allow) {
        if (roleMenu.getMenuCode() != null) {
            PMKey key = new PMKey();
            key.setMenuCode(roleMenu.getMenuCode());
            key.setRoleCode(roleMenu.getRoleCode());
            key.setCompCode(roleMenu.getCompCode());
            PrivilegeMenu privilege = new PrivilegeMenu();
            privilege.setKey(key);
            privilege.setAllow(allow);
            userRepo.savePrivilegeMenu(privilege).doOnSuccess((t) -> {
                if (observer != null) {
                    observer.selected("Menu-Change", "-");
                }
                sendMessage("Menu Update.");
            }).subscribe();
        }

    }
    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.PRIVILEGE_MENU, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

}
