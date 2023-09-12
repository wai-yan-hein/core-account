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

    List<ChartOfAccount> getCOATree(String compCode);

    List<ChartOfAccount> getCOA(String headCode, String compCode);

    List<ChartOfAccount> getCOAChild(String parentCode, String compCode);

    List<ChartOfAccount> getTraderCOA(String compCode);

    List<ChartOfAccount> searchCOA(String str, Integer level, String compCode);

    List<ChartOfAccount> getCOAByGroup(String groupCode, String compCode);

    List<ChartOfAccount> getCOAByHead(String headCode, String compCode);

    List<ChartOfAccount> getCOA(String compCode);

}
