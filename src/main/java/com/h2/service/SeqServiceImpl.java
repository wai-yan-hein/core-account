/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.SeqDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class SeqServiceImpl implements SeqService {

    @Autowired
    private SeqDao dao;

    @Override
    public int getSequence(String option, String period, String compCode) {
        return dao.getSequence(option, period, compCode);
    }

}
