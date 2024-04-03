/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.Location;
import com.inventory.entity.LocationKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
@Slf4j
public class LocationDaoImpl extends AbstractDao<LocationKey, Location> implements LocationDao {

    @Override
    public Location save(Location cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Location o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<Location> findAll(String whCode, String compCode) {
        List<Location> list = new ArrayList<>();

        String sql = """
                select l.*,w.description
                from location l left join warehouse w
                on l.warehouse_code = w.code
                and l.comp_code = w.comp_code
                where l.deleted = false
                and l.active = true
                and l.comp_code =?
                and (l.warehouse_code =? or'-'=?)
                """;
        try {
            ResultSet rs = getResult(sql, compCode, whCode, whCode);
            while (rs.next()) {
                Location l = new Location();
                LocationKey key = new LocationKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setLocCode(rs.getString("loc_code"));
                l.setKey(key);
                l.setDeptId(rs.getInt("dept_id"));
                l.setMacId(rs.getInt("mac_id"));
                l.setLocName(rs.getString("loc_name"));
                l.setCalcStock(rs.getBoolean("calc_stock"));
                l.setCreatedBy(rs.getString("created_by"));
                l.setUpdatedBy(rs.getString("updated_by"));
                l.setUserCode(rs.getString("user_code"));
                l.setDeptCode(rs.getString("dept_code"));
                l.setCashAcc(rs.getString("cash_acc"));
                l.setDeleted(rs.getBoolean("deleted"));
                l.setActive(rs.getBoolean("active"));
                l.setWareHouseCode(rs.getString("warehouse_code"));
                l.setWareHouseName(rs.getString("description"));
                list.add(l);
            }
        } catch (SQLException e) {
            log.error("findAll : " + e.getMessage());
        }
        //loc_code, comp_code, dept_id, mac_id, loc_name, parent, calc_stock,
        // updated_date, location_type, created_date, created_by, updated_by,
        // user_code, intg_upd_status, map_dept_id, dept_code, cash_acc, deleted, active, warehouse_code
        return list;
    }

    @Override
    public Location find(LocationKey key) {
        return getByKey(key);
    }

}
