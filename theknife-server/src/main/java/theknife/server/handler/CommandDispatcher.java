package theknife.server.handler;

import theknife.common.protocol.Response;
import theknife.server.command.Command;
import theknife.common.protocol.Request;
import theknife.common.enums.CommandType;
import theknife.common.enums.ResponseStatus;
import theknife.common.enums.Ruolo;
import theknife.common.dto.UtenteDTO;
import theknife.server.service.SessionManager;

/**
 * Instrada ogni Request al Command che la esegue. Prima di istanziare il
 * Command applica il gate di autorizzazione (decisione 20): risolve il
 * sessionToken in un utente tramite {@link SessionManager} e confronta il
 * ruolo con il requisito di accesso del CommandType, ricavato dalla tabella
 * di autorizzazione del contratto. Se il requisito non e' soddisfatto
 * risponde NON_AUTORIZZATO senza costruire il Command.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class CommandDispatcher {

    private enum Requisito {
        NESSUNO, AUTENTICATO, CLIENTE, RISTORATORE
    }

    private CommandFactory commandFactory;
    private final SessionManager sessionManager;

    public CommandDispatcher(CommandFactory commandFactory, SessionManager sessionManager) {
        this.commandFactory = commandFactory;
        this.sessionManager = sessionManager;
    }

    public Response dispatch(Request request) {
        try {
            if (request == null || request.getCommandType() == null) {
                return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                        "Richiesta priva di comando.");
            }

            UtenteDTO utente = sessionManager.getUtenteFromSession(request.getSessionToken());

            if (!accessoConsentito(request.getCommandType(), utente)) {
                return new Response(ResponseStatus.NON_AUTORIZZATO, null,
                        "Operazione non consentita.");
            }

            Command command = commandFactory.create(request.getCommandType());
            return command.execute(request, utente);
        } catch (IllegalArgumentException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, "Tipo di comando non valido: " + request.getCommandType());
        }
    }

    private boolean accessoConsentito(CommandType comando, UtenteDTO utente) {
        return switch (requisitoDi(comando)) {
            case NESSUNO      -> true;
            case AUTENTICATO  -> utente != null;
            case CLIENTE      -> utente != null && utente.getRuolo() == Ruolo.CLIENTE;
            case RISTORATORE  -> utente != null && utente.getRuolo() == Ruolo.RISTORATORE;
        };
    }

    private Requisito requisitoDi(CommandType comando) {
        return switch (comando) {
            case CERCA_RISTORANTI,
                 OTTIENI_DETTAGLI_RISTORANTE,
                 LEGGI_RECENSIONI,
                 CERCA_VICINO,
                 OTTIENI_LOCALITA_INIZIALE,
                 ACCEDI,
                 REGISTRATI                          -> Requisito.NESSUNO;

            case ESCI                                -> Requisito.AUTENTICATO;

            case AGGIUNGI_PREFERITO,
                 RIMUOVI_PREFERITO,
                 VEDI_PREFERITI,
                 AGGIUNGI_RECENSIONE,
                 MODIFICA_RECENSIONE,
                 ELIMINA_RECENSIONE                  -> Requisito.CLIENTE;

            case AGGIUNGI_RISTORANTE,
                 VEDI_RISTORANTI_GESTITI,
                 LEGGI_RECENSIONI_RISTORANTI_GESTITI,
                 OTTIENI_STATISTICHE_RISTORANTE,
                 RISPONDI_RECENSIONE,
                 ASSOCIA_RISTORANTE                  -> Requisito.RISTORATORE;
        };
    }

}
