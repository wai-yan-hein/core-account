/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.MachineInfo;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
@Slf4j
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
        String jpql = "select o from MachineInfo o where o.serialNo ='" + machineName.toLowerCase() + "'";
        List<MachineInfo> list = findHSQL(jpql);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<MachineInfo> findAll() {
        String sql = "select o from MachineInfo o";
        return findHSQL(sql);
    }

    @Override
    public MachineInfo find(Integer macId) {
        return getByKey(macId);
    }

}
