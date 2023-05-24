/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.Location;
import com.inventory.model.LocationKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface LocationDao {

    Location save(Location stock);

    Location find(LocationKey key);

    String getMaxDate();

    List<Location> findAll(String compCode);

}
