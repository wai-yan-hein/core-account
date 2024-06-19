/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.Global;
import com.inventory.entity.StockUnit;
import com.inventory.ui.setup.dialog.StockUnitChooserDialog;
import com.repo.InventoryRepo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Lenovo
 */
public class UnitChooser {

    private InventoryRepo inventoryRepo;
    private String relCode;
    private StockUnitChooserDialog dialog;
    @Getter
    @Setter
    private String selectUnit;

    public UnitChooser(InventoryRepo inventoryRepo, String relCode) {
        this.inventoryRepo = inventoryRepo;
        this.relCode = relCode;
        initData();
    }

    private void initData() {
        List<StockUnit> list = inventoryRepo.getUnitByRelation(relCode).block();
        if (list.size() > 1) {
            if (dialog == null) {
                dialog = new StockUnitChooserDialog(Global.parentForm);
                dialog.setLocationRelativeTo(null);
            }
            dialog.setListUnit(list);
            setSelectUnit(dialog.getSelectUnit());
        } else {
            setSelectUnit(list.getFirst().getKey().getUnitCode());
        }
    }

}
