/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.entity.PriceOption;
import com.inventory.entity.PriceOptionKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface PriceOptionDao {

    PriceOption save(PriceOption obj);

    PriceOption find(PriceOptionKey key);

    String getMaxDate();

    List<PriceOption> findAll(String compCode);

}
