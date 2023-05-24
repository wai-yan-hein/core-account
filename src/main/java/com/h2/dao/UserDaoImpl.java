/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.AppUser;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class UserDaoImpl extends AbstractDao<String, AppUser> implements UserDao {

    @Override
    public AppUser save(AppUser appUser) {
        saveOrUpdate(appUser, appUser.getUserCode());
        return appUser;
    }

    @Override
    public List<AppUser> findAll() {
        String sql = "select o from AppUser o";
        return findHSQL(sql);
    }

}
