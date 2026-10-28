package ru.stepup.assertions;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

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
}
