
package com.h2.dao;
import com.inventory.model.AccKey;
import com.inventory.model.AccSetting;

import java.util.List;

/**
 * @author pann
 */
public interface AccSettingDao {

    List<AccSetting> findAll(String comCope);

    AccSetting save(AccSetting setting);

    AccSetting findByCode(AccKey key);
    
    String getMaxDate();

}
