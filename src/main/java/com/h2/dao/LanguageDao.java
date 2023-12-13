package com.h2.dao;

import com.inventory.model.Language;
import com.inventory.model.LanguageKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface LanguageDao {

    Language save(Language language);

    List<Language> findAll(String compCode);

    int delete(LanguageKey key);

    Language findById(LanguageKey id);

    List<Language> search(String des);

    List<Language> unUpload();

    String getMaxDate();

    List<Language> getLanguage(LocalDateTime updatedDate);
}
