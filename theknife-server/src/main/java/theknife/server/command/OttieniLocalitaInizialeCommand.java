package theknife.server.command;

import theknife.common.dto.PosizioneDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.service.RistoranteService;

/**
 * Comando OTTIENI_LOCALITA_INIZIALE: suggerisce la localita' da precompilare
 * nella schermata iniziale (decisione 18). Non richiede autenticazione e non
 * ha payload in richiesta.
 * <p>
 * La stima non e' garantita: su rete locale o senza uscita verso Internet il
 * servizio esterno non risponde e la risposta e' NON_TROVATO. Non e' un
 * errore - il percorso valido resta la digitazione manuale del luogo, e il
 * client deve funzionare identico quando la stima manca.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class OttieniLocalitaInizialeCommand implements Command {

    /**
     * Service dei ristoranti, a cui il comando delega la logica di dominio.
     */
    private final RistoranteService ristoranteService;

    /**
     * Costruisce il comando sul service dei ristoranti.
     *
     * @param ristoranteService il service dei ristoranti
     */
    public OttieniLocalitaInizialeCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    /**
     * Restituisce la localita' stimata da suggerire nella schermata iniziale.
     *
     * @param request la richiesta, priva di payload
     * @param utente  ignorato: il comando non richiede autenticazione
     * @return SUCCESSO con la {@link PosizioneDTO} stimata, NON_TROVATO se la
     *         stima non e' disponibile, ERRORE_SERVER se la stima fallisce in
     *         modo imprevisto
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        try {
            PosizioneDTO posizione = ristoranteService.localitaIniziale();

            if (posizione == null) {
                return new Response(ResponseStatus.NON_TROVATO, null,
                        "Localita' non determinabile automaticamente.");
            }

            return new Response(ResponseStatus.SUCCESSO, posizione, "Localita' stimata.");

        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante la stima della localita': " + e.getMessage());
        }
    }
}
