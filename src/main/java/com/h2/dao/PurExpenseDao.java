package com.h2.dao;

import com.inventory.entity.PurExpense;
import java.util.List;

public interface PurExpenseDao {
    PurExpense save(PurExpense p);

    List<PurExpense> search(String vouNo, String compCode);
}
