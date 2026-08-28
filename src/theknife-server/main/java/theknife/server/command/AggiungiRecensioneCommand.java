package theknife.server.command;

import theknife.common.dto.AggiungiRecensioneDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.RecensioneService;

/**
 * Comando AGGIUNGI_RECENSIONE: pubblica una recensione a nome del Cliente
 * autenticato. Risponde senza payload (decisione 15). L'autore arriva
 * dall'utente di sessione, mai dal payload.
 * <p>
 * Il vincolo di unicita' cliente/ristorante e' garantito dal database e
 * segnalato dal DAO come ApplicationException: un secondo tentativo sullo
 * stesso ristorante risponde ERRORE con un messaggio leggibile, non un errore
 * di sistema.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class AggiungiRecensioneCommand implements Command {

    /**
     * Service delle recensioni, a cui il comando delega la logica di dominio.
     */
    private final RecensioneService recensioneService;

    /**
     * Costruisce il comando sul service delle recensioni.
     *
     * @param recensioneService il service delle recensioni
     */
    public AggiungiRecensioneCommand(RecensioneService recensioneService) {
        this.recensioneService = recensioneService;
    }

    /**
     * Pubblica la recensione contenuta nel payload a nome del cliente di sessione.
     *
     * @param request la richiesta, con un {@link AggiungiRecensioneDTO} come payload
     * @param utente  il cliente autenticato, autore della recensione
     * @return SUCCESSO senza payload, ERRORE_VALIDAZIONE se i dati mancano o
     *         sono fuori scala, ERRORE se il cliente ha gia' recensito quel
     *         ristorante, ERRORE_SERVER se l'accesso al database fallisce
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof AggiungiRecensioneDTO dati)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Dati della recensione mancanti o non validi.");
        }

        try {
            recensioneService.aggiungiRecensione(dati, utente.getIdUtente());
            return new Response(ResponseStatus.SUCCESSO, null, "Recensione pubblicata.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.ERRORE, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante la pubblicazione della recensione: " + e.getMessage());
        }
    }
}
