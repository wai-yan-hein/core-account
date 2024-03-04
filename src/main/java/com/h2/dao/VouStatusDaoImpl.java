/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.VouStatus;
import com.inventory.entity.VouStatusKey;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class VouStatusDaoImpl extends AbstractDao<VouStatusKey, VouStatus> implements VouStatusDao {

    @Override
    public VouStatus save(VouStatus cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from VouStatus o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<VouStatus> findAll(String compCode) {
        String hsql = "select o from VouStatus o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public VouStatus find(VouStatusKey key) {
        return getByKey(key);
    }

}
