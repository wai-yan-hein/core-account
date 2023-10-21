package com.h2.dao;

import com.common.Util1;
import com.inventory.model.StockFormula;
import com.inventory.model.StockFormulaKey;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class StockFormulaDaoImpl extends AbstractDao<StockFormulaKey, StockFormula> implements StockFormulaDao {

    @Override
    public StockFormula save(StockFormula s) {
        saveOrUpdate(s, s.getKey());
        return s;
    }

    @Override
    public boolean delete(StockFormulaKey key) {
        StockFormula f = getByKey(key);
        if (f != null) {
            f.setDeleted(true);
            f.setUpdatedDate(LocalDateTime.now());
            update(f);
            return true;
        }
        return false;
    }

    @Override
    public List<StockFormula> getFormula(String compCode) {
        String hsql = "select o from StockFormula o where o.key.compCode = '" + compCode + "' and o.deleted =false order by o.userCode,o.formulaName";
        return findHSQL(hsql);
    }
    
    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from StockFormula o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }
    
    @Override
    public StockFormula find(StockFormulaKey key) {
        return getByKey(key);
    }
}
