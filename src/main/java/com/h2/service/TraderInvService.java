/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.model.Trader;
import com.inventory.model.TraderKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface TraderInvService {

    Trader save(Trader obj);

    Trader find(TraderKey key);

    List<Trader> findAll(String compCode);

    String getMaxDate();
    
    List<Trader> searchTrader(String str, String type, String compCode, Integer deptId);

}
