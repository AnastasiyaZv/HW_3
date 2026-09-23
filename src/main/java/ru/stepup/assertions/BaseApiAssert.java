package ru.stepup.assertions;

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

    //проверка статус кода
    public BaseApiAssert hasStatusCode(int statusCode){
        Assertions.assertThat(actual.statusCode())
                .as(String.format("Статус код не равен ожидаемому значению %d",statusCode))
                .isEqualTo(statusCode);
        return this;
    }

    //имеется поле в ответе
    public BaseApiAssert hasBodyField(String path){
        Assertions.assertThat(actual.jsonPath().getString(path))
                .as(String.format("Поле %s не содержится в ответе", path))
                .isNotNull();
        return this;
    }
}
