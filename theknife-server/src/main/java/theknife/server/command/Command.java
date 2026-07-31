package theknife.server.command;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;

public interface Command {
    Response execute(Request request);
}
