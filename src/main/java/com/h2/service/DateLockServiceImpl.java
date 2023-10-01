package com.h2.service;

import com.h2.dao.DateLockDao;
import com.user.model.DateLock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class DateLockServiceImpl implements DateLockService {

    private final DateLockDao dao;

    @Override
    public DateLock save(DateLock dl) {
        return dao.save(dl);
    }

    @Override
    public List<DateLock> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }
}
