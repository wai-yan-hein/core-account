/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;

import com.inventory.entity.StockInOutDetail;
import com.inventory.entity.StockInOutKey;

import java.util.List;

/**
 * @author wai yan
 */
public interface StockInOutDetailDao {

    StockInOutDetail save(StockInOutDetail sdh);

    List<StockInOutDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(StockInOutKey key);

}
