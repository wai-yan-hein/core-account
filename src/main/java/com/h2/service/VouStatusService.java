/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.entity.VouStatus;
import com.inventory.entity.VouStatusKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface VouStatusService {

    VouStatus save(VouStatus obj);

    VouStatus find(VouStatusKey key);

    List<VouStatus> findAll(String compCode);

    String getMaxDate();

}
