/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.CompanyInfoDao;
import com.user.model.CompanyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Service
@Transactional
public class CompanyInfoServiceImpl implements CompanyInfoService {

    @Autowired
    private CompanyInfoDao compDao;

    @Override
    public CompanyInfo save(CompanyInfo comp) {
        return compDao.save(comp);
    }

}
