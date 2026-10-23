package ui_selenide_tests;

import api_tests.PostTests;
import com.codeborne.selenide.*;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Alert;
import ru.stepup.specifications.Specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.stepup.config.Config.BASE_CONFIG;
import static ru.stepup.config.Config.UI_CONFIG;
import static ru.stepup.data.RandomData.createUniqueProductName;
import static ru.stepup.data.RandomData.randomInt;

public class BasketPageTests {

    private static RequestSpecification requestSpecPost;
    private static RequestSpecification requestSpecDelete;
    List<String> idProducts = new ArrayList<>();
    private String id = null;
    private String productName;
    private int productPrice;
    private Integer idProduct;

    @BeforeAll
    public static void beforeAll() {
        Configuration.baseUrl = BASE_CONFIG.baseUrl();
        Configuration.timeout = UI_CONFIG.timeout();

        requestSpecPost = new Specifications().requestSpecPost();
        requestSpecDelete = new Specifications().requestSpecDelete();
    }

    @BeforeEach
    public void beforeEach() {
        open("/");

        System.out.println("Browser baseUrl: " + BASE_CONFIG.baseUrl());
        System.out.println("Browser timeout: " + UI_CONFIG.timeout());
        System.out.println("Start productName: " + UI_CONFIG.productName());
        System.out.println("Start productPrice: " + UI_CONFIG.productPrice());
    }

    @AfterEach
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

    private void loginAsAdmin() {
        //логин через админа
        $("a[href='/admin']").click();
        $("input[id='username']").sendKeys(BASE_CONFIG.adminLogin());
        $("input[id='password']").sendKeys(BASE_CONFIG.adminPassword());
        $("button[class='primary']").click();
    }

    private void addProducts(int count) {

        for (int i = 0; i < count; i++) {
            //создаем параметры для продукта
            productName = createUniqueProductName();
            productPrice = randomInt(1, 99);
            //создаем продукт
            Response response = given()
                    .spec(requestSpecPost)
                    .body(new PostTests.RequestCreate(productName, productPrice))
                    .when()
                    .post();
            //получаем и записываем его id
            idProduct = response.jsonPath().getObject("data.id", Integer.class);
            idProducts.add(String.valueOf(idProduct));
        }
        System.out.println(idProducts);
    }

    @Tag("Task_2")
    @Test
    @Description("Добавить товар в корзину и проверить, что он отображается")
    void addBasket() {
        // добавляем товары
        addProducts(4);
        //собираем карточки товаров в массив
        ElementsCollection cardProducts = $$("div[class='product-card']");

        List<String> valueNameCardProducts = new ArrayList<>();
        for (SelenideElement element : cardProducts) {
            valueNameCardProducts.add(element.getAttribute("data-name"));
        }
        System.out.println("valueNameCardProducts: " + valueNameCardProducts);
        //добавляем рандомный товар с витрины в корзину
        Random random = new Random();
        String randomName = valueNameCardProducts.get(random.nextInt(valueNameCardProducts.size()));
        System.out.println("randomName = " + randomName);
        $x(String.format("//button[@data-action='add-to-cart' and @data-name='%s']", randomName)).click();
        //проверяем, что товар добавлен
        $("span[id='cart-count']")
                .as("Товар в корзину не добавлен")
                .shouldHave(text("1"));
        //переходим в корзину
        $("button[id='open-cart-btn']").click();
        //собираем товары в корзине в массив и проверяем добавленный товар
        ElementsCollection itemProducts = $$("div>b")
                .as("Добавленный товар отсутствует в корзине")
                .shouldHave(texts(randomName));
    }

    @Tag("Task_2")
    @Test
    @Description("Проверить сохранение товаров в корзине после обновления страницы")
    void updatePage() {
        //собираем карточки товаров в массив
        ElementsCollection cardProducts = $$("div[class='product-card']");

        List<String> valueNameCardProducts = new ArrayList<>();
        for (SelenideElement element : cardProducts) {
            valueNameCardProducts.add(element.getAttribute("data-name"));
        }
        //добавляем рандомный товар с витрины в корзину
        Random random = new Random();
        String randomName = valueNameCardProducts.get(random.nextInt(valueNameCardProducts.size()));
        System.out.println("randomName " + randomName);

        $x(String.format("//button[@data-action='add-to-cart' and @data-name='%s']", randomName)).click();
        //проверяем, что один товар добавлен в корзину
        $("span[id='cart-count']")
                .as("Товар в корзину не добавлен")
                .shouldHave(text("1"));
        //обновляем страницу
        Selenide.refresh();
        //проверяем, что товар не удален из корзины
        $("span[id='cart-count']")
                .as("После обновления страницы товар не должен быть удален")
                .shouldHave(text("1"));
    }

