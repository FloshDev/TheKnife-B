package theknife.server.handler;

import java.util.EnumMap;
import java.util.Map;

import theknife.common.enums.CommandType;
import theknife.server.command.AccediCommand;
import theknife.server.command.AggiungiPreferitoCommand;
import theknife.server.command.AggiungiRecensioneCommand;
import theknife.server.command.AggiungiRistoranteCommand;
import theknife.server.command.AssociaRistoranteCommand;
import theknife.server.command.CercaRistorantiCommand;
import theknife.server.command.CercaVicinoCommand;
import theknife.server.command.Command;
import theknife.server.command.EliminaRecensioneCommand;
import theknife.server.command.EsciCommand;
import theknife.server.command.LeggiRecensioniCommand;
import theknife.server.command.LeggiRecensioniRistorantiGestitiCommand;
import theknife.server.command.ModificaRecensioneCommand;
import theknife.server.command.OttieniDettagliRistoranteCommand;
import theknife.server.command.OttieniLocalitaInizialeCommand;
import theknife.server.command.OttieniStatisticheRistoranteCommand;
import theknife.server.command.RegistratiCommand;
import theknife.server.command.RimuoviPreferitoCommand;
import theknife.server.command.RispondiRecensioneCommand;
import theknife.server.command.VediPreferitiCommand;
import theknife.server.command.VediRistorantiGestitiCommand;
import theknife.server.service.RecensioneService;
import theknife.server.service.RistoranteService;
import theknife.server.service.UtenteService;

/**
 * Costruisce e conserva i Command del server, uno per CommandType. I Command
 * sono privi di stato, quindi vengono istanziati una sola volta alla partenza
 * e riusati da tutti i thread client: la mappa e' popolata nel costruttore e
 * mai piu' modificata, il che la rende sicura in lettura concorrente senza
 * sincronizzazione.
 * <p>
 * La mappa copre tutti e venti i CommandType del contratto: un comando che
 * non risulta registrato indica un errore di programmazione, non una
 * richiesta non valida.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class CommandFactory {

    /**
     * Registro dei Command, uno per CommandType. Popolato nel costruttore e
     * mai piu' modificato: la sola lettura concorrente non richiede
     * sincronizzazione.
     */
    private final Map<CommandType, Command> comandi = new EnumMap<>(CommandType.class);

    /**
     * Costruisce i venti Command sui service indicati e li registra nella
     * mappa.
     *
     * @param ristoranteService logica di dominio di ristoranti e preferiti
     * @param recensioneService logica di dominio delle recensioni
     * @param utenteService     logica di autenticazione e registrazione
     */
    public CommandFactory(RistoranteService ristoranteService,
                          RecensioneService recensioneService,
                          UtenteService utenteService) {

        // Accessibili senza autenticazione
        comandi.put(CommandType.CERCA_RISTORANTI,
                new CercaRistorantiCommand(ristoranteService));
        comandi.put(CommandType.OTTIENI_DETTAGLI_RISTORANTE,
                new OttieniDettagliRistoranteCommand(ristoranteService));
        comandi.put(CommandType.CERCA_VICINO,
                new CercaVicinoCommand(ristoranteService));
        comandi.put(CommandType.OTTIENI_LOCALITA_INIZIALE,
                new OttieniLocalitaInizialeCommand(ristoranteService));
        comandi.put(CommandType.LEGGI_RECENSIONI,
                new LeggiRecensioniCommand(recensioneService));
        comandi.put(CommandType.ACCEDI,
                new AccediCommand(utenteService));
        comandi.put(CommandType.REGISTRATI,
                new RegistratiCommand(utenteService));

        // Riservati al Cliente
        comandi.put(CommandType.AGGIUNGI_PREFERITO,
                new AggiungiPreferitoCommand(ristoranteService));
        comandi.put(CommandType.RIMUOVI_PREFERITO,
                new RimuoviPreferitoCommand(ristoranteService));
        comandi.put(CommandType.VEDI_PREFERITI,
                new VediPreferitiCommand(ristoranteService));
        comandi.put(CommandType.AGGIUNGI_RECENSIONE,
                new AggiungiRecensioneCommand(recensioneService));
        comandi.put(CommandType.MODIFICA_RECENSIONE,
                new ModificaRecensioneCommand(recensioneService));
        comandi.put(CommandType.ELIMINA_RECENSIONE,
                new EliminaRecensioneCommand(recensioneService));

        // Riservati al Ristoratore
        comandi.put(CommandType.AGGIUNGI_RISTORANTE,
                new AggiungiRistoranteCommand(ristoranteService));
        comandi.put(CommandType.VEDI_RISTORANTI_GESTITI,
                new VediRistorantiGestitiCommand(ristoranteService));
        comandi.put(CommandType.LEGGI_RECENSIONI_RISTORANTI_GESTITI,
                new LeggiRecensioniRistorantiGestitiCommand(recensioneService));
        comandi.put(CommandType.OTTIENI_STATISTICHE_RISTORANTE,
                new OttieniStatisticheRistoranteCommand(ristoranteService));
        comandi.put(CommandType.RISPONDI_RECENSIONE,
                new RispondiRecensioneCommand(recensioneService, ristoranteService));
        comandi.put(CommandType.ASSOCIA_RISTORANTE,
                new AssociaRistoranteCommand(ristoranteService));

        // Comune a Cliente e Ristoratore
        comandi.put(CommandType.ESCI,
                new EsciCommand(utenteService));
    }

    /**
     * Restituisce il Command che esegue il tipo di comando indicato.
     *
     * @param commandType il tipo di comando richiesto
     * @return il Command corrispondente, sempre la stessa istanza
     * @throws IllegalArgumentException se nessun Command e' registrato per
     *                                  quel tipo
     */
    public Command create(CommandType commandType) {
        Command command = comandi.get(commandType);
        if (command == null) {
            throw new IllegalArgumentException("Tipo di comando non valido: " + commandType);
        }
        return command;
    }
}
