package com.h2.service;

import com.inventory.entity.GradeDetail;
import com.inventory.entity.GradeDetailKey;
import com.inventory.entity.StockFormula;
import com.inventory.entity.StockFormulaPrice;
import com.inventory.entity.StockFormulaPriceKey;
import com.inventory.entity.StockFormulaKey;
import com.inventory.entity.StockFormulaQty;
import java.util.List;

public interface StockFormulaService {

    StockFormula save(StockFormula s);

    StockFormula find(StockFormulaKey key);

    boolean delete(StockFormulaKey key);

    boolean delete(GradeDetailKey key);

    List<StockFormula> getFormula(String compCode);

    StockFormulaPrice save(StockFormulaPrice s);

    StockFormulaQty save(StockFormulaQty s);

    GradeDetail save(GradeDetail s);

    boolean delete(StockFormulaPriceKey key);

    String getMaxDate();

    String getMaxDateSFPrice();

    String getMaxDateSFQty();

    String getMaxDateGD();

    List<StockFormulaPrice> getStockFormulaPrice(String formulaCode, String compCode);

    List<StockFormulaQty> getStockFormulaQty(String formulaCode, String compCode);

    List<GradeDetail> getGradeDetail(String formulaCode, String criteriaCode, String compCode);

}
