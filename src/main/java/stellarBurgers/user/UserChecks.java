package stellarBurgers.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.net.HttpURLConnection;
import java.util.Map;

import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

public class UserChecks {

    @Step("Успешное создание пользователя")
    public TokenData created(ValidatableResponse createResponse) {
        boolean created = createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("success");

        assertTrue("Пользователь не был создан", created);

        String accessToken = createResponse.extract().path("accessToken");
        String refreshToken = createResponse.extract().path("refreshToken");

        return new TokenData(accessToken, refreshToken);
    }

    @Step("Не получилось создать пользователя без пароля")
    public void checkFailed(ValidatableResponse response) {
        var body = response
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .extract()
                .body().as(Map.class);

        assertEquals("Email, password and name are required fields", body.get("message"));

    }

    @Step("Попытка создать пользователя, который уже зарегистрирован")
    public void checkDuplicateEmailFailed(ValidatableResponse response) {
        var body = response
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .extract()
                .body().as(Map.class);

        assertEquals("User already exists", body.get("message"));
    }

    @Step("Успешный логин пользователя")
    public void loginSuccess(ValidatableResponse createResponse) {
        boolean login = createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("success");

        assertTrue("Пользователь не авторизован", login);

        String accessToken = createResponse.extract().path("accessToken");
        String refreshToken = createResponse.extract().path("refreshToken");

        assertNotNull("accessToken не должен быть null", accessToken);
        assertNotNull("refreshToken не должен быть null", refreshToken);

    }

    @Step("Попытка авторизации пользователя с неправильным паролем")
    public void checkAuthorizeUserWithIncorrectData(ValidatableResponse response) {
        var body = response
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .extract()
                .body().as(Map.class);

        assertEquals("email or password are incorrect", body.get("message"));
    }

    @Step("Изменение данных авторизованного пользователя")
    public void checkUpdateUserData(ValidatableResponse response, String expectedName, String expectedEmail) {
        boolean success = response
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("success");

        assertTrue("Обновление пользователя не прошло успешно", success);

        String actualName = response.extract().path("user.name");
        String actualEmail = response.extract().path("user.email");

        assertEquals("Имя пользователя не совпадает", expectedName, actualName);
        assertEquals("Email пользователя не совпадает", expectedEmail, actualEmail);
    }

    @Step("Попытка изменить данные без авторизации")
    public void checkUpdateUserUnauthorized(ValidatableResponse response) {
        var body = response
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .extract()
                .body().as(Map.class);

        assertEquals("You should be authorised", body.get("message"));
        assertEquals(false, body.get("success"));
    }

    @Step("Попытка изменить email на уже существующий")
    public void checkUpdateToDuplicateEmail(ValidatableResponse response) {
        var body = response
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .extract()
                .body().as(Map.class);

        assertEquals("Ожидалось сообщение о дублирующемся email",
                "User with such email already exists", body.get("message"));
        assertEquals("Поле success должно быть false", false, body.get("success"));
    }


}
