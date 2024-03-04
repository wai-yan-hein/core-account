package com.common;

import com.inventory.entity.MillingOutDetail;
import com.inventory.ui.common.MillingOutTableModel;
import javax.swing.*;
import java.awt.datatransfer.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TableRowTransferHandler extends TransferHandler {

    private final DataFlavor localObjectFlavor;
    private int[] selectedRows;

    public TableRowTransferHandler() {
        localObjectFlavor = new DataFlavor(int[].class, "Array of items");
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTable table = (JTable) c;
        selectedRows = table.getSelectedRows();
        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{localObjectFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.equals(localObjectFlavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return selectedRows;
            }
        };
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        return info.isDataFlavorSupported(localObjectFlavor);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        if (!canImport(info)) {
            return false;
        }

        JTable target = (JTable) info.getComponent();
        JTable.DropLocation dropLocation = (JTable.DropLocation) info.getDropLocation();

        int index = dropLocation.getRow();

        int max = target.getModel().getRowCount();
        if (index < 0 || index > max) {
            index = max;
        }

        addData(info, index);

        return true;
    }

    protected void addData(TransferSupport info, int index) {
        JTable table = (JTable) info.getComponent();
        Transferable transferable = info.getTransferable();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        if (flavors.length > 0 && flavors[0].equals(localObjectFlavor)) {
            try {
                selectedRows = (int[]) transferable.getTransferData(localObjectFlavor);
            } catch (UnsupportedFlavorException | java.io.IOException e) {
                log.error("addData : " + e.getMessage());
                return;
            }
        }

        if (selectedRows != null && selectedRows.length > 0) {
            // Call the swapRows function to swap rows
            if (index < table.getRowCount() - 1) {
                for (int i = 0; i < selectedRows.length; i++) {
                    int selectedRow = selectedRows[i];
                    int dropRow = index + i;
                    swapRows(table, selectedRow, dropRow);
                }
            }

            // Repaint the table
            table.repaint();
        }
    }

// Function to swap rows in a table
    private void swapRows(JTable table, int selectedRow, int dropRow) {
        if (selectedRow < 0 || selectedRow >= table.getRowCount() || dropRow < 0 || dropRow >= table.getRowCount()) {
            return; // Check if indices are valid
        }

        if (table.getModel() instanceof MillingOutTableModel tableModel) {
            MillingOutDetail select = tableModel.getObject(selectedRow);
            MillingOutDetail drop = tableModel.getObject(dropRow);
            tableModel.setObject(selectedRow, drop);
            tableModel.setObject(dropRow, select);
            log.info("swap completed.");
        }
    }

}
