package com.h2.service;

import com.inventory.model.GradeDetail;
import com.inventory.model.GradeDetailKey;
import com.inventory.model.StockFormula;
import com.inventory.model.StockFormulaPrice;
import com.inventory.model.StockFormulaPriceKey;
import com.inventory.model.StockFormulaKey;
import com.inventory.model.StockFormulaQty;
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

    List<StockFormulaPrice> getFormulaDetail(String code, String compCode);

    String getMaxDate();

    String getMaxDateSFPrice();

    String getMaxDateSFQty();
    
    String getMaxDateGD();
}
