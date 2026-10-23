package ru.stepup.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "classpath:config.properties",
        "system:properties",
        "system:env"
})

public interface ProjectConfig extends Config {
    @Key("base.url")
    String baseUrl();

    @Key("admin.login")
    String adminLogin();

    @Key("admin.password")
    String adminPassword();

    @Key("logging.enabled")
    boolean loggingEnabled();

    @Key("logging.level")
    String loggingLevel();
}
