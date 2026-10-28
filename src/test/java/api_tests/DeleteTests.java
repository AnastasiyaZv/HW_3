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
import static ru.stepup.config.Config.BASE_CONFIG;
import static ru.stepup.data.RandomData.createUniqueProductName;
import static ru.stepup.data.RandomData.randomInt;

@Tag("ApiTest")
public class DeleteTests {

    private static RequestSpecification requestSpecDelete;
    private static RequestSpecification requestSpecPost;
    private String productName;
    private int productPrice;
    private Integer id;

    @BeforeAll
    static void beforeAll() {
        requestSpecDelete = new Specifications().requestSpecDelete();
        requestSpecPost = new Specifications().requestSpecPost();
    }

    @BeforeEach
    @Step("Предусловие. Определить параметры для создания товара и создать товар.")
    void beforeEach() {
        productName = createUniqueProductName();
        productPrice = randomInt(1, 100);

        System.out.println("Browser baseUrl: " + BASE_CONFIG.baseUrl());

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

    @Tag("Positive")
    @Test
    @DisplayName("Проверка статус кода 200")
    @Step("Удалить товар. Получить код 200")
    void getStatusCode200ForDelete() {
        Response response = given()
                .spec(requestSpecDelete)
                .pathParam("id", id)
                .when()
                .delete();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(200);
    }

    @Tag("Negative")
    @Test
    @DisplayName("Проверка статус кода 404")
    @Step("Удалить товар. Получить код 404")
    void getStatusCode404ForDelete() {
        Response response = given()
                .spec(requestSpecDelete)
                .pathParam("id", 0)
                .when()
                .delete();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(404);
    }

    public record RequestCreate(String name, Integer price) {
    }
}
