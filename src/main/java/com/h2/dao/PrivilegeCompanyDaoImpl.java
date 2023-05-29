/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.h2.service.CompanyInfoService;
import com.user.model.CompanyInfo;
import com.user.model.PCKey;
import com.user.model.PrivilegeCompany;
import com.user.model.VRoleCompany;
import java.io.Serial;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class PrivilegeCompanyDaoImpl extends AbstractDao<PCKey, PrivilegeCompany> implements PrivilegeCompanyDao {

    @Autowired
    private CompanyInfoService compInfoService;

    @Override
    public PrivilegeCompany save(PrivilegeCompany p) {
        saveOrUpdate(p, p.getKey());
        return p;
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from PrivilegeCompany o";
        Date date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<PrivilegeCompany> getPC(String roleCode) {
        List<PrivilegeCompany> list = getRoleCompany(roleCode);
        list.forEach(p -> {
            CompanyInfo c = compInfoService.findById(p.getKey().getCompCode());
            p.setCompName(c.getCompName());
//            c.ifPresent(info -> p.setCompName(info.getCompName()));
        });
        return list;
    }

    public List<PrivilegeCompany> getRoleCompany(String roleCode) {
        String sql = "select o from PrivilegeCompany o where o.key.roleCode = '" + roleCode + "'";
        return findHSQL(sql);
    }

}
