/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.acc.model.BusinessType;
import com.h2.dao.BusinessTypeDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Lazy
@Service
@Transactional
public class BusinessTypeServiceImpl implements BusinessTypeService {

    @Autowired
    BusinessTypeDao bDao;

    @Override
    public BusinessType save(BusinessType bus) {
        return bDao.save(bus);
    }

    @Override
    public String getMaxDate() {
        return bDao.getMaxDate();
    }

    @Override
    public List<BusinessType> findAll() {
        return bDao.findAll();
    }

    @Override
    public BusinessType findById(Integer id) {
        return bDao.findById(id);
    }

}
