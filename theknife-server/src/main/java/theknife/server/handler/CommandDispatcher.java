package theknife.server.handler;

import theknife.common.protocol.Response;
import theknife.server.command.Command;
import theknife.common.protocol.Request;
import theknife.common.enums.CommandType;
import theknife.common.enums.ResponseStatus;
import theknife.common.enums.Ruolo;
import theknife.common.dto.UtenteDTO;
import theknife.server.service.SessionManager;

/**
 * Instrada ogni Request al Command che la esegue. Prima di istanziare il
 * Command applica il gate di autorizzazione (decisione 20): risolve il
 * sessionToken in un utente tramite {@link SessionManager} e confronta il
 * ruolo con il requisito di accesso del CommandType, ricavato dalla tabella
 * di autorizzazione del contratto. Se il requisito non e' soddisfatto
 * risponde NON_AUTORIZZATO senza costruire il Command.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class CommandDispatcher {

    /**
     * Livello di accesso richiesto da un comando. Riproduce la colonna
     * "accesso" della tabella di autorizzazione del contratto: i quattro valori
     * coprono tutti e venti i CommandType, quindi ogni comando ha un requisito
     * e nessuno resta implicitamente aperto.
     */
    private enum Requisito {

        /** Comando pubblico: eseguibile anche senza sessione. */
        NESSUNO,

        /** Richiede una sessione valida, di qualunque ruolo. */
        AUTENTICATO,

        /** Riservato agli utenti con ruolo Cliente. */
        CLIENTE,

        /** Riservato agli utenti con ruolo Ristoratore. */
        RISTORATORE
    }

    /** Registro dei Command, uno per CommandType, condiviso da tutti i thread. */
    private CommandFactory commandFactory;

    /** Registro delle sessioni attive, usato per risolvere il token in utente. */
    private final SessionManager sessionManager;

    /**
     * Costruisce il dispatcher sui due registri che gli servono.
     *
     * @param commandFactory la factory che risolve il CommandType nel Command
     * @param sessionManager il registro delle sessioni attive
     */
    public CommandDispatcher(CommandFactory commandFactory, SessionManager sessionManager) {
        this.commandFactory = commandFactory;
        this.sessionManager = sessionManager;
    }

    /**
     * Instrada la richiesta al Command che la esegue, dopo il gate di
     * autorizzazione. Non propaga eccezioni: ogni esito, compreso l'errore,
     * torna al client come Response, perche' un'eccezione qui lascerebbe il
     * client in attesa di una risposta che non arriva.
     *
     * @param request la richiesta letta dal socket, eventualmente
     *                <code>null</code> o priva di comando
     * @return la risposta da inviare al client: quella prodotta dal Command,
     *         oppure NON_AUTORIZZATO se il ruolo non soddisfa il requisito del
     *         comando, ERRORE_VALIDAZIONE se la richiesta e' priva di comando,
     *         ERRORE_SERVER se il comando non e' registrato
     */
    public Response dispatch(Request request) {
        try {
            if (request == null || request.getCommandType() == null) {
                return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                        "Richiesta priva di comando.");
            }

            UtenteDTO utente = sessionManager.getUtenteFromSession(request.getSessionToken());

            if (!accessoConsentito(request.getCommandType(), utente)) {
                return new Response(ResponseStatus.NON_AUTORIZZATO, null,
                        "Operazione non consentita.");
            }

            Command command = commandFactory.create(request.getCommandType());
            return command.execute(request, utente);
        } catch (IllegalArgumentException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, "Tipo di comando non valido: " + request.getCommandType());
        }
    }

    /**
     * Confronta il requisito di accesso del comando con l'utente della
     * sessione. Un utente assente - token mancante, scaduto o mai emesso -
     * supera solo il requisito NESSUNO.
     *
     * @param comando il tipo di comando richiesto
     * @param utente  l'utente della sessione, oppure <code>null</code> se la
     *                richiesta non e' autenticata
     * @return <code>true</code> se l'utente puo' eseguire quel comando
     */
    private boolean accessoConsentito(CommandType comando, UtenteDTO utente) {
        return switch (requisitoDi(comando)) {
            case NESSUNO      -> true;
            case AUTENTICATO  -> utente != null;
            case CLIENTE      -> utente != null && utente.getRuolo() == Ruolo.CLIENTE;
            case RISTORATORE  -> utente != null && utente.getRuolo() == Ruolo.RISTORATORE;
        };
    }

    /**
     * Restituisce il livello di accesso richiesto da un comando, secondo la
     * tabella di autorizzazione del contratto. Lo switch e' esaustivo
     * sull'enum: aggiungere un CommandType senza classificarlo qui e' un
     * errore di compilazione, non un comando lasciato aperto per distrazione.
     *
     * @param comando il tipo di comando da classificare
     * @return il requisito di accesso corrispondente
     */
    private Requisito requisitoDi(CommandType comando) {
        return switch (comando) {
            case CERCA_RISTORANTI,
                 OTTIENI_DETTAGLI_RISTORANTE,
                 LEGGI_RECENSIONI,
                 CERCA_VICINO,
                 OTTIENI_LOCALITA_INIZIALE,
                 ACCEDI,
                 REGISTRATI                          -> Requisito.NESSUNO;

            case ESCI                                -> Requisito.AUTENTICATO;

            case AGGIUNGI_PREFERITO,
                 RIMUOVI_PREFERITO,
                 VEDI_PREFERITI,
                 AGGIUNGI_RECENSIONE,
                 MODIFICA_RECENSIONE,
                 ELIMINA_RECENSIONE                  -> Requisito.CLIENTE;

            case AGGIUNGI_RISTORANTE,
                 VEDI_RISTORANTI_GESTITI,
                 LEGGI_RECENSIONI_RISTORANTI_GESTITI,
                 OTTIENI_STATISTICHE_RISTORANTE,
                 RISPONDI_RECENSIONE,
                 ASSOCIA_RISTORANTE                  -> Requisito.RISTORATORE;
        };
    }

}
