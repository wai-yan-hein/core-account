/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.inventory.model.VStockBalance;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class StockBalanceTableModel extends AbstractTableModel {

    static Logger log = LoggerFactory.getLogger(StockBalanceTableModel.class.getName());
    private List<VStockBalance> listStockBalance = new ArrayList();
    private final String[] columnNames = {"Locaiton", "Qty",};
    @Autowired
    private WebClient inventoryApi;
    private JProgressBar progress;

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

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
        if (listStockBalance != null) {
            try {
                VStockBalance stock = listStockBalance.get(row);
                switch (column) {
                    case 0 -> {
                        //Location
                        if (stock.getLocationName() == null) {
                            return "No Stock";
                        } else {
                            return stock.getLocationName();
                        }
                    }
                    case 1 -> {
                        //Unit
                        if (stock.getUnitName() == null) {
                            return "No Unit";
                        } else {
                            return stock.getUnitName();
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            }
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    public List<VStockBalance> getListStockBalance() {
        return listStockBalance;
    }

    public void setListStockBalance(List<VStockBalance> listStockBalance) {
        this.listStockBalance = listStockBalance;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return listStockBalance.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void clearList() {
        if (listStockBalance != null) {
            this.listStockBalance.clear();
            fireTableDataChanged();
        }
    }

    public void calStockBalance(String stockCode) {
        if (ProUtil.isCalStock()) {
            progress.setIndeterminate(true);
            Mono<ResponseEntity<List<VStockBalance>>> result = inventoryApi.get()
                    .uri(builder -> builder.path("/report/get-stock-balance")
                    .queryParam("stockCode", stockCode)
                    .queryParam("compCode", Global.compCode)
                    .queryParam("macId", Global.macId)
                    .build())
                    .retrieve().toEntityList(VStockBalance.class);
            result.subscribe((t) -> {
                setListStockBalance(t.getBody());
                progress.setIndeterminate(false);
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            });
        }
    }
}
