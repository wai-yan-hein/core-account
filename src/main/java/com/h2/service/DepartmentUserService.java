/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.user.model.DepartmentKey;
import com.user.model.DepartmentUser;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface DepartmentUserService {

    DepartmentUser save(DepartmentUser dept);

    String getMaxDate();

    DepartmentUser findById(DepartmentKey key);

    List<DepartmentUser> findAll(Boolean active,String compCode);
}
