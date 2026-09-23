package api_tests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import ru.stepup.BaseApiAssert;
import utils.Specifications;

import static data.RandomData.createUniqueProductName;
import static data.RandomData.randomInt;
import static io.restassured.RestAssured.given;

@Tag("ApiTest")
public class PatchTests {
    private static RequestSpecification requestSpecPatch;
    private static RequestSpecification requestSpecPost;
    private static RequestSpecification requestSpecDelete;
    private String productName;
    private int productPrice;
    private Integer id;

    @BeforeAll
    static void beforeAll(){
        requestSpecPatch = new Specifications().requestSpecPath();
        requestSpecPost = new Specifications().requestSpecPost();
        requestSpecDelete = new Specifications().requestSpecDelete();
    }

    @BeforeEach
    void beforeEach(){
        productName = createUniqueProductName();
        productPrice = randomInt(1, 100);
        System.out.println(String.format("!!! Создаем переменные для теста productName = %s,  productPrice = %s",
                productName, productPrice));

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
    void getStatusCode200ForPatch(){
        Response response = given()
                .spec(requestSpecPatch)
                .pathParam("id", id)
                .body(new RequestUpdate("Anything", 100))
                .when()
                .patch();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(200);
    }

    @Tag("Negative")
    @Test
    @Description("Проверка статус кода 400")
    void getStatusCode400ForPatch(){
        Response response = given()
                .spec(requestSpecPatch)
                .pathParam("id",id)
                .body(new RequestUpdate(null, 100))
                .when()
                .patch();

        BaseApiAssert
                .assertThat(response)
                .hasStatusCode(400);
    }

    @Tag("Negative")
    @Test
    @Description("Проверка статус кода 404")
    void getStatusCode404ForPatch(){
        Response response = given()
                .spec(requestSpecPatch)
                .pathParam("id",0)
                .body(new RequestUpdate("Any", 100))
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
