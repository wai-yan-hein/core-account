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
public class AccSetting implements Serializable {

    private AccKey key;
    private String disAccount;
    private String payAccount;
    private String taxAccount;
    private String department;
    private String soureAccount;
    private String balAccount;

}
