/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.user.model.CompanyInfo;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface CompanyInfoService {

    CompanyInfo save(CompanyInfo comp);

    String getMaxDate();

    List<CompanyInfo> findAll(boolean active);

    CompanyInfo findById(String id);
}
