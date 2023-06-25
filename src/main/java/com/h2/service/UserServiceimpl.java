/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.UserDao;
import com.user.model.AppUser;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Service
@Transactional
public class UserServiceimpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public AppUser save(AppUser appUser) {
        return userDao.save(appUser);
    }

    @Override
    public List<AppUser> findAll() {
        return userDao.findAll();
    }

    @Override
    public String getMaxDate() {
        return userDao.getMaxDate();
    }

    @Override
    public AppUser login(String userName, String password) {
        return userDao.login(userName, password);
    }

    @Override
    public HashMap<String, String> getProperty(String compCode, String roleCode, Integer macId) {
        return userDao.getProperty(compCode, roleCode, macId);
    }

}
