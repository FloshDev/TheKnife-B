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
 * Comando ASSOCIA_RISTORANTE: assegna al Ristoratore autenticato un ristorante
 * gia' presente in catalogo - tipicamente una delle righe importate dal
 * dataset, che nascono senza gestore. Risponde senza payload.
 * <p>
 * <b>Controllo di dominio.</b> Il modello concettuale ammette al massimo un
 * gestore per ristorante: se il ristorante ne ha gia' uno la richiesta viene
 * respinta, altrimenti un ristoratore potrebbe sottrarre a un altro i suoi
 * locali. Il DAO esegue un <code>UPDATE</code> incondizionato, quindi il
 * controllo esiste solo qui.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class AssociaRistoranteCommand implements Command {

    private final RistoranteService ristoranteService;

    public AssociaRistoranteCommand(RistoranteService ristoranteService) {
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

            if (ristorante.getIdGestore() != null) {
                if (ristorante.getIdGestore() == utente.getIdUtente()) {
                    return new Response(ResponseStatus.ERRORE, null,
                            "Il ristorante e' gia' fra quelli che gestisci.");
                }
                return new Response(ResponseStatus.NON_AUTORIZZATO, null,
                        "Il ristorante e' gia' gestito da un altro utente.");
            }

            ristoranteService.associaRistorante(id, utente.getIdUtente());
            return new Response(ResponseStatus.SUCCESSO, null, "Ristorante associato.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.NON_TROVATO, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante l'associazione del ristorante: " + e.getMessage());
        }
    }
}
