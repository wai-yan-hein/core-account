/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.VouStatus;
import com.inventory.model.VouStatusKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface VouStatusDao {

    VouStatus save(VouStatus obj);

    VouStatus find(VouStatusKey key);

    String getMaxDate();

    List<VouStatus> findAll(String compCode);

}
