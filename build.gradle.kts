plugins {
    id("java")
}

group = "ru.stepup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    //implementation("org.junit.jupiter:junit-jupiter")
    implementation("org.junit.jupiter:junit-jupiter:6.1.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.3")
    implementation("org.junit.platform:junit-platform-launcher")
    implementation("io.rest-assured:rest-assured:5.5.6")
    implementation("org.assertj:assertj-core:3.27.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    implementation("com.codeborne:selenide:7.18.1")
    implementation("org.seleniumhq.selenium:selenium-java:4.46.0")
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

defaultTasks("test")