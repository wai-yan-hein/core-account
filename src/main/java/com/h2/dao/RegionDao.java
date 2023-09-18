package com.h2.dao;

import com.inventory.model.Region;
import com.inventory.model.RegionKey;
import java.util.List;

public interface RegionDao {

    Region save(Region region);

    Region findByCode(RegionKey id);

    List<Region> search(String code, String name, String compCode, String parentCode);

    int delete(RegionKey key);

    List<Region> findAll(String compCode);

    String getMaxDate();

}
