package com.smaato.handlerequests.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smaato.handlerequests.externalCalls.ExternalCallHandler;

@Service
@ComponentScan
public class RecordRequestService {

    static final Logger applog = Logger.getLogger("debugLogger");

    @Value("${kafka.url}")
    private String kafkaUrl; 

    @Value("${kafka.enabled}")
    private boolean kafkaFlow;
 
    @Autowired
    private ExternalCallHandler externalCallHandler;
    
    Map<String, Integer> minIdMap = new HashMap<String, Integer>();
    
    Set<Integer> currentIdCount = new HashSet<Integer>();

    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public void clearAndStore(){        
        minIdMap.put(dateFormatter.format(Calendar.getInstance().getTime()), 0); 
        applog.debug(dateFormatter.format(Calendar.getInstance().getTime()) + "=" + currentIdCount.size());
        currentIdCount.clear();  
    }
     
    /*
     * <<<<<< Extension 2 :
     * Using @Transactional annotation for synchronous acccess for write operation
     */
    /**
	 * Storing the Input ID into a Log file .
	 * @param id random id input
     * @return Count of IDs in current Minute
	 */
    @Transactional
    public int recordRequests(int id) throws FileNotFoundException, IOException {

        currentIdCount.add(id);
        minIdMap.put(dateFormatter.format(Calendar.getInstance().getTime()), currentIdCount.size());
        int currentMinTraffic = currentIdCount.size();

        if (kafkaFlow) {

            publishToKafka(currentMinTraffic);
        }
        return currentMinTraffic;
    }

    /*
     * <<<<<< Extension 3 : Sending the Id Count to Kafka : Distributed streaming service >>>>>>>>> 
     */
    private void publishToKafka(int trafficCount) {

        externalCallHandler.callExternal(kafkaUrl,
                trafficCount);

    }

}