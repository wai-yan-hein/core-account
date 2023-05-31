/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.inventory.model.RetOutHisDetail;
import com.inventory.model.RetOutKey;
import java.util.List;

/**
 * @author wai yan
 */
public interface RetOutDetailService {

    RetOutHisDetail save(RetOutHisDetail pd);

    List<RetOutHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(RetOutKey key);

}
