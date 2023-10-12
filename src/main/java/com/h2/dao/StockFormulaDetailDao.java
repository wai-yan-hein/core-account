package com.h2.dao;

import com.inventory.model.StockFormulaPrice;
import com.inventory.model.StockFormulaPriceKey;
import java.util.List;

public interface StockFormulaDetailDao {

    StockFormulaPrice save(StockFormulaPrice s);

    boolean delete(StockFormulaPriceKey key);

    List<StockFormulaPrice> getFormulaDetail(String code, String compCode);
}
