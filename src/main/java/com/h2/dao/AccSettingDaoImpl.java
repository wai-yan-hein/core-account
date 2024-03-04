/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;
import com.common.Util1;
import com.inventory.entity.AccKey;
import com.inventory.entity.AccSetting;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class AccSettingDaoImpl extends AbstractDao<AccKey, AccSetting> implements AccSettingDao {

    @Override
    public List<AccSetting> findAll(String compCode) {
        String hsql = "select o from AccSetting o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public AccSetting save(AccSetting setting) {
        saveOrUpdate(setting,setting.getKey());
        return setting;

    }

    @Override
    public AccSetting findByCode(AccKey key) {
        return getByKey(key);
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from AccSetting o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }
}
