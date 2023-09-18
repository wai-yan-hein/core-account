package com.h2.service;

import com.h2.dao.RegionDao;
import com.inventory.model.Region;
import com.inventory.model.RegionKey;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionDao dao;

    @Override
    public Region save(Region rg) {
        return dao.save(rg);
    }

    @Override
    public Region findByCode(RegionKey id) {
        return dao.findByCode(id);
    }

    @Override
    public List<Region> search(String code, String name, String compCode, String parentCode) {
        return dao.search(code, name, compCode, parentCode);
    }

    @Override
    public int delete(RegionKey key) {
        return dao.delete(key);
    }

    @Override
    public List<Region> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }
}
