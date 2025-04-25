package stellarBurgers.orders;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.net.HttpURLConnection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderChecks {

    @Step("Проверка успешного создания заказа")
    public int checkOrderCreated(ValidatableResponse createResponse) {
        createResponse.assertThat()
                .statusCode(HttpURLConnection.HTTP_OK);
        createResponse.assertThat()
                .body("name", org.hamcrest.Matchers.notNullValue())
                .body("order.number", org.hamcrest.Matchers.notNullValue())
                .body("success", org.hamcrest.Matchers.equalTo(true));

        int orderNumber = createResponse.extract().path("order.number");

        assertTrue("Номер заказа не может быть 0", orderNumber > 0);

        return orderNumber;
    }

    @Step("Проверка ошибки при создании заказа без ингредиентов")
    public void checkOrderCreationFailedWithoutIngredients(ValidatableResponse response) {
        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, statusCode);
        assertEquals(false, success);
        assertEquals("Ingredient ids must be provided", message);
    }

    @Step("Проверка ошибки при создании заказа с невалидным хешем ингредиента")
    public void checkOrderCreationFailedWithInvalidIngredient(ValidatableResponse response) {
        response.assertThat()
                .statusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    @Step("Проверка успешного получения заказов авторизованного пользователя")
    public void checkOrdersListResponse(ValidatableResponse response) {
        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        List<?> orders = response.extract().path("orders");

        assertEquals(HttpURLConnection.HTTP_OK, statusCode);
        assertTrue("Ожидалось, что success = true", success);
        assertTrue("Список заказов не должен быть null", orders != null);
        assertTrue("Количество заказов не должно превышать 50", orders.size() <= 50);
    }

    @Step("Проверка ошибки при получении заказов без авторизации")
    public void checkGetOrdersUnauthorized(ValidatableResponse response) {
        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, statusCode);
        assertEquals(false, success);
        assertEquals("You should be authorised", message);
    }


}



