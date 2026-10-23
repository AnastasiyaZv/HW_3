package api_tests;

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
public class PostTests {

    private static RequestSpecification requestSpecPost;
    private static RequestSpecification requestSpecDelete;
    private String productName;
    private int productPrice;
    private Integer id;

    @BeforeAll
    static void beforeAll() {
        requestSpecPost = new Specifications().requestSpecPost();
        requestSpecDelete = new Specifications().requestSpecDelete();
    }

    @BeforeEach
    void beforeEach() {
        productName = createUniqueProductName();
        productPrice = randomInt(1, 100);

        System.out.println("Browser baseUrl: " + BASE_CONFIG.baseUrl());

        System.out.printf("!!! Создаем переменные для теста productName = %s,  productPrice = %s%n",
                productName, productPrice);
    }

    @AfterEach
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
    @Description("Проверка статус кода 200")
    void getStatusCode200ForPost() {
        Response response = given()
                .spec(requestSpecPost)
                .body(new RequestCreate(productName, productPrice))
                .when()
                .post();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(200)
                .hasBodyField("data.id");

        id = response.jsonPath().getObject("data.id", Integer.class);
    }

    @Tag("Negative")
    @Test
    @Description("Проверка статус кода 400")
    void getStatusCode400ForPost() {
        Response response = given()
                .spec(requestSpecPost)
                .body(new RequestCreate(null, productPrice))
                .when()
                .post();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(400);
    }

    public record RequestCreate(String name, Integer price) {
    }
}
