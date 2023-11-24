
package com.h2.service;

import com.h2.dao.AccSettingDao;
import com.inventory.model.AccSetting;
import com.inventory.model.AccKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class AccSettingServiceImpl implements AccSettingService {

    @Autowired
    private AccSettingDao dao;

    @Override
    public List<AccSetting> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public AccSetting save(AccSetting setting) {
        return dao.save(setting);
    }

    @Override
    public AccSetting findByCode(AccKey key) {
        return dao.findByCode(key);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }
}
