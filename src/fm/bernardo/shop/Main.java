package fm.bernardo.shop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Main extends Application
{

    static Pane navigation, content;
    static JSONObject database;
    static String loggedInAs;
    static Stage mainStage;
    private final static File databaseLocation = new File(System.getProperty("user.home") + File.separator + ".kn04" + File.separator + ".shopDatabase.json");

    static void showAlert (final Alert.AlertType type, final String title, final String text)
    {
        final Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    static boolean isNullOrEmpty (final Object object)
    {
        return object == null;
    }

    @Override
    public final void start (final Stage stage) throws Exception
    {
        JSONObject database;
        try {
            database = (JSONObject) new JSONParser().parse(new FileReader(Main.databaseLocation));
        } catch (final FileNotFoundException e) {
            database = (JSONObject) new JSONParser().parse("{}");
        }
        start(stage, database);
    }

    final void start (final Stage stage, final JSONObject database) throws Exception
    {
        final Scene root = new Scene(FXMLLoader.load(getClass().getResource("assets/fxml/main.fxml")));

        Main.navigation = (Pane) root.lookup("#navigationRoot");
        Main.content = (Pane) root.lookup("#contentRoot");
        Main.mainStage = stage;
        Main.database = database;

        Main.navigation.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/navigationStart.fxml")));
        Main.content.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/loginContent.fxml")));

        ((AnchorPane) root.lookup("#navigationStart")).prefWidthProperty().bind(root.widthProperty());

        Main.mainStage.setScene(root);
        Main.mainStage.setResizable(false);
        Main.mainStage.setTitle("Shop");
        Main.mainStage.getIcons().add(new Image(getClass().getResourceAsStream("assets/img/icon.png")));
        Main.mainStage.show();
    }

    @Override
    public final void stop () throws Exception
    {
        try {
            final FileWriter file = new FileWriter(Main.databaseLocation);
            file.write(Main.database.toJSONString());
            file.flush();
        } catch (final FileNotFoundException e) {
            Files.createDirectories(Paths.get(Main.databaseLocation.getParent()));
            stop();
        }
    }

    public static void main (final String[] args)
    {
        launch(args);
    }
}
