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

    /**
     * Service delle recensioni, a cui il comando delega la logica di dominio.
     */
    private final RecensioneService recensioneService;

    /**
     * Costruisce il comando sul service delle recensioni.
     *
     * @param recensioneService il service delle recensioni
     */
    public LeggiRecensioniCommand(RecensioneService recensioneService) {
        this.recensioneService = recensioneService;
    }

    /**
     * Restituisce le recensioni del ristorante indicato, risposte comprese.
     *
     * @param request la richiesta, con un {@link IdRistoranteDTO} come payload
     * @param utente  ignorato: il comando non richiede autenticazione
     * @return SUCCESSO con la lista dei {@link RecensioneDTO} trovati,
     *         eventualmente vuota, ERRORE_VALIDAZIONE se l'identificativo manca
     *         o e' di tipo errato, ERRORE_SERVER se l'accesso al database fallisce
     */
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
