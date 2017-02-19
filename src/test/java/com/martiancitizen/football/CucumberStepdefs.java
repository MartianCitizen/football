package com.martiancitizen.football;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;


public class CucumberStepdefs {

    public static final Supplier<AssertionError> OEE = () -> new AssertionError("Optional does not contain a value");

    private static final Log LOG = LogFactory.getLog(CucumberStepdefs.class);
    private static final String baseUrl = "http://localhost:8083";
    private static final HttpClient client = new HttpClient();

    private Optional<ResponseEntity> responseOpt = Optional.empty();

    @When("^I retrieve the team with the id \"([^\"]*)\"$")
    public void iRetrieveTheTeamWithTheName(String id) throws Throwable {
        String uri = baseUrl + "/team/" + id;
        client.get(uri);
        responseOpt = Optional.of(client.responseOpt.orElseThrow(OEE));
    }

    @Then("^the HTTP status code should be (\\d+)$")
    public void the_HTTP_status_code_should_be(int statusCode) throws Throwable {
        client.isResponseStatus(statusCode);
    }

    @And("^the team should have the name \"([^\"]*)\"$")
    public void teamShouldHaveName(String name) {
        Map<String, Object> data = client.getResponseData();
        assertTrue("Response does not contain a `name` key", data.containsKey("name"));
        String returnedName = data.get("name").toString();
        assertTrue("Invalid team name returned: " + returnedName, name.equals(returnedName));
    }

    @And("^the HTTP content should contain \"([^\"]*)\"$")
    public void the_content_should_contain(String expectedMsg) throws Throwable {
        client.doesResponseContentContain(expectedMsg);
    }
}
