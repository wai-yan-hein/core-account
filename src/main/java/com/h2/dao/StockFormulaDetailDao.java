package com.h2.dao;

import com.inventory.model.StockFormulaDetail;
import com.inventory.model.StockFormulaDetailKey;
import java.util.List;

public interface StockFormulaDetailDao {

    StockFormulaDetail save(StockFormulaDetail s);

    boolean delete(StockFormulaDetailKey key);

    List<StockFormulaDetail> getFormulaDetail(String code, String compCode);
}
