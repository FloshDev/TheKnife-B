package theknife.client.app;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import theknife.client.network.ServerConnection;
import theknife.common.config.ConfigurazioneServer;

/**
 * Applicazione JavaFX del client di TheKnife: apre la connessione al server e
 * mostra la prima schermata. Non è il {@code Main-Class} del jar shaded — un
 * {@code Main-Class} che estende direttamente {@link Application} fa
 * rifiutare l'avvio a {@code java -jar} con "componenti runtime di JavaFX
 * mancanti", anche a runtime JavaFX presente nel jar. Il punto d'ingresso
 * reale è {@link TheKnifeClient}, che chiama {@link #main(String[])} qui.
 *
 * @author Barlera Marco, 760000, VA
 */
public class ClientApplication extends Application {

    /**
     * Apre automaticamente la connessione al server (decisione 22, nessun
     * dialog) e solo se riesce mostra lo splash. Host e porta vengono dagli
     * argomenti della riga di comando, con default in
     * {@link ConfigurazioneServer} (decisione 27); se la porta non è un
     * numero valido o la connessione fallisce, mostra un errore leggibile e
     * termina l'applicazione senza caricare nessuna schermata.
     *
     * @param stage la finestra principale dell'applicazione
     * @throws Exception se il caricamento di {@code splash.fxml} fallisce
     */
    @Override public void start(Stage stage) throws Exception {
        List<String> args = getParameters().getRaw();
        String host = args.size() >= 1 ? args.get(0) : ConfigurazioneServer.HOST_DEFAULT;
        int porta;
        try {
            porta = args.size() >= 2 ? Integer.parseInt(args.get(1)) : ConfigurazioneServer.PORTA_DEFAULT;
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Errore nella configurazione del server: " + e.getMessage()).showAndWait();
            Platform.exit();
            return;
        }
        try {
            ServerConnection.getInstance().connect(host, porta);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Impossibile connettersi a " + host + ":" + porta + ". Verifica che il server sia avviato e riprova con: java -jar clientTK.jar [host] [porta]").showAndWait();
            Platform.exit();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/splash.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/theknife/client/ui/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("TheKnife");
        stage.show();
    }

    /**
     * Chiude la connessione al server alla chiusura dell'applicazione,
     * chiamato automaticamente da JavaFX. Se {@link #start(Stage)} è uscito
     * prima di connettersi, {@link ServerConnection#disconnect()} non fa
     * nulla invece di lanciare un'eccezione.
     *
     * @throws Exception se la chiusura della connessione fallisce
     */
    @Override public void stop() throws Exception {
        ServerConnection.getInstance().disconnect();
    }

    /**
     * Avvia l'applicazione JavaFX, chiamato da {@link TheKnifeClient#main(String[])}.
     *
     * @param args argomenti della riga di comando, letti da {@link #start(Stage)}
     */
    public static void main(String[] args) {
        launch(args);
    }
}
