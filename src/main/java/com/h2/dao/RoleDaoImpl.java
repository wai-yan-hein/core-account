/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.AppRole;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class RoleDaoImpl extends AbstractDao<String, AppRole> implements RoleDao {

    @Override
    public AppRole save(AppRole p) {
        saveOrUpdate(p, p.getRoleCode());
        return p;
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from AppRole o";
        LocalDateTime date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<AppRole> findAll(String compCode) {
        String sql = "select o from AppRole o";
        return findHSQL(sql);
    }

    @Override
    public AppRole findById(String roleCode) {
        return getByKey(roleCode);
    }

}
