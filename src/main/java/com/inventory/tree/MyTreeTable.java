/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.tree;

import com.common.Global;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Lenovo
 */
public class MyTreeTable extends JTable {

    private MyTreeTableCellRenderer tree;

    public MyTreeTable(MyAbstractTreeTableModel treeTableModel) {
        super();
        getTableHeader().setFont(Global.tblHeaderFont);
        // JTree erstellen.
        tree = new MyTreeTableCellRenderer(this, treeTableModel);
        tree.setFont(Global.textFont);
        tree.setRowHeight(Global.tblRowHeight);
        // Modell setzen.
        super.setModel(new MyTreeTableModelAdapter(treeTableModel, tree));

        // Gleichzeitiges Selektieren fuer Tree und Table.
        MyTreeTableSelectionModel model = new MyTreeTableSelectionModel();
        tree.setSelectionModel(model); //For the tree
        setSelectionModel(model.getListSelectionModel()); //For the table
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderer fuer den Tree.
        setDefaultRenderer(MyTreeTableModel.class, tree);
        // Editor fuer die TreeTable
        setDefaultEditor(MyTreeTableModel.class, new MyTreeTableCellEditor(tree, this));

        // Kein Grid anzeigen.
        setShowGrid(false);

        // Keine Abstaende.
        setIntercellSpacing(new Dimension(0, 0));

    }
}
