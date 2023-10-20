package com.h2.dao;

import com.inventory.model.StockFormulaQty;
import com.inventory.model.StockFormulaQtyKey;
import java.util.List;

public interface StockFormulaQtyDao {

    StockFormulaQty save(StockFormulaQty s);

    boolean delete(StockFormulaQtyKey key);

    List<StockFormulaQty> getStockFormulaQty(String formulaCode, String compCode);

    String getMaxDate();
}
