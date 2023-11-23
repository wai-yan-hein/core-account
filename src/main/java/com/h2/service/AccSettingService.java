
package com.h2.service;

import com.inventory.model.AccKey;
import com.inventory.model.AccSetting;
import java.util.List;

/**
 * @author pann
 */
public interface AccSettingService {

    List<AccSetting> findAll(String compCode);

    AccSetting save(AccSetting setting);

    AccSetting findByCode(AccKey key);

    String getMaxDate();
}
