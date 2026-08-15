package theknife.server.command;

import java.util.List;

import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.RecensioneService;

/**
 * Comando LEGGI_RECENSIONI: restituisce le recensioni di un ristorante, con le
 * eventuali risposte del gestore. Non richiede autenticazione.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class LeggiRecensioniCommand implements Command {

    private final RecensioneService recensioneService;

    public LeggiRecensioniCommand(RecensioneService recensioneService) {
        this.recensioneService = recensioneService;
    }

    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof IdRistoranteDTO id)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Identificativo del ristorante mancante o non valido.");
        }

        try {
            List<RecensioneDTO> recensioni = recensioneService.leggiRecensioni(id);
            return new Response(ResponseStatus.SUCCESSO, recensioni, "Recensioni recuperate.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante il recupero delle recensioni: " + e.getMessage());
        }
    }
}
