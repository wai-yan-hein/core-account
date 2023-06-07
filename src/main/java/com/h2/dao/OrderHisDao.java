/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;

import com.inventory.model.General;
import com.inventory.model.OrderHis;
import com.inventory.model.OrderHisKey;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface OrderHisDao {

    OrderHis save(OrderHis sh);

    List<OrderHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String remark, String userCode);

    OrderHis findById(OrderHisKey id);

    void delete(OrderHisKey key) throws Exception;

    void restore(OrderHisKey key) throws Exception;

    List<OrderHis> unUploadVoucher(String compCode);

    Date getMaxDate();

    List<OrderHis> search(String updatedDate, List<String> location);

    void truncate(OrderHisKey key);

    General getVoucherInfo(String vouDate, String compCode, Integer depId);
    
    OrderHis updateACK(OrderHisKey key);
    
    List<OrderHis> findAll(String compCode);

}