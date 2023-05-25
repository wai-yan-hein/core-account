/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.dao;

import com.user.model.PrivilegeMenu;

/**
 *
 * @author Athu Sint
 */
public interface PrivilegeMenuDao {

    PrivilegeMenu save(PrivilegeMenu p);

    String getMaxDate();
}
