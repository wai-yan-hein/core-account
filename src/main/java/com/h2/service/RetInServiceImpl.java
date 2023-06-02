/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.common.Util1;
import com.h2.dao.RetInDao;
import com.h2.dao.RetInDetailDao;
import com.h2.dao.SeqDao;
import com.inventory.model.RetInHis;
import com.inventory.model.RetInHisDetail;
import com.inventory.model.RetInHisKey;
import com.inventory.model.RetInKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Wai Yan
 */
@Service
@Transactional
@Slf4j
public class RetInServiceImpl implements RetInService {

    @Autowired
    private RetInDao rDao;
    @Autowired
    private RetInDetailDao sdDao;
    @Autowired
    private SeqDao seqDao;

    @Override
    public RetInHis save(RetInHis rin) {
        rin.setVouDate(Util1.toDateTime(rin.getVouDate()));
        if (Util1.isNullOrEmpty(rin.getKey().getVouNo())) {
            rin.getKey().setVouNo(getVoucherNo(rin.getKey().getDeptId(), rin.getMacId(), rin.getKey().getCompCode()));
        }
        List<RetInHisDetail> listSD = rin.getListRD();
        List<RetInKey> listDel = rin.getListDel();
        String vouNo = rin.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(key -> sdDao.delete(key));
        }
        for (int i = 0; i < listSD.size(); i++) {
            RetInHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                RetInKey key = new RetInKey();
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
                        RetInHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                sdDao.save(cSd);
            }
            rDao.save(rin);
            rin.setListRD(listSD);
        }
        return rin;
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "RETURN_IN", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    @Override
    public RetInHis update(RetInHis ri) {
        return rDao.save(ri);
    }

    @Override
    public List<RetInHis> search(String fromDate, String toDate, String cusCode, String vouNo, String remark, String userCode) {
        return rDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public RetInHis findById(RetInHisKey id) {
        return rDao.findById(id);
    }

    @Override
    public void delete(RetInHisKey key) throws Exception {
        rDao.delete(key);
    }

    @Override
    public void restore(RetInHisKey key) throws Exception {
        rDao.restore(key);
    }

    @Override
    public List<RetInHis> unUploadVoucher(String syncDate) {
        return rDao.unUploadVoucher(syncDate);
    }

    @Override
    public List<RetInHis> unUpload(String syncDate) {
        return rDao.unUpload(syncDate);
    }

    @Override
    public Date getMaxDate() {
        return rDao.getMaxDate();
    }

    @Override
    public List<RetInHis> search(String updatedDate, List<String> keys) {
        return rDao.search(updatedDate, keys);
    }

    @Override
    public void truncate(RetInHisKey key) {
        rDao.truncate(key);
    }

}
