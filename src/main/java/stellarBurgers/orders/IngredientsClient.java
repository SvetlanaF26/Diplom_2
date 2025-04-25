package stellarBurgers.orders;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarBurgers.Client;

import java.util.List;

public class IngredientsClient extends Client {

    @Step("Получение ингредиентов")
    public Response getIngredients() {
        return spec("/api/ingredients")
                .when()
                .get();
    }

    @Step("Получение валидных id ингредиентов")
    public List<String> getValidIngredientIds() {
        return getIngredients()
                .then()
                .statusCode(200)
                .extract()
                .path("data._id");
    }
}
