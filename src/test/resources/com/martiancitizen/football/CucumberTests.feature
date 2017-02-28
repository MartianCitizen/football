Feature: Basic endpoint tests


  Scenario: Test the team endpoint with a valid team id

    When I retrieve the team with the id "NEP"
    Then the HTTP status code should be 200
    And the team should have the following properties:
    | name | New England Patriots |
    | conference | AFC            |
    | division   | East           |


  Scenario: Test the team endpoint with an invalid id
    When I retrieve the team with the id "foobar"
    Then the HTTP status code should be 404
    And the HTTP content should contain "No such team"


  Scenario: Test the roster endpoint with a valid team id

    When I retrieve the roster for the team with the id "NEP"
    Then the HTTP status code should be 200
    And the roster should include the following players:
      | Amendola, Danny |
      | Allen, Ryan     |


  Scenario: Test the roster endpoint with an invalid team id
    When I retrieve the roster for the team with the id "foobar"
    Then the HTTP status code should be 404
    And the HTTP content should contain "No such team"
