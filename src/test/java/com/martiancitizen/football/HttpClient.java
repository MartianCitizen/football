package com.martiancitizen.football;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static com.martiancitizen.football.CucumberStepdefs.OEE;

public class HttpClient {

    private static final Log LOG = LogFactory.getLog(HttpClient.class);

    private static final ObjectMapper JSON = new ObjectMapper();

    static {
        JSON.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }

    public Optional<ResponseEntity> responseOpt = Optional.empty();

    public HttpClient() {
    }

    public void get(String uri){
        responseOpt = Optional.empty();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new CustomHttpErrorHandler());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
        Optional<ResponseEntity<String>> respOptTemp = Optional.empty();
        try {
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> resp = restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.GET, entity, String.class);
            respOptTemp = Optional.of(resp);
            LinkedHashMap<String, Object> jsonMap = new LinkedHashMap<>();
            int resultCode = resp.getStatusCode().value();
            jsonMap.put("code", resultCode);
            jsonMap.put("data", JSON.readValue(resp.getBody(), LinkedHashMap.class));
            ResponseEntity<Map<String, Object>> respJson = new ResponseEntity<>(jsonMap, resp.getStatusCode());
            responseOpt = Optional.of(respJson);
        } catch (JsonParseException e) {
            List<String> errors = new ArrayList<>();
            errors.add(e.toString());
            errors.add("Raw response: " + (respOptTemp.isPresent() ? respOptTemp.get().getBody() : "Not available"));
            throw new AssertionError(errors.stream().collect(Collectors.joining("\n")));
        } catch (Exception e) {
            throw new AssertionError(e.toString());
        }
    }

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


    public void isResponseStatus(int code1) throws Throwable {
        ResponseEntity response = responseOpt.orElseThrow(OEE);
        String responseStr = response.getBody().toString();
        Object obj = response.getBody();
        assertTrue("API response is not a LinkedHashMap", obj instanceof LinkedHashMap);
        LinkedHashMap<String, Object> jsonMap = (LinkedHashMap) obj;
        int resultCode = response.getStatusCode().value();
        assertTrue("Unexpected HTTP status code: " + resultCode + "  message: " + responseStr, resultCode == code1);
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

