package com.h2.dao;

import com.common.Util1;
import com.inventory.model.WareHouse;
import com.inventory.model.WareHouseKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class WareHouseDaoImpl extends AbstractDao<WareHouseKey, WareHouse> implements WareHouseDao{
    @Override
    public WareHouse save(WareHouse g) {
        g.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(g, g.getKey());
        return g;
    }

    @Override
    public List<WareHouse> findAll(String compCode) {
        String hsql = "select o from WareHouse o where o.key.compCode = '" + compCode + "' and o.deleted =false";
        return findHSQL(hsql);
    }

    @Override
    public int delete(WareHouseKey key) {
        remove(key);
        return 1;
    }

    @Override
    public WareHouse findById(WareHouseKey id) {
        return getByKey(id);
    }


    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from WareHouse o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<WareHouse> getWareHouse(LocalDateTime updatedDate) {
        String hsql = "select o from WareHouse o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
