package ui_selenide_tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import ru.stepup.pages.AdminPage;
import ru.stepup.pages.MainPage;
import ru.stepup.pages.SignInPage;
import ru.stepup.specifications.Specifications;
import ru.stepup.tools.PrintConfig;

import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;
import static ru.stepup.config.Config.BASE_CONFIG;
import static ru.stepup.config.Config.UI_CONFIG;
import static ru.stepup.data.RandomData.createUniqueProductName;
import static ru.stepup.data.RandomData.randomInt;

@Tag("Task_5")
public class AdminPageTests {

    private static RequestSpecification requestSpecDelete;

    private final AdminPage adminPage = new AdminPage();
    private final MainPage mainPage = new MainPage();
    private final SignInPage signInPage = new SignInPage();

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
        //печать параметров конфигурации
        new PrintConfig().printConfig();
        //создание параметров для нового товара
        productName = createUniqueProductName();
        productPrice = randomInt(1, 100);
        System.out.printf("!!! Создаем переменные для теста productName = %s,  productPrice = %s%n",
                productName, productPrice);
    }

    @AfterEach
    void afterEach() {
        System.out.println("!!! id = " + id);
        //удаление созданного товара
        if (id != null) {
            Response response = given()
                    .spec(requestSpecDelete)
                    .pathParam("id", id)
                    .when()
                    .delete();
            System.out.println("!!! Удален товар с id = " + id);
        }

        Selenide.closeWebDriver();
    }

    @Tag("Task_2")
    @Test
    @DisplayName("Добавить товар через админку, выйти на витрину и проверить, что товар отображается")
    void addProduct() {
        mainPage
                .check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .goToSignInPage();
        signInPage
                .hasHeader("Please sign in")
                .authAdmin(BASE_CONFIG.adminLogin(), BASE_CONFIG.adminPassword());
        adminPage
                .check()
                .hasHeader("SmartShop Admin");
        id = adminPage
                .createNewProduct(productName, String.valueOf(productPrice));
        adminPage
                .check()
                .hasAddProduct(productName)
                .page()
                .goToMainPage();
        mainPage
                .check()
                .hasHeaderMainPage("SmartShop")
                .productIsVisible(productName);
    }

    @Tag("Task_2")
    @Test
    @DisplayName("Попытаться войти в админку с неверным логином и паролем.")
    void invalidAuth() {
        mainPage
                .check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .goToSignInPage();
        signInPage
                .hasHeader("Please sign in")
                .authAdmin(UI_CONFIG.invalidLogin(), UI_CONFIG.invalidPassword())
                .assertNotAuth();
    }

    @Tag("Task_3")
    @Test
    @DisplayName("Войти в админку и добавить товар. Проверить уведомление после добавления товара.")
    void messageAfterAddProduct() {
        mainPage
                .check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .goToSignInPage();
        signInPage
                .hasHeader("Please sign in")
                .authAdmin(BASE_CONFIG.adminLogin(), BASE_CONFIG.adminPassword());
        adminPage
                .check()
                .hasHeader("SmartShop Admin");
        id = adminPage
                .createNewProduct(productName, String.valueOf(productPrice));
        adminPage
                .check()
                .hasAddProduct(productName)
                .hasMessageCreateProduct("Товар успешно добавлен!");
    }

    @Tag("Task_3")
    @Test
    @DisplayName("Войти в админку и отредактировать товар. Выйти на список товаров и проверить, что изменения применились.")
    void modifyProduct() {
        mainPage
                .check()
                .hasHeaderMainPage("SmartShop")
                .page()
                .goToSignInPage();
        signInPage
                .hasHeader("Please sign in")
                .authAdmin(BASE_CONFIG.adminLogin(), BASE_CONFIG.adminPassword());
        adminPage
                .check()
                .hasHeader("SmartShop Admin");
        id = adminPage
                .createNewProduct(productName, String.valueOf(productPrice));
        adminPage
                .check()
                .hasAddProduct(productName)
                .page()
                .modifyProduct(id, UI_CONFIG.productName(), UI_CONFIG.productPrice())
                .check()
                .hasFieldProductName(UI_CONFIG.productName())
                .hasFieldProductPrice(UI_CONFIG.productPrice())
                .page()
                .goToMainPage();
        mainPage
                .isModifyProduct(UI_CONFIG.productName(), UI_CONFIG.productPrice());
    }
}
