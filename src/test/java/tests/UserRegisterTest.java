package tests;
import Lib.Assertions;
import Lib.BaseTestCase;
import Lib.DataGenerator;
import Lib.ApiCoreRequests;

import io.qameta.allure.*;
import io.restassured.response.Response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@Epic("CRUD")
@Feature("Create User")
@Stories({@Story("Not possible to create user with existing e-mail"),
        @Story("Possible to create user with new e-mail"),
        @Story("Not possible to create user providing unacceptable parameter"),
        @Story("Not possible to create user w/o providing one of the required field"),
        @Story("Not possible to create user providing unacceptable parameters"),})

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests ApiCoreRequests = new ApiCoreRequests();
    @Test
    @Description("This test unsuccessfully create user with existing email")
    @DisplayName("Test negative create user with existing email")
    @Owner("xennaz")
    @Severity(SeverityLevel.CRITICAL)

    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String,String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);


        Response responseCreateAuth = ApiCoreRequests
                .makePostRequestReg("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");

    }

@Test
@Description("This test successfully create user with new email")
@DisplayName("Test positive create user with new email")
@Owner("xennaz")
@Severity(SeverityLevel.BLOCKER)
    public void testCreateUserSuccsessfully(){

        Map<String,String> userData = DataGenerator.getRegistrationData();

    Response responseCreateAuth = ApiCoreRequests
            .makePostRequestReg("https://playground.learnqa.ru/api/user/",userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        System.out.println(responseCreateAuth.asString());
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("This test unsuccessfully create user with unacceptable email")
    @DisplayName("Test negative create user with unacceptable email")
    @Owner("xennaz")
    @Severity(SeverityLevel.NORMAL)

    public void testNegativeCreateUserWithWrongEmail(){
        String email = "vinkotov_example.com";

        Map<String,String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);


        Response responseCreateAuth = ApiCoreRequests
                .makePostRequestReg("https://playground.learnqa.ru/api/user/",userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");

    }

    @Description("This test unsuccessfully create user without one of the required field")
    @DisplayName("Test negative create user when one of the required field is absent")
    @Owner("xennaz")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest
    @ValueSource (strings={"email","password","username","firstName","lastName"})

    public void testNegativeCreateUserWithoutParam(String condition){

        Map<String,String> userData = new HashMap<>();
        userData.put(condition, null);
        userData = DataGenerator.getRegistrationData(userData);

                  Response responseCreateAuth = ApiCoreRequests
                          .makePostRequestReg("https://playground.learnqa.ru/api/user/",userData);
            Assertions.assertResponseCodeEquals(responseCreateAuth,400);
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + condition);
    }
    @Description("This test unsuccessfully create user with unacceptable parameter")
    @DisplayName("Test negative create user when firstName length is more than 250 symbols and 1 symbol")
    @Owner("xennaz")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest
    @ValueSource (ints={1,251})

    public void testNegativeCreateUserWithShortOrLongName(int condition) {
        String username = StringUtils.repeat('x', condition);

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = ApiCoreRequests
                .makePostRequestReg("https://playground.learnqa.ru/api/user/", userData);
        System.out.println(responseCreateAuth.asString());
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        if (condition == 1) {
            Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
        } else {
            Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
        }
    }


           }
