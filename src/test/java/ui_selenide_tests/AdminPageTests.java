package ui_selenide_tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import ru.stepup.specifications.Specifications;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
import static ru.stepup.config.Config.BASE_CONFIG;
import static ru.stepup.config.Config.UI_CONFIG;
import static ru.stepup.data.RandomData.createUniqueProductName;
import static ru.stepup.data.RandomData.randomInt;

public class AdminPageTests {

    private static RequestSpecification requestSpecDelete;

    private String productName;
    private int productPrice;
    private String id;

    @BeforeAll
    public static void beforeAll() {
        Configuration.baseUrl = BASE_CONFIG.baseUrl();
        Configuration.timeout = UI_CONFIG.timeout();

        requestSpecDelete = new Specifications().requestSpecDelete();
    }

    @BeforeEach
    public void beforeEach() {
        open("/");

        System.out.println("Browser baseUrl: " + BASE_CONFIG.baseUrl());
        System.out.println("Browser timeout: " + UI_CONFIG.timeout());
        System.out.println("Start productName: " + UI_CONFIG.productName());
        System.out.println("Start productPrice: " + UI_CONFIG.productPrice());
        System.out.println("Start productName: " + UI_CONFIG.invalidLogin());
        System.out.println("Start productPrice: " + UI_CONFIG.invalidPassword());

        //создание параметров для нового товара
        productName = createUniqueProductName();
        productPrice = randomInt(1, 100);
        System.out.printf("!!! Создаем переменные для теста productName = %s,  productPrice = %s%n",
                productName, productPrice);
    }

    @AfterEach
    void afterEach() {
        if (id != null) {
            //удаление созданного товара
            Response response = given()
                    .spec(requestSpecDelete)
                    .pathParam("id", id)
                    .when()
                    .delete();
            System.out.println("!!! Удален товар с id = " + id);
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

    @Tag("Task_2")
    @Test
    @Description("Добавить товар через админку, выйти на витрину и проверить, что товар отображается")
    void addProduct() {
        //логирование через админа
        loginAsAdmin();
        //проверка, что находимся на странице админа
        $("h1")
                .as("открыта не страница администрирования")
                .shouldHave(exactText("SmartShop Admin"));
        //добавление нового товара
        $("input[id='n-name']").sendKeys(productName);
        $("input[id='n-price']").sendKeys(String.valueOf(productPrice));
        $("button[id='add-btn']").click();
        //получим id для удаления
        id = $x(String.format("//tr[td/input[@value='%s']]/td[1]", productName)).getText();
        System.out.println("id = " + id);
        //переходим на витрину
        $("a[href='/']").click();
        //проверка, что находимся на витрине
        $("h1[id='main-title']")
                .as("Открыта не страница витрины")
                .shouldHave(text("SmartShop"));
        //проверяем, что товар отображается
        $(String.format("div[data-name='%s']", productName))
                .as("Добавленный товар не найден на витрине")
                .shouldBe(visible);
    }

    @Tag("Task_2")
    @Test
    @Description("Попытаться войти в админку с неверным логином и паролем.")
    void invalidAuth() {
        //логин через админа
        $("a[href='/admin']").click();
        $("input[id='username']").sendKeys(UI_CONFIG.invalidLogin());
        $("input[id='password']").sendKeys(UI_CONFIG.invalidPassword());
        $("button[class='primary']").click();
        //проверяем, что залогиниться не получилось
        $("div[class='alert alert-danger']")
                .as("Залогинились под админом")
                .shouldHave(exactText("Неверные учетные данные пользователя"));
    }

    @Tag("Task_3")
    @Test
    @Description("Войти в админку и добавить товар. Проверить уведомление после добавления товара.")
    void messageAfterAddProduct() {
        //логирование через админа
        loginAsAdmin();
        //проверка, что находимся на странице админа
        $("h1")
                .as("открыта не страница администрирования")
                .shouldHave(exactText("SmartShop Admin"));
        //добавление нового товара
        $("input[id='n-name']").sendKeys(productName);
        $("input[id='n-price']").sendKeys(String.valueOf(productPrice));
        $("button[id='add-btn']").click();
        //получим id для удаления
        id = $x(String.format("//tr[td/input[@value='%s']]/td[1]", productName)).getText();
        System.out.println("id = " + id);
        //проверка, что товар успешно добавлен
        $("div[class='toast']")
                .as("Товар не добавлен")
                .shouldHave(exactText("Товар успешно добавлен!"));
    }

    @Tag("Task_3")
    @Test
    @Description("Войти в админку и отредактировать товар. Выйти на список товаров и проверить, что изменения применились.")
    void modifyProduct() {
        //логирование через админа
        loginAsAdmin();
        //проверка, что находимся на странице админа
        $("h1")
                .as("открыта не страница администрирования")
                .shouldHave(exactText("SmartShop Admin"));
        //добавление нового товара
        $("input[id='n-name']").sendKeys(productName);
        $("input[id='n-price']").sendKeys(String.valueOf(productPrice));
        $("button[id='add-btn']").click();
        //получим id для удаления
        id = $x(String.format("//tr[td/input[@value='%s']]/td[1]", productName)).getText();
        System.out.println("id = " + id);
        //редактирование товара
        $(String.format("input[id='nm-%s']", id)).setValue(UI_CONFIG.productName());
        $(String.format("input[id='pr-%s']", id)).setValue(UI_CONFIG.productPrice());
        $(String.format("button[data-action='update'][data-id='%s']", id)).click();
        //проверка изменений
        $(String.format("input[id='nm-%s']", id))
                .as("Имя не изменено")
                .shouldHave(value(UI_CONFIG.productName()));

        $(String.format("input[id='pr-%s']", id))
                .as("Цена не изменена")
                .shouldHave(value(UI_CONFIG.productPrice()));
    }
}
