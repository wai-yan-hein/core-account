/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.PrivilegeCompanyDao;
import com.user.model.PrivilegeCompany;
import com.user.model.VRoleCompany;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Service
@Transactional
public class PrivilegeCompanyServiceImpl implements PrivilegeCompanyService {

    @Autowired
    private PrivilegeCompanyDao dao;

    @Override
    public PrivilegeCompany save(PrivilegeCompany pc) {
        return dao.save(pc);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<PrivilegeCompany> getPC(String roleCode) {
        return dao.getPC(roleCode);
    }

}
