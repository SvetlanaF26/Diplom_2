package stellarBurgers.user;

import io.qameta.allure.Step;
import io.restassured.http.Header;
import io.restassured.response.ValidatableResponse;
import stellarBurgers.Client;


public class UserClient extends Client {

    @Step("Создать пользователя")
    public ValidatableResponse createUser(User user) {
        return spec()
                .body(user)
                .when()
                .post("/register")
                .then().log().all();
    }

    @Step("Логин пользователя")
    public ValidatableResponse logIn(Credentials dataUser) {
        return spec()
                .body(dataUser)
                .when()
                .post("/login")
                .then().log().all();
    }

    @Step("Удалить пользователя")
    public ValidatableResponse delete(String accessToken) {
        return spec()
                .header(new Header("Authorization", accessToken))
                .when()
                .delete("/user")
                .then().log().all();
    }

    @Step("Изменить данные пользователя")
    public ValidatableResponse updateUser(String token, User updatedUser) {
        return spec()
                .header("Authorization", token)
                .body(updatedUser)
                .when()
                .patch("/user")
                .then()
                .log().all();
    }

    @Step("Попытка изменить данные пользователя, который не авторизован")
    public ValidatableResponse updateUserWithoutAuth(User updatedUser) {
        return spec()
                .body(updatedUser)
                .when()
                .patch("/user")
                .then().log().all();
    }
}
