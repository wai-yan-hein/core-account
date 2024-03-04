/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.common.ReportFilter;
import com.inventory.entity.SaleHis;
import com.inventory.entity.SaleHisKey;
import com.inventory.entity.VSale;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface SaleHisService {

    SaleHis save(SaleHis obj);

    SaleHis find(SaleHisKey key);

    SaleHis updateACK(SaleHisKey key);

    List<SaleHis> findAll(String compCode);

    List<SaleHis> unUploadVoucher(String compCode);

    String getMaxDate();

    void delete(SaleHisKey key);

    void restore(SaleHisKey key);

    List<VSale> getSale(ReportFilter filter);

    List<VSale> getSaleReport(String vouNo, String compCode, Integer deptId);

    public int getUploadCount();

}
