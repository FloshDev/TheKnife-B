package theknife.server.command;

import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.service.UtenteService;

/**
 * Comando ESCI: chiude la sessione corrente invalidando il token. Richiede un
 * utente autenticato, di qualunque ruolo. Non ha payload ne' in richiesta ne'
 * in risposta.
 * <p>
 * Non chiude il socket: la connessione resta aperta e il client puo'
 * autenticarsi di nuovo senza riconnettersi.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class EsciCommand implements Command {

    /**
     * Service degli utenti, a cui il comando delega autenticazione e sessioni.
     */
    private final UtenteService utenteService;

    /**
     * Costruisce il comando sul service degli utenti.
     *
     * @param utenteService il service degli utenti
     */
    public EsciCommand(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    /**
     * Invalida il token della richiesta, chiudendo la sessione senza chiudere il socket.
     *
     * @param request la richiesta, di cui si usa il solo token di sessione
     * @param utente  l'utente autenticato, non usato: la sessione si identifica
     *                dal token
     * @return SUCCESSO senza payload, ERRORE_SERVER se la chiusura fallisce
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        try {
            utenteService.esci(request.getSessionToken());
            return new Response(ResponseStatus.SUCCESSO, null, "Sessione chiusa.");

        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante la chiusura della sessione: " + e.getMessage());
        }
    }
}
