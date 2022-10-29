/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
public class ProcessHisDetail {

    private ProcessHisDetailKey key;
    private Date vouDate;
    private String stockName;
    private String stockUsrCode;
    private float qty;
    private String unit;
    private float price;
    private float amount;
    private String locName;
}
