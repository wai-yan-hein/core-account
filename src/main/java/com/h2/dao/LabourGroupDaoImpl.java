package com.h2.dao;


import com.common.Util1;
import com.inventory.model.LabourGroup;
import com.inventory.model.LabourGroupKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class LabourGroupDaoImpl extends AbstractDao<LabourGroupKey, LabourGroup> implements LabourGroupDao{
    @Override
    public LabourGroup save(LabourGroup LabourGroup) {
        saveOrUpdate(LabourGroup, LabourGroup.getKey());
        return LabourGroup;
    }

    @Override
    public List<LabourGroup> findAll(String compCode) {
        String hsql = "select o from LabourGroup o where o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(LabourGroupKey key) {
        remove(key);
        return 1;
    }

    @Override
    public LabourGroup findById(LabourGroupKey id) {
        return getByKey(id);
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from LabourGroup o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    
}
