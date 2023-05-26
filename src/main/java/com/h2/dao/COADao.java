/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;
import com.acc.model.ChartOfAccount;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface COADao {

    ChartOfAccount save(ChartOfAccount coa);

    String getMaxDate();

    List<ChartOfAccount> findAll(String compCode);

}