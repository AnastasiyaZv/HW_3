package ru.stepup.specifications;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static ru.stepup.config.Config.BASE_CONFIG;

public class Specifications {

    private static final String PATH_POST = "/goods/add";
    private static final String PATH_GET_ALL = "/goods/list";
    private static final String PATH_DELETE = "/goods/{id}";
    private static final String PATH_GET = "/goods/{id}";
    private static final String PATH_PATCH = "/goods/{id}";
    private static final String LOGIN = BASE_CONFIG.adminLogin();
    private static final String PASS = BASE_CONFIG.adminPassword();
    private final String BASE_URL = BASE_CONFIG.baseUrl();

    @Step("Дополнительно. Создать общую спецификацию для API.")
    public RequestSpecification requestSpec(String uri, String path) {
        return new RequestSpecBuilder()
                .setBaseUri(uri)
                .setBasePath(path)
                .setAuth(RestAssured.basic(LOGIN, PASS))
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new AllureRestAssured())
                .build();
    }

@Step("Дополнительно. Создать спецификацию для API POST.")
    public RequestSpecification requestSpecPost() {
        return requestSpec(BASE_URL, PATH_POST);
    }

    @Step("Дополнительно. Создать спецификацию для API GET ALL.")
    public RequestSpecification requestSpecGetAll() {
        return requestSpec(BASE_URL, PATH_GET_ALL);
    }

    @Step("Дополнительно. Создать спецификацию для API DELETE.")
    public RequestSpecification requestSpecDelete() {
        return requestSpec(BASE_URL, PATH_DELETE);
    }

    @Step("Дополнительно. Создать спецификацию для API GET.")
    public RequestSpecification requestSpecGet() {
        return requestSpec(BASE_URL, PATH_GET);
    }

    @Step("Дополнительно. Создать спецификацию для API PATCH.")
    public RequestSpecification requestSpecPath() {
        return requestSpec(BASE_URL, PATH_PATCH);
    }
}
