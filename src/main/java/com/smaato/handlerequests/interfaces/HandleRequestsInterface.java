package com.smaato.handlerequests.interfaces;

import org.springframework.http.ResponseEntity;

public interface HandleRequestsInterface {

    ResponseEntity<String> handleRequests(int id, String endpoint);

}
