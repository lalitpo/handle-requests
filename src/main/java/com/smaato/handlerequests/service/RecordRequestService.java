package com.smaato.handlerequests.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smaato.handlerequests.constants.HandleRequestsConstant;

@Service
public class RecordRequestService {

    static final Logger applog = Logger.getLogger("debugLogger");

    @Value("${kafka.url}")
    private String kafkaUrl;

    @Value("${kafka.port}")
    private String kafkaPort;

    @Value("${kafka.enabled}")
    private boolean kafkaFlow;

    @Value("${logFile}")
    private String logFileName;

    @Autowired
    private ExternalCallService externalCallService;

    Set<Integer> idMinRec = new HashSet<>();

    /*
     * <<<<<< Extension 2 :
     * Using @Transactional annotation for synchronous acccess for write operation
     */
    @Transactional
    public int recordRequests(int id, boolean returnReqCount) throws FileNotFoundException, IOException {

        idMinRec.add(id);

            applog.info(String.valueOf(id));

            if (returnReqCount) {

                if (kafkaFlow) {
                    publishToKafka(idMinRec.size());
                }
                return idMinRec.size(); // as current request ID has also been added into the log file.

            }

        return idMinRec.size();
    }

    /*
     * <<<<<< Extension 3 : Sending the Id Count to Kafka : Distributed streaming
     * service >>>>>>>>>
     * 
     */
    private void publishToKafka(int trafficCount) {

        externalCallService.callThirdParty(HandleRequestsConstant.CALLING_HOST + kafkaPort.concat(kafkaUrl),
                trafficCount);

    }

}