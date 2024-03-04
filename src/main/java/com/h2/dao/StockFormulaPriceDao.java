package com.h2.dao;

import com.inventory.entity.StockFormulaPrice;
import com.inventory.entity.StockFormulaPriceKey;
import java.util.List;

public interface StockFormulaPriceDao {

    StockFormulaPrice save(StockFormulaPrice s);

    boolean delete(StockFormulaPriceKey key);

    List<StockFormulaPrice> getStockFormulaPrice(String formulaCode, String compCode);

    String getMaxDate();
}
