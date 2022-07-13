package com.smaato.handlerequests.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smaato.handlerequests.constants.HandleRequestsConstant;

@Service
@ComponentScan
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
    
    Map<Integer, Integer> minIdMap = new HashMap<Integer, Integer>();
    
    Set<Integer> currentIdCount = new HashSet<Integer>();

    public void store(){
        minIdMap.forEach((key,value) -> applog.info(key + ":" + value));
        minIdMap.clear();
    }
    
    public void clearRecord(){
        currentIdCount.clear();
    }
    /*
     * <<<<<< Extension 2 :
     * Using @Transactional annotation for synchronous acccess for write operation
     */
    @Transactional
    public int recordRequests(int id, boolean returnReqCount) throws FileNotFoundException, IOException {

        Calendar rightNow = Calendar.getInstance();

        currentIdCount.add(id);
        minIdMap.put(rightNow.get(Calendar.MINUTE), currentIdCount.size());
        minIdMap.entrySet().forEach(System.out::println);
        int currentMinCount = currentIdCount.size();

        if (returnReqCount) {

            if (kafkaFlow) {
               publishToKafka(currentMinCount);
            } 
            return currentMinCount; // as current request ID has also been added into the log file.
        }
        return currentMinCount;
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