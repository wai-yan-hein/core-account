package com.h2.dao;

import com.common.Util1;
import com.user.model.DateLock;
import com.user.model.DateLockKey;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DateLockDaoImpl extends AbstractDao<DateLockKey, DateLock> implements DateLockDao {

    @Override
    public DateLock save(DateLock dl) {
        saveOrUpdate(dl, dl.getKey());
        return dl;
    }

    @Override
    public List<DateLock> findAll(String compCode) {
        String hsql = "select o from DateLock o where o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from DateLock o";
        LocalDateTime date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

}
