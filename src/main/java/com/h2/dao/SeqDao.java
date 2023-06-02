/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.SeqKey;
import com.inventory.model.SeqTable;

/**
 *
 * @author Lenovo
 */
public interface SeqDao {

    SeqTable save(SeqTable st);

    SeqTable findById(SeqKey id);

//    int getSequence(String option, String period, String compCode);

    int getSequence(Integer macId, String option, String period, String compCode);
}
