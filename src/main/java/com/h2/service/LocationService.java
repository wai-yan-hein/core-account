/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.model.Location;
import com.inventory.model.LocationKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface LocationService {

    Location save(Location stock);

    Location find(LocationKey key);

    List<Location> findAll(String compCode);

    String getMaxDate();

}
