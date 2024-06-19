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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Repository
@Service
@Transactional
@Slf4j
public class MacPropertyRepo extends AbstractDao<MachinePropertyKey, MachineProperty> {

    public MachineProperty save(MachineProperty mProp) {
        saveOrUpdate(mProp, mProp.getKey());
        return mProp;
    }

    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from MachineProperty o";
        LocalDateTime date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    public List<MachineProperty> getMacProperty(Integer macId) {
        String sql = "select o from MachineProperty o where o.key.macId = " + macId;
        return findHSQL(sql);
    }
}
