package fm.bernardo.shop;

import com.jcabi.manifests.Manifests;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;


public final class Controller {

    static void login(String username, String password) throws Exception {

        if (Main.isNullOrEmpty(username) || Main.isNullOrEmpty(password)) {
            username = ((TextField) Main.mainScene.lookup("#loginUsername")).getText();
            password = ((PasswordField) Main.mainScene.lookup("#loginPassword")).getText();
        }

        if (Main.database.containsKey(username) && ((JSONObject) Main.database.get(username)).get("password").equals(password)) {
            Main.saveFile(Main.loginData, (JSONObject) new JSONParser().parse("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"));

            Main.loggedInAs = username;

            Main.navigation.getChildren().clear();
            Main.navigation.getChildren().add(FXMLLoader.load(Main.class.getResource("assets/fxml/navigationShop.fxml")));
            Controller.loadShop();
            ((Button) Main.mainScene.lookup("#accountName")).setText(Main.loggedInAs);
            ((AnchorPane) Main.mainScene.lookup("#navigationShop")).prefWidthProperty().bind(Main.mainScene.widthProperty());

            Main.showAlert(Alert.AlertType.INFORMATION, "Erfolgreich", "Sie wurden erfolgreich weitergeleitet und angemeldet.");
        } else {
            Main.showAlert(Alert.AlertType.ERROR, "Fehler", "Ungültige Anmeldedaten.");
        }
    }

    public final void login() throws Exception {
        login(null, null);
    }

    private static void loadShop() throws Exception {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Shop");
        Main.content.getChildren().add(FXMLLoader.load(Main.class.getResource("assets/fxml/shopContent.fxml")));
    }

    public final void loadShop(final ActionEvent event) throws Exception {
        loadShop();
    }

    private static void logout() throws Exception {
        Main.mainStage.close();
        Main.loginData.delete();
        new Main().start(new Stage(), Main.database);
        Main.showAlert(Alert.AlertType.INFORMATION, "Information", "Sie wurden vom Konto abgemeldet.");
    }

    public final void logout(final ActionEvent event) throws Exception {
        logout();
    }

    public final void loadLogin() throws Exception {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Anmelden");
        Main.content.getChildren().add(FXMLLoader.load(Main.class.getResource("assets/fxml/loginContent.fxml")));
    }

    public final void loadRegister() throws Exception {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Registrierung");
        Main.content.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/registerContent.fxml")));
    }

    public final void loadCart() throws Exception {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Bestellungen");
        Main.content.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/cartContent.fxml")));
    }

    public final void loadInfo() {
        Main.showAlert(Alert.AlertType.INFORMATION, "Information",
                "Ersteller: " + Manifests.read("Creator") + "\n" +
                        "Letzte Änderung: " + Manifests.read("Last-Change") + "\n" +
                        "© 2019 BERNARDO.FM - Alle Rechte vorbehalten.");
    }

    public final void accountClick(final ActionEvent event) {
        final Node button = (Node) event.getSource();
        final Bounds buttonBounds = button.getBoundsInLocal();
        final double centerX = buttonBounds.getMinX() + buttonBounds.getWidth() / 2, centerY = buttonBounds.getMinY() + buttonBounds.getHeight() / 2;
        final Point p = MouseInfo.getPointerInfo().getLocation();
        final double x = p.getX(), y = p.getY();

        final Event contextEvent = new ContextMenuEvent(ContextMenuEvent.CONTEXT_MENU_REQUESTED, centerX, centerY, x, y, false, new PickResult(button, x, y));
        Event.fireEvent(button, contextEvent);
    }

    public final void loadSettings() throws Exception {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Einstellungen");
        Main.content.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/settingsContent.fxml")));
    }

    public final void saveSettings() {

    }

    public final void register() throws Exception {
        final TextField username = (TextField) Main.mainScene.lookup("#registerUsername");
        final PasswordField password = (PasswordField) Main.mainScene.lookup("#registerPassword"), passwordRepeat = (PasswordField) Main.mainScene.lookup("#registerPasswordRepeat");


        if (username.getText().equals("") || password.getText().equals("") || passwordRepeat.getText().equals("")) {
            Main.showAlert(Alert.AlertType.ERROR, "Fehler", "Sie müssen alle Felder ausfüllen.");
            return;
        }

        if (!username.getText().matches("[a-zA-Z0-9]*")) {
            Main.showAlert(Alert.AlertType.ERROR, "Fehler", "Der Benutzername darf keine Sonderzeichen enthalten.");
            return;
        }

        if (!password.getText().equals(passwordRepeat.getText())) {
            Main.showAlert(Alert.AlertType.ERROR, "Fehler", "Die Passwörter stimmen nicht überein.");
            return;
        }

        if (password.getText().length() < 8) {
            Main.showAlert(Alert.AlertType.ERROR, "Fehler", "Das Passwort ist zu schwach.");
            return;
        }

        try {
            if (Main.database.containsKey(username.getText())) {
                Main.showAlert(Alert.AlertType.ERROR, "Fehler", "Dieser Benutzername wird bereits verwendet.");
                return;
            }

            throw new NullPointerException();
        } catch (final NullPointerException e) {
            Main.database.put(username.getText(), new JSONParser().parse("{\"password\": \"" + password.getText() + "\"}"));
            Main.showAlert(Alert.AlertType.INFORMATION, "Erfolgreich", "Ihr Konto wurde erfolgreich erstellt.");
            login(username.getText(), password.getText());
        }
    }

}
