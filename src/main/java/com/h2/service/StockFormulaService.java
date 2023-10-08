package com.h2.service;

import com.inventory.model.StockFormula;
import com.inventory.model.StockFormulaDetail;
import com.inventory.model.StockFormulaDetailKey;
import com.inventory.model.StockFormulaKey;
import java.util.List;

public interface StockFormulaService {

    StockFormula save(StockFormula s);

    boolean delete(StockFormulaKey key);

    List<StockFormula> getFormula(String compCode);

    StockFormulaDetail save(StockFormulaDetail s);

    boolean delete(StockFormulaDetailKey key);

    List<StockFormulaDetail> getFormulaDetail(String code, String compCode);

    String getMaxDate();
}
