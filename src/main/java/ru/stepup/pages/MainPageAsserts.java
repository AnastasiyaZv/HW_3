package ru.stepup.pages;

import io.qameta.allure.Step;
import org.assertj.core.api.AbstractAssert;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static ru.stepup.pages.MainPage.NAME_PRODUCT_CSS;

public class MainPageAsserts extends AbstractAssert<MainPageAsserts, MainPage> {

    public MainPageAsserts(MainPage actual) {
        super(actual, MainPageAsserts.class);
    }

    @Step("Витрина. Проверка, что товар добавлен на кнопке [В корзину].")
    public MainPageAsserts hasBasketButtonCount(String count) {
        actual.basketButtonCount
                .as("Товар в корзине отсутствует")
                .shouldHave(text(count));
        return this;
    }

    @Step("Витрина. Проверка, что открыта страница 'Витрина'.")
    public MainPageAsserts hasHeaderMainPage(String expectedHeader) {
        actual.headerMainPage
                .as(String.format("Открыта не страница %s", expectedHeader))
                .shouldHave(text(expectedHeader));
        return this;
    }

    @Step("Витрина. Проверка, что товар с наименованием '{name}' отображается." )
    public MainPageAsserts productIsVisible(String name) {
        actual.nameProduct = $(String.format(NAME_PRODUCT_CSS, name));
        actual.nameProduct
                .as("Добавленный товар не найден на витрине")
                .shouldBe(visible);
        return this;
    }

    public MainPage page() {
        return new MainPage();
    }
}
