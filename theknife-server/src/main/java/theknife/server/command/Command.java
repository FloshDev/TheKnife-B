package theknife.server.command;

import theknife.common.dto.UtenteDTO;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;

/**
 * Operazione eseguibile dal server, una per {@code CommandType} del protocollo
 * (pattern Command, decisione I4).
 * <p>
 * Le implementazioni sono prive di stato: vengono istanziate una volta sola
 * dalla {@code CommandFactory} e riusate da tutti i thread client, quindi non
 * devono conservare nulla fra una chiamata e l'altra.
 * <p>
 * {@code execute} non dichiara eccezioni: ogni errore va tradotto in una
 * {@link Response} con lo {@code ResponseStatus} appropriato. Un'eccezione che
 * sfuggisse da qui ucciderebbe il thread che serve quel client, lasciandolo in
 * attesa di una risposta che non arriva.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public interface Command {

    /**
     * Esegue l'operazione richiesta.
     *
     * @param request la richiesta ricevuta, con il payload da interpretare
     * @param utente  l'utente della sessione corrente, oppure <code>null</code>
     *                se la richiesta non e' autenticata; il gate del dispatcher
     *                garantisce che non sia <code>null</code> per i comandi che
     *                richiedono autenticazione
     * @return la risposta da inviare al client, mai <code>null</code>
     */
    Response execute(Request request, UtenteDTO utente);
}
