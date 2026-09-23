package api_tests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.stepup.assertions.BaseApiAssert;
import ru.stepup.specifications.Specifications;

import static ru.stepup.data.RandomData.createUniqueProductName;
import static ru.stepup.data.RandomData.randomInt;
import static io.restassured.RestAssured.given;

@Tag("ApiTest")
public class DeleteTests {

    private static RequestSpecification requestSpecDelete;
    private static RequestSpecification requestSpecPost;
    private String productName;
    private int productPrice;
    private Integer id;

    @BeforeAll
    static void beforeAll(){
        requestSpecDelete = new Specifications().requestSpecDelete();
        requestSpecPost = new Specifications().requestSpecPost();
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

    @Tag("Positive")
    @Test
    @Description("Проверка статус кода 200")
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
    @Description("Проверка статус кода 404")
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
