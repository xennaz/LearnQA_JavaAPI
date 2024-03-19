package Lib;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Make a GET-request with auth_cookie and token")
    public Response makeGetRequest(String url, String cookie, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie ("auth_sid",cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth_cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie ("auth_sid",cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithHeader(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a Post-request for Auth")
    public Response makePostRequest(String url, Map<String, String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a Post-request for Registration")
    public Response makePostRequestReg(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }




}
