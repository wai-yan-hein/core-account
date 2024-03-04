package com.h2.dao;

import com.inventory.entity.StockFormulaQty;
import com.inventory.entity.StockFormulaQtyKey;
import java.util.List;

public interface StockFormulaQtyDao {

    StockFormulaQty save(StockFormulaQty s);

    boolean delete(StockFormulaQtyKey key);

    List<StockFormulaQty> getStockFormulaQty(String formulaCode, String compCode);

    String getMaxDate();
}
