package api_tests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import utils.Specifications;

import static data.RandomData.createUniqueProductName;
import static data.RandomData.randomInt;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;


@Tag("Lesson")
@Tag("ApiTest")
public class GetAllTests {

    private static String productName = createUniqueProductName();
    private static int productPrice = randomInt(1, 100);
    RequestSpecification requestSpecGetAll = new Specifications().requestSpecGetAll();

    @Test
    @Description("Проверка статус кода 200 и тело ответа пустое через given().when().then()")
    void statusCode200AndBodyIsNullTest() {
        given()
                .baseUri("http://localhost:8080")
                .queryParam("page", 1)
                .queryParam("size", 10)
                .log().all()
                .when()
                .get("/goods/list")
                .then()
                .statusCode(200)
                .body("goods", hasSize(0))
                .log().all();
    }

    @Test
    @Description("Проверка статус кода 400 и тело ответа пустое через RequestSpecification")
    void statusCode400AndBodyIsNullTest() {
        given()
                .spec(requestSpecGetAll)
                .queryParam("page", 0)
                .queryParam("size", 0)
                .when()
                .get()
                .then()
                .statusCode(400)
                //.body(equalTo("Page size must not be less than one"));
                .body(nullValue());
    }

    @Test
    @Description("Создали 1 продукт и получили его в списке через REST Assured")
    void getProductWithRestAssured() {
        given()
                .spec(requestSpecGetAll)
                .queryParam("page", 1)
                .queryParam("size", 10)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("goods", not((empty())))
                .body("goods.name", hasItem(productName));
    }

    @Test
    @Description("Создали 1 продукт и получили его в списке через AssertJ")
    void getProductWithAssertJ() {

        Response response = given()
                .spec(requestSpecGetAll)
                .queryParam("page", 1)
                .queryParam("size", 10)
                .when()
                .get();

        assertThat(response.statusCode())
                .as("Статус код не соответствует ожидаемому: 200")
                .isEqualTo(200);

        assertThat(response.jsonPath().getList("goods"))
                .as("Список товаров пуст")
                .isNotEmpty();

        assertThat(response.jsonPath().getList("goods.name", String.class))
                .as(String.format("Товар с именем %s отсутствует в списке", productName))
                .contains(productName);
    }
}
