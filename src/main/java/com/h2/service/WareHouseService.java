package com.h2.service;
import com.inventory.model.WareHouse;
import com.inventory.model.WareHouseKey;
import java.time.LocalDateTime;
import java.util.List;

public interface WareHouseService {
    WareHouse save(WareHouse status);
    
    List<WareHouse> findAll(String compCode);
    
    int delete(WareHouseKey key);
    
    WareHouse findById(WareHouseKey key);

    String getMaxDate();
    
    List<WareHouse> getWarehouse(LocalDateTime updatedDate);
}
