package theknife.server;

import theknife.common.config.ConfigurazioneServer;
import theknife.server.network.Server;

/**
 * Punto di ingresso del server TheKnife.
 * <p>
 * La porta di ascolto si puo' indicare come primo argomento della riga di
 * comando; senza argomenti vale il default condiviso con il client
 * ({@link ConfigurazioneServer}). E' la simmetria della decisione 27, che
 * prevede la stessa forma lato client: cosi' il collaudo su due macchine non
 * richiede di ricompilare nulla.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class TheKnifeServer {

    /**
     * Avvia il server.
     *
     * @param args opzionalmente la porta di ascolto come primo argomento
     */
    public static void main(String[] args) {

        int porta;
        try {
            porta = leggiPorta(args);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.out.println("Uso: java -jar serverTK.jar [porta]");
            return;
        }

        ConfigurazioneDB config = ConfigurazioneDB.leggiDaTerminale();
        new Server(config, porta).avvia();

    }

    /**
     * Ricava la porta di ascolto dagli argomenti della riga di comando.
     *
     * @param args gli argomenti passati al processo
     * @return la porta indicata, oppure il default condiviso se assente
     * @throws IllegalArgumentException se l'argomento non e' un numero di porta valido
     */
    private static int leggiPorta(String[] args) {
        if (args.length == 0) {
            return ConfigurazioneServer.PORTA_DEFAULT;
        }

        int porta;
        try {
            porta = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Porta non numerica: " + args[0]);
        }

        if (porta < 1 || porta > 65535) {
            throw new IllegalArgumentException("Porta fuori intervallo: " + porta);
        }

        return porta;
    }

}
