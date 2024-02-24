/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.inventory.model.StockCriteria;
import com.inventory.model.StockCriteriaKey;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockCriteriaService {

    StockCriteria findByCode(StockCriteriaKey key);

    StockCriteria save(StockCriteria category);

    List<StockCriteria> findAll(String compCode, boolean active);

    int delete(String id);

    List<StockCriteria> search(String text, String compCode);

    List<StockCriteria> unUpload();

    String getMaxDate();

}
