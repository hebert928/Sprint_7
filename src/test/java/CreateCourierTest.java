import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CreateCourierTest extends BaseTest{

    @Test
    public void checkCreateCourierSuccess() {
        CourierRequest courierRequest = new CourierRequest("Vasya1234", "228566", "Василий");
        Response response = createCourier(courierRequest);
        response.then().assertThat().body("ok", is(true)).and().statusCode(201);
        CourierLoginRequest courierLoginRequest = new CourierLoginRequest(courierRequest.getLogin(), courierRequest.getPassword());
        deleteCourier(courierLoginRequest);
    }

    @Test
    public void checkCreateTheSameCourierConflict() {
        CourierRequest courierRequest = new CourierRequest("Vasya1234", "228566", "Василий");
        createCourier(courierRequest);
        try {
            createCourier(courierRequest)
                    .then()
                    .assertThat()
                    .body("message", is("Этот логин уже используется. Попробуйте другой."))
                    .and()
                    .statusCode(409);
        } finally {
            CourierLoginRequest courierLoginRequest = new CourierLoginRequest(courierRequest.getLogin(), courierRequest.getPassword());
            deleteCourier(courierLoginRequest);
        }
    }

    @Test
    public void checkCreateCourierWithoutPasswordField() {
        CourierRequest courierRequest = new CourierRequest("Vasya1234", null, "Василий");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courierRequest)
                .post("/api/v1/courier");
        response
                .then()
                .assertThat()
                .body("message", is("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);

        printResponse(response);
    }

    @Step("Send POST request to /api/v1/courier")
    public Response createCourier(CourierRequest courierRequest) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(courierRequest)
                .post("/api/v1/courier");

        printResponse(response);

        return response;
    }

    @Step("Login, get Id and delete courier")
    public void deleteCourier(CourierLoginRequest courierLoginRequest) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(courierLoginRequest)
                .post("/api/v1/courier/login");

        printResponse(response);

        given().delete("/api/v1/courier/" + response.as(CourierIDResponse.class).getId());
    }
}
