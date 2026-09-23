package api_tests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import ru.stepup.assertions.BaseApiAssert;
import ru.stepup.specifications.Specifications;

import static ru.stepup.data.RandomData.createUniqueProductName;
import static ru.stepup.data.RandomData.randomInt;
import static io.restassured.RestAssured.given;

@Tag("ApiTest")
public class GetTests {

    private static RequestSpecification requestSpecGet;
    private static RequestSpecification requestSpecPost;
    private static RequestSpecification requestSpecDelete;
    private String productName;
    private int productPrice;
    private Integer id;

    @BeforeAll
    static void beforeAll() {
        requestSpecGet = new Specifications().requestSpecGet();
        requestSpecPost = new Specifications().requestSpecPost();
        requestSpecDelete = new Specifications().requestSpecDelete();
    }

    @BeforeEach
    void beforeEach() {
        productName = createUniqueProductName();
        productPrice = randomInt(1, 100);
        System.out.printf("!!! Создаем переменные для теста productName = %s,  productPrice = %s%n",
                productName, productPrice);

        id = given()
                .spec(requestSpecPost)
                .body(new DeleteTests.RequestCreate(productName, productPrice))
                .when()
                .post()
                .then()
                .extract()
                .body()
                .jsonPath()
                .getObject("data.id", Integer.class);

        System.out.println("!!! Создаем товар с id = " + id);
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
    void getStatusCode200ForGet() {
        Response response = given()
                .spec(requestSpecGet)
                .pathParam("id", 10)
                //.pathParam("id",id)
                .when()
                .get();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(200);
    }

    @Tag("Negative")
    @Test
    @Description("Проверка статус кода 404")
    void getStatusCode404ForGet() {
        Response response = given()
                .spec(requestSpecGet)
                .pathParam("id", 0)
                .when()
                .get();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(404);
    }
}
