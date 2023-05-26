/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.model.PriceOption;
import com.inventory.model.PriceOptionKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface PriceOptionService {

    PriceOption save(PriceOption obj);

    PriceOption find(PriceOptionKey key);

    List<PriceOption> findAll(String compCode);

    String getMaxDate();

}
