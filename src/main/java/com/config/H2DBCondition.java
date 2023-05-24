/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.config;

import com.common.Util1;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 *
 * @author Lenovo
 */
public class H2DBCondition implements Condition {

    @Override
    public boolean matches(ConditionContext cc, AnnotatedTypeMetadata atm) {
        String local = cc.getEnvironment().getProperty("local.database");
        return Util1.getBoolean(local);
    }
}
