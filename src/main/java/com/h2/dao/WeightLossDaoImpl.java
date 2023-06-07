package com.h2.dao;

import com.inventory.model.WeightLossHis;
import com.inventory.model.WeightLossHisKey;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class WeightLossDaoImpl extends AbstractDao<WeightLossHisKey, WeightLossHis> implements WeightLossDao {

    @Autowired
    private WeightLossHisDetailDao dao;

    @Override
    public WeightLossHis save(WeightLossHis l) {
        saveOrUpdate(l, l.getKey());
        return l;
    }

    @Override
    public WeightLossHis findById(WeightLossHisKey key) {
        return getByKey(key);
    }

    @Override
    public void delete(WeightLossHisKey key) {
        String sql = "update weight_loss_his set deleted =1 where vou_no = '" + key.getVouNo() + "' and comp_code =" + key.getCompCode() + " and dept_id =" + key.getDeptId() + " ";
        execSql(sql);
    }

    @Override
    public void restore(WeightLossHisKey key) {
        String sql = "update weight_loss_his set deleted =0 where vou_no = '" + key.getVouNo() + "' and comp_code =" + key.getCompCode() + " and dept_id =" + key.getDeptId() + " ";
        execSql(sql);
    }

    @Override
    public List<WeightLossHis> search(String fromDate, String toDate, String locCode, String compCode, Integer deptId) {
        return null;
    }

    @Override
    public WeightLossHis updateACK(WeightLossHisKey key) {
        WeightLossHis wh = getByKey(key);
        wh.setIntgUpdStatus("ACK");
        saveOrUpdate(wh, key);
        return wh;
    }

    @Override
    public List<WeightLossHis> unUpload(String compCode) {
        String hsql = "select o from WeightLossHis o where o.key.compCode = '" + compCode + "' and o.intgUpdStatus is null";
        List<WeightLossHis> list = findHSQL(hsql);
        list.forEach((s) -> {
            s.setListDetail(dao.search(s.getKey().getVouNo(),
                    s.getKey().getCompCode(), s.getKey().getDeptId()));
        });
        return list;
    }
}
