package com.martiancitizen.football;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;


/**
 * Our HttpClient class utilizes the Spring http client but adds functionality useful to our stepdefs.
 */

public class HttpClient {

    private static final Log LOG = LogFactory.getLog(HttpClient.class);
    private static final Supplier<AssertionError> OEE = () -> new AssertionError("Optional does not contain a value");
    private static final ObjectMapper JSON = new ObjectMapper();
    static {
        JSON.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    // Prevent default constructor from being called
    private HttpClient() {
    }

    // This is the only public constructor
    private Optional<ResponseEntity> responseOpt;
    private String baseUrl;
    public HttpClient(String baseUrl) {
        assertNotNull(baseUrl);
        this.baseUrl = baseUrl;
        this.responseOpt = Optional.empty();
    }

    // Issue a GET request to a RESTful endpoint and put the response into a canonical form
    public void get(String uri) {
        makeHttpRequest(uri, HttpMethod.GET, Optional.empty());
    }

    // Issue a POST request to a RESTful endpoint and put the response into a canonical form
    public void post(String uri) {
        makeHttpRequest(uri, HttpMethod.POST, Optional.empty());  // We currently only support empty payload
    }

    private void makeHttpRequest(String uri, HttpMethod method, Optional<Map<String, Object>> payloadOpt) {

        responseOpt = Optional.empty();  // Clear out the previous response

        // Create a request entity that accepts JSON responses
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Create a REST accessor. We set a custom error handler because the default handler throws an exception for non-20x responses
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new CustomHttpErrorHandler());

        URI fullUri = UriComponentsBuilder.fromHttpUrl(baseUrl + uri).build().toUri();
        Optional<ResponseEntity<String>> respOptTemp = Optional.empty();

        try {
            // Call the REST endpoint and return the response
            RequestEntity<Map<String, Object>> request = payloadOpt.isPresent()
                    ? new RequestEntity<>(payloadOpt.get(), headers, method, fullUri)
                    : new RequestEntity<>(headers, method, fullUri);
            ResponseEntity<String> resp = restTemplate.exchange(request, String.class);
            respOptTemp = Optional.of(resp);

            // Create a standardized JSON container for the response so that 20x and error responses have a consistent format
            // for validation. The container has "code" and "data" attributes.
            LinkedHashMap<String, Object> jsonMap = new LinkedHashMap<>();
            int resultCode = resp.getStatusCode().value();
            jsonMap.put("code", resultCode);
            String body = resp.getBody();
            if (body.startsWith("[")) {
                jsonMap.put("data", JSON.readValue(resp.getBody(), ArrayList.class));
            } else {
                jsonMap.put("data", JSON.readValue(resp.getBody(), LinkedHashMap.class));
            }

            // Now create a ResponseEntity to hold the map. This seems excessive, but in a more complete test suite
            // we may have cases where a valid endpoint response is not a map.
            ResponseEntity<Map<String, Object>> respJson = new ResponseEntity<>(jsonMap, resp.getStatusCode());
            responseOpt = Optional.of(respJson);

        } catch (JsonParseException e) {
            // Yes, we could build the error message as a concatenated string, but this code illustrates a more
            // extendable way to handle any number of errors.
            List<String> errors = new ArrayList<>();
            errors.add(e.toString());
            errors.add("Raw response: " + (respOptTemp.isPresent() ? respOptTemp.get().getBody() : "Not available"));
            throw new AssertionError(errors.stream().collect(Collectors.joining("\n")));

        } catch (Exception e) {
            throw new AssertionError(e.toString());
        }
    }

    // Since we are only dealing with documented REST API endpoints, we know the expected response schema for any given request.
    // If any other schema is returned, it is an error. The next two methods handle the cases where the response is a map or a
    // list of maps. We can add other methods as needed.
    public HashMap<String, Object> getResponseData() {
        Object obj = responseOpt.orElseThrow(OEE).getBody();
        assertTrue("API response is not a LinkedHashMap", obj instanceof LinkedHashMap);
        LinkedHashMap<String, Object> jsonMap = (LinkedHashMap) obj;
        assertTrue("Response does not contain a `data` key", jsonMap.containsKey("data"));
        Object data = jsonMap.get("data");
        assertTrue("Response data attribute response is not a LinkedHashMap", data instanceof LinkedHashMap);
        return (HashMap<String, Object>) data;
    }

    public ArrayList<LinkedHashMap<String,Object>> getResponseDataAsList(){
        Object obj = responseOpt.orElseThrow(OEE).getBody();
        assertTrue("API response is not a LinkedHashMap", obj instanceof LinkedHashMap);
        LinkedHashMap<String, Object> jsonMap = (LinkedHashMap) obj;
        assertTrue("Response does not contain a `data` key", jsonMap.containsKey("data"));
        Object data = jsonMap.get("data");
        assertTrue("Response data attribute response is not an ArrayList", data instanceof ArrayList<?>);
        return (ArrayList<LinkedHashMap<String, Object>>) data;
    }


    // The following methods are generally useful.
    public void isResponseStatusOneOf(Integer... validCodes) throws Throwable {
        ResponseEntity response = responseOpt.orElseThrow(OEE);
        String responseStr = response.getBody().toString();
        Object obj = response.getBody();
        assertTrue("API response is not a LinkedHashMap", obj instanceof LinkedHashMap);
        int resultCode = response.getStatusCode().value();
        assertTrue("Unexpected HTTP status code: " + resultCode + "  message: " + responseStr,
                Arrays.stream(validCodes).filter(code -> code == resultCode).count() > 0);
    }

    public void doesResponseContentContain(String expectedMsg) throws Throwable {
        String normalizedMsg = expectedMsg.replaceAll("\\$QUOTE", "\"");
        ResponseEntity response = responseOpt.orElseThrow(OEE);
        String responseMsg = response.getBody().toString();
        assertTrue("\nActual response: " + responseMsg + "\nExpected response: " + normalizedMsg, responseMsg.contains(normalizedMsg));
    }

    private boolean isValidJSON(final String json) throws IOException {
        boolean valid = true;
        try{
            JSON.readTree(json);
        } catch(JsonProcessingException e){
            valid = false;
        }
        return valid;
    }

}

