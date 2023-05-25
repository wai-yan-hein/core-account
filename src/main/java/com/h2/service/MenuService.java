/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.user.model.Menu;

/**
 *
 * @author Athu Sint
 */
public interface MenuService {

    Menu save(Menu menu);

    String getMaxDate();
}
