package theknife.common.config;

/**
 * Contiene i parametri di configurazione condivisi tra client e server.
 * Definisce i valori predefiniti per host e porta del server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public final class ConfigurazioneServer {

/**
 * La porta predefinita del server utilizzata per la comunicazione tra client e server.
 */
    public static final int PORTA_DEFAULT = 9999;

/**
 * L'host predefinito del server utilizzato dal client per connettersi al server.
 */
    public static final String HOST_DEFAULT = "localhost";

/**
 * Costruttore privato per impedire l'instanziazione della classe ConfigurazioneServer.
 * La classe è progettata per essere utilizzata solo come contenitore di costanti di configurazione e non 
 * deve essere istanziata.
 */
    private ConfigurazioneServer() {
        
    }
}
