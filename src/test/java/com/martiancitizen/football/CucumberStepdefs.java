package com.martiancitizen.football;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


public class CucumberStepdefs {

    private static final Log LOG = LogFactory.getLog(CucumberStepdefs.class);
    private static final HttpClient client = new HttpClient("http://localhost:8083");


    /*
    HTTP client stepdefs
     */
    @Then("^the HTTP status code should be (\\d+)$")
    public void the_HTTP_status_code_should_be(int statusCode) throws Throwable {
        client.isResponseStatus(statusCode);
    }


    // This stepdef is used to verify error messages. The test case will pass if the endpoint response contains the
    // specified string. It is OK if the response contains additional data.
    @And("^the HTTP content should contain \"([^\"]*)\"$")
    public void the_content_should_contain(String expectedMsg) throws Throwable {
        client.doesResponseContentContain(expectedMsg);
    }

    /*
    team endpoint stepdefs
     */
    @When("^I retrieve the team with the id \"([^\"]*)\"$")
    public void iRetrieveTheTeamWithTheId(String id) throws Throwable {
        String uri = "/team/" + id;
        client.get(uri);
    }

    // The following stepdef verifies that exactly the specified set of properties is returned for the team. It is an error if
    // there are additional properties.
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
        String uri = "/roster/" + id;
        client.get(uri);
    }

    // The following stepdef verifies that the specified players exist in the roster. It is OK if the roster
    // contains additional players.
    @Then("^the roster should include the following players:$")
    public void rosterShouldHavePlayers(DataTable table) throws Throwable {
        List<LinkedHashMap<String, Object>> data = client.getResponseDataAsList();
        List<DataTableRow> rows = table.getGherkinRows();
        assertTrue(rows != null && !rows.isEmpty());
        for (DataTableRow row : rows) {
            List<String> cells = row.getCells();
            assertEquals(1, cells.size());
            String expectedName = cells.get(0);
            // Verify that there is exactly one instance of the player.
            long numMatchingNames = data.stream()
                    .map(player -> player.getOrDefault("name", "N/A"))
                    .filter(expectedName::equals)
                    .count();
            assertEquals(String.format("Invalid number of players with the name %s: ", expectedName), 1, numMatchingNames);
        }
    }
}
