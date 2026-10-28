package ui_selenide_tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import ru.stepup.pages.AdminPage;
import ru.stepup.pages.MainPage;
import ru.stepup.pages.SignInPage;
import ru.stepup.specifications.Specifications;
import ru.stepup.tools.PrintConfig;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;
import static ru.stepup.config.Config.BASE_CONFIG;
import static ru.stepup.config.Config.UI_CONFIG;

@Tag("Task_5")
public class BasketPageTests {

    private static RequestSpecification requestSpecPost;
    private static RequestSpecification requestSpecDelete;
    private final MainPage mainPage = new MainPage();
    private final AdminPage adminPage = new AdminPage();
    private final SignInPage signInPage = new SignInPage();
    List<String> idProducts = new ArrayList<>();
    String randomName;
    private String id = null;

    @BeforeAll
    public static void beforeAll() {
        Configuration.baseUrl = BASE_CONFIG.baseUrl();
        Configuration.timeout = UI_CONFIG.timeout();
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .includeSelenideSteps(true)
                        .screenshots(true)
                        .savePageSource(true)
        );

        requestSpecPost = new Specifications().requestSpecPost();
        requestSpecDelete = new Specifications().requestSpecDelete();
    }

    @BeforeEach
    @Step("Предусловие. Открыть страницу 'Витрина'.")
    public void beforeEach() {
        open("/");
        //печать параметров конфигурации
        new PrintConfig().printConfig();
    }

    @AfterEach
    @Step("Постусловие. Удалить созданные товары.")
    void afterEach() {
        if (id != null) {
            Response response = given()
                    .spec(requestSpecDelete)
                    .pathParam("id", Integer.parseInt(id))
                    .when()
                    .delete();
            System.out.println("!!! Удаляем товар с id = " + id);
        }

        if (idProducts.size() > 0) {
            for (String el : idProducts) {
                Response response = given()
                        .spec(requestSpecDelete)
                        .pathParam("id", Integer.parseInt(el))
                        .when()
                        .delete();
                System.out.println("!!! Удаляем товар с id = " + el);
            }
        }

        Selenide.closeWebDriver();
    }

    @Tag("Task_2")
    @Test
    @DisplayName("Добавить товар в корзину и проверить, что он отображается")
    void addBasket() {
        idProducts = mainPage
                .createProducts(4);
        mainPage.check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .updateMainPage();
        randomName = mainPage
                .selectRandomName();
        mainPage
                .addProductInBasket(randomName)
                .check()
                .hasBasketButtonCount("1")
                .page()
                .goToBasket()
                .new Basket()
                .hasAddProductInBasket(randomName);
    }

    @Tag("Task_2")
    @Test
    @DisplayName("Проверить сохранение товаров в корзине после обновления страницы")
    void updatePage() {
        idProducts = mainPage
                .createProducts(1);
        mainPage.check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .updateMainPage();
        randomName = mainPage
                .selectRandomName();
        mainPage
                .updateMainPage()
                .addProductInBasket(randomName)
                .check()
                .hasBasketButtonCount("1")
                .page()
                .updateMainPage()
                .check()
                .hasBasketButtonCount("1");
    }

    @Tag("Task_2")
    @Test
    @DisplayName("Добавить в корзину товаров более чем на 300 рублей и нажать на кнопку «Оформить заказ». " +
            "Проверить, что отображается JS Alert")
    void checkAlert() {
        mainPage
                .goToSignInPage();
        signInPage
                .hasHeader("Please sign in")
                .authAdmin(BASE_CONFIG.adminLogin(), BASE_CONFIG.adminPassword());
        adminPage
                .check()
                .hasHeader("SmartShop Admin");
        id = adminPage.createNewProduct(UI_CONFIG.productName(), UI_CONFIG.productPrice());
        adminPage
                .check()
                .hasAddProduct(UI_CONFIG.productName())
                .page()
                .goToMainPage();
        mainPage
                .updateMainPage()
                .check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .addProductInBasket(UI_CONFIG.productName())
                .addProductInBasket(UI_CONFIG.productName())
                .addProductInBasket(UI_CONFIG.productName())
                .check()
                .hasBasketButtonCount("3")
                .page()
                .goToBasket()
                .new Basket()
                .hasHeaderBasket("Корзина SmartShop")
                .arrangeOrder()
                .hasAlert();
    }

    @Tag("Task_3")
    @Test
    @DisplayName("Добавить три единицы товара в корзину и оплатить их (общая стоимость не должна превышать 300 рублей). " +
            "Проверить уведомление об обработке заказа")
    void orderNotification() {
        idProducts = mainPage
                .createProducts(1);
        mainPage
                .updateMainPage()
                .check()
                .hasHeaderMainPage("SmartShop");
        randomName = mainPage
                .selectRandomName();
        mainPage
                .addProductInBasket(randomName)
                .addProductInBasket(randomName)
                .addProductInBasket(randomName)
                .check()
                .hasBasketButtonCount("3")
                .page()
                .goToBasket()
                .new Basket()
                .arrangeOrder()
                .hasNotification("Заказ принят в обработку!");
    }

    @Tag("Task_3")
    @Test
    @DisplayName("Добавить в корзину несколько разных товаров и проверить, что общая цена в корзине считается корректно.")
    void totalPriceBasket() {
        idProducts = mainPage
                .createProducts(3);
        mainPage
                .updateMainPage()
                .check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .addProductsInBasket(idProducts);
        int sumTotal = mainPage.
                totalAmountAddProducts(idProducts);
        mainPage
                .goToBasket()
                .new Basket()
                .hasHeaderBasket("Корзина SmartShop")
                .hasAmounBasket(sumTotal);
    }

    @Tag("Task_4")
    @Test
    @DisplayName("Удалить добавленный элемент из корзины и проверить, что он там больше не отображается")
    void deleteProductFromBasket() {
        idProducts = mainPage
                .createProducts(1);
        mainPage
                .updateMainPage()
                .check()
                .hasHeaderMainPage("SmartShop");
        randomName = mainPage
                .selectRandomName();
        mainPage
                .check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .addProductInBasket(randomName)
                .check()
                .hasBasketButtonCount("1")
                .page()
                .goToBasket()
                .new Basket()
                .hasHeaderBasket("Корзина SmartShop")
                .deletedProductFromBasket(idProducts)
                .hasNotProductInBasket(idProducts);
    }

    @Tag("Task_4")
    @Test
    @DisplayName("Перетащить элемент в корзину с помощью Drag-and-Drop")
    void productDnD() {
        idProducts = mainPage
                .createProducts(1);
        mainPage.check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .updateMainPage();
        randomName = mainPage
                .selectRandomName();
        mainPage
                .updateMainPage()
                .check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .dragAndDropProductInBasket(randomName)
                .check()
                .hasBasketButtonCount("1")
                .page()
                .goToBasket()
                .new Basket()
                .hasHeaderBasket("Корзина SmartShop")
                .hasAddProductInBasket(randomName);
    }
}
