package com.h2.dao;

import com.user.model.DateLock;
import java.util.List;

public interface DateLockDao {

    DateLock save(DateLock dl);

    List<DateLock> findAll(String compCode);

    String getMaxDate();

}
