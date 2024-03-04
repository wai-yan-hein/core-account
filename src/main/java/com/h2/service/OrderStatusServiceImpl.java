/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.OrderStatusDao;
import com.inventory.entity.OrderStatus;
import com.inventory.entity.OrderStatusKey;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Lazy
@Service
@Transactional
public class OrderStatusServiceImpl implements OrderStatusService {

    @Autowired
    private OrderStatusDao dao;

    @Override
    public OrderStatus save(OrderStatus cat) {
        return dao.save(cat);
    }

    @Override
    public List<OrderStatus> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public OrderStatus find(OrderStatusKey key) {
        return dao.find(key);
    }

}
