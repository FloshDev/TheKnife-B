package theknife.server;

import theknife.server.network.Server;

/**
 * Punto di ingresso del server TheKnife.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class TheKnifeServer {
    public static void main(String[] args) {
        
        ConfigurazioneDB config = ConfigurazioneDB.leggiDaTerminale();
        new Server(config).avvia();
    }
    
}
