
Feature: Basic functional tests for REST API endpoints


  # Basic tests for valid endpoints

  Scenario: Test the teams endpoint
    When I retrieve all of the teams
    Then the HTTP status code should be 200
    And the response should include the following names:
      | New England Patriots |
      | Dallas Cowboys       |


  Scenario: Test the team endpoint with a valid team id
    When I retrieve the team with the id "NEP"
    Then the HTTP status code should be 200
    And the team should have the following properties:
      | name       | New England Patriots |
      | conference | AFC                  |
      | division   | East                 |


  Scenario: Test the team endpoint with an invalid id
    When I retrieve the team with the id "foobar"
    Then the HTTP status code should be 404
    And the HTTP content should contain "No such team"


  Scenario: Test the roster endpoint with a valid team id
    When I retrieve the roster for the team with the id "NEP"
    Then the HTTP status code should be 200
    And the response should include the following names:
      | Amendola, Danny |
      | Allen, Ryan     |


  Scenario: Test the roster endpoint with an invalid team id
    When I retrieve the roster for the team with the id "foobar"
    Then the HTTP status code should be 404
    And the HTTP content should contain "No such team"


  #  HTTP request error cases

  Scenario: Verify that requesting an unsupported endpoint returns a 404
    When I send a GET request to the endpoint "foobar"
    Then the HTTP status code should be 404
    And the HTTP content should contain "Not Found"


  Scenario: Verify that requesting an unsupported HTTP method returns a 405
    When I send a POST request to the endpoint "teams"
    Then the HTTP status code should be 405
    And the HTTP content should contain "Method Not Allowed"
