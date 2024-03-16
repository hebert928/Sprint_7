import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest extends BaseTest{

    @Test
    public void checkGetOrderList() {
        CourierRequest courierRequest = new CourierRequest("Vasya1234", "228566", "Василий"); //1.создать курьера
        createCourier(courierRequest);
        CourierIDResponse courierIDResponse = loginCourier(new CourierLoginRequest(courierRequest.getLogin(), courierRequest.getPassword())); //2.залогиниться
        OrderTrackResponse orderTrackResponse = createOrder(new OrderCreateRequest(
                "Василий",
                "Тёркин",
                "Фрунзенская набережная 46",
                "4",
                "89045678735",
                3,
                "2024-03-23",
                "не звонить в домофон",
                new String[] {"GREY"})); //3.создать заказ
        OrderResponse orderResponse = getOrderId(orderTrackResponse.getTrack()); //4.получиь заказ по его номеру
        int courierId = courierIDResponse.getId();
        acceptOrderByCourier(orderResponse.getOrder().getId(), courierId); //5.принять заказ курьером
        getOrderList(courierId); //6.получить список заказов курьера
        deleteCourier(courierId); //7.удалить курьера
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
    public CourierIDResponse loginCourier(CourierLoginRequest courierLoginRequest) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(courierLoginRequest)
                .post("/api/v1/courier/login");

        printResponse(response);

        return response.as(CourierIDResponse.class);
    }

    @Step("Send POST request to /api/v1/orders")
    public OrderTrackResponse createOrder(OrderCreateRequest orderCreateRequest) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(orderCreateRequest)
                .post("/api/v1/orders");

        printResponse(response);

        return response.as(OrderTrackResponse.class);
    }

    @Step("Send GET request to /api/v1/orders/track")
    public OrderResponse getOrderId(int orderTrack) {
        Response response = given()
                .get("/api/v1/orders/track?t=" + orderTrack);

        printResponse(response);

        return response.as(OrderResponse.class);
    }

    @Step("Send PUT request to /api/v1/orders/accept/:id")
    public void acceptOrderByCourier(int orderId, int courierId) {
        Response response = given()
                .put("/api/v1/orders/accept/" + orderId + "?courierId=" + courierId);

        printResponse(response);
    }

    @Step("Send GET request to /api/v1/orders")
    public void getOrderList(int courierId) {
        Response response = given()
                .get("/api/v1/orders?courierId=" + courierId);
        response.then().assertThat().body("orders", notNullValue()).and().statusCode(200);
    }

    @Step("Send DELETE to /api/v1/courier/:id")
    public void deleteCourier(int courierId) {
        given().delete("/api/v1/courier/" + courierId);
    }
}
