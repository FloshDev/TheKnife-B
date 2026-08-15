package theknife.server.command;

import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.RistoranteService;

/**
 * Comando OTTIENI_DETTAGLI_RISTORANTE: restituisce la scheda completa di un
 * ristorante a partire dal suo identificativo. Non richiede autenticazione.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class OttieniDettagliRistoranteCommand implements Command {

    private final RistoranteService ristoranteService;

    public OttieniDettagliRistoranteCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof IdRistoranteDTO id)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Identificativo del ristorante mancante o non valido.");
        }

        try {
            RistoranteDTO ristorante = ristoranteService.ottieniDettagli(id);
            return new Response(ResponseStatus.SUCCESSO, ristorante, "Ristorante trovato.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.NON_TROVATO, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante il recupero del ristorante: " + e.getMessage());
        }
    }
}
