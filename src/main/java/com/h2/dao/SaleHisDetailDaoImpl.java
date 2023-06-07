/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.SaleDetailKey;
import com.inventory.model.SaleHisDetail;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class SaleHisDetailDaoImpl extends AbstractDao<SaleDetailKey, SaleHisDetail> implements SaleHisDetailDao {

    @Override
    public SaleHisDetail save(SaleHisDetail cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from SaleHisDetail o";
        Date date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<SaleHisDetail> findAll(String compCode) {
        String hsql = "select o from SaleHisDetail o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public SaleHisDetail find(SaleDetailKey key) {
        return getByKey(key);
    }

    @Override
    public void delete(SaleDetailKey key) {
        remove(key);
    }

    @Override
    public List<SaleHisDetail> search(String vouNo, String compCode, Integer deptId) {
       String hsql = "select o from SaleHisDetail o where o.key.compCode ='" + compCode + "' "
               + "and o.key.vouNo = '" + vouNo + "' and o.key.deptId = " + deptId + "";
        return findHSQL(hsql);
    }

}
