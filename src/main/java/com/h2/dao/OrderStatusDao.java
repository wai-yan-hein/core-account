/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.OrderStatus;
import com.inventory.model.OrderStatusKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface OrderStatusDao {

    OrderStatus save(OrderStatus obj);

    OrderStatus find(OrderStatusKey key);

    String getMaxDate();

    List<OrderStatus> findAll(String compCode);

}
