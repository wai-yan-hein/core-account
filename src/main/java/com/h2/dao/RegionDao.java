package com.h2.dao;

import com.inventory.entity.Region;
import com.inventory.entity.RegionKey;
import java.util.List;

public interface RegionDao {

    Region save(Region region);

    Region findByCode(RegionKey id);

    List<Region> search(String code, String name, String compCode, String parentCode);

    int delete(RegionKey key);

    List<Region> findAll(String compCode);

    String getMaxDate();

}
