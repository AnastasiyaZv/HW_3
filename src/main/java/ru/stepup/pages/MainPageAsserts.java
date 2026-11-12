package ru.stepup.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.assertj.core.api.AbstractAssert;
import org.openqa.selenium.Alert;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.stepup.pages.MainPage.CARD_PRODUCT_BY_NAME_CSS;
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

    @Step("Переход обратно к странице 'Витрина'." )
    public MainPage page() {
        return actual;
    }

//    private final SelenideElement headerBasket = $("h2");
//    private final ElementsCollection itemProductsBasket = $$("div>b");
//    private final SelenideElement amountBasket = $("span[id='total-price']");
//    private final ElementsCollection notificationBasket = $$("div[class='toast']");

        @Step("Корзина. Проверка, что открыта страница 'Корзина'.")
        public MainPageAsserts hasHeaderBasket(String expectedHeader) {
            actual.headerBasket
                    .as(String.format("Открыта не страница %s", expectedHeader))
                    .shouldHave(text(expectedHeader));
            return this;
        }

    @Step("Корзина. Проверка, что товар с наименованием '{expectedName}' имеется в корзине.")
    public MainPageAsserts hasAddProductInBasket(String expectedName) {
        actual.itemProductsBasket
                .as("Добавленный товар отсутствует в корзине")
                .shouldHave(texts(expectedName));
        return this;
    }

    @Step("Корзина. Проверка, что появилось сообщение '...Денег не хватает!'.")
    public MainPageAsserts hasAlert() {
        Alert alert = Selenide.switchTo().alert();
        System.out.println("!!! текст алерта = " + alert.getText());
        assertThat(alert.getText())
                .as("Окно алерта не появилось")
                .contains("Денег не хватает!");
        alert.accept();
        return this;
    }

    @Step("Корзина. Проверка, что появилась нотификация 'expectedNotification'.")
    public MainPageAsserts hasNotification(String expectedNotification) {
        actual.notificationBasket
                .as(String.format("Нотификация '%s' не найдена", expectedNotification))
                .shouldHave(CollectionCondition.itemWithText(expectedNotification));
        return this;
    }

    @Step("Корзина. Проверка, что фактическая сумма товаров" +
            " соответствует ожидаемому значению {expectedAmount}.")
    public MainPageAsserts hasAmounBasket(int expectedAmount) {
        actual.amountBasket
                .as(String.format("Ожидаемая сумма товаров %d не соответствует действительной %s",
                        expectedAmount, actual.amountBasket.getText()))
                .shouldHave(exactText(String.valueOf(expectedAmount)));
        return this;
    }

    @Step("Корзина. Проверка, что товар не отображается")
    public MainPageAsserts hasNotProductInBasket(List<String> idProducts) {
        for (String el : idProducts) {
            $(String.format(CARD_PRODUCT_BY_NAME_CSS, el))
                    .as("Товар не должен отображаться")
                    .shouldNot(exist);
        }
        return this;
    }

}
