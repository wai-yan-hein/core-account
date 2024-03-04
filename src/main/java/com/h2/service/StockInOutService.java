/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.inventory.entity.StockIOKey;
import com.inventory.entity.StockInOut;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockInOutService {

    StockInOut save(StockInOut io);

    List<StockInOut> search(String fromDate, String toDate, String remark, String desp,
            String vouNo, String userCode, String vouStatus);

    StockInOut findById(StockIOKey id);

}
