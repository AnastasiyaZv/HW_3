package ru.stepup.data;

import io.qameta.allure.Step;

import java.util.Random;

public class RandomData {
    static Random random = new Random();

    @Step("Дополнительно. Создать уникальное наименование для товара")

    public static String createUniqueProductName() {
        return "product" + System.currentTimeMillis();
    }

    @Step("Дополнительно. Создать рандомно цену для товара")
    public static int randomInt(int a, int b) {
        return random.nextInt(a, b);
    }
}
