/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.tree;

import com.common.Global;
import com.common.ReturnObject;
import com.common.SelectionObserver;
import com.inventory.model.PrivilegeMenu;
import com.inventory.model.PMKey;
import com.inventory.model.VRoleMenu;
import java.util.List;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
public class MyDataModel extends MyAbstractTreeTableModel {

    private static final Logger log = LoggerFactory.getLogger(MyDataModel.class);

    // Spalten Name.
    static protected String[] columnNames = {"Name", "Type", "Allow"};
    private final WebClient userApi;
    private final SelectionObserver observer;
    // Spalten Typen.
    static protected Class<?>[] columnTypes = {MyTreeTableModel.class, String.class, Boolean.class};

    public MyDataModel(VRoleMenu rootNode, WebClient userApi, SelectionObserver observer) {
        super(rootNode);
        root = rootNode;
        this.userApi = userApi;
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
                        if (roleMenu.getChild() != null) {
                            setAllow(roleMenu.getChild(), (Boolean) aValue);
                        }
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
        try {
            if (roleMenu.getMenuCode() != null) {
                PMKey key = new PMKey();
                key.setMenuCode(roleMenu.getMenuCode());
                key.setRoleCode(roleMenu.getRoleCode());
                PrivilegeMenu privilege = new PrivilegeMenu();
                privilege.setKey(key);
                privilege.setAllow(allow);
                Mono<ReturnObject> result = userApi.post()
                        .uri("/user/save-privilege-menu")
                        .body(Mono.just(privilege), PrivilegeMenu.class)
                        .retrieve()
                        .bodyToMono(ReturnObject.class);
                result.block();
                if (observer != null) {
                    observer.selected("Menu-Change", "-");
                }

            }
        } catch (Exception e) {
            log.error("Save Priviliges  :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Permission Allow.", JOptionPane.ERROR_MESSAGE);
        }
    }

}
