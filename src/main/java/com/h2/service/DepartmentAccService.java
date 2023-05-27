/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;
import com.acc.model.DepartmentA;
import com.acc.model.DepartmentAKey;
import java.util.List;

/**
 *
 * @author Dell
 */
public interface DepartmentAccService {

    DepartmentA save(DepartmentA dep);

    DepartmentA find(DepartmentAKey key);

    List<DepartmentA> findAll(String compCode);

    String getMaxDate();

}
