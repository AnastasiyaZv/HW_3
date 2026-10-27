package ru.stepup.tools;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.stepup.specifications.Specifications;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static ru.stepup.data.RandomData.createUniqueProductName;
import static ru.stepup.data.RandomData.randomInt;

public class CreateProducts {

    private static final RequestSpecification requestSpecPost = new Specifications().requestSpecPost();
    List<String> idProducts = new ArrayList<>();
    private String productName;
    private int productPrice;
    private Integer idProduct;

    public List<String> addProducts(int count) {
        for (int i = 0; i < count; i++) {
            //создаем параметры для продукта
            productName = createUniqueProductName();
            productPrice = randomInt(1, 99);
            //создаем продукт
            Response response = given()
                    .spec(requestSpecPost)
                    .body(new RequestCreate(productName, productPrice))
                    .when()
                    .post();
            //получаем и записываем его id
            idProduct = response.jsonPath().getObject("data.id", Integer.class);
            idProducts.add(String.valueOf(idProduct));
        }
        System.out.println(idProducts);
        return idProducts;
    }

    public record RequestCreate(String name, Integer price) {
    }

}
