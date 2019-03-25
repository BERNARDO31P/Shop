package fm.bernardo.shop;

import com.jcabi.manifests.Manifests;
import fm.bernardo.shop.assets.classes.showAlert;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;


public final class Controller
{

    static void login (String username, String password) throws Exception
    {

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

            new showAlert(Alert.AlertType.INFORMATION, "Erfolgreich", "Sie wurden erfolgreich weitergeleitet und angemeldet.");
        } else {
            new showAlert(Alert.AlertType.ERROR, "Fehler", "Ungültige Anmeldedaten.");
        }
    }

    private static void loadShop () throws Exception
    {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Shop");
        Main.content.getChildren().add(FXMLLoader.load(Main.class.getResource("assets/fxml/shopContent.fxml")));
    }

    private static void logout () throws Exception
    {
        Main.mainStage.close();
        Main.loginData.delete();
        new Main().start(new Stage(), Main.database);
        new showAlert(Alert.AlertType.INFORMATION, "Information", "Sie wurden vom Konto abgemeldet.");
    }

    public final void login () throws Exception
    {
        login(null, null);
    }

    public final void loadShop (final ActionEvent event) throws Exception
    {
        loadShop();
    }

    public final void logout (final ActionEvent event) throws Exception
    {
        logout();
    }

    public final void loadLogin () throws Exception
    {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Anmelden");
        Main.content.getChildren().add(FXMLLoader.load(Main.class.getResource("assets/fxml/loginContent.fxml")));
    }

    public final void loadRegister () throws Exception
    {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Registrierung");
        Main.content.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/registerContent.fxml")));
    }

    public final void loadCart () throws Exception
    {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Bestellungen");
        Main.content.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/cartContent.fxml")));
    }

    public final void loadInfo ()
    {
        new showAlert(Alert.AlertType.INFORMATION, "Information",
                "Ersteller: " + Manifests.read("Creator") + "\n" +
                        "Letzte Änderung: " + Manifests.read("Last-Change") + "\n" +
                        "© 2019 BERNARDO.FM - Alle Rechte vorbehalten.");
    }

    public final void accountClick (final ActionEvent event)
    {
        final Node button = (Node) event.getSource();
        final Bounds buttonBounds = button.getBoundsInLocal();
        final double centerX = buttonBounds.getMinX() + buttonBounds.getWidth() / 2, centerY = buttonBounds.getMinY() + buttonBounds.getHeight() / 2;
        final Point p = MouseInfo.getPointerInfo().getLocation();
        final double x = p.getX(), y = p.getY();

        final Event contextEvent = new ContextMenuEvent(ContextMenuEvent.CONTEXT_MENU_REQUESTED, centerX, centerY, x, y, false, new PickResult(button, x, y));
        Event.fireEvent(button, contextEvent);
    }

    public final void saveSettings () throws Exception
    {
        final String currentPassword = ((PasswordField) Main.content.lookup("#settingsCurrentPassword")).getText(),
                password = ((PasswordField) Main.content.lookup("#settingsPassword")).getText(),
                passwordRepeat = ((PasswordField) Main.content.lookup("#settingsPasswordRepeat")).getText();
        final CheckBox stayLoggedIn = (CheckBox) Main.content.lookup("#stayLoggedIn"),
                showAlerts = (CheckBox) Main.content.lookup("#showAlerts");


        if ((password.equals("") && !currentPassword.equals("")) || passwordRepeat.equals("") && !currentPassword.equals("")) {
            new showAlert(Alert.AlertType.ERROR, "Fehler", "Es wurden nicht alle Passwortfelder ausgefüllt.");
            return;
        } else {
            if (!((JSONObject) Main.database.get(Main.loggedInAs)).get("password").equals(currentPassword) && !password.equals("")) {
                new showAlert(Alert.AlertType.ERROR, "Fehler", "Das alte Passwort ist falsch.");
                return;
            } else if (!password.equals("")) {
                final JSONObject data = (JSONObject) Main.database.get(Main.loggedInAs);
                data.replace("password", password);
                Main.database.replace(Main.loggedInAs, data);
            }
        }

        Main.settings.put(Main.loggedInAs, new JSONParser().parse("{\"stayLoggedIn\": \"" + stayLoggedIn.isSelected() + "\", \"showAlerts\": \"" + showAlerts.isSelected() + "\"}"));

        new showAlert(Alert.AlertType.INFORMATION, "Erfolgreich", "Die Einstellungen wurden übernommen.", true);
    }

    public final void loadSettings () throws Exception
    {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Einstellungen");
        Main.content.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/settingsContent.fxml")));

        try {
            ((CheckBox) Main.content.lookup("#stayLoggedIn")).setSelected(Boolean.parseBoolean(((JSONObject) Main.settings.get(Main.loggedInAs)).get("stayLoggedIn").toString()));
            ((CheckBox) Main.content.lookup("#showAlerts")).setSelected(Boolean.parseBoolean(((JSONObject) Main.settings.get(Main.loggedInAs)).get("showAlerts").toString()));
        } catch (final Exception ignored) {
        }
    }

    public final void register () throws Exception
    {
        final String username = ((TextField) Main.mainScene.lookup("#registerUsername")).getText(),
                password = ((PasswordField) Main.mainScene.lookup("#registerPassword")).getText(),
                passwordRepeat = ((PasswordField) Main.mainScene.lookup("#registerPasswordRepeat")).getText();


        if (username.equals("") || password.equals("") || passwordRepeat.equals("")) {
            new showAlert(Alert.AlertType.ERROR, "Fehler", "Sie müssen alle Felder ausfüllen.");
            return;
        }

        if (!username.matches("[a-zA-Z0-9]*")) {
            new showAlert(Alert.AlertType.ERROR, "Fehler", "Der Benutzername darf keine Sonderzeichen enthalten.");
            return;
        }

        if (!password.equals(passwordRepeat)) {
            new showAlert(Alert.AlertType.ERROR, "Fehler", "Die Passwörter stimmen nicht überein.");
            return;
        }

        if (password.length() < 8) {
            new showAlert(Alert.AlertType.ERROR, "Fehler", "Das Passwort ist zu schwach.");
            return;
        }

        try {
            if (Main.database.containsKey(username)) {
                new showAlert(Alert.AlertType.ERROR, "Fehler", "Dieser Benutzername wird bereits verwendet.");
                return;
            }

            throw new NullPointerException();
        } catch (final NullPointerException e) {
            Main.database.put(username, new JSONParser().parse("{\"password\": \"" + password + "\"}"));
            new showAlert(Alert.AlertType.INFORMATION, "Erfolgreich", "Ihr Konto wurde erfolgreich erstellt.");
            login(username, password);
        }
    }

}
