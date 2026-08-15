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

    private final UtenteService utenteService;

    public EsciCommand(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

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
