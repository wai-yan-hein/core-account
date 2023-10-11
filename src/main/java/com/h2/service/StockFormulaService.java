package com.h2.service;

import com.inventory.model.StockFormula;
import com.inventory.model.StockFormulaPrice;
import com.inventory.model.StockFormulaPriceKey;
import com.inventory.model.StockFormulaKey;
import java.util.List;

public interface StockFormulaService {

    StockFormula save(StockFormula s);

    boolean delete(StockFormulaKey key);

    List<StockFormula> getFormula(String compCode);

    StockFormulaPrice save(StockFormulaPrice s);

    boolean delete(StockFormulaPriceKey key);

    List<StockFormulaPrice> getFormulaDetail(String code, String compCode);

    String getMaxDate();
}
