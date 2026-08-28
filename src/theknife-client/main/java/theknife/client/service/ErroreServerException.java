package theknife.client.service;

import java.io.IOException;

/**
 * Segnala che il server ha risposto con uno stato diverso da {@code SUCCESSO}
 * (validazione fallita, credenziali errate, operazione non autorizzata) —
 * distinta da una vera {@link IOException} di rete (connessione caduta,
 * server irraggiungibile), che i service non lanciano mai esplicitamente,
 * solo {@link theknife.client.network.ServerConnection#inviaRichiesta} può
 * farlo. Estende {@code IOException} apposta: i metodi dei service dichiarano
 * già {@code throws IOException}, così questa distinzione non tocca nessuna
 * firma di metodo, solo il tipo dell'eccezione lanciata.
 * <p>
 * {@link theknife.client.ui.TaskRunner} usa questa distinzione per decidere
 * il messaggio da mostrare: il testo del server (già pensato per l'utente)
 * per questa classe, un messaggio generico di connessione per il resto.
 *
 * @author Barlera Marco, 760000, VA
 */
public class ErroreServerException extends IOException {

    /**
     * Crea l'eccezione col messaggio del server, già in linguaggio utente.
     *
     * @param messaggio il messaggio della {@code Response} non riuscita
     */
    public ErroreServerException(String messaggio) {
        super(messaggio);
    }
}
