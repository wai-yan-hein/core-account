/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.inventory.model.SaleDetailKey;
import com.inventory.model.SaleHisDetail;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface SaleHisDetailService {

    SaleHisDetail save(SaleHisDetail obj);

    SaleHisDetail find(SaleDetailKey key);

    void delete(SaleDetailKey key);

    String getMaxDate();

    List<SaleHisDetail> search(String vouNo, String compCode, Integer deptId);
    
    List<SaleHisDetail> searchDetail(String vouNo, String compCode, Integer deptId);

    List<SaleHisDetail> findAll(String compCode);

}
