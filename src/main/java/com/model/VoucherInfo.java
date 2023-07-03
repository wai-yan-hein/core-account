/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
@AllArgsConstructor
@Builder
public class VoucherInfo {

    private String vouNo;
    private Double vouTotal;
    private Double hmsVouTotal;
    private Double accVouTotal;
    private Double diffAmt;
    private String option;

    public VoucherInfo() {
    }
    
}
