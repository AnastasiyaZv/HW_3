package ru.stepup.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "classpath:config.properties",
        "system:properties",
        "system:env"
})

public interface ApiConfig extends Config {
    @Key("product.name.api")
    String productName();

    @Key("product.price.api")
    int productPrice();
}
