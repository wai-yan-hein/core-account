/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author WSwe
 */
@Data
public class Currency {

    private String curCode;
    private String currencyName;
    private String currencySymbol;
    private Boolean active;
    private String createdBy;
    private Date createdDt;
    private String updatedBy;
    private Date updatedDt;

    public Currency() {
    }

    public Currency(String curCode, String currencyName) {
        this.curCode = curCode;
        this.currencyName = currencyName;
    }

}
