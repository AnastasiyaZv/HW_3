package api_tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import ru.stepup.assertions.BaseApiAssert;
import ru.stepup.specifications.Specifications;

import static io.restassured.RestAssured.given;
import static ru.stepup.config.Config.API_CONFIG;
import static ru.stepup.config.Config.BASE_CONFIG;
import static ru.stepup.data.RandomData.createUniqueProductName;
import static ru.stepup.data.RandomData.randomInt;

@Tag("ApiTest")
public class PatchTests {
    private static RequestSpecification requestSpecPatch;
    private static RequestSpecification requestSpecPost;
    private static RequestSpecification requestSpecDelete;
    private String productName;
    private int productPrice;
    private Integer id;

    @BeforeAll
    static void beforeAll() {
        requestSpecPatch = new Specifications().requestSpecPath();
        requestSpecPost = new Specifications().requestSpecPost();
        requestSpecDelete = new Specifications().requestSpecDelete();
    }

    @BeforeEach
    @Step("Предусловие. Определить параметры для создания товара и создать товар.")
    void beforeEach() {
        productName = createUniqueProductName();
        productPrice = randomInt(1, 100);

        System.out.println("Browser baseUrl: " + BASE_CONFIG.baseUrl());
        System.out.println("Start productName: " + API_CONFIG.productName());
        System.out.println("Start productPrice: " + API_CONFIG.productPrice());

        id = given()
                .spec(requestSpecPost)
                .body(new RequestCreate(productName, productPrice))
                .when()
                .post()
                .then()
                .extract()
                .body()
                .jsonPath()
                .getObject("data.id", Integer.class);

        Allure.addAttachment("Browser baseUrl: ", String.valueOf(BASE_CONFIG.baseUrl()));
        Allure.addAttachment("Наименование продукта", productName);
        Allure.addAttachment("Цена продукта", String.valueOf(productPrice));
        Allure.addAttachment("Создан продукт с id", String.valueOf(id));
    }

    @AfterEach
    @Step("Постусловие. Удалить товары через API.")
    void afterEach() {
        if (id != null) {
            System.out.println("!!! Удаляем товар с id = " + id);
            given()
                    .spec(requestSpecDelete)
                    .pathParam("id", id)
                    .when()
                    .delete();
            id = null;
        } else {
            System.out.println("!!! Товар не создавался, нечего удалять");
        }
    }

    @Tag("Positive")
    @Test
    @DisplayName("Проверка статус кода 200")
    @Step("Изменить товар. Получить код 200")
    void getStatusCode200ForPatch() {
        Response response = given()
                .spec(requestSpecPatch)
                .pathParam("id", id)
                .body(new RequestUpdate(API_CONFIG.productName(), API_CONFIG.productPrice()))
                .when()
                .patch();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(200);
    }

    @Tag("Negative")
    @Test
    @DisplayName("Проверка статус кода 400")
    @Step("Изменить товар. Получить код 400")
    void getStatusCode400ForPatch() {
        Response response = given()
                .spec(requestSpecPatch)
                .pathParam("id", id)
                .body(new RequestUpdate(null, API_CONFIG.productPrice()))
                .when()
                .patch();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(400);
    }

    @Tag("Negative")
    @Test
    @DisplayName("Проверка статус кода 404")
    @Step("Изменить товар. Получить код 404")
    void getStatusCode404ForPatch() {
        Response response = given()
                .spec(requestSpecPatch)
                .pathParam("id", 0)
                .body(new RequestUpdate(API_CONFIG.productName(), API_CONFIG.productPrice()))
                .when()
                .patch();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(404);
    }

    public record RequestCreate(String name, Integer price) {
    }

    public record RequestUpdate(String name, Integer price) {
    }
}
