/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;

import com.inventory.model.PurDetailKey;
import com.inventory.model.PurHisDetail;
import java.util.List;

/**
 * @author wai yan
 */
public interface PurHisDetailDao {

    PurHisDetail save(PurHisDetail pd);

    List<PurHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(PurDetailKey key);

}
