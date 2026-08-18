package theknife.server;

import java.util.Scanner;
import java.io.Console;

/**
 * Configurazione di connessione al database (host, credenziali, nome DB, porta).
 * Legge i parametri da terminale all'avvio del server.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class ConfigurazioneDB {

    /**
     * Nome del database di supporto, fissato dalle specifiche di progetto: non
     * viene chiesto all'avvio perche' non e' una scelta dell'installazione.
     */
    private static final String NOME_DB = "dbTK";

    /** Porta di ascolto di PostgreSQL, quella predefinita del DBMS. */
    private static final int PORTA_DB = 5432;

    /** Host su cui gira PostgreSQL, indicato all'avvio del server. */
    private String host;

    /** Nome dell'utente PostgreSQL con cui il server si connette. */
    private String utente;

    /**
     * Password dell'utente PostgreSQL. Resta in memoria per l'intera vita del
     * processo: serve a ogni apertura di connessione, non solo alla prima.
     */
    private String password;

    /**
     * Costruisce la configurazione con i parametri indicati.
     *
     * @param host     l'host su cui gira PostgreSQL
     * @param utente   il nome dell'utente PostgreSQL
     * @param password la password dell'utente PostgreSQL
     */
    public ConfigurazioneDB (String host, String utente, String password) {

        this.host = host;
        this.utente = utente;
        this.password = password;

    }

    /**
     * Restituisce l'host su cui gira PostgreSQL.
     *
     * @return l'host indicato all'avvio
     */
    public String getHost() {
        return host;
    }

    /**
     * Restituisce il nome dell'utente PostgreSQL.
     *
     * @return il nome utente indicato all'avvio
     */
    public String getUtente() {
        return utente;
    }

    /**
     * Restituisce la password dell'utente PostgreSQL.
     *
     * @return la password indicata all'avvio
     */
    public String getPassword() {
        return password;
    }

    /**
     * Restituisce il nome del database di supporto.
     *
     * @return il nome fissato dalle specifiche di progetto
     */
    public static String getNomeDb() {
        return NOME_DB;
    }

    /**
     * Restituisce la porta di ascolto di PostgreSQL.
     *
     * @return la porta predefinita del DBMS
     */
    public static int getPortaDb() {
        return PORTA_DB;
    }

    /**
     * Chiede da terminale i parametri di connessione al database, come
     * richiesto dalle specifiche all'avvio del server. La password viene letta
     * dalla {@link Console} quando disponibile, cosi' da non comparire a
     * schermo; se il processo non ha una console - come accade lanciandolo da
     * un IDE o con l'input rediretto - si ripiega sulla lettura normale, che e'
     * l'unico modo per non impedire l'avvio in quel contesto.
     *
     * @return la configurazione composta con i valori digitati
     */
    public static ConfigurazioneDB leggiDaTerminale () {

        Scanner sc = new Scanner(System.in);

        System.out.print("Host del database: ");
        String host = sc.nextLine();

        System.out.print("Nome utente: ");
        String utente = sc.nextLine();

        String password;
        Console console = System.console();
        if (console != null) {
            password = new String(console.readPassword("Password: "));
        } else {
            System.out.print("Password: ");
            password = sc.nextLine();
        }

        System.out.println("Connessione a " + host 
            + ":" + PORTA_DB + "/" + NOME_DB + " come " + utente);


        return new ConfigurazioneDB(host, utente, password);

    }

}