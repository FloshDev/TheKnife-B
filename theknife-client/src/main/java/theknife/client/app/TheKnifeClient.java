package theknife.client.app;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Punto d'ingresso dell'applicazione client JavaFX di TheKnife.
 *
 * @author Barlera Marco, 760000, VA
 */
public class TheKnifeClient extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("TheKnife");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
