/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.user.model.PrivilegeCompany;
import com.user.model.VRoleCompany;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface PrivilegeCompanyService {

    PrivilegeCompany save(PrivilegeCompany pc);

    String getMaxDate();

    List<PrivilegeCompany> getPC(String roleCode);
}
