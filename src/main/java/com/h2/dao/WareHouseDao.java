package com.h2.dao;

import com.inventory.model.WareHouse;
import com.inventory.model.WareHouseKey;
import java.time.LocalDateTime;
import java.util.List;

public interface WareHouseDao {
    WareHouse save(WareHouse WareHouse);

    List<WareHouse> findAll(String compCode);

    int delete(WareHouseKey key);

    WareHouse findById(WareHouseKey id);

    String getMaxDate();

    List<WareHouse> getWareHouse(LocalDateTime updatedDate);
}
