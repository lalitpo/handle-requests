package com.smaato.handlerequests.executorService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.smaato.handlerequests.service.RecordRequestService;

public class RecordClearExecutorService {
    
    @Autowired
    private RecordRequestService recordRequestService;
    
    private final ScheduledExecutorService recClearExecutor = Executors.newScheduledThreadPool(1);

    public void refreshRecords(){

        final Runnable logRecordRunnable = new Runnable() {
            public void run() {
                recordRequestService.store();
            }
        };

        final Runnable clearRecordRunnable = new Runnable() {
            public void run() {
                recordRequestService.clearRecord();
            }
        };
   
    recClearExecutor.scheduleAtFixedRate(logRecordRunnable, 0, 1, TimeUnit.HOURS);
    recClearExecutor.scheduleAtFixedRate(clearRecordRunnable, 0, 1, TimeUnit.MINUTES);

    }       
}
