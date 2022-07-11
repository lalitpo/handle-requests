package com.smaato.handlerequests.externalCalls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.smaato.handlerequests.constants.HandleRequestsConstant;

@Service
public class ExternalCallHandler {

    @Autowired
    private RestTemplate restTemplate;

    public int callThirdParty(String uri, int trafficCount) {

        String getUri = createURI(uri, trafficCount);

        int statusCode = 0;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(getUri, String.class); // HTTP-GET
            // request
            statusCode = response.getStatusCode().value();

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * <<<<<< Extension 1 : HTTP-POST request >>>>>>
         * 
         * ResponseEntity<String> response =
         * restTemplate.postForObject(ManageRequestsConstants.CALLING_HOST.concat(getUri
         * ),
         * trafficCount, String.class);
         * 
         */
        return statusCode;
    }

    private String createURI(String uri, int trafficCount) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri)
                .queryParam(HandleRequestsConstant.ENDPOINT_QUERYPARAM, trafficCount);

        return uriBuilder.toUriString();
    }

}