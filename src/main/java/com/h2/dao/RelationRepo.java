/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.RelationKey;
import com.inventory.entity.StockUnit;
import com.inventory.entity.StockUnitKey;
import com.inventory.entity.UnitRelation;
import com.inventory.entity.UnitRelationDetail;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
public class RelationRepo extends AbstractDao<RelationKey, UnitRelation> {

    private final RelationDetailRepo detailRepo;

    public UnitRelation save(UnitRelation cat) {
        List<UnitRelationDetail> detail = cat.getDetailList();
        if (detail != null && !detail.isEmpty()) {
            detail.forEach((d) -> {
                detailRepo.save(d);
            });
        }
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from UnitRelation o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    public List<UnitRelation> findAll(String compCode) {
        String hsql = "select o from UnitRelation o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    public UnitRelation findByKey(RelationKey key) {
        return getByKey(key);
    }

    public List<StockUnit> getUnitByRelation(String relCode, String compCode) {
        List<StockUnit> list = new ArrayList<>();
        String sql = """
                   select unit,comp_code
                   from unit_relation_detail 
                   where rel_code = ?
                   and comp_code = ?
                   order by unique_id
                   """;
        ResultSet rs = getResult(sql, relCode, compCode);
        try {
            while (rs.next()) {
                StockUnit obj = new StockUnit();
                StockUnitKey key = new StockUnitKey();
                key.setUnitCode(rs.getString("unit"));
                key.setCompCode(rs.getString("comp_code"));
                obj.setKey(key);
                list.add(obj);
            }
        } catch (SQLException e) {
            log.error("getUnit : " + e.getMessage());
        }
        return list;
    }

    public List<UnitRelationDetail> getRelationDetail(String relCode, String compCode) {
        return detailRepo.getRelationDetail(relCode, compCode);
    }

    public List<UnitRelationDetail> getSmallestUnit(String compCode) {
        return detailRepo.getSmallestUnit(compCode);
    }

    public double getSmallestQty(String relCode, String unit, String compCode) {
        return detailRepo.getSmallestQty(relCode, unit, compCode);
    }

}
