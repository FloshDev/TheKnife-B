package theknife.server.command;

import theknife.common.dto.ModificaRecensioneDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.RecensioneService;

/**
 * Comando MODIFICA_RECENSIONE: aggiorna una recensione del Cliente
 * autenticato. Risponde senza payload (decisione 15).
 * <p>
 * <b>Controllo di proprieta' (decisione 20/24).</b> Il DAO filtra solo per
 * identificativo di recensione: senza il confronto fatto qui, un cliente
 * potrebbe modificare la recensione di chiunque conoscendone l'id. La
 * recensione viene riletta e il suo autore confrontato con l'utente di
 * sessione prima di toccare il database.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class ModificaRecensioneCommand implements Command {

    /**
     * Service delle recensioni, a cui il comando delega la logica di dominio.
     */
    private final RecensioneService recensioneService;

    /**
     * Costruisce il comando sul service delle recensioni.
     *
     * @param recensioneService il service delle recensioni
     */
    public ModificaRecensioneCommand(RecensioneService recensioneService) {
        this.recensioneService = recensioneService;
    }

    /**
     * Aggiorna la recensione indicata, dopo aver verificato che appartenga al cliente di sessione.
     *
     * @param request la richiesta, con un {@link ModificaRecensioneDTO} come payload
     * @param utente  il cliente autenticato, di cui si confronta l'identificativo
     *                con l'autore della recensione
     * @return SUCCESSO senza payload, NON_AUTORIZZATO se la recensione e' di un
     *         altro utente, NON_TROVATO se non esiste, ERRORE_VALIDAZIONE se i
     *         dati mancano o sono fuori scala, ERRORE_SERVER se l'accesso al database fallisce
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof ModificaRecensioneDTO dati)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Dati della recensione mancanti o non validi.");
        }

        try {
            RecensioneDTO recensione =
                recensioneService.ottieniRecensione(dati.getIdRecensione());

            if (recensione.getIdUtente() != utente.getIdUtente()) {
                return new Response(ResponseStatus.NON_AUTORIZZATO, null,
                        "La recensione appartiene a un altro utente.");
            }

            recensioneService.modificaRecensione(dati);
            return new Response(ResponseStatus.SUCCESSO, null, "Recensione modificata.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.NON_TROVATO, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante la modifica della recensione: " + e.getMessage());
        }
    }
}
