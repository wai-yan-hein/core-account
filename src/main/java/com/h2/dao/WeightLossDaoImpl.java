package com.h2.dao;

import com.inventory.model.WeightLossHis;
import com.inventory.model.WeightLossHisKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WeightLossDaoImpl extends AbstractDao<WeightLossHisKey, WeightLossHis> implements WeightLossDao {

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
}
