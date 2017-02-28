# football
This is a simple Spring web application which demonstrates the language features introduced in Java 8. It includes a basic REST API test suite implemented in  the Cucumber framework.

## To run the app locally

1. Clone the repo to your machine: `git clone https://github.com/MartianCitizen/football.git`
2. cd into the local repo: `cd <your-repo-path>/football`
3. Run the application: `mvn -Ddb.fullpath=./src/main/resources/FootballData.xls spring-boot:run`
4. Verify that the application is running: `curl http://localhost:8083/ping` . The response should be `{"message": "Pong"}`

The Tomcat server will listen on port 8083. If you need to use a different port, edit the file `./football/src/main/resources/application.properties`

## REST API endpoints

- Get metadata for a team: `/team/{id}`, for example: `curl http://localhost:8083/team/NEP`
- Get team roster: `/roster/{team-id}`, for example: `curl http://localhost:8083/roster/NEP`
- Reload the database: `curl http://localhost:8083/refresh`
