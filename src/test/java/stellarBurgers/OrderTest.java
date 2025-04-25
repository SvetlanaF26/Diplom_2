package stellarBurgers;

import io.qameta.allure.junit4.DisplayName;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarBurgers.orders.IngredientsClient;
import stellarBurgers.orders.Order;
import stellarBurgers.orders.OrderChecks;
import stellarBurgers.orders.OrderClient;
import stellarBurgers.user.User;
import stellarBurgers.user.UserClient;
import stellarBurgers.user.UserChecks;
import stellarBurgers.user.TokenData;

import java.util.List;


public class OrderTest {

    private final UserClient userClient = new UserClient();
    private final UserChecks userChecks = new UserChecks();
    private final OrderClient orderClient = new OrderClient();
    private final OrderChecks orderChecks = new OrderChecks();
    private final IngredientsClient ingredientsClient = new IngredientsClient();

    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = User.random();
        ValidatableResponse createResponse = userClient.createUser(user);
        TokenData tokens = userChecks.created(createResponse);
        accessToken = tokens.getAccessToken();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthTest() {
        List<String> ingredientIds = ingredientsClient.getValidIngredientIds();
        Order order = new Order(ingredientIds);

        ValidatableResponse response = orderClient.createOrder(order, accessToken);

        orderChecks.checkOrderCreated(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        List<String> ingredientIds = ingredientsClient.getValidIngredientIds();
        Order order = new Order(ingredientIds);
        ValidatableResponse response = orderClient.createOrderWithoutAuth(order);

        orderChecks.checkOrderCreated(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order(); // пустой список по умолчанию
        ValidatableResponse response = orderClient.createOrder(order, accessToken);

        orderChecks.checkOrderCreationFailedWithoutIngredients(response);
    }

    @Test
    @DisplayName("Создание заказа с невалидным хешем ингредиента")
    public void createOrderWithInvalidIngredientTest() {
        Order order = new Order("invalid_ingredient_hash_123");
        ValidatableResponse response = orderClient.createOrder(order, accessToken);

        orderChecks.checkOrderCreationFailedWithInvalidIngredient(response);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getOrdersWithAuthTest() {

        List<String> ingredientIds = ingredientsClient.getValidIngredientIds();
        Order order = new Order(ingredientIds);
        orderClient.createOrder(order, accessToken);

        ValidatableResponse response = orderClient.getUserOrders(accessToken);

        orderChecks.checkOrdersListResponse(response);
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    public void getOrdersWithoutAuthTest() {
        ValidatableResponse response = orderClient.getUserOrdersWithoutAuth();
        orderChecks.checkGetOrdersUnauthorized(response);
    }

    @After
    public void tearDown() {

        if (accessToken != null) {
            userClient.delete(accessToken);
            accessToken = null;
        }
    }
}





