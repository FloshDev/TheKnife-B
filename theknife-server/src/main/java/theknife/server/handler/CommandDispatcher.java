package theknife.server.handler;
import theknife.common.protocol.Response;
import theknife.server.command.Command;
import theknife.common.protocol.Request;
import theknife.common.enums.ResponseStatus;
import theknife.common.dto.UtenteDTO;

public class CommandDispatcher {

    private CommandFactory commandFactory;
    private final SessionManager sessionManager;

    public CommandDispatcher(CommandFactory commandFactory, SessionManager sessionManager) {
        this.commandFactory = commandFactory;
        this.sessionManager = sessionManager;
    }

    public Response dispatch(Request request) {
        try {
            UtenteDTO utente = sessionManager.getUtenteFromSession(request.getSessionToken());
            Command command = commandFactory.create(request.getCommandType());
            return command.execute(request, utente);
        } catch (IllegalArgumentException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, "Tipo di comando non valido: " + request.getCommandType());
        }
    }
}
