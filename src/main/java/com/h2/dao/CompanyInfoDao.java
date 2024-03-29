/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.dao;

import com.user.model.CompanyInfo;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface CompanyInfoDao {

    CompanyInfo save(CompanyInfo comp);

    String getMaxDate();
    
    List<CompanyInfo> findAll(boolean active);
    
    CompanyInfo findById(String id);

}
