package systemwide;

/**
 * SystemStatus indicates the activation status of the system.
 *
 * @author Liam Tripp
 */
public class SystemStatus {

    private boolean systemActivated;

    /**
     * Constructor for SystemStatus
     *
     * @param systemActivated a boolean for whether the system is activated
     */
    public SystemStatus(boolean systemActivated) {
        this.systemActivated = systemActivated;
    }

    /**
     * Sets the activation status of the system.
     *
     * @param systemStatus the new activation status for the system
     */
    public void setSystemActivated(boolean systemStatus) {
        systemActivated = systemStatus;
    }

    /**
     * Indicates whether the system has been activated.
     *
     * @return true if the system is activated, false otherwise
     */
    public boolean activated() {
        return systemActivated;
    }
}
