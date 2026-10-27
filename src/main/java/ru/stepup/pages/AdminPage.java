package ru.stepup.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class AdminPage {

    private static final String PRODUCT_ID_BY_NAME_XPATH = "//tr[td/input[@value='%s']]/td[1]";
    private static final String CARD_PRODUCT_NAME_CSS = "input[id='nm-%s']";
    private static final String CARD_PRODUCT_PRICE_CSS = "input[id='pr-%s']";
    private static final String CARD_PRODUCT_BUTTON_SAVE_CSS = "button[data-action='update'][data-id='%s']";
    private final SelenideElement nameNewProductForm = $("input[id='n-name']");
    private final SelenideElement priceNewProductForm = $("input[id='n-price']");
    private final SelenideElement createButtonNewProductForm = $("button[id='add-btn']");
    private final SelenideElement returnToWebsiteButton = $("a[href='/']");
    SelenideElement productIdByName;
    SelenideElement cardProductName;
    SelenideElement cardProductPrice;
    SelenideElement headerAdminPage = $("h1");
    SelenideElement messageCreateProduct = $("div[class='toast']");
    private SelenideElement cardProductButtonSafe;
    private String productId;

    public String createNewProduct(String name, String price) {
        //заполняем поля формы "Новый товар"
        nameNewProductForm
                .setValue(name)
                .shouldHave(exactValue(name));
        priceNewProductForm
                .setValue(price)
                .shouldHave(exactValue(price));
        createButtonNewProductForm
                .shouldBe(visible)
                .click();
        //получаем id созданного продукта из карточки продукта по имени
        productIdByName = $x(String.format(PRODUCT_ID_BY_NAME_XPATH, name));
        productId = productIdByName.getText();
        //определяем локатор для проверки элемента на странице
        cardProductName = $(String.format(CARD_PRODUCT_NAME_CSS, productId));
        return productId;
    }

    public AdminPage modifyProduct(String id, String name, String price) {
        cardProductName = $(String.format(CARD_PRODUCT_NAME_CSS, id));
        cardProductPrice = $(String.format(CARD_PRODUCT_PRICE_CSS, id));
        cardProductButtonSafe = $(String.format(CARD_PRODUCT_BUTTON_SAVE_CSS, id));

        cardProductName
                .setValue(name)
                .shouldHave(exactValue(name));
        cardProductPrice
                .setValue(price)
                .shouldHave(exactValue(price));
        cardProductButtonSafe
                .shouldBe(visible)
                .click();
        return this;
    }

    public AdminPage goToMainPage() {
        returnToWebsiteButton
                .shouldBe(visible)
                .click();
        return this;
    }

    public AdminPageAsserts check() {
        return new AdminPageAsserts(this);
    }
}
