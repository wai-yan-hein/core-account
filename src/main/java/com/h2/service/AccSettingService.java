
package com.h2.service;

import com.inventory.entity.AccKey;
import com.inventory.entity.AccSetting;
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
