/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.acc.model.BusinessType;

/**
 *
 * @author Athu Sint
 */
public interface BusinessTypeService {

    BusinessType save(BusinessType bus);

    String getMaxDate();
}
