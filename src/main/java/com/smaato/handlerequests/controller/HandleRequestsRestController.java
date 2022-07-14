package com.smaato.handlerequests.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.smaato.handlerequests.constants.HandleRequestsConstant;
import com.smaato.handlerequests.externalCalls.ExternalCallHandler;
import com.smaato.handlerequests.interfaces.HandleRequestsInterface;
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
public class HandleRequestsRestController implements HandleRequestsInterface {

    @Autowired
    private RecordRequestService recordRequestService;

    @Autowired
    private ExternalCallHandler externalCallHandler;

    /**
	 * Logs the Input ID and calls external entities .
	 * @param id random id input
     * @param endpoint Optional Parameter : Endpoint to call another REST API
     * @return Status(ok/failed) of the Rest call
	 */
    @Override
    @GetMapping("/accept")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<String> handleRequests(@RequestParam int id,
            @RequestParam(value = "endpoint", required = false) String endpoint) {

        try {
            int trafficCount = recordRequestService.recordRequests(id);
            if(endpoint!=null && !endpoint.isEmpty()) {
                    externalCallHandler.callExternal(id, endpoint, trafficCount);
            }
            
        } catch (Exception e) {
            return new ResponseEntity<String>(HandleRequestsConstant.FAILURE_RESPONSE,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(HandleRequestsConstant.SUCCESSFUL_RESPONSE, HttpStatus.OK);
    }
 

}