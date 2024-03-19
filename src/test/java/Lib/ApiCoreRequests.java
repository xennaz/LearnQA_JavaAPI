package Lib;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Make a GET-request with auth_cookie and token")
    @Attachment()
    public Response makeGetRequest(String url, String cookie, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie ("auth_sid",cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth_cookie only")
    @Attachment()
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie ("auth_sid",cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    @Attachment()
    public Response makeGetRequestWithHeader(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a Post-request for Auth")
    @Attachment()
    public Response makePostRequest(String url, Map<String, String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a Post-request for Registration")
    @Attachment()
    public Response makePostRequestReg(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step ("Make a Post_request for Registration with parsing data")
    @Attachment()
    public JsonPath makePostRequestRegJsonpath(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .jsonPath();
    }

    @Step("Make a Put_request without authorithation")
    @Attachment()
    public Response makePutRequestWOAuth(String url, Map<String, String> editData) {
        return given()
                .filter(new AllureRestAssured())
                .body(editData)
                .put(url)
                .andReturn();
    }

    @Step ("Make a Put_request with authorithation")
    @Attachment()
    public Response makePutRequestWithAuth(String url, Map<String, String> editData,String cookie, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie ("auth_sid",cookie)
                .body(editData)
                .put(url)
                .andReturn();
    }

    @Step("Make a Delete-request with auth_cookie and token")
    @Attachment()
    public Response makeDeleteRequest(String url, String cookie, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie ("auth_sid",cookie)
                .delete(url)
                .andReturn();
    }





}
