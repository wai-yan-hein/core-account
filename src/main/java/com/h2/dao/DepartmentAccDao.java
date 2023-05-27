/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;
import com.acc.model.DepartmentA;
import com.acc.model.DepartmentAKey;
import java.util.List;

/**
 *
 * @author dell
 */
public interface DepartmentAccDao {

    DepartmentA save(DepartmentA dep);

    DepartmentA find(DepartmentAKey key);

    String getMaxDate();

    List<DepartmentA> findAll(String compCode);

}
