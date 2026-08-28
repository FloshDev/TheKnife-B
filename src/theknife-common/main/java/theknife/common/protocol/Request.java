package theknife.common.protocol;
import java.io.Serializable;
import theknife.common.enums.CommandType;

/**
 * Rappresenta una richiesta inviata dal client al server.
 * 
 * @author Gasparini Lorenzo, 759929, VA
 */

public class Request implements Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e 
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * Il tipo di comando della richiesta.
 */
    private CommandType commandType;
    
/**
 * L'oggetto payload contenente i dati associati alla richiesta.
 */
    private Object payload;
    
/**
 * Il token di sessione associato alla richiesta.
 */
    private String sessionToken;
    
/**
 * L'indirizzo IP del client che ha inviato la richiesta.
 */
    private String indirizzoClient;

//Costruttori
/**
 * Costruisce una nuova richiesta con il tipo di comando, il payload, il token di sessione e l'indirizzo IP del 
 * client specificati.
 * @param commandType il tipo di comando
 * @param payload l'oggetto payload
 * @param sessionToken il token di sessione
 * @param indirizzoClient l'indirizzo del client
 */
    public Request(CommandType commandType, Object payload, String sessionToken, String indirizzoClient) {
        this.commandType = commandType;
        this.payload = payload;
        this.sessionToken = sessionToken;
        this.indirizzoClient = indirizzoClient;
    }

/**
 * Crea una nuova richiesta con il tipo di comando, il payload e il token di sessione specificati.
 * @param commandType il tipo di comando
 * @param payload l'oggetto payload
 * @param sessionToken il token di sessione
 */
    public Request(CommandType commandType, Object payload, String sessionToken) {
        this(commandType, payload, sessionToken, null);
    }

//Setters
/**
 * Imposta il tipo di comando della richiesta.
 * @param commandType il tipo di comando della richiesta
 */
    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

/**
 * Imposta l'oggetto payload della richiesta.
 * @param payload l'oggetto payload della richiesta
 */
    public void setPayload(Object payload) {
        this.payload = payload;
    }

/**
 * Imposta il token di sessione della richiesta.
 * @param sessionToken il token di sessione della richiesta
 */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

/**
 * Imposta l'indirizzo IP del client che ha inviato la richiesta.
 * @param indirizzoClient l'indirizzo del client che ha inviato la richiesta
 */
    public void setIndirizzoClient(String indirizzoClient) {
        this.indirizzoClient = indirizzoClient;
    }

//Getters
/**
 * Restituisce il tipo di comando della richiesta.
 * @return il tipo di comando della richiesta
 */
    public CommandType getCommandType() {
        return commandType;
    }

/**
 * Restituisce il payload della richiesta.
 * @return il payload della richiesta
 */
    public Object getPayload() {
        return payload;
    } 

/**
 * Restituisce il token di sessione associato alla richiesta.
 * @return il token di sessione associato alla richiesta
 */
    public String getSessionToken() {
        return sessionToken;
    }

/**
 * Restituisce l'indirizzo IP del client che ha inviato la richiesta.
 * @return l'indirizzo del client che ha inviato la richiesta
 */
    public String getIndirizzoClient() {
        return indirizzoClient;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto Request.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "Request [commandType=" + commandType + ", payload=" + payload + 
            ", sessionToken=" + sessionToken + ", indirizzoClient=" + indirizzoClient + "]";
    }
}
