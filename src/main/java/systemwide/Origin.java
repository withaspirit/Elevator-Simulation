package systemwide;

/**
 * Origin is an enum representing the Runnable system from which Requests come from.
 *
 * @author Liam Tripp
 */
public enum Origin {
    FLOOR_SYSTEM,
    ELEVATOR_SYSTEM,
    SCHEDULER;

    /**
     * Provides an Origin in the opposite direction of the Origin provided.
     * If origin is ELEVATOR_SYSTEM, return FLOOR_SYSTEM. Otherwise, return
     * FLOOR_SUBSYSTEM.
     *
     * @param origin the Origin provided
     * @return return Origin opposite to the one provided
     */
    public static Origin changeOrigin(Origin origin) {
        if (origin == FLOOR_SYSTEM) {
            return ELEVATOR_SYSTEM;
        } else if (origin == ELEVATOR_SYSTEM) {
            return FLOOR_SYSTEM;
        } else {
            throw new IllegalArgumentException("Error: Trying to change origin when origin is Scheduler.");
        }
    }
}