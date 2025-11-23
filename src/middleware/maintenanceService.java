package middleware;

import dbEndpoints.Maintenance;

public class maintenanceService {

    private final Maintenance maintenance;

    public maintenanceService() {
        maintenance = new Maintenance();
    }

    public boolean setSystemMaintenance(boolean enable) {
        return maintenance.setMaintenanceMode(enable);
    }

    public boolean isMaintenanceActive() {
        return maintenance.isMaintenanceModeEnabled();
    }

    /**
     * call this method before allowing any critical action.
     * @throws RuntimeException if system is under maintenance.
     */
    public void checkAccess() throws RuntimeException {
        if (maintenance.isMaintenanceModeEnabled()) {
            throw new RuntimeException("System is currently under maintenance. Operations are restricted.");
        }
    }
}
