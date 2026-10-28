package ru.stepup.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;

public class SignInPage {

    private final SelenideElement loginForm = $("input[id='username']");
    private final SelenideElement passwordForm = $("input[id='password']");
    private final SelenideElement signInButtonForm = $("button[class='primary']");
    private final SelenideElement headerSignInPage = $("h2");
    private final SelenideElement alert = $("div[class='alert alert-danger']");

    @Step("Авторизация. Заполнить форму авторизации и нажать кнопку [Sign in].")
    public SignInPage authAdmin(String login, String pass) {
        loginForm.sendKeys(login);
        passwordForm.sendKeys(pass);
        signInButtonForm.click();
        return this;
    }

    @Step("Авторизация. Проверка, что нельзя залогиниться с неверным логин/пароль")
    public SignInPage assertNotAuth() {
        alert
                .as("Залогинились под админом")
                .shouldHave(exactText("Неверные учетные данные пользователя"));
        return this;
    }

    @Step("Авторизация. Проверка, что открыта страница 'Авторизация'.")
    public SignInPage hasHeader(String expectedHeader) {
        headerSignInPage
                .as("Открыта не страница авторизации админа")
                .shouldHave(exactText(expectedHeader));
        return this;
    }
}
