package theknife.server.command;

import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RispondiRecensioneDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.RecensioneService;
import theknife.server.service.RistoranteService;

/**
 * Comando RISPONDI_RECENSIONE: registra la risposta del Ristoratore a una
 * recensione di un proprio ristorante. Risponde senza payload (decisione 15).
 * <p>
 * <b>Controllo di proprieta' (decisione 24).</b> E' il caso che la decisione
 * nomina per esteso: si risale dalla recensione al ristorante e dal ristorante
 * al gestore, e lo si confronta con l'utente di sessione. Il filtro nella GUI
 * e' cosmetico; il rifiuto vero avviene qui.
 * <p>
 * Una recensione ammette una sola risposta: il secondo tentativo viene
 * respinto dal DAO come ApplicationException e tradotto in ERRORE.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class RispondiRecensioneCommand implements Command {

    /**
     * Service delle recensioni, a cui il comando delega la logica di dominio.
     */
    private final RecensioneService recensioneService;

    /**
     * Service dei ristoranti, usato per risalire al gestore del ristorante recensito.
     */
    private final RistoranteService ristoranteService;

    /**
     * Costruisce il comando sui due service coinvolti nel controllo di proprieta'.
     *
     * @param recensioneService il service delle recensioni
     * @param ristoranteService il service dei ristoranti
     */
    public RispondiRecensioneCommand(RecensioneService recensioneService,
                                     RistoranteService ristoranteService) {
        this.recensioneService = recensioneService;
        this.ristoranteService = ristoranteService;
    }

    /**
     * Registra la risposta del ristoratore di sessione a una recensione di un suo ristorante.
     *
     * @param request la richiesta, con un {@link RispondiRecensioneDTO} come payload
     * @param utente  il ristoratore autenticato, confrontato con il gestore del
     *                ristorante recensito
     * @return SUCCESSO senza payload, NON_AUTORIZZATO se il ristorante e'
     *         gestito da un altro utente, ERRORE se la recensione ha gia' una
     *         risposta, ERRORE_VALIDAZIONE se i dati mancano o sono di tipo
     *         errato, ERRORE_SERVER se l'accesso al database fallisce
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof RispondiRecensioneDTO dati)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Dati della risposta mancanti o non validi.");
        }

        try {
            RecensioneDTO recensione =
                recensioneService.ottieniRecensione(dati.getIdRecensione());

            RistoranteDTO ristorante = ristoranteService.ottieniDettagli(
                    new IdRistoranteDTO(recensione.getIdRistorante()));

            if (ristorante.getIdGestore() == null
                    || ristorante.getIdGestore() != utente.getIdUtente()) {
                return new Response(ResponseStatus.NON_AUTORIZZATO, null,
                        "Il ristorante recensito e' gestito da un altro utente.");
            }

            recensioneService.rispondiRecensione(dati);
            return new Response(ResponseStatus.SUCCESSO, null, "Risposta pubblicata.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.ERRORE, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante la pubblicazione della risposta: " + e.getMessage());
        }
    }
}
