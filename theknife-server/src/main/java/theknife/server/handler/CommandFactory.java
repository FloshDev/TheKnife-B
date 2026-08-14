package theknife.server.handler;

import java.util.EnumMap;
import java.util.Map;
import theknife.common.enums.CommandType;
import theknife.server.command.CercaRistorantiCommand;
import theknife.server.command.Command;
import theknife.server.service.RistoranteService;
/**
 * Costruisce e conserva i Command del server, uno per CommandType. I Command
 * sono privi di stato, quindi vengono istanziati una sola volta alla partenza
 * e riusati da tutti i thread client: la mappa e' popolata nel costruttore e
 * mai piu' modificata.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class CommandFactory {

    private final Map<CommandType, Command> comandi = new EnumMap<>(CommandType.class);

    public CommandFactory(RistoranteService ristoranteService) {
        comandi.put(CommandType.CERCA_RISTORANTI, new CercaRistorantiCommand(ristoranteService));
        
    }

    public Command create(CommandType commandType) {
        Command command = comandi.get(commandType);
        if (command == null) {
            throw new IllegalArgumentException("Tipo di comando non valido: " + commandType);
        }
        return command;
    }
}
