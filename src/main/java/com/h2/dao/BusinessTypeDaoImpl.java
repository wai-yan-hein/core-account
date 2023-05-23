/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.acc.model.BusinessType;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class BusinessTypeDaoImpl extends AbstractDao<Integer, BusinessType> implements BusinessTypeDao {

    @Override
    public BusinessType save(BusinessType bus) {
        saveOrUpdate(bus, bus.getBusId());
        return bus;
    }

}
