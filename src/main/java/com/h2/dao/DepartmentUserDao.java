/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.dao;

import com.user.model.DepartmentUser;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface DepartmentUserDao {

    DepartmentUser save(DepartmentUser dept);

    String getMaxDate();
    
    DepartmentUser findById(Integer id);
    
    List<DepartmentUser> findAll();
}
