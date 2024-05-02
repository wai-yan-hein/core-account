/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.entity.Trader;
import com.inventory.entity.TraderKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface TraderInvDao {

    Trader save(Trader obj);

    Trader find(TraderKey key);

    Boolean delete(TraderKey key);

    String getMaxDate();

    List<Trader> findAll(String compCode);

    List<Trader> getTrader(String compCode, String type);

    List<Trader> searchTrader(String str, String type, String compCode);

}
