package keylivery;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import keylivery.qrcode.QRCanvas;
import keylivery.qrcode.QRCodeGen;
import keylivery.qrcode.QREncoder;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Text text = new Text("Scan this!");

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/sample.fxml"));

//        BorderPane root = new BorderPane();
//        root.setCenter(qrCanvas);
//        root.setTop(text);
//        BorderPane.setAlignment(text, Pos.CENTER);

        primaryStage.setTitle("QR Code");
        primaryStage.setScene(new Scene(root, 275, 375));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}