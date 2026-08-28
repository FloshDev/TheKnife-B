package theknife.server.command;

import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.StatisticheRistoranteDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.RistoranteService;

/**
 * Comando OTTIENI_STATISTICHE_RISTORANTE: restituisce media delle stelle e
 * numero di recensioni di un ristorante del Ristoratore autenticato.
 * <p>
 * <b>Controllo di proprieta'.</b> A differenza di VEDI_RISTORANTI_GESTITI,
 * qui l'identificativo del ristorante arriva dal payload e potrebbe essere di
 * chiunque: il ristorante viene riletto e il suo gestore confrontato con
 * l'utente di sessione. Il requisito RF-22 riserva questo riepilogo ai propri
 * ristoranti.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class OttieniStatisticheRistoranteCommand implements Command {

    /**
     * Service dei ristoranti, a cui il comando delega la logica di dominio.
     */
    private final RistoranteService ristoranteService;

    /**
     * Costruisce il comando sul service dei ristoranti.
     *
     * @param ristoranteService il service dei ristoranti
     */
    public OttieniStatisticheRistoranteCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    /**
     * Restituisce il riepilogo delle recensioni di un ristorante del ristoratore di sessione.
     *
     * @param request la richiesta, con un {@link IdRistoranteDTO} come payload
     * @param utente  il ristoratore autenticato, confrontato con il gestore del
     *                ristorante richiesto
     * @return SUCCESSO con le {@link StatisticheRistoranteDTO} del ristorante,
     *         NON_AUTORIZZATO se il ristorante e' gestito da un altro utente,
     *         NON_TROVATO se non esiste, ERRORE_VALIDAZIONE se l'identificativo
     *         manca o e' di tipo errato, ERRORE_SERVER se l'accesso al database fallisce
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof IdRistoranteDTO id)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Identificativo del ristorante mancante o non valido.");
        }

        try {
            RistoranteDTO ristorante = ristoranteService.ottieniDettagli(id);

            if (!gestitoDa(ristorante, utente)) {
                return new Response(ResponseStatus.NON_AUTORIZZATO, null,
                        "Il ristorante e' gestito da un altro utente.");
            }

            StatisticheRistoranteDTO statistiche = ristoranteService.statistiche(id);
            return new Response(ResponseStatus.SUCCESSO, statistiche, "Statistiche recuperate.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.NON_TROVATO, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante il recupero delle statistiche: " + e.getMessage());
        }
    }

    /**
     * Verifica che il ristorante sia gestito dall'utente indicato. Il campo
     * <code>idGestore</code> e' un <code>Long</code> e vale <code>null</code>
     * sui ristoranti senza gestore: il controllo del null precede il confronto,
     * altrimenti lo unboxing lancerebbe NullPointerException.
     *
     * @param ristorante il ristorante da verificare
     * @param utente     l'utente della sessione corrente
     * @return <code>true</code> se l'utente e' il gestore del ristorante
     */
    private boolean gestitoDa (RistoranteDTO ristorante, UtenteDTO utente) {
        return ristorante.getIdGestore() != null
            && ristorante.getIdGestore() == utente.getIdUtente();
    }
}
