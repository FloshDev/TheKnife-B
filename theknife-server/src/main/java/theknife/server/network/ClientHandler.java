package theknife.server.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputFilter;
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

    /**
     * Classi ammesse in deserializzazione. Tutto cio' che non e' un tipo del
     * protocollo o un tipo base usato dentro i DTO viene rifiutato prima di
     * essere istanziato: senza questo filtro, chiunque raggiunga la porta puo'
     * far costruire oggetti arbitrari fra quelli presenti nel classpath, che e'
     * la via nota per l'esecuzione di codice non autorizzato. I limiti su
     * profondita' e numero di riferimenti fermano invece i payload costruiti
     * per esaurire la memoria.
     */
    private static final String FILTRO_DESERIALIZZAZIONE =
        "maxdepth=32;maxrefs=10000;"
      + "theknife.common.**;"
      + "java.lang.**;java.util.**;java.time.**;java.math.**;"
      + "!*";

    private final Socket socket;
    private final CommandDispatcher dispatcher;

    /**
     * @param socket     la connessione col client, gia' accettata
     * @param dispatcher l'instradatore condiviso da tutti i thread client
     */
    public ClientHandler (Socket socket, CommandDispatcher dispatcher) {

        this.socket = socket;
        this.dispatcher = dispatcher;

    }

    /**
     * Serve le richieste del client finche' la connessione resta aperta.
     * Ogni richiesta e' indipendente: un errore su una non chiude la
     * conversazione, viene restituito al client come Response di errore dal
     * dispatcher.
     */
    @Override
    public void run() {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            // Lo stream header resta nel buffer finche' non viene forzato: senza
            // questo flush il client si blocca nel costruttore del proprio
            // ObjectInputStream e nessuno dei due lati procede.
            out.flush();

            in.setObjectInputFilter(
                ObjectInputFilter.Config.createFilter(FILTRO_DESERIALIZZAZIONE));

            System.out.println("Client connesso: " + socket.getRemoteSocketAddress());

            while (true) {
                Request request = (Request) in.readObject();
                System.out.println("Richiesta ricevuta: " + request.getCommandType());

                Response response = dispatcher.dispatch(request);
                out.writeObject(response);
                out.flush();

                // Ogni Response contiene i DTO appena letti dal database: senza
                // reset, ObjectOutputStream li riconosce come oggetti gia'
                // inviati e rispedisce il riferimento invece dei dati nuovi.
                out.reset();
            }
        } catch (EOFException e) {
            // client chiuso normalmente: fine conversazione
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Errore comunicazione col client: " + e.getMessage());
        } finally {
            System.out.println("Client disconnesso: " + socket.getRemoteSocketAddress());
        }
    }

}
