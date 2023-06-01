/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.SaleHis;
import com.inventory.model.SaleHisKey;
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

}
