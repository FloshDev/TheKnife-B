import java.io.Serializable;

public class Request implements Serializable{
    private static final long serialVersionUID = 1L;
    private CommandType commandType;
    private Object payload;
    private String sessionToken;
//Costruttore
    public Request(CommandType commandType, Object payload, String sessionToken) {
        this.commandType = commandType;
        this.payload = payload;
        this.sessionToken = sessionToken;
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
//Metodo toString
@Override
    public String toString() {
        return "Request [commandType=" + commandType + ", payload=" + payload + 
            ", sessionToken=" + sessionToken + "]";
    }
}
