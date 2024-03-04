/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.entity.SaleMan;
import com.inventory.entity.SaleManKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface SaleManDao {

    SaleMan save(SaleMan stock);

    SaleMan find(SaleManKey key);

    String getMaxDate();

    List<SaleMan> findAll(String compCode);

}
