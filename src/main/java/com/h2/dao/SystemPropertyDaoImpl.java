/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.PropertyKey;
import com.user.model.SysProperty;
import java.util.Date;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class SystemPropertyDaoImpl extends AbstractDao<PropertyKey, SysProperty> implements SystemPropertyDao {

    @Override
    public SysProperty save(SysProperty p) {
        saveOrUpdate(p, p.getKey());
        return p;
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from SysProperty o";
        Date date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

}
