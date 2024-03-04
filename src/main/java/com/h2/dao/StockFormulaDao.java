package com.h2.dao;

import com.inventory.entity.StockFormula;
import com.inventory.entity.StockFormulaKey;
import java.util.List;

public interface StockFormulaDao {
    StockFormula save(StockFormula s);

    boolean delete(StockFormulaKey key);

    List<StockFormula> getFormula(String compCode);
    
    String getMaxDate();
    
    StockFormula find(StockFormulaKey key);
}
