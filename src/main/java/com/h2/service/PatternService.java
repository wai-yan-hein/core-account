package com.h2.service;

import com.inventory.model.Pattern;
import com.inventory.model.PatternKey;
import java.util.List;

public interface PatternService {

    Pattern findByCode(PatternKey key);

    Pattern save(Pattern pattern);

    List<Pattern> search(String stockCode, String compCode);

    void delete(Pattern pattern);

    List<Pattern> unUpload();

    String getMaxDate();

}
