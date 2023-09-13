package com.h2.service;

import com.common.FilterObject;
import com.common.Util1;
import com.h2.dao.ProcessHisDao;
import com.h2.dao.ProcessHisDetailDao;
import com.h2.dao.SeqDao;
import com.inventory.model.ProcessHis;
import com.inventory.model.ProcessHisDetail;
import com.inventory.model.ProcessHisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.context.annotation.Lazy;

@Lazy
@Service
@Transactional
public class ProcessHisServiceImpl implements ProcessHisService {

    @Autowired
    private ProcessHisDao dao;
    @Autowired
    private ProcessHisDetailDao phDao;
    @Autowired
    private SeqDao seqDao;

    @Override
    public ProcessHis save(ProcessHis ph) {
        ph.setVouDate(Util1.toDateTime(ph.getVouDate()));
        if (Util1.isNullOrEmpty(ph.getKey().getVouNo())) {
            ph.getKey().setVouNo(getVoucherNo(ph.getMacId(), ph.getKey().getCompCode()));
        }
        List<ProcessHisDetail> list = ph.getListDetail();
        for (int i = 0; i < list.size(); i++) {
            ProcessHisDetail cSd = list.get(i);
            if (cSd.getKey().getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        ProcessHisDetail pSd = list.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                cSd.getKey().setVouNo(ph.getKey().getVouNo());
                phDao.save(cSd);
            }
        }
        return dao.save(ph);
    }

    @Override
    public ProcessHis findById(ProcessHisKey key) {
        return dao.findById(key);
    }

    @Override
    public List<ProcessHis> search(String fromDate, String toDate, String vouNo, String processNo, String remark,
            String stockCode, String pt, String locCode, boolean finish, boolean deleted, String compCode, Integer deptId) {
        return dao.search(fromDate, toDate, vouNo, processNo, remark, stockCode, pt, locCode, finish, deleted, compCode, deptId);
    }

    @Override
    public void delete(ProcessHisKey key) {
        dao.delete(key);
    }

    @Override
    public void restore(ProcessHisKey key) {
        dao.restore(key);
    }

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "PROCESS", period, compCode);
        return String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    @Override
    public List<ProcessHis> unUpload(String compCode) {
        return dao.unUpload(compCode);
    }

    @Override
    public ProcessHis updateACK(ProcessHisKey key) {
        return dao.updateACK(key);
    }
    
    @Override
    public List<ProcessHis> getProcess(FilterObject filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        boolean finished = filter.isFinished();
        String processNo = Util1.isNull(filter.getProcessNo(), "-");
        String pt = filter.getVouStatus();
        return dao.search(fromDate, toDate, vouNo, processNo, remark, stockCode, pt, locCode, finished, deleted, compCode, deptId);
    }
}
