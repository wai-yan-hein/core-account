/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.MachineInfo;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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
        String sql = "select max(o.updatedDate) from MachineInfo o";
        LocalDateTime date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public MachineInfo getMachineInfo(String machineName) {
        MachineInfo mac = new MachineInfo();
        mac.setMacId(0);
        String jpql = "select o from MachineInfo o where o.machineName ='" + machineName + "'";
        List<MachineInfo> list = findHSQL(jpql);
        return list.isEmpty() ? mac : list.get(0);
    }

    @Override
    public List<MachineInfo> findAll() {
        String sql = "select o from MachineInfo o";
        return findHSQL(sql);
    }

}
