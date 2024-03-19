package tests;

import Lib.ApiCoreRequests;
import Lib.Assertions;
import Lib.BaseTestCase;

import Lib.DataGenerator;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("CRUD")
@Feature("Read User")
@Stories({@Story("Not possible to read user data without auth"),
        @Story("Possible to read user with authorization"),
        @Story("Not possible to read user with authorization under another user")})

public class UserGetTest extends BaseTestCase {

    private final Lib.ApiCoreRequests ApiCoreRequests = new ApiCoreRequests();
    @Test
    @Description("This test unsuccessfully read user w/o auth")
    @DisplayName("Test negative read user w/o auth")
    @Owner("xennaz")
    @Severity(SeverityLevel.CRITICAL)

    public void testGetUserDataNotAuth(){
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();
        System.out.println(responseUserData.asString());

        Assertions.assertJsonHasField(responseUserData,"username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");

    }

    @Test
    @Description("This test successfully read user with auth")
    @DisplayName("Test positive read user witho auth")
    @Owner("xennaz")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserDataAuthAsSameUser(){

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");


        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String [] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @Description("This test unsuccessfully read user data when auth with other user")
    @DisplayName("Test negative read user data when auth with other user")
    @Owner("xennaz")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserDataAuthAsOtherUser(){

        Map<String,String> userData = DataGenerator.getRegistrationData();
        String email = userData.get("email");

        Response responseCreateAuth = ApiCoreRequests
                .makePostRequestReg("https://playground.learnqa.ru/api/user/",userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");


        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", "123");

        Response responseGetAuth = ApiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",authData);

        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");


        Response responseUserData = ApiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/2",
                        cookie,header
                );

        Assertions.assertJsonHasField(responseUserData,"username");

        String [] unexpectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);


    }
}
