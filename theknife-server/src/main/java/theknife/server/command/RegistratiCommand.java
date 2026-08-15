package theknife.server.command;

import theknife.common.dto.RegistrazioneDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.UtenteService;

/**
 * Comando REGISTRATI: crea un nuovo utente. Non richiede autenticazione e
 * risponde <b>senza payload</b> (decisione 15): niente auto-login, l'utente
 * accede poi con ACCEDI.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class RegistratiCommand implements Command {

    private final UtenteService utenteService;

    public RegistratiCommand(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof RegistrazioneDTO dati)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Dati di registrazione mancanti o non validi.");
        }

        try {
            utenteService.registra(dati);
            return new Response(ResponseStatus.SUCCESSO, null, "Registrazione completata.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.ERRORE, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante la registrazione: " + e.getMessage());
        }
    }
}
