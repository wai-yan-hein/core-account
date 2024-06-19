/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.entity.RelationDetailKey;
import com.inventory.entity.UnitRelationDetail;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Repository
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RelationDetailRepo extends AbstractDao<RelationDetailKey, UnitRelationDetail> {

    public UnitRelationDetail save(UnitRelationDetail cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    public List<UnitRelationDetail> getRelationDetail(String relCode, String compCode) {
        String hsql = "select o from UnitRelationDetail o where o.key.compCode = '" + compCode + "' and o.key.relCode = '" + relCode + "'";
        return findHSQL(hsql);
    }

    public double getSmallestQty(String relCode, String unit, String compCode) {
        String sql = """
                   select smallest_qty
                   from unit_relation_detail
                   where comp_code =?
                   and rel_code = ?
                   and unit = ?
                   """;
        ResultSet rs = getResult(sql, compCode, relCode, unit);
        if (rs != null) {
            try {
                if (rs.next()) {
                    return rs.getDouble("smallest_qty");
                }
            } catch (SQLException e) {
                log.error("getSmallestUnit : " + e.getMessage());
            }
        }
        return 0;

    }

    public List<UnitRelationDetail> getSmallestUnit(String compCode) {
        List<UnitRelationDetail> list = new ArrayList<>();
        String sql = """
                   with row_position as
                   (
                   select rel_code,unit,row_number() over (partition by rel_code order by unique_id desc) as position
                   from unit_relation_detail
                   where comp_code = ?
                   )
                   select rel_code,unit
                   from row_position
                   where position =1
                   """;
        ResultSet rs = getResult(sql, compCode);
        if (rs != null) {
            try {
                while (rs.next()) {
                    UnitRelationDetail d = new UnitRelationDetail();
                    RelationDetailKey key = new RelationDetailKey();
                    key.setRelCode(rs.getString("rel_code"));
                    d.setKey(key);
                    d.setUnit(rs.getString("unit"));
                    list.add(d);
                }
            } catch (SQLException e) {
                log.error("getSmallestUnit : " + e.getMessage());
            }
        }
        return list;
    }

}
