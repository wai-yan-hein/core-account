package com.h2.service;

import com.inventory.model.Language;
import com.inventory.model.LanguageKey;
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
