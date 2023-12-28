/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.dao;

import com.user.model.PrivilegeCompany;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface PrivilegeCompanyDao {

    PrivilegeCompany save(PrivilegeCompany p);

    String getMaxDate();
    
    List<PrivilegeCompany> getPC(String roleCode);
}
