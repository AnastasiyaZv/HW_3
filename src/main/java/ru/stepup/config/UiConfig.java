package ru.stepup.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "classpath:config.properties",
        "system:properties",
        "system:env"
})

public interface UiConfig extends Config {
    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("browser.timeout")
    int timeout();

    @Key("product.name.ui")
    String productName();

    @Key("product.price.ui")
    String productPrice();

    @Key("invalid.login")
    String invalidLogin();

    @Key("invalid.password")
    String invalidPassword();
}
