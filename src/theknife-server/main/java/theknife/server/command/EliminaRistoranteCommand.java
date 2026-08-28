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
 * Comando ELIMINA_RISTORANTE: cancella un ristorante del Ristoratore
 * autenticato. Risponde senza payload (decisione 15).
 * <p>
 * <b>Controllo di proprieta' (decisione 32).</b> L'identificativo arriva dal
 * payload e potrebbe essere di chiunque, mentre il DAO cancella per solo
 * identificativo: il ristorante viene riletto e il suo gestore confrontato con
 * l'utente di sessione prima della cancellazione, stesso schema di
 * {@link OttieniStatisticheRistoranteCommand}. Qui il controllo pesa piu' che
 * altrove, perche' l'operazione e' distruttiva e irreversibile.
 * <p>
 * <b>Effetto a cascata.</b> Con il ristorante spariscono le sue recensioni, i
 * suoi servizi e i preferiti che lo puntano: la cascata e' dichiarata nello
 * schema, non ricostruita qui.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class EliminaRistoranteCommand implements Command {

    /**
     * Service dei ristoranti, a cui il comando delega la logica di dominio.
     */
    private final RistoranteService ristoranteService;

    /**
     * Costruisce il comando sul service dei ristoranti.
     *
     * @param ristoranteService il service dei ristoranti
     */
    public EliminaRistoranteCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    /**
     * Cancella il ristorante indicato, dopo aver verificato che sia gestito dal
     * ristoratore di sessione.
     *
     * @param request la richiesta, con un {@link IdRistoranteDTO} come payload
     * @param utente  il ristoratore autenticato, confrontato con il gestore del
     *                ristorante da cancellare
     * @return SUCCESSO senza payload, NON_AUTORIZZATO se il ristorante e'
     *         gestito da un altro utente o non ha gestore, NON_TROVATO se non
     *         esiste, ERRORE_VALIDAZIONE se l'identificativo manca o e' di tipo
     *         errato, ERRORE_SERVER se l'accesso al database fallisce
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

            ristoranteService.elimina(id);
            return new Response(ResponseStatus.SUCCESSO, null, "Ristorante eliminato.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.NON_TROVATO, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante l'eliminazione del ristorante: " + e.getMessage());
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
