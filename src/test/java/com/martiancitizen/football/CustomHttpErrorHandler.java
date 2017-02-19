package com.martiancitizen.football;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;


public class CustomHttpErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return false;  // we always want the RestTemplate call to return
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // should never be called
    }

}
