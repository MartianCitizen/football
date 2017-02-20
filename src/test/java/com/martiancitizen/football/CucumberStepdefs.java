package com.martiancitizen.football;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


public class CucumberStepdefs {

    public static final Supplier<AssertionError> OEE = () -> new AssertionError("Optional does not contain a value");

    private static final Log LOG = LogFactory.getLog(CucumberStepdefs.class);
    private static final String baseUrl = "http://localhost:8083";
    private static final HttpClient client = new HttpClient();

    private Optional<ResponseEntity> responseOpt = Optional.empty();


    /*
    HTTP client stepdefs
     */
    @Then("^the HTTP status code should be (\\d+)$")
    public void the_HTTP_status_code_should_be(int statusCode) throws Throwable {
        client.isResponseStatus(statusCode);
    }


    @And("^the HTTP content should contain \"([^\"]*)\"$")
    public void the_content_should_contain(String expectedMsg) throws Throwable {
        client.doesResponseContentContain(expectedMsg);
    }

    /*
    team endpoint stepdefs
     */

    @When("^I retrieve the team with the id \"([^\"]*)\"$")
    public void iRetrieveTheTeamWithTheId(String id) throws Throwable {
        String uri = baseUrl + "/team/" + id;
        client.get(uri);
        responseOpt = Optional.of(client.responseOpt.orElseThrow(OEE));
    }

    @Then("^the team should have the following properties:$")
    public void teamShouldHaveProperties(DataTable table) throws Throwable {
        Map<String, Object> data = client.getResponseData();
        List<DataTableRow> rows = table.getGherkinRows();
        assertTrue(rows != null && !rows.isEmpty());
        for (DataTableRow row : rows) {
            List<String> cells = row.getCells();
            assertEquals(2, cells.size());
            String propName = cells.get(0);
            String expectedValue = cells.get(1);
            assertTrue(String.format("Response does not contain a `%s` key", propName), data.containsKey(propName));
            String returnedValue = data.get(propName).toString();
            assertEquals(String.format("Invalid %s returned: ", propName), expectedValue, returnedValue);
        }
    }

    /*
    roster endpoint stepdefs
     */

    @When("^I retrieve the roster for the team with the id \"([^\"]*)\"$")
    public void iRetrieveTheRosterForTeamWithTheId(String id) throws Throwable {
        String uri = baseUrl + "/roster/" + id;
        client.get(uri);
        responseOpt = Optional.of(client.responseOpt.orElseThrow(OEE));
    }

    @Then("^the roster should include the following players:$")
    public void rosterShouldHavePlayers(DataTable table) throws Throwable {
        List<LinkedHashMap<String, Object>> data = client.getResponseDataAsList();
        List<DataTableRow> rows = table.getGherkinRows();
        assertTrue(rows != null && !rows.isEmpty());
        for (DataTableRow row : rows) {
            List<String> cells = row.getCells();
            assertEquals(1, cells.size());
            String expectedName = cells.get(0);
            long numMatchingNames = data.stream()
                    .map(player -> player.getOrDefault("name", "N/A"))
                    .filter(expectedName::equals)
                    .count();
            assertEquals(String.format("Invalid number of players with the name %s: ", expectedName), 1, numMatchingNames);
        }
    }
}
