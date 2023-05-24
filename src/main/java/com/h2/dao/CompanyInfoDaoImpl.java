/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.user.model.CompanyInfo;
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

}
