package theknife.common.config;

/**
 * Contiene i parametri di configurazione condivisi tra client e server.
 * Definisce i valori predefiniti per host e porta del server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public final class ConfigurazioneServer {

    public static final int PORTA_DEFAULT = 9999;
    public static final String HOST_DEFAULT = "localhost";

// Costruttore privato per impedire l'instanziazione
    private ConfigurazioneServer() {
        
    }
}
