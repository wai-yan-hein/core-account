/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.MenuDao;
import com.user.model.Menu;
import com.user.model.MenuKey;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Lazy
@Service
@Transactional
public class MenuServiceimpl implements MenuService {

    @Autowired
    private MenuDao dao;

    @Override
    public Menu save(Menu menu) {
        return dao.save(menu);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<Menu> getMenuTree(String compCode) {
        return dao.getMenuTree(compCode);
    }

    @Override
    public List<Menu> getMenuDynamic(String compCode) {
        return dao.getMenuDynamic(compCode);
    }

    @Override
    public boolean delete(MenuKey key){
        return dao.delete(key);
    }

}
