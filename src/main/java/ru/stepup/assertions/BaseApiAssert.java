package ru.stepup.assertions;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;

public class BaseApiAssert extends AbstractAssert<BaseApiAssert, Response> {

    protected BaseApiAssert(Response response) {
        super(response, BaseApiAssert.class);
    }

    public static BaseApiAssert assertThat(Response actual){
        return new BaseApiAssert(actual);
    }

    @Step("Проверка статус-кода ответа")
    public BaseApiAssert hasStatusCode(int statusCode){
        Allure.addAttachment("Ожидаемое значение", String.valueOf(statusCode));
        Allure.addAttachment("Фактическое значение", String.valueOf(actual.statusCode()));

        Assertions.assertThat(actual.statusCode())
                .as(String.format("Статус код не равен ожидаемому значению %d",statusCode))
                .isEqualTo(statusCode);
        return this;
    }

    @Step("Проверка наличия поля в ответе")
    public BaseApiAssert hasBodyField(String path){
        Allure.addAttachment("Ожидаемое значение", "isNotNull");
        Allure.addAttachment("Фактическое значение", actual.jsonPath().getString(path));

        Assertions.assertThat(actual.jsonPath().getString(path))
                .as(String.format("Поле %s не содержится в ответе", path))
                .isNotNull();
        return this;
    }

    @Step("Проверка, что тело содержит текст")
    public BaseApiAssert hasBodyText(String expectedText){

        String actualText = actual.getBody().asString();
        Allure.addAttachment("Ожидаемое значение", expectedText);
        Allure.addAttachment("Фактическое значение", actualText);
        System.out.println("!!! actual.body() = "+actualText);

        Assertions.assertThat(actualText)
                .as("Тело ответа")
                .isEqualTo(expectedText);
        return this;
    }

    @Step("Проверка что поле содержит значение")
    public BaseApiAssert hasBodyTextInField(String field, String expectedText){

        List<String> fieldValues = actual.jsonPath().getList(field);
        Assertions.assertThat(fieldValues)
                .as(String.format("Тег '%s': %s",field, fieldValues))
                .contains(expectedText);
        return this;
    }
}
