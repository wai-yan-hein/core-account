/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.tree;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.DefaultTreeSelectionModel;

/**
 *
 * @author Lenovo
 */
public final class MyTreeTableSelectionModel extends DefaultTreeSelectionModel {

    public MyTreeTableSelectionModel() {
        super();

        getListSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
        });
    }

    ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }
}
