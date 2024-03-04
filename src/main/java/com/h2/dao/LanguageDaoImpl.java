package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.Language;
import com.inventory.entity.LanguageKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class LanguageDaoImpl extends AbstractDao<LanguageKey, Language> implements LanguageDao {

    @Override
    public Language save(Language s) {
        s.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(s, s.getKey());
        return s;
    }

    @Override
    public List<Language> findAll(String compCode) {
        String hsql = "select o from Language o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(LanguageKey key) {
        remove(key);
        return 1;
    }

    @Override
    public Language findById(LanguageKey id) {
        return getByKey(id);
    }

    @Override
    public List<Language> search(String des) {
        String strSql = "";

        if (!des.equals("-")) {
            strSql = "o.lanValue like '%" + des + "%'";
        }

        if (strSql.isEmpty()) {
            strSql = "select o from Language o";
        } else {
            strSql = "select o from Language o where " + strSql;
        }

        return findHSQL(strSql);
    }

    @Override
    public List<Language> unUpload() {
        String hsql = "select o from Language o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Language o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<Language> getLanguage(LocalDateTime updatedDate) {
        String hsql = "select o from Language o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
