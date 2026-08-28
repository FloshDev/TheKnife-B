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
 * Comando AGGIUNGI_PREFERITO: aggiunge un ristorante ai preferiti del Cliente
 * autenticato. L'identificativo del cliente arriva dall'utente di sessione,
 * mai dal payload: il gate del dispatcher garantisce che sia valorizzato.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class AggiungiPreferitoCommand implements Command {

    /**
     * Service dei ristoranti, a cui il comando delega la logica di dominio.
     */
    private final RistoranteService ristoranteService;

    /**
     * Costruisce il comando sul service dei ristoranti.
     *
     * @param ristoranteService il service dei ristoranti
     */
    public AggiungiPreferitoCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    /**
     * Aggiunge il ristorante indicato ai preferiti del cliente di sessione.
     *
     * @param request la richiesta, con un {@link IdRistoranteDTO} come payload
     * @param utente  il cliente autenticato, garantito non <code>null</code> dal
     *                gate del dispatcher
     * @return SUCCESSO senza payload, ERRORE_VALIDAZIONE se l'identificativo
     *         manca o e' di tipo errato, ERRORE_SERVER se l'accesso al database fallisce
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof IdRistoranteDTO id)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Identificativo del ristorante mancante o non valido.");
        }

        try {
            ristoranteService.aggiungiPreferito(utente.getIdUtente(), id);
            return new Response(ResponseStatus.SUCCESSO, null,
                    "Ristorante aggiunto ai preferiti.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante l'aggiunta ai preferiti: " + e.getMessage());
        }
    }
}
