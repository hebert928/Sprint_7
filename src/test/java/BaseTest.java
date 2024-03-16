import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;

public class BaseTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    protected void printResponse(Response response) {
        System.out.println(response.body().asString());
    }
}
