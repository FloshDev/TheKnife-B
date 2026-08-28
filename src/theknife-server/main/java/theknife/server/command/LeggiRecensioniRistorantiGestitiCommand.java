package theknife.server.command;

import java.util.List;

import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.DataAccessException;
import theknife.server.service.RecensioneService;

/**
 * Comando LEGGI_RECENSIONI_RISTORANTI_GESTITI: restituisce le recensioni di
 * tutti i ristoranti del Ristoratore autenticato. Non ha payload:
 * l'identificativo del gestore arriva dall'utente di sessione, quindi la query
 * non puo' essere puntata sui ristoranti di un altro.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class LeggiRecensioniRistorantiGestitiCommand implements Command {

    /**
     * Service delle recensioni, a cui il comando delega la logica di dominio.
     */
    private final RecensioneService recensioneService;

    /**
     * Costruisce il comando sul service delle recensioni.
     *
     * @param recensioneService il service delle recensioni
     */
    public LeggiRecensioniRistorantiGestitiCommand(RecensioneService recensioneService) {
        this.recensioneService = recensioneService;
    }

    /**
     * Restituisce le recensioni di tutti i ristoranti del ristoratore di sessione.
     *
     * @param request la richiesta, priva di payload
     * @param utente  il ristoratore autenticato, di cui si usa l'identificativo
     *                per selezionare i ristoranti
     * @return SUCCESSO con la lista dei {@link RecensioneDTO} trovati,
     *         eventualmente vuota, ERRORE_SERVER se l'accesso al database fallisce
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        try {
            List<RecensioneDTO> recensioni =
                recensioneService.recensioniRistorantiGestiti(utente.getIdUtente());

            return new Response(ResponseStatus.SUCCESSO, recensioni, "Recensioni recuperate.");

        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante il recupero delle recensioni: " + e.getMessage());
        }
    }
}
