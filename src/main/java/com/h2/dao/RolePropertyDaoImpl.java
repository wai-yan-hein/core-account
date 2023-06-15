/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.RoleProperty;
import com.user.model.RolePropertyKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class RolePropertyDaoImpl extends AbstractDao<RolePropertyKey, RoleProperty> implements RolePropertyDao {

    @Override
    public RoleProperty save(RoleProperty p) {
        saveOrUpdate(p, p.getKey());
        return p;
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from RoleProperty o";
        LocalDateTime date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<RoleProperty> getRoleProperty(String roleCode) {
        String sql = "select o from RoleProperty o where o.key.roleCode = '" + roleCode + "'";
        return findHSQL(sql);
    }

}
