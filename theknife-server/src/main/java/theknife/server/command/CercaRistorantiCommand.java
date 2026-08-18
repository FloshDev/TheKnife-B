package theknife.server.command;

import java.util.List;

import theknife.common.dto.CercaRistorantiDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.RistoranteService;

/**
 * Comando CERCA_RISTORANTI: restituisce i ristoranti che soddisfano i filtri
 * di ricerca. Non richiede autenticazione. Risponde NON_TROVATO, non SUCCESSO
 * con lista vuota, quando nessun ristorante corrisponde ai criteri.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class CercaRistorantiCommand implements Command {

    /**
     * Service dei ristoranti, a cui il comando delega la logica di dominio.
     */
    private final RistoranteService ristoranteService;

    /**
     * Costruisce il comando sul service dei ristoranti.
     *
     * @param ristoranteService il service dei ristoranti
     */
    public CercaRistorantiCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    /**
     * Cerca i ristoranti che soddisfano i filtri contenuti nel payload.
     *
     * @param request la richiesta, con un {@link CercaRistorantiDTO} come payload
     * @param utente  ignorato: il comando non richiede autenticazione
     * @return SUCCESSO con la lista dei {@link RistoranteDTO} trovati,
     *         NON_TROVATO con lista vuota se nessuno corrisponde ai criteri,
     *         ERRORE_VALIDAZIONE se i criteri mancano o sono di tipo errato,
     *         ERRORE_SERVER se l'accesso al database fallisce
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof CercaRistorantiDTO filtri)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Criteri di ricerca mancanti o non validi.");
        }

        try {
            List<RistoranteDTO> ristoranti = ristoranteService.cercaRistoranti(filtri);

            if (ristoranti.isEmpty()) {
                return new Response(ResponseStatus.NON_TROVATO, ristoranti,
                        "Nessun ristorante trovato con i criteri specificati.");
            }

            return new Response(ResponseStatus.SUCCESSO, ristoranti,
                    "Ristoranti trovati con successo.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante la ricerca dei ristoranti: " + e.getMessage());
        }
    }
}
