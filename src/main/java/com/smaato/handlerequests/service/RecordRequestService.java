
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smaato.managerequests.constants.ManageRequestsConstants;

import org.apache.log4j.Logger;

@Service
public class RecordRequestService {

    static final Logger applog = Logger.getLogger("debugLogger");

    @Value("${kafka.url}")
    private String kafkaUrl;

    @Value("${kafka.port}")
    private String kafkaPort;

    @Value("${kafka.enabled}")
    private boolean kafkaFlow;

    @Autowired
    private ExternalCallService externalCallService;

    /*
     * <<<<<< Extension 2 :
     * Using @Transactional annotation for synchronous acccess for write operation
     */
    @Transactional
    public int recordRequests(int id, boolean returnReqCount) throws FileNotFoundException, IOException {

        List<String> logRecord = readLogFile();

        if (!logRecord.contains(String.valueOf(id))) {

            applog.info(id);

            if (returnReqCount) {

                if (kafkaFlow) {
                    publishToKafka(logRecord.size() + 1);
                }
                return logRecord.size() + 1; // as current request ID has also been added into the log file.

            }

        }
        if (kafkaFlow) {
            publishToKafka(logRecord.size());
        }
        return logRecord.size();
    }

    /*
     * <<<<<< Extension 3 : Sending the Id Count to Kafka : Distributed streaming
     * service >>>>>>>>>
     * 
     */
    private void publishToKafka(int trafficCount) {

        externalCallService.callThirdParty(ManageRequestsConstants.CALLING_HOST + kafkaPort.concat(kafkaUrl),
                trafficCount);

    }

    private List<String> readLogFile() {

        List<String> loggedId = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("manage-requests-idTraffic.log"))) {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                loggedId.add(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loggedId;
    }

}