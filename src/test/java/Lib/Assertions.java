package Lib;
import io.restassured.response.Response;


import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {

    public static void AssertJsonByName(Response Response, String name, int expectedValue){

        Response.then().assertThat().body("$", hasKey(name));
        int value = Response.jsonPath().getInt(name);

        assertEquals(expectedValue, value, "JSON value isn't equal to expected value");

    }
    public static void AssertJsonByName(Response Response, String name, String expectedValue){

        Response.then().assertThat().body("$", hasKey(name));
        String value = Response.jsonPath().getString(name);

        assertEquals(expectedValue, value, "JSON value isn't equal to expected value");

    }



    public static void assertResponseTextEquals(Response Response, String expectedAnswer){

        assertEquals(expectedAnswer,
                    Response.asString(),
                "Response text is not as expected");
    }
    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode){

        assertEquals(expectedStatusCode,
                Response.statusCode(),
                "Response status code is not as expected");
    }

    public static void assertJsonHasField(Response Response, String expectedFieldName){
        Response.then().assertThat().body("$", hasKey(expectedFieldName));

    }

    public static void assertJsonHasFields(Response Response,String [] expectedFieldNames){
        for (String expectedFiledName : expectedFieldNames){
            Assertions.assertJsonHasField(Response, expectedFiledName);}
    }

    public static void assertJsonHasNotField(Response Response, String unexpectedFieldName){
        Response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));

    }
    public static void assertJsonHasNotFields(Response Response,String [] unexpectedFieldNames) {
        for (String unexpectedFiledName : unexpectedFieldNames) {
            Assertions.assertJsonHasNotField(Response, unexpectedFiledName);
        }
    }


}
