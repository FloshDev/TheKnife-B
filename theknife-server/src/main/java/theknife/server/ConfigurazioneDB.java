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

    private static final String NOME_DB = "dbTK";
    private static final int PORTA_DB = 5432;

    private String host;
    private String utente;
    private String password;

    public ConfigurazioneDB (String host, String utente, String password) {

        this.host = host;
        this.utente = utente;
        this.password = password;

    }

    public String getHost() {
        return host;
    }

    public String getUtente() {
        return utente;
    }

    public String getPassword() {
        return password;
    }

    public static String getNomeDb() {
        return NOME_DB;
    }
    
    public static int getPortaDb() {
        return PORTA_DB;
    }

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