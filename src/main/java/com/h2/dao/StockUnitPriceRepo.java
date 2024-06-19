/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.StockUnitPrice;
import com.inventory.entity.StockUnitPriceKey;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
public class StockUnitPriceRepo extends AbstractDao<StockUnitPriceKey, StockUnitPrice> {

    private final NamedParameterJdbcTemplate template;

    public StockUnitPrice save(StockUnitPrice p) {
        saveOrUpdate(p, p.getKey());
        return p;
    }

    public StockUnitPrice findById(StockUnitPriceKey key) {
        return getByKey(key);
    }

    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from StockUnitPrice o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    public List<StockUnitPrice> getStockUnitPrice(String stockCode, String compCode) {
        String hsql = "select o from StockUnitPrice o where o.key.compCode='" + compCode + "' and o.key.stockCode ='" + stockCode + "' order by o.uniqueId";
        return findHSQL(hsql);
    }

}
