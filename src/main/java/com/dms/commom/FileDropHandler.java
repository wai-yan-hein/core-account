package com.dms.commom;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.TransferHandler;

public class FileDropHandler extends TransferHandler {

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        // Get the transferable data
        Transferable transferable = support.getTransferable();
        try {
            List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

            // Process the dropped files (in this case, just print their paths)
            for (File file : fileList) {
                System.out.println("Dropped file: " + file.getAbsolutePath());
            }

        } catch (UnsupportedFlavorException | IOException e) {
            return false;
        }

        return true;
    }
}
