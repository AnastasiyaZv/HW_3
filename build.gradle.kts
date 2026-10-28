plugins {
    id("java")
    id("io.qameta.allure") version "2.11.2"
}

group = "ru.stepup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allure {
    version.set("2.29.0")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.rest-assured:rest-assured:5.5.6")
    implementation("org.assertj:assertj-core:3.27.7")

    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    implementation("com.codeborne:selenide:7.18.1")

    //конфигурация
    implementation("org.aeonbits.owner:owner:1.0.12")

    //Allure
    testImplementation("io.qameta.allure:allure-selenide:2.29.0")
    implementation("io.qameta.allure:allure-rest-assured:2.27.0")
    runtimeOnly("org.aspectj:aspectjweaver:1.9.25.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Test>("Task_1") {
    group = "autotests"
    description = "Запуск тестов с пометкой Selenium для задачи 1"
    useJUnitPlatform{
        includeTags("Selenium")
    }
}

tasks.register<Test>("Task_2") {
    group = "autotests"
    description = "Запуск тестов с пометкой Task_2 для задачи 2"
    useJUnitPlatform{
        includeTags("Task_2")
    }
}

tasks.register<Test>("Task_3") {
    group = "autotests"
    description = "Запуск тестов с пометкой Task_3 для задачи 3"
    useJUnitPlatform{
        includeTags("Task_3")
    }
}

tasks.register<Test>("Task_4") {
    group = "autotests"
    description = "Запуск тестов с пометкой Task_4 для темы 7"
    useJUnitPlatform{
        includeTags("Task_4")
    }
}
tasks.register<Test>("Task_5") {
    group = "autotests"
    description = "Запуск тестов с пометкой Task_5 для темы 9 PageObject"
    useJUnitPlatform{
        includeTags("Task_5")
    }
}

defaultTasks("test")