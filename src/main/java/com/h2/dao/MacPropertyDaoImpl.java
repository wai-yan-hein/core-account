/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.MachineProperty;
import com.user.model.MachinePropertyKey;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class MacPropertyDaoImpl extends AbstractDao<MachinePropertyKey, MachineProperty> implements MacPropertyDao {

    @Override
    public MachineProperty save(MachineProperty mProp) {
        saveOrUpdate(mProp, mProp.getKey());
        return mProp;
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from MachineProperty o";
        LocalDateTime date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<MachineProperty> getMacProperty(Integer macId) {
        String sql = "select o from MachineProperty o where o.key.macId = " + macId;
        return findHSQL(sql);
    }
}
