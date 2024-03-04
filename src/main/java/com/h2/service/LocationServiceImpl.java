/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.LocationDao;
import com.inventory.entity.Location;
import com.inventory.entity.LocationKey;
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
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationDao dao;

    @Override
    public Location save(Location cat) {
        return dao.save(cat);
    }

    @Override
    public List<Location> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public Location find(LocationKey key) {
        return dao.find(key);
    }

}
