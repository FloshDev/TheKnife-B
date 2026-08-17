package theknife.common.protocol;
import java.io.Serializable;
import theknife.common.enums.CommandType;

/**
 * Rappresenta una richiesta inviata dal client al server.
 * 
 * @author Gasparini Lorenzo, 759929, VA
 */

public class Request implements Serializable{
    private static final long serialVersionUID = 1L;
    private CommandType commandType;
    private Object payload;
    private String sessionToken;
    private String indirizzoClient;
//Costruttori
    public Request(CommandType commandType, Object payload, String sessionToken, String indirizzoClient) {
        this.commandType = commandType;
        this.payload = payload;
        this.sessionToken = sessionToken;
        this.indirizzoClient = indirizzoClient;
    }
    public Request(CommandType commandType, Object payload, String sessionToken) {
        this(commandType, payload, sessionToken, null);
    }
//Setters
    public void setCommandType(CommandType x) {
        this.commandType = x;
    }

    public void setPayload(Object x) {
        this.payload = x;
    }

    public void setSessionToken(String x) {
        this.sessionToken = x;
    }

    public void setIndirizzoClient(String x) {
        this.indirizzoClient = x;
    }
//Getters
    public CommandType getCommandType() {
        return commandType;
    }

    public Object getPayload() {
        return payload;
    } 

    public String getSessionToken() {
        return sessionToken;
    }

    public String getIndirizzoClient() {
        return indirizzoClient;
    }
//Metodo toString
    @Override
    public String toString() {
        return "Request [commandType=" + commandType + ", payload=" + payload + 
            ", sessionToken=" + sessionToken + ", indirizzoClient=" + indirizzoClient + "]";
    }
}
