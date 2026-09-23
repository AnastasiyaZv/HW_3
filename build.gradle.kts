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
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("io.rest-assured:rest-assured:5.5.6")
    implementation("org.assertj:assertj-core:3.27.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
}

tasks.test {
    useJUnitPlatform()
}
tasks.register<Test>("firstTask") {
    group = "autotests"
    description = "Запуск тестов с пометкой ЛЕКЦИЯ"
    useJUnitPlatform{
        includeTags("Lesson")
    }
}

tasks.register<Test>("secondTask") {
    group = "autotests"
    description = "Запускает все API тесты"
    useJUnitPlatform{
        includeTags("ApiTest")
    }
    doLast {
        println("Test run is over")
    }
}
defaultTasks("secondTask")