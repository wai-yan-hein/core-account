/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.CompanyInfo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class CompanyInfoDaoImpl extends AbstractDao<String, CompanyInfo> implements CompanyInfoDao {

    @Override
    public CompanyInfo save(CompanyInfo comp) {
        saveOrUpdate(comp, comp.getCompCode());
        return comp;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from CompanyInfo o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<CompanyInfo> findAll(boolean active) {
        String sql = "select o from CompanyInfo o order by o.orderId";
        return findHSQL(sql);
    }

    @Override
    public CompanyInfo findById(String id) {
        return getByKey(id);
    }

}
