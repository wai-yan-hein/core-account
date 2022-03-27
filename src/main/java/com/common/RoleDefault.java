/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import com.user.model.Currency;
import com.inventory.model.Location;
import com.inventory.model.SaleMan;
import com.inventory.model.Trader;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class RoleDefault {

    private Currency defaultCurrency;
    private Location defaultLocation;
    private SaleMan defaultSaleMan;
    private Trader defaultCustomer;
    private Trader defaultSupplier;
}
