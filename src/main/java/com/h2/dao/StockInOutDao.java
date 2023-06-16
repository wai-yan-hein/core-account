/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;

import com.inventory.model.LocationKey;
import com.inventory.model.StockIOKey;
import com.inventory.model.StockInOut;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockInOutDao {

    StockInOut save(StockInOut saleHis);

    List<StockInOut> search(String fromDate, String toDate, String remark, String desp,
            String vouNo, String userCode, String vouStatus);

    StockInOut findById(StockIOKey id);

    void delete(StockIOKey key) throws Exception;

    void restore(StockIOKey key) throws Exception;

    List<StockInOut> unUpload(String syncDate);


    List<StockInOut> search(String updatedDate, List<LocationKey> keys);

    StockInOut updateACK(StockIOKey key);

}
