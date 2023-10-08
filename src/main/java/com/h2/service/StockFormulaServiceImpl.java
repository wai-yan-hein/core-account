package com.h2.service;

import com.common.Util1;
import com.h2.dao.StockFormulaDao;
import com.h2.dao.StockFormulaDetailDao;
import com.inventory.model.StockFormula;
import com.inventory.model.StockFormulaDetail;
import com.inventory.model.StockFormulaDetailKey;
import com.inventory.model.StockFormulaKey;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockFormulaServiceImpl implements StockFormulaService {

    private final StockFormulaDao formulaDao;
    private final StockFormulaDetailDao formulaDetailDao;
    @Autowired
    private SeqService seqService;

    @Override
    public StockFormula save(StockFormula s) {
        if (Util1.isNullOrEmpty(s.getKey().getFormulaCode())) {
            s.getKey().setFormulaCode(getCode(s.getKey().getCompCode()));
            s.setCreatedDate(Util1.getTodayLocalDateTime());
        } else {
            s.setUpdatedDate(Util1.getTodayLocalDateTime());
        }
        List<StockFormulaDetail> listSD = s.getListDtl();
        String vouNo = s.getKey().getFormulaCode();
        if (listSD != null) {
            for (int i = 0; i < listSD.size(); i++) {
                StockFormulaDetail cSd = listSD.get(i);
                if (Util1.isNullOrEmpty(cSd.getKey())) {
                    StockFormulaDetailKey key = new StockFormulaDetailKey();
                    key.setCompCode(s.getKey().getCompCode());
                    key.setFormulaCode(vouNo);
                    key.setUniqueId(0);
                    cSd.setKey(key);
                }
                if (cSd.getKey().getFormulaCode() != null) {
                    if (cSd.getKey().getUniqueId() == 0) {
                        if (i == 0) {
                            cSd.getKey().setUniqueId(1);
                        } else {
                            StockFormulaDetail pSd = listSD.get(i - 1);
                            cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                        }
                    }
                    formulaDetailDao.save(cSd);
                }
            }
        }
        formulaDao.save(s);
        return s;
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "StockFormula", "-", compCode);
        return String.format("%0" + 5 + "d", seqNo);
    }

    @Override
    public boolean delete(StockFormulaKey key) {
        return formulaDao.delete(key);
    }

    @Override
    public List<StockFormula> getFormula(String compCode) {
        return formulaDao.getFormula(compCode);
    }

    @Override
    public StockFormulaDetail save(StockFormulaDetail s) {
        return formulaDetailDao.save(s);
    }

    @Override
    public boolean delete(StockFormulaDetailKey key) {
        return formulaDetailDao.delete(key);
    }

    @Override
    public List<StockFormulaDetail> getFormulaDetail(String code, String compCode) {
        return formulaDetailDao.getFormulaDetail(code, compCode);
    }

    @Override
    public String getMaxDate() {
        return formulaDao.getMaxDate();
    }
}
