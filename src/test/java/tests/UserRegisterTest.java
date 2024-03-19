package tests;
import Lib.Assertions;
import Lib.BaseTestCase;
import Lib.DataGenerator;
import Lib.ApiCoreRequests;

import io.restassured.response.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests ApiCoreRequests = new ApiCoreRequests();
    @Test

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
    public void testCreateUserSuccsessfully(){

        Map<String,String> userData = DataGenerator.getRegistrationData();

    Response responseCreateAuth = ApiCoreRequests
            .makePostRequestReg("https://playground.learnqa.ru/api/user/",userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        System.out.println(responseCreateAuth.asString());
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test

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
