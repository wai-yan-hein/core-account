/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class StockBalanceTmpKey implements Serializable {

    private Stock stock;
    private Integer locId;
    private Integer machineId;

}
