package com.h2.service;

import com.user.model.DateLock;
import java.util.List;

public interface DateLockService {

    DateLock save(DateLock dl);

    List<DateLock> findAll(String compCode);

    String getMaxDate();

}
