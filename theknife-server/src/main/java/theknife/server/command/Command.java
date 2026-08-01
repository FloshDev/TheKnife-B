package theknife.server.command;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.common.dto.UtenteDTO;

public interface Command {
    Response execute(Request request, UtenteDTO utente);
}
