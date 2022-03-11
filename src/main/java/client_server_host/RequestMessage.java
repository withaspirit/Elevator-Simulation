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
	
	RequestMessage(String msg){
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}
}
