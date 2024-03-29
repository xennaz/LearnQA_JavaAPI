package tests;
import Lib.Assertions;
import Lib.BaseTestCase;
import Lib.ApiCoreRequests;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;


import org.junit.jupiter.api.DisplayName;


@Epic("Authorization cases")
@Feature("Authorization")
@Stories({@Story("User should be authorized when authorization cookie and token are provided"),
        @Story("User shouldn't be authorized when both or only cookie/token are provided")})

        public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;

    private final ApiCoreRequests ApiCoreRequests = new ApiCoreRequests();

    @BeforeEach

    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = ApiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",authData);

        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    @Description("This test successfully authorize user by email and password")
    @DisplayName("Test positive auth user")
    @Owner("LearnQA")
    @Severity(SeverityLevel.CRITICAL)
    public void testAuthUser(){
               Response responseCheckAuth = ApiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/auth",
                        this.cookie,this.header
                );

        Assertions.AssertJsonByName(responseCheckAuth,"user_id", this.userIdOnAuth);
    }

    @Description("This test checks authorization status w/o sending aut cookie or token")
    @DisplayName("Test negative auth user")
    @Owner("LearnQA")
    @Severity(SeverityLevel.CRITICAL)
    @ParameterizedTest
    @ValueSource(strings={"cookie", "headers"})

    public void testNegativeAuthUser(String condition){

                if(condition.equals("cookie")){
            Response responseCheckAuth = ApiCoreRequests.makeGetRequestWithCookie(
                    "https://playground.learnqa.ru/api/user/auth",
                    this.cookie);
            Assertions.AssertJsonByName(responseCheckAuth,"user_id", 0);
        }else if (condition.equals("headers")) {
            Response responseCheckAuth = ApiCoreRequests.makeGetRequestWithHeader(
                    "https://playground.learnqa.ru/api/user/auth",
                    this.header);
            Assertions.AssertJsonByName(responseCheckAuth,"user_id", 0);

        }
            else {
                    throw new IllegalArgumentException("Condition value is not known: " + condition);
                }


    }
}
