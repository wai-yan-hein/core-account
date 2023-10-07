package com.h2.dao;

import com.inventory.model.StockFormula;
import com.inventory.model.StockFormulaKey;
import java.util.List;

public interface StockFormulaDao {
    StockFormula save(StockFormula s);

    boolean delete(StockFormulaKey key);

    List<StockFormula> getFormula(String compCode);
    
    String getMaxDate();
}
