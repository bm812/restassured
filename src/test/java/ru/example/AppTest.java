package ru.example;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.core.IsIterableContaining.hasItem;

public class AppTest 
{
    private static RequestSpecification requestSpec;
    private static ResponseSpecification responseSpec;

    private static void setupRequest() {
        String baseURI = "https://www.googleapis.com/customsearch/v1";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", "AIzaSyA4juEzAjVFoyK64ZVG0D_S-96sR_HsLtg");
        params.put("q", "7703228474");
        params.put("cx", "009273001490562717861:hi7m1d4fgss");
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .addParams(params)
                .setContentType(ContentType.JSON)
                .build();
    }

    private static void setupExpectedResponse() {
        responseSpec = new ResponseSpecBuilder()
                .expectBody("queries.request.title", hasItem("Google Custom Search - 7703228474"))
                .expectBody("queries.request.totalResults", hasItem("0"))
                .expectBody("queries.request.searchTerms",  hasItem("7703228474"))
                .expectBody("queries.request.count",  hasItem(10))
                .expectBody("queries.request.inputEncoding",  hasItem("utf8"))
                .expectBody("queries.request.outputEncoding",  hasItem("utf8"))
                .expectBody("queries.request.safe",  hasItem("off"))
                .expectBody("queries.request.cx",  hasItem("009273001490562717861:hi7m1d4fgss"))
                .build();
    }

    @BeforeClass
    public static void setup() {
        setupRequest();
        setupExpectedResponse();
    }

    @Test(description="Verify status code is 200")
    public void Test01_statusCodeIs200() {
        given(requestSpec).get().then().statusCode(HttpStatus.SC_OK);
    }

    @Test (description="Verify content type is JSON")
    public void Test02_checkContentType() {
        given(requestSpec).get().then().contentType(ContentType.JSON);
    }

    @Test (description="Verify response body")
    public void Test03_checkBody() {
        given(requestSpec).get().then().spec(responseSpec);
    }

    @Test (description="Validate JSON schema")
    public void Test04_validateJSON() {
        given(requestSpec).get().then().body(matchesJsonSchemaInClasspath("json-schema.json"));
    }
}
