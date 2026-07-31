package theknife.server.handler;
import theknife.common.enums.CommandType;
import theknife.server.command.CercaRistorantiCommand;
import theknife.server.command.Command;

public class CommandFactory {

    private final Command cercaRistorantiCommand;

    public CommandFactory(RistoranteService ristoranteService) {
        this.cercaRistorantiCommand = new CercaRistorantiCommand(ristoranteService);
    }

    public Command create(CommandType commandType) {
        switch (commandType) {
            case CERCA_RISTORANTI:
                return this.cercaRistorantiCommand;
            // ...
            default:
                throw new IllegalArgumentException("Tipo di comando non valido: " + commandType);
        }
    }
}
