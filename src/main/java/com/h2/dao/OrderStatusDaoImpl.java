/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.OrderStatus;
import com.inventory.model.OrderStatusKey;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
@Slf4j
public class OrderStatusDaoImpl extends AbstractDao<OrderStatusKey, OrderStatus> implements OrderStatusDao {
    
    @Override
    public OrderStatus save(OrderStatus cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }
    
    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from OrderStatus o";
        LocalDateTime date = getDate(jpql);
        log.info("order status = " + date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date));
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }
    
    @Override
    public List<OrderStatus> findAll(String compCode) {
        String hsql = "select o from OrderStatus o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }
    
    @Override
    public OrderStatus find(OrderStatusKey key) {
        return getByKey(key);
    }
    
}
