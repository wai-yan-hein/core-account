/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.dao;

import com.user.model.AppUser;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface UserDao {

    AppUser save(AppUser appUser);

    List<AppUser> findAll();

    String getMaxDate();
    
    AppUser login(String userName, String password);
    
    HashMap<String, String> getProperty(String compCode, String roleCode, Integer macId);
}
