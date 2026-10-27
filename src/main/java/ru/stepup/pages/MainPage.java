package ru.stepup.pages;

import com.codeborne.selenide.*;
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
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

    public MainPage updateMainPage() {
        Selenide.refresh();
        return this;
    }

    //создаем товары
    public List<String> createProducts(int count) {
        idProducts = new CreateProducts().addProducts(count);
        return idProducts;
    }

    //добавляем товары в корзину по id
    public MainPage addProductsInBasket(List<String> idProducts) {
        for (String el : idProducts) {
            $x(String.format(IN_BASKET_BUTTON_BY_ID_XPATH, el)).click();
        }
        return this;
    }

    public int totalAmountAddProducts(List<String> idProducts) {
        int sum = 0;
        for (String el : idProducts) {
            sum = sum + Integer.parseInt($x(String.format(IN_BASKET_BUTTON_BY_ID_XPATH, el))
                    .getAttribute("data-price"));
        }
        return sum;
    }

    public String selectRandomName() {
        valueNameCardProducts = new CollectCardProduct().getCardProductByAttribute(cardProducts, "data-name");
        System.out.println("!!! valueNameCardProducts!!! = " + valueNameCardProducts);
        randomName = valueNameCardProducts.get(random.nextInt(valueNameCardProducts.size()));
        System.out.println("!!! randomName in basket = " + randomName);
        return randomName;
    }

    //добавляем рандомный товар с витрины в корзину
    public MainPage addProductInBasket(String name) {
        $x(String.format(IN_BASKET_BUTTON_BY_NAME_XPATH, name))
                .shouldBe(visible)
                .click();
        return this;
    }

    public MainPage goToBasket() {
        basketButton.click();
        return this;
    }

    public void goToSignInPage() {
        administrationButton
                .shouldBe(visible)
                .click();
    }

    public MainPage dragAndDropProductInBasket(String productName) {
        $(String.format(CARD_PRODUCT_BY_NAME_CSS, productName))
                .shouldBe(visible)
                .dragAndDrop(DragAndDropOptions.to(basketButton));
        return this;
    }

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

        public Basket hasHeaderBasket(String expectedHeader) {
            headerBasket
                    .as(String.format("Открыта не страница %s", expectedHeader))
                    .shouldHave(text(expectedHeader));
            return this;
        }

        //собираем товары в корзине в массив и проверяем добавленный товар
        public Basket hasAddProductInBasket(String expectedName) {

            itemProducts
                    .as("Добавленный товар отсутствует в корзине")
                    .shouldHave(texts(expectedName));
            return this;
        }

        public Basket arrangeOrder() {
            arrangeOrderButton
                    .shouldBe(visible)
                    .click();
            return this;
        }

        public Basket hasAlert() {
            Alert alert = Selenide.switchTo().alert();
            System.out.println("!!! текст алерта = " + alert.getText());
            assertThat(alert.getText())
                    .as("Окно алерта не появилось")
                    .contains("Денег не хватает!");
            alert.accept();
            return this;
        }

        public Basket hasNotification(String expectedNotification) {
            notification
                    .as(String.format("Нотификация '%s' не найдена", expectedNotification))
                    .shouldHave(CollectionCondition.itemWithText(expectedNotification));
            return this;
        }

        public Basket hasAmounBasket(int expectedAmount) {
            amountBasket
                    .as(String.format("Ожидаемая сумма товаров %d не соответствует действительной %s",
                            expectedAmount, amountBasket.getText()))
                    .shouldHave(exactText(String.valueOf(expectedAmount)));
            return this;
        }

        public Basket deletedProductFromBasket(List<String> idProducts) {
            for (String el : idProducts) {
                $(String.format(DELETE_PRODUCT_FROM_BASKET_CSS, el))
                        .shouldBe(visible)
                        .click();
            }
            return this;
        }

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

