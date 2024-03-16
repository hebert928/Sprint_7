import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LoginCourierTest extends BaseTest{

    @Test
    public void checkLoginCourierReturnId() {
        CourierRequest courierRequest = new CourierRequest("Vasya1234", "228566", "Василий");
            createCourier(courierRequest);
            Response response = loginCourier(new CourierLoginRequest(courierRequest.getLogin(), courierRequest.getPassword()));
            response.then().assertThat().body("id", notNullValue()).and().statusCode(200);
            CourierIDResponse courierIDResponse = response.as(CourierIDResponse.class);
            deleteCourier(courierIDResponse.getId());
    }

    @Test
    public void checkLoginWithNonexistentCourier() {
        loginCourier(new CourierLoginRequest("NotVasya", "123456"))
                .then()
                .assertThat()
                .body("message", is("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @Test
    public void checkLoginWithEmptyField() {
       loginCourier(new CourierLoginRequest(null, "123456"))
                .then()
                .assertThat()
                .body("message", is("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
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

    @Step("Send POST request to /api/v1/courier/login")
    public Response loginCourier(CourierLoginRequest courierLoginRequest) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(courierLoginRequest)
                .post("/api/v1/courier/login");

        return response;
    }

    @Step("Send DELETE to /api/v1/courier/:id")
    public void deleteCourier(int id) {
        given().delete("/api/v1/courier/" + id);
    }
}
