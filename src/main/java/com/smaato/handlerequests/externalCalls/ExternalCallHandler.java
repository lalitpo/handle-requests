package com.smaato.handlerequests.externalCalls;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.smaato.handlerequests.constants.HandleRequestsConstant;

@Service
public class ExternalCallHandler {

    private static final Logger EXTLOGGER = LogManager.getLogger(ExternalCallHandler.class);

    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    
    @Autowired
    private RestTemplate restTemplate;

    public int callExternal(String uri, int trafficCount) {

        String getUri = createURI(uri, trafficCount);

        int statusCode = 0;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(getUri, String.class); // HTTP-GET
            // request
            //<<<<<< Extension 1 : HTTP-POST request >>>>>>
            //String response = restTemplate.postForObject(getUri,  trafficCount, String.class);
            statusCode = response.getStatusCode().value();

            EXTLOGGER.debug(dateFormatter.format(Calendar.getInstance().getTime()) + " : "+statusCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return statusCode;
    }

    private String createURI(String uri, int trafficCount) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri)
                .queryParam(HandleRequestsConstant.ENDPOINT_QUERYPARAM, trafficCount);

        return uriBuilder.toUriString();
    }

}