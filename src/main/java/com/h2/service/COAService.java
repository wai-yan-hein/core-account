package com.h2.service;
import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import java.util.Date;
import java.util.List;

public interface COAService {

    ChartOfAccount save(ChartOfAccount coa);

    ChartOfAccount save(ChartOfAccount coa, String opDate) throws Exception;

    ChartOfAccount findById(COAKey id);

    List<ChartOfAccount> getCOA(String compCode);

    List<ChartOfAccount> getCOA(String headCode, String compCode);

    Boolean delete(COAKey key);

    List<ChartOfAccount> searchCOA(String str, Integer level, String compCode);

    List<ChartOfAccount> getCOATree(String compCode);

    List<ChartOfAccount> getCOAChild(String parentCode, String compCode);

    List<ChartOfAccount> getTraderCOA(String compCode);

    List<ChartOfAccount> search(String updatedDate);

    List<ChartOfAccount> unUpload();

    List<ChartOfAccount> findAllActive(String compCode);

    Date getMaxDate();
}
