# football
This is a simple Spring web application which demonstrates the language features introduced in Java 8. It includes a basic REST API test suite implemented in  the Cucumber framework.

The following instructions show you how to run the application and test suite on MacOS.

## To run the app locally

1. Open a terminal window and verify that you have Java 8 installed: `java -version`. The version should be 1.8.xx.
2. Clone the repo to your machine: `git clone https://github.com/MartianCitizen/football.git`
3. cd into the local repo: `cd <your-repo-path>/football`
4. Run the application: `mvn -Ddb.fullpath=./src/main/resources/FootballData.xls spring-boot:run`
5. Verify that the application is running: `curl http://localhost:8083/ping` . The response should be `{"message": "Pong"}`

The Tomcat server will listen on port 8083. If you need to use a different port, edit the file `./football/src/main/resources/application.properties`

## To run the Cucumber test suite

1. Start the app as described above
2. Open another terminal window and cd into the local repo: `cd <your-repo-path>/football`
3. Run the tests: `mvn test`

## REST API endpoints

- Get the list of teams: `curl http://localhost:8083/teams`
- Get metadata for a team: `/team/{id}`, for example: `curl http://localhost:8083/team/NEP`
- Get team roster: `/roster/{team-id}`, for example: `curl http://localhost:8083/roster/NEP`
- Reload the database: `curl http://localhost:8083/refresh`
