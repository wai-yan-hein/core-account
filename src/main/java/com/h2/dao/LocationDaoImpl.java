/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.Location;
import com.inventory.entity.LocationKey;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class LocationDaoImpl extends AbstractDao<LocationKey, Location> implements LocationDao {

    @Override
    public Location save(Location cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Location o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<Location> findAll(String compCode) {
        String hsql = "select o from Location o where o.key.compCode ='" + compCode + "' order by o.userCode";
        return findHSQL(hsql);
    }

    @Override
    public Location find(LocationKey key) {
        return getByKey(key);
    }

}
