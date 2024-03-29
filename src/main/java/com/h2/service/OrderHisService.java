/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.common.ReportFilter;
import com.inventory.entity.General;
import com.inventory.entity.OrderHis;
import com.inventory.entity.OrderHisKey;
import com.inventory.entity.VOrder;
import java.util.List;

/**
 * @author wai yan
 */
public interface OrderHisService {

    OrderHis save(OrderHis orderHis);

    OrderHis update(OrderHis orderHis);

    List<OrderHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String remark, String userCode);

    OrderHis findById(OrderHisKey id);

    void delete(OrderHisKey key) throws Exception;

    void restore(OrderHisKey key) throws Exception;

    List<OrderHis> unUploadVoucher(String compCode);

    List<OrderHis> search(String updatedDate, List<String> keys);

    void truncate(OrderHisKey key);

    General getVoucherInfo(String vouDate, String compCode, Integer depId);

    List<OrderHis> findAll(String compCode);

    OrderHis updateACK(OrderHisKey key);

    public List<VOrder> getOrder(ReportFilter filter);

}
