package com.h2.dao;

import com.inventory.entity.Pattern;
import com.inventory.entity.PatternKey;
import java.util.List;

public interface PatternDao {

    Pattern findByCode(PatternKey key);

    Pattern save(Pattern pattern);

    void delete(Pattern pattern);

    List<Pattern> search(String stockCode, String compCode);

    List<Pattern> unUpload();

    String getMaxDate();
}
