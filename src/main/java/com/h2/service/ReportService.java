/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.inventory.model.General;

/**
 *
 * @author Athu Sint
 */
public interface ReportService {

    General getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode, Integer deptId);

    General getSmallestQty(String stockCode, String unit, String compCode, Integer deptId);

}
