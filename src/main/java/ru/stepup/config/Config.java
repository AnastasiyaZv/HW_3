package ru.stepup.config;

import org.aeonbits.owner.ConfigFactory;

public class Config {

    public static final ProjectConfig BASE_CONFIG = ConfigFactory.create(ProjectConfig.class);
    public static final ApiConfig API_CONFIG = ConfigFactory.create(ApiConfig.class);
    public static final UiConfig UI_CONFIG = ConfigFactory.create(UiConfig.class);
}
