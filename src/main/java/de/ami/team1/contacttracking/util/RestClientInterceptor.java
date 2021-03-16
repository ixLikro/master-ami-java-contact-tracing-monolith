package de.ami.team1.contacttracking.util;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.IOException;

public class RestClientInterceptor implements ClientRequestFilter, ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        System.out.printf("Executing REST request: %s %s%n", requestContext.getMethod(), requestContext.getUri());
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (responseContext.getStatus() > 201) {
            System.out.printf("REST request %s finished with status code: %s %s%n", requestContext.getMethod(), requestContext.getUri(), responseContext.getStatus());
            if (responseContext.hasEntity()) {
                byte[] responseBody = responseContext.getEntityStream().readAllBytes();
                System.out.printf("Result: %s%n%n", new String(responseBody));
            }
        }
    }
}