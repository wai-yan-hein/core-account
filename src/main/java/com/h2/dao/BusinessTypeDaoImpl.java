/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.acc.model.BusinessType;
import com.common.Util1;
import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from BusinessType o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<BusinessType> findAll() {
        String sql = "select o from BusinessType o";
        return findHSQL(sql);
    }

    @Override
    public BusinessType findById(Integer id) {
        return getByKey(id);
    }

}
