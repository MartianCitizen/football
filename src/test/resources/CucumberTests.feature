Feature: Basic tests

  Scenario: Test the team endpoint with a valid team id

    When I retrieve the team with the id "NEP"
    Then the HTTP status code should be 200
    And the team should have the name "New England Patriots"


  Scenario: Test the team endpoint with an invalid id
    When I retrieve the team with the id "foobar"
    Then the HTTP status code should be 404
    And the HTTP content should contain "No such team"