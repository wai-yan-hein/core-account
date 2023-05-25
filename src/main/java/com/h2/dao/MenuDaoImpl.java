/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.Menu;
import com.user.model.MenuKey;
import java.util.Date;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class MenuDaoImpl extends AbstractDao<MenuKey, Menu> implements MenuDao {

    @Override
    public Menu save(Menu menu) {
        saveOrUpdate(menu, menu.getKey());
        return menu;
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from Menu o";
        Date date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

}
