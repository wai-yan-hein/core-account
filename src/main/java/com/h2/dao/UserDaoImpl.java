/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.h2.service.MacPropertyService;
import com.h2.service.RolePropertyService;
import com.h2.service.SystemPropertyService;
import com.user.model.AppUser;
import com.user.model.MachineProperty;
import com.user.model.RoleProperty;
import com.user.model.SysProperty;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
@Slf4j
public class UserDaoImpl extends AbstractDao<String, AppUser> implements UserDao {

    @Autowired
    private RolePropertyService rpService;
    @Autowired
    private MacPropertyService mpService;
    @Autowired
    private SystemPropertyService spService;

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
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public AppUser login(String userName, String password) {
        String sql = "select o from AppUser o where o.userShortName = '" + userName + "' and o.password = '" + password + "'";
        List<AppUser> list = findHSQL(sql);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public HashMap<String, String> getProperty(String compCode, String roleCode, Integer macId) {
        HashMap<String, String> hm = new HashMap<>();
        List<SysProperty> systemProperty = spService.getSystemProperty(compCode);
        if (!systemProperty.isEmpty()) {
            for (SysProperty p : systemProperty) {
                hm.put(p.getKey().getPropKey(), p.getPropValue());
            }
        }

        List<RoleProperty> roleProperty = rpService.getRoleProperty(roleCode);
        if (!roleProperty.isEmpty()) {
            for (RoleProperty rp : roleProperty) {
                String value = rp.getPropValue();
                if (!Util1.isNullOrEmpty(value) || !value.equals("-")) {
                    hm.put(rp.getKey().getPropKey(),value);
                }
            }
        }
        List<MachineProperty> machineProperties = mpService.getMacProperty(macId);
        if (!machineProperties.isEmpty()) {
            for (MachineProperty p : machineProperties) {
                String value = p.getPropValue();
                if (!Util1.isNullOrEmpty(value) || !value.equals("-")) {
                    hm.put(p.getKey().getPropKey(),value);
                }
            }
        }
        return hm;
    }

}
