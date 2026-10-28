package ru.stepup.pages;

import com.codeborne.selenide.*;
import io.qameta.allure.Step;
import org.openqa.selenium.Alert;
import ru.stepup.tools.CollectCardProduct;
import ru.stepup.tools.CreateProducts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MainPage {

    final static String NAME_PRODUCT_CSS = "div[data-name='%s']";
    private final static String IN_BASKET_BUTTON_BY_NAME_XPATH = "//button[@data-action='add-to-cart' and @data-name='%s']";
    private final static String IN_BASKET_BUTTON_BY_ID_XPATH = "//button[@data-action='add-to-cart' and @data-id='%s']";
    private final static String CARD_PRODUCT_BY_NAME_CSS = "div[data-name='%s']";
    final SelenideElement headerMainPage = $("h1[id='main-title']");
    final SelenideElement basketButtonCount = $("span[id='cart-count']");
    private final SelenideElement administrationButton = $("a[href='/admin']");
    private final SelenideElement basketButton = $("button[id='open-cart-btn']");
    private final ElementsCollection cardProducts = $$("div[class='product-card']");
    Random random = new Random();
    SelenideElement nameProduct;
    private SelenideElement cardProductByName;
    private List<String> valueNameCardProducts = new ArrayList<>();
    private List<String> idProducts = new ArrayList<>();
    private String randomName;

    @Step("Витрина. Обновить страницу 'Витрина'.")
    public MainPage updateMainPage() {
        Selenide.refresh();
        return this;
    }

    @Step("Витрина. Создать товары в количестве {count} через API.")
    public List<String> createProducts(int count) {
        idProducts = new CreateProducts().addProducts(count);
        return idProducts;
    }

    @Step("Витрина. Добавить товары в корзину по id: {idProducts}.")
    public MainPage addProductsInBasket(List<String> idProducts) {
        for (String el : idProducts) {
            $x(String.format(IN_BASKET_BUTTON_BY_ID_XPATH, el)).click();
        }
        return this;
    }

    @Step("Витрина. Посчитать общую сумму товаров, добавленных в корзину по id: {idProducts}.")
    public int totalAmountAddProducts(List<String> idProducts) {
        int sum = 0;
        for (String el : idProducts) {
            sum = sum + Integer.parseInt($x(String.format(IN_BASKET_BUTTON_BY_ID_XPATH, el))
                    .getAttribute("data-price"));
        }
        return sum;
    }

    @Step("Витрина. Выбрать рандомно один товар из списка.")
    public String selectRandomName() {
        valueNameCardProducts = new CollectCardProduct().getCardProductByAttribute(cardProducts, "data-name");
        System.out.println("!!! valueNameCardProducts!!! = " + valueNameCardProducts);
        randomName = valueNameCardProducts.get(random.nextInt(valueNameCardProducts.size()));
        System.out.println("!!! randomName in basket = " + randomName);
        return randomName;
    }

    @Step("Витрина. Добавить товар в корзину по кнопке [В корзину] на карточке продукта.")
    public MainPage addProductInBasket(String name) {
        $x(String.format(IN_BASKET_BUTTON_BY_NAME_XPATH, name))
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Витрина. Перейти на страницу 'Корзина' по кнопке [Корзина].")
    public MainPage goToBasket() {
        basketButton.click();
        return this;
    }

    @Step("Витрина. Перейти на страницу 'Авторизация' по кнопке [Администрирование].")
    public void goToSignInPage() {
        administrationButton
                .shouldBe(visible)
                .click();
    }

    @Step("Витрина. Добавить товар в корзину с помощью dragAndDrop.")
    public MainPage dragAndDropProductInBasket(String productName) {
        $(String.format(CARD_PRODUCT_BY_NAME_CSS, productName))
                .shouldBe(visible)
                .dragAndDrop(DragAndDropOptions.to(basketButton));
        return this;
    }

    @Step("Витрина. Проверка, что наименование и цена товара изменены.")
    public void isModifyProduct(String productName, String productPrice) {
        List<String> cardsProductsByName = new CollectCardProduct().getCardProductByAttribute(cardProducts, "data-name");
        List<String> cardsProductsByPrice = new CollectCardProduct().getCardProductByAttribute(cardProducts, "data-price");

        assertThat(cardsProductsByName)
                .as("Продукт, который был изменен, отсутствует")
                .contains(productName);
        assertThat(cardsProductsByPrice)
                .as("Продукт, который был изменен, отсутствует")
                .contains(productPrice);
    }

    public MainPageAsserts check() {
        return new MainPageAsserts(this);
    }

    public class Basket {

        private final static String DELETE_PRODUCT_FROM_BASKET_CSS = "div[id='cart-item-%s']>button[data-action='remove']";
        private final SelenideElement headerBasket = $("h2");
        private final SelenideElement arrangeOrderButton = $("button[id='makeOrder']");
        private final SelenideElement amountBasket = $("span[id='total-price']");
        private final ElementsCollection notification = $$("div[class='toast']");
        private final ElementsCollection itemProducts = $$("div>b");

        @Step("Корзина. Проверка, что открыта страница 'Корзина'.")
        public Basket hasHeaderBasket(String expectedHeader) {
            headerBasket
                    .as(String.format("Открыта не страница %s", expectedHeader))
                    .shouldHave(text(expectedHeader));
            return this;
        }

        @Step("Корзина. Проверка, что товар с наименованием '{expectedName}' имеется в корзине.")
        public Basket hasAddProductInBasket(String expectedName) {
            itemProducts
                    .as("Добавленный товар отсутствует в корзине")
                    .shouldHave(texts(expectedName));
            return this;
        }

        @Step("Корзина. Оформить заказ по кнопке [Оформить заказ]")
        public Basket arrangeOrder() {
            arrangeOrderButton
                    .shouldBe(visible)
                    .click();
            return this;
        }

        @Step("Корзина. Проверка, что появилось сообщение '...Денег не хватает!'.")
        public Basket hasAlert() {
            Alert alert = Selenide.switchTo().alert();
            System.out.println("!!! текст алерта = " + alert.getText());
            assertThat(alert.getText())
                    .as("Окно алерта не появилось")
                    .contains("Денег не хватает!");
            alert.accept();
            return this;
        }

        @Step("Корзина. Проверка, что появилась нотификация 'expectedNotification'.")
        public Basket hasNotification(String expectedNotification) {
            notification
                    .as(String.format("Нотификация '%s' не найдена", expectedNotification))
                    .shouldHave(CollectionCondition.itemWithText(expectedNotification));
            return this;
        }

        @Step("Корзина. Проверка, что фактическая сумма товаров" +
                " соответствует ожидаемому значению {expectedAmount}.")
        public Basket hasAmounBasket(int expectedAmount) {
            amountBasket
                    .as(String.format("Ожидаемая сумма товаров %d не соответствует действительной %s",
                            expectedAmount, amountBasket.getText()))
                    .shouldHave(exactText(String.valueOf(expectedAmount)));
            return this;
        }

        @Step("Корзина. Удалить товар по кнопке [Х].")
        public Basket deletedProductFromBasket(List<String> idProducts) {
            for (String el : idProducts) {
                $(String.format(DELETE_PRODUCT_FROM_BASKET_CSS, el))
                        .shouldBe(visible)
                        .click();
            }
            return this;
        }

        @Step("Корзина. Проверка, что товар не отображается")
        public Basket hasNotProductInBasket(List<String> idProducts) {
            for (String el : idProducts) {
                $(String.format(CARD_PRODUCT_BY_NAME_CSS, el))
                        .as("Товар не должен отображаться")
                        .shouldNot(exist);
            }
            return this;
        }
    }
}

