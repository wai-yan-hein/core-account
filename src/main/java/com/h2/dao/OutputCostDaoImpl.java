/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;
import com.common.Util1;
import com.inventory.model.OutputCost;
import com.inventory.model.OutputCostKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class OutputCostDaoImpl extends AbstractDao<OutputCostKey, OutputCost> implements OutputCostDao {

    @Override
    public OutputCost save(OutputCost item) {
        item.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(item, item.getKey());
        return item;
    }

    @Override
    public List<OutputCost> findAll(String compCode) {
        String hsql = "select o from OutputCost o where o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public boolean delete(OutputCostKey key) {
        return true;
    }

    @Override
    public List<OutputCost> search(String catName) {
        String strFilter = "";

        if (!catName.equals("-")) {
            strFilter = "o.catName like '%" + catName + "%'";
        }

        if (strFilter.isEmpty()) {
            strFilter = "select o from OutputCost o";
        } else {
            strFilter = "select o from OutputCost o where " + strFilter;
        }
        return findHSQL(strFilter);
    }

    @Override
    public List<OutputCost> unUploadOutputCost() {
        String hsql = "select o from OutputCost o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public OutputCost findByCode(OutputCostKey key) {
        return getByKey(key);
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from OutputCost o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<OutputCost> getOutputCost(LocalDateTime updatedDate) {
        String hsql = "select o from OutputCost o where o.updatedDate >: updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
