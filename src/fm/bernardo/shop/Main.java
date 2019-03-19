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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Main extends Application {

    static Pane navigation, content;
    static JSONObject database;
    static String loggedInAs;
    static Stage mainStage;
    private final static File databaseLocation = new File(Main.programData + ".shopDatabase.json"),
            settingsLocation = new File(Main.programData + "user" + File.separator + "settings.json");
    private static final String programData = System.getProperty("user.home") + File.separator + ".kn04" + File.separator;
    static Scene mainScene;
    static File loginData = new File(Main.programData + "user" + File.separator + ".user.txt");

    static void showAlert(final Alert.AlertType type, final String title, final String text) {
        final Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    static void saveFile(final File fileLocation, final JSONObject data) throws Exception {
        try {
            final FileWriter file = new FileWriter(fileLocation);
            file.write(data.toJSONString());
            file.flush();
        } catch (final FileNotFoundException e) {
            Files.createDirectories(Paths.get(fileLocation.getParent()));
            saveFile(fileLocation, data);
        }
    }

    static JSONObject loadFile(final File fileLocation) throws Exception {
        JSONObject data;
        try {
            data = (JSONObject) new JSONParser().parse(new FileReader(fileLocation));
        } catch (final FileNotFoundException e) {
            data = (JSONObject) new JSONParser().parse("{}");
        }
        return data;
    }

    static boolean isNullOrEmpty(final Object object) {
        return object == null;
    }

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public final void start(final Stage stage) throws Exception {
        Main.database = Main.loadFile(Main.databaseLocation);
        start(stage, database);
    }

    final void start(final Stage stage, final JSONObject database) throws Exception {
        Main.mainScene = new Scene(FXMLLoader.load(getClass().getResource("assets/fxml/main.fxml")));


        Main.navigation = (Pane) Main.mainScene.lookup("#navigationRoot");
        Main.content = (Pane) Main.mainScene.lookup("#contentRoot");
        Main.mainStage = stage;
        Main.database = database;

        Main.mainStage.setScene(Main.mainScene);
        Main.mainStage.setResizable(false);
        Main.mainStage.setTitle("Anmelden");
        Main.mainStage.getIcons().add(new Image(getClass().getResourceAsStream("assets/img/icon.png")));

        Main.navigation.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/navigationStart.fxml")));
        Main.content.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/loginContent.fxml")));
        ((AnchorPane) Main.mainScene.lookup("#navigationStart")).prefWidthProperty().bind(Main.mainScene.widthProperty());

        final JSONObject data = loadFile(Main.loginData);
        if (data.get("username") != null)
            Controller.login(data.get("username").toString(), data.get("password").toString());

        Main.mainStage.show();
    }

    @Override
    public final void stop() throws Exception {
        saveFile(Main.databaseLocation, Main.database);
    }

}
