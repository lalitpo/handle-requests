package com.smaato.handlerequests.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.smaato.handlerequests.constants.HandleRequestsConstants;
import com.smaato.handlerequests.interfaces.HandleRequestsInterface;
import com.smaato.handlerequests.service.ExternalCallService;
import com.smaato.handlerequests.service.RecordRequestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/smaato")
public class HandleRequestsRestController implements ManageRequestsInterface {

    @Autowired
    private RecordRequestService recordRequestService;

    @Autowired
    private ExternalCallService externalCallService;

    @Value("${server.port}")
    private String appPort;

    @Override
    @GetMapping("/accept")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<String> handleRequests(@RequestParam int id,
            @RequestParam(value = "endpoint", required = false) String endpoint) {

        try {

            int trafficCount = recordRequestService.recordRequests(id, endpoint.isEmpty());
            if (!endpoint.isEmpty()) {
                externalCallService.callThirdParty(ManageRequestsConstants.CALLING_HOST + appPort + endpoint,
                        trafficCount);
            }

        } catch (Exception e) {
            return new ResponseEntity<String>(ManageRequestsConstants.FAILURE_RESPONSE,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(ManageRequestsConstants.SUCCESSFUL_RESPONSE, HttpStatus.OK);
    }

}