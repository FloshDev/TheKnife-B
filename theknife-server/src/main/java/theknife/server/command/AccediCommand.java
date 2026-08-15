package theknife.server.command;

import theknife.common.dto.LoginDTO;
import theknife.common.dto.LoginResultDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.UtenteService;

/**
 * Comando ACCEDI: verifica le credenziali e apre una sessione. Non richiede
 * autenticazione, ovviamente: e' il comando che la produce. Il token viene
 * generato da {@code UtenteService.accedi()} e viaggia nel
 * {@link LoginResultDTO} della risposta (decisioni 12 e 13).
 * <p>
 * Le credenziali sbagliate producono ERRORE, non NON_AUTORIZZATO: il secondo
 * e' riservato al gate del Dispatcher, che riguarda i permessi di un utente
 * gia' riconosciuto. Il messaggio non distingue username inesistente da
 * password errata.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class AccediCommand implements Command {

    private final UtenteService utenteService;

    public AccediCommand(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof LoginDTO credenziali)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Credenziali mancanti o non valide.");
        }

        try {
            LoginResultDTO esito = utenteService.accedi(credenziali);
            return new Response(ResponseStatus.SUCCESSO, esito, "Accesso effettuato.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.ERRORE, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante l'accesso: " + e.getMessage());
        }
    }
}
