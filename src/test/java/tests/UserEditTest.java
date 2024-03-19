package tests;

import Lib.ApiCoreRequests;
import Lib.Assertions;
import Lib.BaseTestCase;
import Lib.DataGenerator;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

@Epic("CRUD")
@Feature("Edit User")
@Stories({@Story("New user can be edited"),
        @Story("Not possible to edit user w/o authorization"),
        @Story("Not possible to edit user when authorized under another user"),
@Story("Not possible to edit user providing unacceptable data")})

public class UserEditTest extends BaseTestCase {
    private final Lib.ApiCoreRequests ApiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test successfully edit new user")
    @DisplayName("Test positive edit of new user")
    @Owner("xennaz")
    @Severity(SeverityLevel.NORMAL)

    public void testEditJustCreatedTest(){
        //GENERATE USER

        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        String userId = responseCreateAuth.getString("id");

        //LOGIN (AUTHORIZATION)
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth,"auth_sid" ))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth,"auth_sid" ))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.AssertJsonByName(responseUserData,"firstName", newName);

    }

    @Test
    @Description("This test unsuccessfully edit user w/o authorization")
    @DisplayName("Test negative edit of user w/o auth")
    @Owner("xennaz")
    @Severity(SeverityLevel.CRITICAL)

    public void testNegativeEditWithoutAuthorization(){
        //GENERATE USER

        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = ApiCoreRequests
                .makePostRequestRegJsonpath("https://playground.learnqa.ru/api/user",userData);

        String userId = responseCreateAuth.getString("id");

        //EDIT

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = ApiCoreRequests
                .makePutRequestWOAuth("https://playground.learnqa.ru/api/user/" + userId, editData);


        //LOGIN (AUTHORIZATION)
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = ApiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",authData);
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");


        //GET

        Response responseUserData = ApiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/"+ userId,
                        cookie,header
                );

        System.out.print(responseUserData.asString());
        Assertions.AssertJsonByName(responseUserData,"firstName", userData.get("firstName"));

   }


    @Test
    @Description("This test unsuccessfully edit user when authorized under another user")
    @DisplayName("Test negative edit of user when auth under another user")
    @Owner("xennaz")
    @Severity(SeverityLevel.CRITICAL)

    public void testNegativeEditAnotherUser() {
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

        //EDIT

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = ApiCoreRequests
                .makePutRequestWithAuth("https://playground.learnqa.ru/api/user/2", editData, cookie, header);

        //GET

        Response responseUserData = ApiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        cookie, header
                );

        System.out.print(responseUserData.asString());
        Assertions.AssertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    @Description("This test unsuccessfully edit auth user with unacceptable data")
    @DisplayName("Test negative edit of auth user for mew email w/o @")
    @Owner("xennaz")
    @Severity(SeverityLevel.NORMAL)

    public void testNegativeEditWrongEmail() {
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

        //EDIT
        String newEmail = "learnqaexample.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newEmail);

        Response responseEditUser = ApiCoreRequests
                .makePutRequestWithAuth("https://playground.learnqa.ru/api/user/" + userId, editData, cookie, header);

        //GET

        Response responseUserData = ApiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        cookie, header
                );

        System.out.print(responseUserData.asString());
        Assertions.AssertJsonByName(responseUserData, "email", userData.get("email"));
    }

    @Test
    @Description("This test unsuccessfully edit auth user with unacceptable data")
    @DisplayName("Test negative edit of auth user for 1 symbol new firstname")
    @Owner("xennaz")
    @Severity(SeverityLevel.NORMAL)

    public void testNegativeEditShortName() {
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

        //EDIT
        String newFirstName = "M";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newFirstName);

        Response responseEditUser = ApiCoreRequests
                .makePutRequestWithAuth("https://playground.learnqa.ru/api/user/" + userId, editData, cookie, header);

        //GET

        Response responseUserData = ApiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        cookie, header
                );

        System.out.print(responseUserData.asString());
        Assertions.AssertJsonByName(responseUserData, "firstName", userData.get("firstName"));

    }









}
