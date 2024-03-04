package com.h2.service;

import com.inventory.entity.Pattern;
import com.inventory.entity.PatternKey;
import java.util.List;

public interface PatternService {

    Pattern findByCode(PatternKey key);

    Pattern save(Pattern pattern);

    List<Pattern> search(String stockCode, String compCode);

    void delete(Pattern pattern);

    List<Pattern> unUpload();

    String getMaxDate();

}
