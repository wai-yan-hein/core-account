/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;

import com.inventory.entity.StockCriteria;
import com.inventory.entity.StockCriteriaKey;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockCriteriaDao {

    StockCriteria findByCode(StockCriteriaKey key);

    StockCriteria save(StockCriteria item);

    List<StockCriteria> findAll(String compCode, boolean active);

    List<StockCriteria> search(String text, String compCode);

    List<StockCriteria> unUpload();

    int delete(String id);

    String getMaxDate();

    List<StockCriteria> search(String stockCode, String stockType, String cat, String brand, String compCode, boolean orderFavorite);

}
