package ru.stepup.specifications;

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

    public RequestSpecification requestSpec(String uri, String path) {
        return new RequestSpecBuilder()
                .setBaseUri(uri)
                .setBasePath(path)
                .setAuth(RestAssured.basic(LOGIN, PASS))
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    public RequestSpecification requestSpecPost() {
        return requestSpec(BASE_URL, PATH_POST);
    }

    public RequestSpecification requestSpecGetAll() {
        return requestSpec(BASE_URL, PATH_GET_ALL);
    }

    public RequestSpecification requestSpecDelete() {
        return requestSpec(BASE_URL, PATH_DELETE);
    }

    public RequestSpecification requestSpecGet() {
        return requestSpec(BASE_URL, PATH_GET);
    }

    public RequestSpecification requestSpecPath() {
        return requestSpec(BASE_URL, PATH_PATCH);
    }
}
