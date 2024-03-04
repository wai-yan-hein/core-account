/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;

import com.inventory.entity.RetInHis;
import com.inventory.entity.RetInHisKey;
import java.util.List;

/**
 * @author wai yan
 */
public interface RetInDao {

    RetInHis save(RetInHis saleHis);

    List<RetInHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String remark, String userCode);

    RetInHis findById(RetInHisKey id);

    void delete(RetInHisKey key) throws Exception;

    void restore(RetInHisKey key) throws Exception;

    List<RetInHis> unUploadVoucher(String syncDate);


    List<RetInHis> search(String updatedDate, List<String> keys);

    void truncate(RetInHisKey key);

    RetInHis updateACK(RetInHisKey key);

}