    @Tag("Task_2")
    @Test
    @Description("Добавить в корзину товаров более чем на 300 рублей и нажать на кнопку «Оформить заказ». " +
            "Проверить, что отображается JS Alert")
    void checkAlert() {
        //логин через админа
        loginAsAdmin();
        //проверка, что находимся на странице админа
        $("h1")
                .as("Открыта не страница администрирования")
                .shouldHave(exactText("SmartShop Admin"));
        //добавление нового товара
        $("input[id='n-name']").sendKeys(UI_CONFIG.productName());
        $("input[id='n-price']").sendKeys(UI_CONFIG.productPrice());
        $("button[id='add-btn']").click();
        //получим id для удаления
        id = $x(String.format("//tr[td/input[@value='%s']]/td[1]", UI_CONFIG.productName())).getText();
        System.out.println("id = " + id);
        //проверка, что товар успешно добавлен
        $("div[class='toast']")
                .as("Товар не добавлен")
                .shouldHave(exactText("Товар успешно добавлен!"));
        //переходим на витрину
        $("a[href='/']").click();
        //проверка, что находимся на витрине
        $("h1[id='main-title']")
                .as("Открыта не страница витрины")
                .shouldHave(text("SmartShop"));
        //добавляем 3 раза товар в корзину
        for (int i = 0; i < 3; i++) {
            $x(String.format("//button[@data-action='add-to-cart' and @data-name='%s']", UI_CONFIG.productName())).click();
        }
        //проверяем что в корзине 3 позиции одного товара
        $("span[id='cart-count']")
                .as("Товар в корзину не добавлен")
                .shouldHave(text("3"));
        //переходим в корзину
        $("button[id='open-cart-btn']").click();
        //проверка, что находимся в корзине
        $("h2")
                .as("Открыта не страница корзины")
                .shouldHave(exactText("Корзина SmartShop"));
        //нажимаем кнопку оформить заказ
        $("button[id='makeOrder']").click();
        //проверяем окно алерта и выводим текст алерта
        Alert alert = Selenide.switchTo().alert();
        System.out.println(alert.getText());
        assertThat(alert.getText())
                .as("Окно алерта не появилось")
                .contains("Денег не хватает!");
        alert.accept();

    }

    @Tag("Task_3")
    @Test
    @Description("Добавить три единицы товара в корзину и оплатить их (общая стоимость не должна превышать 300 рублей). " +
            "Проверить уведомление об обработке заказа")
    void orderNotification() {
        //логин через админа
        loginAsAdmin();
        //проверка, что находимся на странице админа
        $("h1")
                .as("Открыта не страница администрирования")
                .shouldHave(Condition.text("SmartShop Admin"));
        //добавление нового товара
        addProducts(1);
        //переходим на витрину
        $("a[href='/']").click();
        //проверка, что находимся на витрине
        $("h1[id='main-title']")
                .as("Открыта не страница витрины")
                .shouldHave(Condition.text("SmartShop"));
        //добавляем 3 раза товар в корзину
        for (int i = 0; i < 3; i++) {
            $x(String.format("//button[@data-action='add-to-cart' and @data-name='%s']", productName)).click();
        }
        //проверяем что в корзине 3 товара
        $("span[id='cart-count']")
                .as("Товар в корзину не добавлен")
                .shouldHave(text("3"));
        //переходим в корзину
        $("button[id='open-cart-btn']").click();
        //проверка, что находимся в корзине
        $("h2")
                .as("Открыта не страница корзины")
                .shouldHave(exactText("Корзина SmartShop"));
        //нажимаем кнопку оформить заказ
        $("button[id='makeOrder']").click();
        //собираем нотификации и проверяем, что среди них есть "Заказ принят в обработку!"
        ElementsCollection notification = $$("div[class='toast']")
                .as("Заказ не оформлен")
                .shouldHave(CollectionCondition.itemWithText("Заказ принят в обработку!"));
    }

