package model.client_server;

/**
 * Port indicates the specified ports and their port numbers.
 *
 * @author Liam Tripp
 */
public enum Port {
    CLIENT(30),
    SERVER(69),
    CLIENT_TO_SERVER(23),
    SERVER_TO_CLIENT(25);

    private int number;

    /**
     * Constructor for Port.
     *
     * @param number the number of the port
     */
    Port(int number) {
        this.number = number;
    }

    /**
     * Returns the port's associated port number.
     *
     * @return number the number associated with the port
     */
    public int getNumber() {
        return number;
    }
}
