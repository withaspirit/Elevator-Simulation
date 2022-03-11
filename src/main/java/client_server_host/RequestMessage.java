package client_server_host;

/**
 * RequestMessage indicates the String messages exchanged between systems.
 *
 * @author Julian
 */
public enum RequestMessage {
    REQUEST("Request"),
    ACKNOWLEDGE("Message Received"),
    EMPTYQUEUE("Queue is empty"),
    LIGHTON("Light is ON"),
    LIGHTOFF("Light is OFF"),
    DOOROPENED("Door is open"),
    DOORCLOSED("Door is closed");

    private String msg;

    /**
     * Constructor for RequestMessage.
     *
     * @param msg the message of the request
     */
    RequestMessage(String msg) {
        this.msg = msg;
    }

    /**
     * Returns the message associated.
     *
     * @return msg the message of the request
     */
    public String getMsg() {
        return msg;
    }
}
