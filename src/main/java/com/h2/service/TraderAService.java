/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;
import com.acc.model.TraderA;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface TraderAService {

    TraderA save(TraderA stock);

    List<TraderA> findAll(String compCode);

    String getMaxDate();

}
