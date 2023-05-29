/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;
import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface COAService {

    ChartOfAccount save(ChartOfAccount coa);

    List<ChartOfAccount> findAll(String compCode);

    String getMaxDate();
    
    ChartOfAccount findById(COAKey key);
}
