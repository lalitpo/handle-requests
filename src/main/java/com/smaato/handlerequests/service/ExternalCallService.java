import com.smaato.managerequests.externalCalls.ExternalCallHandler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalCallService {

    static Logger externalCallLog = Logger.getLogger("reportsLogger");

    @Autowired
    private ExternalCallHandler callHandler;

    public void callThirdParty(String uri, int trafficCount) {

        int statusCode = callHandler.callThirdParty(uri, trafficCount);

        externalCallLog.info(statusCode);
    }

}