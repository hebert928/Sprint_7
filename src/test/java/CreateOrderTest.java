import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest extends BaseTest{

    private final OrderCreateRequest orderCreateRequest;

    public CreateOrderTest(OrderCreateRequest orderCreateRequest) {
        this.orderCreateRequest = orderCreateRequest;
    }

    @Parameterized.Parameters
    public static Object[] [] getTestData() {
        return new Object[][] {
                {   new OrderCreateRequest(
                        "Василий",
                        "Тёркин",
                        "Фрунзенская набережная 46",
                        "4",
                        "89045678735",
                        3,
                        "2024-03-23",
                        "не звонить в домофон",
                        new String[] {"GREY"})
                },
                {   new OrderCreateRequest(
                        "Василий",
                        "Тёркин",
                        "Фрунзенская набережная 46",
                        "4",
                        "89045678735",
                        3,
                        "2024-03-23",
                        "не звонить в домофон",
                        new String[] {"BLACK", "GREY"})
                },
                {   new OrderCreateRequest(
                        "Василий",
                        "Тёркин",
                        "Фрунзенская набережная 46",
                        "4",
                        "89045678735",
                        3,
                        "2024-03-23",
                        "не звонить в домофон",
                        new String[0])
                },
        };
    }

    @Test
    public void colorCheckCreateOrder() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(orderCreateRequest)
                .post("/api/v1/orders");

        response.then().assertThat().body("track", notNullValue()).and().statusCode(201);

        printResponse(response);
    }
}
