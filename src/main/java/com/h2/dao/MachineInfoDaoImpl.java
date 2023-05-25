/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.MachineInfo;
import java.util.Date;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class MachineInfoDaoImpl extends AbstractDao<Integer, MachineInfo> implements MachineInfoDao {

    @Override
    public MachineInfo save(MachineInfo mInfo) {
        saveOrUpdate(mInfo, mInfo.getMacId());
        return mInfo;
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from MachineInfo";
        Date date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

}
