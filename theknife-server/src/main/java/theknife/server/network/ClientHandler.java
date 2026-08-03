package theknife.server.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.handler.CommandDispatcher;

/**
 * Gestisce la comunicazione con un singolo client su un thread dedicato.
 * Legge la Request dal socket, la instrada al dispatcher e scrive la Response.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class ClientHandler implements Runnable {
    
    private final Socket socket;
    private final CommandDispatcher dispatcher;

    public ClientHandler (Socket socket, CommandDispatcher dispatcher) {

        this.socket = socket;
        this.dispatcher = dispatcher;

    }

    @Override
    public void run() {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            while (true) {
                Request request = (Request) in.readObject();
                Response response = dispatcher.dispatch(request);
                out.writeObject(response);
                out.flush();
            }
        } catch (EOFException e) {
            // client chiuso normalmente: fine conversazione
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Errore comunicazione col client: " + e.getMessage());
        }
    }

}
