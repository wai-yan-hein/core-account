package com.h2.dao;

import com.inventory.model.Pattern;
import com.inventory.model.PatternKey;
import java.util.List;

public interface PatternDao {

    Pattern findByCode(PatternKey key);

    Pattern save(Pattern pattern);

    void delete(Pattern pattern);

    List<Pattern> search(String stockCode, String compCode);

    List<Pattern> unUpload();

    String getMaxDate();
}
