/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;
import com.inventory.entity.PurHis;
import com.inventory.entity.PurHisKey;
import java.util.List;

/**
 * @author wai yan
 */
public interface PurHisDao {

    PurHis save(PurHis ph);

    List<PurHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String remark, String userCode);

    PurHis findById(PurHisKey id);

    void delete(PurHisKey key);

    void restore(PurHisKey key) throws Exception;

    List<PurHis> unUploadVoucher(String compCode);


    List<PurHis> search(String updatedDate, List<String> keys);

    PurHis updateACK(PurHisKey key);

}
