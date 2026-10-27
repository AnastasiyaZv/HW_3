package ru.stepup.tools;

import static ru.stepup.config.Config.BASE_CONFIG;
import static ru.stepup.config.Config.UI_CONFIG;

public class PrintConfig {
    //печать параметров конфигурации
    public void printConfig() {
        System.out.println("Browser baseUrl: " + BASE_CONFIG.baseUrl());
        System.out.println("Browser timeout: " + UI_CONFIG.timeout());
        System.out.println("Start productName: " + UI_CONFIG.productName());
        System.out.println("Start productPrice: " + UI_CONFIG.productPrice());
        System.out.println("Start invalidLogin: " + UI_CONFIG.invalidLogin());
        System.out.println("Start invalidPassword: " + UI_CONFIG.invalidPassword());
    }
}
