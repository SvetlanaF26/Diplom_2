package stellarBurgers;

import io.restassured.http.ContentType;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Client {
    public static final String BASE_PATH = "/api/auth";
    public static final String BASE_PATH_ORDERS = "/api/orders";

    public RequestSpecification spec() {
        return spec(BASE_PATH);
    }


    public RequestSpecification spec(String basePath) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .baseUri(UrlConfig.BASE_URI)
                .basePath(basePath);
    }


}