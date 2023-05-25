/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.user.model.PrivilegeMenu;

/**
 *
 * @author Athu Sint
 */
public interface PrivilegeMenuService {

    PrivilegeMenu save(PrivilegeMenu pc);

    String getMaxDate();
}
