/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.AppUser;
import java.util.Date;
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

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from AppUser o";
        Date date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public AppUser login(String userName, String password) {
        String sql = "select o from AppUser o where o.userShortName = '" + userName +"' and o.password = '" + password + "'";
        List<AppUser> list = findHSQL(sql);
        return list.isEmpty() ? null : list.get(0);
    }

}
