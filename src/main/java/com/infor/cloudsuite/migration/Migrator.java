package com.infor.cloudsuite.migration;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import com.infor.cloudsuite.platform.components.SettingsProvider;

/**
 * User: bcrow
 * Date: 1/10/12 11:34 AM
 */
@Component
public abstract class Migrator {

    @Resource
    private SettingsProvider settingsProvider;
    @Resource(name="settings")
    private PropertyPlaceholderConfigurer settings;

    public abstract void migrate();
}
