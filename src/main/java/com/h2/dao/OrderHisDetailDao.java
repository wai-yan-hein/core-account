/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;


import com.inventory.model.OrderDetailKey;
import com.inventory.model.OrderHisDetail;
import java.util.List;

/**
 * @author wai yan
 */
public interface OrderHisDetailDao {

    OrderHisDetail save(OrderHisDetail sdh);

    List<OrderHisDetail> search(String vouNo, String compCode, Integer deptId);

    List<OrderHisDetail> searchDetail(String vouNo, String compCode, Integer deptId);

    int delete(OrderDetailKey key);


}
