package theknife.common.protocol;
import java.io.Serializable;
import theknife.common.enums.ResponseStatus;

public class Response implements Serializable{
    private static final long serialVersionUID = 1L;
    private ResponseStatus status;
    private Object payload;
    private String messaggio;
//Costruttore
    public Response(ResponseStatus status, Object payload, String messaggio) {
        this.status = status;
        this.payload = payload;
        this.messaggio = messaggio;
    }
//Setters
    public void setStatus(ResponseStatus x) {
        this.status = x;
    }

    public void setPayload(Object x) {
        this.payload = x;
    }

    public void setMessaggio(String x) {
        this.messaggio = x;
    }

//Getters
    public ResponseStatus getStatus() {
        return status;
    }

    public Object getPayload() {
        return payload;
    } 

    public String getMessaggio() {
        return messaggio;
    }
//Metodo toString
    @Override
    public String toString() {
        return "Response [status=" + status + ", payload=" + payload + 
            ", messaggio=" + messaggio + "]";
    }
    
}
