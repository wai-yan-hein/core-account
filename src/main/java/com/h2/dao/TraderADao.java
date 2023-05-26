/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;
import com.acc.model.TraderA;
import java.util.List;

public interface TraderADao {

    TraderA save(TraderA trader);

    String getMaxDate();

    List<TraderA> findAll(String compCode);

}
