/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.common.Util1;
import com.h2.dao.RetOutDao;
import com.h2.dao.RetOutDetailDao;
import com.h2.dao.SeqDao;
import com.inventory.model.RetOutHis;
import com.inventory.model.RetOutHisDetail;
import com.inventory.model.RetOutHisKey;
import com.inventory.model.RetOutKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Wai Yan
 */
@Service
@Transactional
@Slf4j
public class RetOutServiceImpl implements RetOutService {

    @Autowired
    private RetOutDao rDao;
    @Autowired
    private RetOutDetailDao rd;
    @Autowired
    private SeqDao seqDao;

    @Override
    public RetOutHis save(RetOutHis rin) {
        rin.setVouDate(Util1.toDateTime(rin.getVouDate()));
        if (Util1.isNullOrEmpty(rin.getKey().getVouNo())) {
            rin.getKey().setVouNo(getVoucherNo(rin.getKey().getDeptId(), rin.getMacId(), rin.getKey().getCompCode()));
        }
        List<RetOutHisDetail> listSD = rin.getListRD();
        List<RetOutKey> listDel = rin.getListDel();
        String vouNo = rin.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(key -> rd.delete(key));
        }
        for (int i = 0; i < listSD.size(); i++) {
            RetOutHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                RetOutKey key = new RetOutKey();
                key.setDeptId(rin.getKey().getDeptId());
                key.setCompCode(rin.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        RetOutHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                rd.save(cSd);
            }
        }
        rDao.save(rin);
        rin.setListRD(listSD);
        return rin;
    }

    @Override
    public RetOutHis update(RetOutHis ro) {
        return rDao.save(ro);
    }

    @Override
    public List<RetOutHis> search(String fromDate, String toDate, String cusCode, String vouNo, String remark, String userCode) {
        return rDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public RetOutHis findById(RetOutHisKey id) {
        return rDao.findById(id);
    }

    @Override
    public void delete(RetOutHisKey key) throws Exception {
        rDao.delete(key);
    }

    @Override
    public void restore(RetOutHisKey key) throws Exception {
        rDao.restore(key);
    }

    @Override
    public List<RetOutHis> unUploadVoucher(String syncDate) {
        return rDao.unUploadVoucher(syncDate);
    }

    @Override
    public List<RetOutHis> search(String updatedDate, List<String> keys) {
        return rDao.search(updatedDate, keys);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "RETURN_OUT", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    @Override
    public RetOutHis updateACK(RetOutHisKey key) {
        return rDao.updateACK(key);
    }

}
