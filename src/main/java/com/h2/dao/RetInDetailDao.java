/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;


import com.inventory.entity.RetInHisDetail;
import com.inventory.entity.RetInKey;
import java.util.List;

/**
 * @author wai yan
 */
public interface RetInDetailDao {

    RetInHisDetail save(RetInHisDetail pd);

    List<RetInHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(RetInKey key);

}
