/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.common;

import com.inventory.model.Currency;
import com.inventory.model.Location;
import com.inventory.model.SaleMan;
import com.inventory.model.Trader;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class SystemSetting {

    private Currency defaultCurrency;
    private Location defaultLocation;
    private SaleMan defaultSaleMan;
    private Trader defaultCustomer;
    private Trader defaultSupplier;
    private String cashDown;
    private String curKey = "D-CUR";
    private String locKey = "D-LOC";
    private String saleManKey = "D-SM";
    private String cusKey = "D-CUS";
    private String supKey = "D-SUP";
    private String cashDownKey = "IS-CD";
    private String roleCode;
}
