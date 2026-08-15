package theknife.server.command;

import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.RistoranteService;

/**
 * Comando RIMUOVI_PREFERITO: rimuove un ristorante dai preferiti del Cliente
 * autenticato. L'identificativo del cliente arriva dall'utente di sessione,
 * mai dal payload: il gate del dispatcher garantisce che sia valorizzato.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class RimuoviPreferitoCommand implements Command {

    private final RistoranteService ristoranteService;

    public RimuoviPreferitoCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof IdRistoranteDTO id)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Identificativo del ristorante mancante o non valido.");
        }

        try {
            ristoranteService.rimuoviPreferito(utente.getIdUtente(), id);
            return new Response(ResponseStatus.SUCCESSO, null,
                    "Ristorante rimosso dai preferiti.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante la rimozione dai preferiti: " + e.getMessage());
        }
    }
}
