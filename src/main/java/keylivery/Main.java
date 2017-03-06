package keylivery;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent exportroot = FXMLLoader.load(getClass().getResource("/fxml/ExportTab.fxml"));
        Parent importroot = FXMLLoader.load(getClass().getResource("/fxml/ImportTab.fxml"));

        Tab exportTab = new Tab("Export");
        exportTab.setContent(exportroot);
        exportTab.setClosable(false);
        Tab importTab = new Tab("Import");
        importTab.setContent(importroot);
        importTab.setClosable(false);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(exportTab, importTab);

        Scene scene = new Scene(tabPane, 600, 432);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Keylivery");
        primaryStage.show();
    }
}