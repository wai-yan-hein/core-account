package com.dms.commom;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRenderer extends DefaultTreeCellRenderer {

    private final Icon leafIcon = UIManager.getIcon("Tree.closedIcon");
    private final Icon openIcon = UIManager.getIcon("Tree.openIcon");
    private final Icon closedIcon = UIManager.getIcon("Tree.closedIcon");

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (leaf) {
            setIcon(leafIcon);
        } else if (expanded) {
            setIcon(openIcon);
        } else {
            setIcon(closedIcon);
        }

        return this;
    }

}
