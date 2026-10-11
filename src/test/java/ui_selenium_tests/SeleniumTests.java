package ui_selenium_tests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.stepup.specifications.Specifications;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Fail.fail;

@Tag("Selenium")
public class SeleniumTests {

    private static RequestSpecification requestSpecDelete;
    WebDriver driver;
    private String id = null;

    @BeforeAll
    static void beforeAll() {
        requestSpecDelete = new Specifications().requestSpecDelete();
    }

    @BeforeEach
    void beforeEach() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.get("http://localhost:8080/");
    }

    @AfterEach
    void afterEach() {
        if (driver != null)
            driver.quit();

        if (id != null) {
            Response response = given()
                    .spec(requestSpecDelete)
                    .pathParam("id", Integer.parseInt(id))
                    .when()
                    .delete();
            System.out.println("!!! Удален товар с id = " + id);
        }
    }

    @Test
    @Description("Добавить товар через админку, выйти на витрину и проверить, что товар отображается")
    void addProduct() {
        //логин через админа
        driver.findElement(By.cssSelector("a[href='/admin']")).click();
        driver.findElement(By.cssSelector("input[id='username']")).sendKeys("admin");
        driver.findElement(By.cssSelector("input[id='password']")).sendKeys("secret123");
        driver.findElement(By.cssSelector("button[class='primary']")).click();
        //проверка, что находимся на странице админа
        Assertions.assertThat(driver.findElement(By.cssSelector("h1")).getText())
                .as("Это не страница администрирования")
                .isEqualTo("SmartShop Admin");
        //добавление нового товара
        driver.findElement(By.cssSelector("input[id='n-name']")).sendKeys("Watermelon");
        driver.findElement(By.cssSelector("input[id='n-price']")).sendKeys("13");
        driver.findElement(By.cssSelector("button[id='add-btn']")).click();
        //получим id для удаления
        id = driver.findElement(By.xpath("//tr[td/input[@value='Watermelon']]/td[1]")).getText();
        System.out.println("id = " + id);
        //проверка, что товар успешно добавлен
        Assertions.assertThat(driver.findElement(By.cssSelector("div[class='toast']")).getText())
                .as("Товар не добавлен")
                .isEqualTo("Товар успешно добавлен!");
        //переходим на витрину
        driver.findElement(By.cssSelector("a[href='/']")).click();
        //проверка, что находимся на витрине
        Assertions.assertThat(driver.findElement(By.cssSelector("h1[id='main-title']")).getText())
                .as("Это не страница витрины")
                .contains("SmartShop");
        //проверяем добавленный товар на витрине
        WebElement addedNameProduct = null;
        try {
            addedNameProduct = driver.findElement(By.cssSelector("div[data-name='Watermelon']"));
        } catch (NoSuchElementException e) {
            System.out.println("Добавленный товар не найден: " + e.getMessage());
        }

        Assertions.assertThat(addedNameProduct)
                .isNotNull();
    }

    @Test
    @Description("Добавить товар в корзину и проверить, что он отображается")
    void addBasket() {
        //собираем карточки товаров в массив
        List<WebElement> cardProducts = new ArrayList<>(driver.findElements(By.cssSelector("div[class='product-card']")));

        List<String> valueNameCardProducts = new ArrayList<>();
        for (WebElement element : cardProducts) {
            valueNameCardProducts.add(element.getAttribute("data-name"));
        }
        //добавляем рандомный товар с витрины в корзину
        Random random = new Random();
        String randomName = valueNameCardProducts.get(random.nextInt(valueNameCardProducts.size()));

        driver.findElement(By.xpath(String.format("//button[@data-action='add-to-cart' and @data-name='%s']", randomName))).click();
        //проверяем, что один товар добавлен в корзину
        Assertions.assertThat(driver.findElement(By.cssSelector("span[id='cart-count']")).getText())
                .as("Товар в корзину не добавлен")
                .isEqualTo("1");
        //переходим в корзину
        driver.findElement(By.cssSelector("button[id='open-cart-btn']")).click();
        //проверяем, что товар отображается
        Assertions.assertThat(driver.findElement(By.cssSelector("div>b")).getText())
                .as("Добавленный товар отсутствует в корзине")
                .isEqualTo(randomName);
    }

    @Test
    @Description("Попытаться войти в админку с неверным логином и паролем.")
    void invalidAuth() {
        //логин через админа
        driver.findElement(By.cssSelector("a[href='/admin']")).click();
        driver.findElement(By.cssSelector("input[id='username']")).sendKeys("admin1");
        driver.findElement(By.cssSelector("input[id='password']")).sendKeys("secret123");
        driver.findElement(By.cssSelector("button[class='primary']")).click();
        //проверяем, что залогиниться не получилось
        WebElement allertMessage = null;
        try {
            allertMessage = driver.findElement(By.cssSelector("div[class='alert alert-danger']"));
        } catch (NoSuchElementException e) {
            fail("Залогинились под админом: " + e.getMessage());
            return;
        }

        Assertions.assertThat(allertMessage.getText())
                .isEqualTo("Неверные учетные данные пользователя");
    }

    @Test
    @Description("Проверить сохранение товаров в корзине после обновления страницы")
    void updatePage() {
        //собираем карточки товаров в массив
        List<WebElement> cardProducts = new ArrayList<>(driver.findElements(By.cssSelector("div[class='product-card']")));

        List<String> valueNameCardProducts = new ArrayList<>();
        for (WebElement element : cardProducts) {
            valueNameCardProducts.add(element.getAttribute("data-name"));
        }
        //добавляем рандомный товар с витрины в корзину
        Random random = new Random();
        String randomName = valueNameCardProducts.get(random.nextInt(valueNameCardProducts.size()));
        System.out.println("randomName " + randomName);

        driver.findElement(By.xpath(String.format("//button[@data-action='add-to-cart' and @data-name='%s']", randomName))).click();
        //проверяем, что один товар добавлен в корзину
        Assertions.assertThat(driver.findElement(By.cssSelector("span[id='cart-count']")).getText())
                .as("Товар в корзину не добавлен")
                .isEqualTo("1");
        //обновляем страницу
        driver.navigate().refresh();
        //проверяем, что товар не исчез
        Assertions.assertThat(driver.findElement(By.cssSelector("span[id='cart-count']")).getText())
                .as("После обновления страницы товар не должен быть удален")
                .isEqualTo("1");
    }
}
