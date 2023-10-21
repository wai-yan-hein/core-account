package com.h2.dao;

import com.inventory.model.StockFormulaPrice;
import com.inventory.model.StockFormulaPriceKey;
import java.util.List;

public interface StockFormulaPriceDao {

    StockFormulaPrice save(StockFormulaPrice s);

    boolean delete(StockFormulaPriceKey key);

    List<StockFormulaPrice> getStockFormulaPrice(String formulaCode, String compCode);

    String getMaxDate();
}
