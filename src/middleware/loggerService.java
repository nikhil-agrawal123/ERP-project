package middleware;

import dbEndpoints.logPoints;
import dbClasses.logClass;
import java.util.List;

public class loggerService {

    private logPoints loggerRepository;

    public loggerService() {
        this.loggerRepository = new logPoints();
    }

    /**
     * Logs a specific event.
     */
    public void log(String userId, String action, String details) {
        loggerRepository.logAction(userId, action, details);
    }

    /**
     * Gets the list of logs for the UI.
     */
    public List<logClass> getSystemLogs() {
        return loggerRepository.getAllLogs();
    }
}