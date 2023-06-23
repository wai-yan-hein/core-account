/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.SaleHis;
import com.inventory.model.SaleHisKey;
import com.inventory.model.VSale;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface SaleHisDao {

    SaleHis save(SaleHis obj);

    SaleHis find(SaleHisKey key);

    List<SaleHis> unUploadVoucher(String compCode);

    SaleHis updateACK(SaleHisKey key);

    String getMaxDate();

    List<SaleHis> findAll(String compCode);

    void delete(SaleHisKey key);

    void restore(SaleHisKey key);

    List<VSale> getSaleHistory(String fromDate, String toDate, String traderCode, String saleManCode, String vouNo,
            String remark, String reference, String userCode, String stockCode, String locCode,
            String compCode, Integer deptId, String deleted, String nullBatch, String batchNo,
            String projectNo, String curCode);

    List<VSale> getSaleReport(String vouNo, String compCode, Integer deptId);

    public int getUploadCount();

}
