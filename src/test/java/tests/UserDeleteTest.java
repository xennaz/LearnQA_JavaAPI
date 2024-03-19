package tests;

import Lib.ApiCoreRequests;
import Lib.Assertions;
import Lib.BaseTestCase;
import Lib.DataGenerator;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


@Epic("CRUD")
@Feature("Delete User")
@Stories({@Story("Service user 2 is not possible to delete"),
        @Story("When authorized user can be deleted"),
@Story("Unauthorized user cannot be deleted")})

public class UserDeleteTest extends BaseTestCase {
    private final Lib.ApiCoreRequests ApiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test unsuccessfully delete user2")
    @DisplayName("Test negative delete of service user2")
    @Owner("xennaz")
    @Severity(SeverityLevel.CRITICAL)

    public void testDeleteUser2() {

        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = ApiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // Delete

        Response responseUserData = ApiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/2", cookie, header);

        Assertions.assertJsonHasField(responseUserData, "error");
    }

    @Test
    @Description("This test successfully delete random user")
    @DisplayName("Test positive delete of authorized user")
    @Owner("xennaz")
    @Severity(SeverityLevel.NORMAL)


    public void testSuccessfulDeleteAuthUser() {
        //GENERATE USER

        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = ApiCoreRequests
                .makePostRequestRegJsonpath("https://playground.learnqa.ru/api/user", userData);

        String userId = responseCreateAuth.getString("id");

        //LOGIN (AUTHORIZATION)
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = ApiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //Delete

        Response deleteUserData = ApiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId, cookie, header);

        //GET

        Response responseUserData = ApiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        cookie, header
                );

        System.out.print(responseUserData.asString());


        String [] unexpectedFields = {"username","firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    @Description("This test unsuccessfully delete user without authorization")
    @DisplayName("Test negative delete of unauthorized user")
    @Owner("xennaz")
    @Severity(SeverityLevel.NORMAL)

    public void testNegativeDeleteUserWOAuth() {
        //GENERATE USER1

        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = ApiCoreRequests
                .makePostRequestRegJsonpath("https://playground.learnqa.ru/api/user", userData);

        String userId = responseCreateAuth.getString("id");

        //Login  user2
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = ApiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //Delete user1

        Response deleteUserData = ApiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId, cookie, header);

        //Get user1 data

        Response responseUserData = ApiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        cookie, header
                );

        Assertions.assertJsonHasField(responseUserData,"username");
    }


















    }
