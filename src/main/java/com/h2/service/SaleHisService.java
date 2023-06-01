/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.model.SaleHis;
import com.inventory.model.SaleHisKey;
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

}
