package keylivery;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import keylivery.gnupg.GnuPG;
import keylivery.gnupg.GnuPGKeyID;
import keylivery.gnupg.GnuPGProcessCaller;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

//        *********


        GnuPG gpg = new GnuPGProcessCaller();
        GnuPGKeyID[] keys = gpg.listKeys();
        String keyBlock = gpg.exportKeyAsString(keys[0]);
        System.out.println(keyBlock);

        for (GnuPGKeyID key : keys
                ) {
            System.out.println(key.getCreationDate() + " / " + key.getUserID());
        }


//        **********

        Text text = new Text("Scan this!");

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/intro.fxml"));

//        BorderPane root = new BorderPane();
//        root.setCenter(qrCanvas);
//        root.setTop(text);
//        BorderPane.setAlignment(text, Pos.CENTER);

        primaryStage.setTitle("QR Code");
        primaryStage.setScene(new Scene(root, 275, 375));
        primaryStage.show();
    }
}