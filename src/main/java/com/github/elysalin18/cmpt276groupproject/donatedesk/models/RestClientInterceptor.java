package com.github.elysalin18.cmpt276groupproject.donatedesk.models;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class RestClientInterceptor  implements ClientHttpRequestInterceptor{

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }
}
