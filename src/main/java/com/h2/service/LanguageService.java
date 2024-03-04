package com.h2.service;

import com.inventory.entity.Language;
import com.inventory.entity.LanguageKey;
import java.time.LocalDateTime;
import java.util.List;

public interface LanguageService {

    Language save(Language status);

    List<Language> findAll(String compCode);

    int delete(LanguageKey key);

    Language findById(LanguageKey key);

    List<Language> search(String description);

    List<Language> unUpload();

    String getMaxDate();

    List<Language> getLanguage(LocalDateTime updatedDate);
}