    @Tag("Task_3")
    @Test
    @Description("Добавить в корзину несколько разных товаров и проверить, что общая цена в корзине считается корректно.")
    void totalPriceBasket() {
        //логин через админа
        loginAsAdmin();
        //проверка, что находимся на странице админа
        $("h1")
                .as("Открыта не страница администрирования")
                .shouldHave(Condition.text("SmartShop Admin"));
        //добавление нового товара
        addProducts(3);
        //переходим на витрину
        $("a[href='/']").click();
        //проверка, что находимся на витрине
        $("h1[id='main-title']")
                .as("Открыта не страница витрины")
                .shouldHave(Condition.text("SmartShop"));
        //добавляем 3 товара в корзину и считаем общую сумму добавленных товаров
        int sum = 0;
        for (String element : idProducts) {
            $x(String.format("//button[@data-action='add-to-cart' and @data-id='%s']", element)).click();
            sum = sum + Integer.parseInt($x(String.format("//button[@data-action='add-to-cart' and @data-id='%s']", element))
                    .getAttribute("data-price"));
        }
        //проверяем что в корзине 3 товара
        $("span[id='cart-count']")
                .as("Товар в корзину не добавлен")
                .shouldHave(text("3"));
        //переходим в корзину
        $("button[id='open-cart-btn']").click();
        //проверка, что находимся в корзине
        $("h2")
                .as("Открыта не страница корзины")
                .shouldHave(exactText("Корзина SmartShop"));
        //проверяем суммы
        $("span[id='total-price']")
                .as("Суммы не совпадают")
                .shouldHave(exactText(String.valueOf(sum)));
    }

    @Tag("Task_4")
    @Test
    @Description("Удалить добавленный элемент из корзины и проверить, что он там больше не отображается")
    void deleteProductFromBasket() {
        //логин через админа
        loginAsAdmin();
        //проверка, что находимся на странице админа
        $("h1")
                .as("Открыта не страница администрирования")
                .shouldHave(Condition.text("SmartShop Admin"));
        //добавление нового товара
        addProducts(3);
        //переходим на витрину
        $("a[href='/']").click();
        //проверка, что находимся на витрине
        $("h1[id='main-title']")
                .as("Открыта не страница витрины")
                .shouldHave(Condition.text("SmartShop"));
        //добавляем 3 товара в корзину и считаем общую сумму добавленных товаров
        for (String element : idProducts) {
            $x(String.format("//button[@data-action='add-to-cart' and @data-id='%s']", element)).click();
        }
        //проверяем, что в корзине 3 товара
        $("span[id='cart-count']")
                .as("Товар в корзину не добавлен")
                .shouldHave(text("3"));
        //переходим в корзину
        $("button[id='open-cart-btn']").click();
        //проверка, что находимся в корзине
        $("h2")
                .as("Открыта не страница корзины")
                .shouldHave(exactText("Корзина SmartShop"));
        //удаляем товар из корзины
        $(String.format("div[id='cart-item-%d']>button[data-action='remove']", idProduct)).click();
        //проверяем, что удаленный товар не отображается в корзине
        $(String.format("div[id='cart-item-%d']", idProduct))
                .as("Товар не должен отображаться")
                .shouldNot(exist);
    }

    @Tag("Task_4")
    @Test
    @Description("Перетащить элемент в корзину с помощью Drag-and-Drop")
    void productDnD() {
        //логин через админа
        loginAsAdmin();
        //проверка, что находимся на странице админа
        $("h1")
                .as("Открыта не страница администрирования")
                .shouldHave(Condition.text("SmartShop Admin"));
        // добавляем товары
        addProducts(1);
        //переходим на витрину
        $("a[href='/']").click();
        //проверка, что находимся на витрине
        $("h1[id='main-title']")
                .as("Открыта не страница витрины")
                .shouldHave(Condition.text("SmartShop"));
        //добавляем товар в корзину с помощью Drag-and-Drop
        $(String.format("div[id='card-%d']", idProduct)).dragAndDrop(DragAndDropOptions.to($("button[id='open-cart-btn']")));
        //проверяем, что товар добавлен
        $("span[id='cart-count']")
                .as("Товар в корзину не добавлен")
                .shouldHave(text("1"));
    }
}
