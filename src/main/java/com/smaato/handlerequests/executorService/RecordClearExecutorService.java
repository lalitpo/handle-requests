package com.smaato.handlerequests.executorService;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.smaato.handlerequests.service.RecordRequestService;

@Component
public class RecordClearExecutorService {
    
    @Autowired
    private RecordRequestService recordRequestService;
    
    private final ScheduledExecutorService recClearExecutor = Executors.newScheduledThreadPool(1);

        @Bean
        public void refreshRecords(){
  
        final Runnable clearRecordRunnable = new Runnable() {
            int lastMinute;
            int currentMinute;
            public void run() {
                lastMinute = currentMinute;
                while (true)
                {   Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    currentMinute = calendar.get(Calendar.MINUTE);
                    if (currentMinute != lastMinute){
                        lastMinute = currentMinute;
                        recordRequestService.clearAndStore();
                    }
                }
            }
        };
   
        recClearExecutor.scheduleAtFixedRate(clearRecordRunnable, 0, 1, TimeUnit.SECONDS);

    }       
}
