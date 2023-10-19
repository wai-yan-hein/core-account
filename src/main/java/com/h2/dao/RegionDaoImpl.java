package com.h2.dao;

import com.common.Util1;
import com.inventory.model.Region;
import com.inventory.model.RegionKey;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class RegionDaoImpl extends AbstractDao<RegionKey, Region> implements RegionDao {

    @Override
    public Region save(Region region) {
        saveOrUpdate(region, region.getKey());
        return region;
    }

    @Override
    public Region findByCode(RegionKey id) {
        return getByKey(id);
    }

    @Override
    public List<Region> search(String code, String name, String compCode, String parentCode) {
        String strSql = "select o from Region o ";
        String strFilter = "";

        if (!code.equals("-")) {
            strFilter = "o.regionCode = '" + code + "'";
        }

        if (!name.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.regionName like '%" + name + "%'";
            } else {
                strFilter = strFilter + " and o.regionName like '%" + name + "%'";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.compCode= '" + compCode + "'";
            }
        }
        if (!parentCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.parentRegion = '" + parentCode + "'";
            } else {
                strFilter = strFilter + " and o.parentRegion = '" + parentCode + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.regionName";
        }

        return findHSQL(strSql);
    }

    @Override
    public int delete(RegionKey key) {
        Region r = getByKey(key);
        r.setDeleted(true);
        r.setUpdatedDate(LocalDateTime.now());
        update(r);
        return 1;
    }

    @Override
    public List<Region> findAll(String compCode) {
        String hsql = "select o from Region o where o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }


    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Stock o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }
}
