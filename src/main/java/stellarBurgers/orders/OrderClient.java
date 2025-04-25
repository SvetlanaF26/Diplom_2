package stellarBurgers.orders;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import stellarBurgers.Client;


public class OrderClient extends Client {

    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createOrder(Order order, String token) {
        return spec(BASE_PATH_ORDERS)
                .header("Authorization", token)
                .body(order)
                .when()
                .post()
                .then().log().all();

    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrderWithoutAuth(Order order) {
        return spec(BASE_PATH_ORDERS)
                .body(order)
                .when()
                .post()
                .then().log().all();
    }

    @Step("Получение заказов с авторизацией")
    public ValidatableResponse getUserOrders(String token) {
        return spec(BASE_PATH_ORDERS)
                .header("Authorization", token)
                .when()
                .get()
                .then().log().all();
    }

    @Step("Получение заказов без авторизации")
    public ValidatableResponse getUserOrdersWithoutAuth() {
        return spec(BASE_PATH_ORDERS)
                .when()
                .get()
                .then().log().all();
    }


}
