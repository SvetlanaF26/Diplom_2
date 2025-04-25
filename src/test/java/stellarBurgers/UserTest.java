package stellarBurgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;

import org.junit.After;
import org.junit.Test;

import stellarBurgers.user.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserTest {

    private final UserClient client = new UserClient();
    private final UserChecks check = new UserChecks();
    private String accessToken;
    private String refreshToken;

    @Test
    @DisplayName("Успешное создание пользователя")
    public void setUser() {
        var user = User.random();
        ValidatableResponse createResponse = client.createUser(user);

        TokenData tokenData = check.created(createResponse);

        accessToken = tokenData.getAccessToken();
        refreshToken = tokenData.getRefreshToken();

        assertThat(accessToken, not(emptyOrNullString()));
        assertThat(refreshToken, not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Негативная проверка, пользователь без пароля")
    public void cannotCreateWithoutPassword() {
        var user = User.randomWithoutPassword();
        ValidatableResponse createResponse = client.createUser(user);
        check.checkFailed(createResponse);
    }

    @Test
    @DisplayName("Ошибка при создании пользователя, который уже зарегистрирован")
    public void cannotCreateCourierWithExistingEmail() {
        var user = User.random();
        ValidatableResponse successResponse = client.createUser(user);
        check.created(successResponse);

        ValidatableResponse errorResponse = client.createUser(user);
        check.checkDuplicateEmailFailed(errorResponse);

    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void successfulUserAuthorization() {

        var user = User.random();

        ValidatableResponse createResponse = client.createUser(user);
        check.created(createResponse);

        Credentials credentials = Credentials.fromUser(user);
        ValidatableResponse loginResponse = client.logIn(credentials);

        check.loginSuccess(loginResponse);
    }

    @Test
    @DisplayName("Ошибка при авторизации пользователя с неправильным паролем")
    public void cannotAuthorizeUserWithIncorrectPassword() {
        var user = User.random();

        ValidatableResponse createResponse = client.createUser(user);
        check.created(createResponse);

        var credentialWithIncorrectPassword = new Credentials(user.getEmail(), "0000");
        ValidatableResponse loginErrorResponse = client.logIn(credentialWithIncorrectPassword);
        check.checkAuthorizeUserWithIncorrectData(loginErrorResponse);
    }

    @Test
    @DisplayName("Ошибка при авторизации пользователя с неправильным email")
    public void cannotAuthorizeUserWithIncorrectEmail() {
        var user = User.random();

        ValidatableResponse createResponse = client.createUser(user);
        check.created(createResponse);

        var credentialWithIncorrectEmail = new Credentials("UserFalse@yandex.ru", user.getPassword());
        ValidatableResponse loginErrorResponse = client.logIn(credentialWithIncorrectEmail);
        check.checkAuthorizeUserWithIncorrectData(loginErrorResponse);
    }

    @Test
    @DisplayName("Успешное изменение данных авторизованного пользователя")
    public void shouldSuccessfullyModifyUserData() {
        var user = User.random();

        ValidatableResponse createResponse = client.createUser(user);
        check.created(createResponse);
        TokenData tokens = check.created(createResponse);

        Credentials credentials = Credentials.fromUser(user);
        ValidatableResponse loginResponse = client.logIn(credentials);

        check.loginSuccess(loginResponse);

        String updatedName = "UpdatedName";
        String updatedEmail = "updated_" + user.getEmail();
        User updatedUser = user.withUpdatedData(updatedEmail, updatedName);


        ValidatableResponse updateResponse = client.updateUser(tokens.getAccessToken(), updatedUser);

        check.checkUpdateUserData(updateResponse, updatedName, updatedEmail);

    }

    @Test
    @DisplayName("Обновление данных без авторизации — должно вернуть 401 Unauthorized")
    public void shouldNotAllowUpdateWithoutAuth() {

        User user = User.random();

        String updatedName = "NoAuthUser";
        String updatedEmail = "noauth_" + user.getEmail();
        User updatedUser = user.withUpdatedData(updatedEmail, updatedName);

        ValidatableResponse response = client.updateUserWithoutAuth(updatedUser);


        check.checkUpdateUserUnauthorized(response);
    }

    @Test
    @DisplayName("Обновление email на уже существующий — ошибка 403")
    public void shouldNotAllowUpdatingToDuplicateEmail() {

        User existingUser = User.random();
        ValidatableResponse response1 = client.createUser(existingUser);
        check.created(response1);


        User userToUpdate = User.random();
        ValidatableResponse response2 = client.createUser(userToUpdate);
        TokenData token = check.created(response2);


        Credentials credentials = Credentials.fromUser(userToUpdate);
        ValidatableResponse loginResponse = client.logIn(credentials);
        check.loginSuccess(loginResponse);


        User duplicateEmailUser = userToUpdate.withUpdatedData(existingUser.getEmail(), "NameDoesNotMatter");
        ValidatableResponse updateResponse = client.updateUser(token.getAccessToken(), duplicateEmailUser);


        check.checkUpdateToDuplicateEmail(updateResponse);
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            client.delete(accessToken);
            accessToken = null;
        }
    }
}
