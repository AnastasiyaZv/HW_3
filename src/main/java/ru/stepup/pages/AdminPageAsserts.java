package ru.stepup.pages;

import io.qameta.allure.Step;
import org.assertj.core.api.AbstractAssert;

import static com.codeborne.selenide.Condition.*;

public class AdminPageAsserts extends AbstractAssert<AdminPageAsserts, AdminPage> {

    public AdminPageAsserts(AdminPage actual) {
        super(actual, AdminPageAsserts.class);
        as("Страница администрирования");
    }

    @Step("Администрирование. Проверка, что открыта страница 'Администрирование'.")
    public AdminPageAsserts hasHeader(String expectedHeader) {
        actual.headerAdminPage
                .as("Открыта не страница администрирования")
                .shouldHave(exactText(expectedHeader));
        return this;
    }

    @Step("Администрирование. Проверка, что отображается нотификация '{expectedText}'.")
    public AdminPageAsserts hasMessageCreateProduct(String expectedText) {
        actual.messageCreateProduct
                .as("Товар не добавлен")
                .shouldHave(exactText(expectedText));
        return this;
    }

    @Step("Администрирование. Проверка, что карточка товара отображается по имени {nameProduct}")
    public AdminPageAsserts hasAddProduct(String nameProduct) {
        actual.cardProductName
                .as("Созданный товар не отображается")
                .shouldHave(exactValue(nameProduct));
        return this;
    }

    @Step("Администрирование. Проверка, что наименование товара изменено.")
    public AdminPageAsserts hasFieldProductName(String name) {
        actual.cardProductName
                .as("Имя не изменено")
                .shouldHave(value(name));
        return this;
    }

    @Step("Администрирование. Проверка, что цена товара изменена.")
    public AdminPageAsserts hasFieldProductPrice(String price) {
        actual.cardProductPrice
                .as("Цена не изменена")
                .shouldHave(value(price));
        return this;
    }

    public AdminPage page() {
        return new AdminPage();
    }
}
