package theknife.server.handler;
import theknife.common.protocol.Response;
import theknife.server.command.Command;
import theknife.common.protocol.Request;
import theknife.common.enums.ResponseStatus;

public class CommandDispatcher {

    private CommandFactory commandFactory;

    public CommandDispatcher(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public Response dispatch(Request request) {
        try {
            Command command = commandFactory.create(request.getCommandType());
            return command.execute(request);
        } catch (IllegalArgumentException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, "Tipo di comando non valido: " + request.getCommandType());
        }
    }
}
