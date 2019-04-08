package fm.bernardo.shop;

import com.jcabi.manifests.Manifests;
import fm.bernardo.shop.assets.classes.showAlert;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public final class Controller
{

    private static JSONObject userData;
    private static ArrayList<HashMap> products = new ArrayList<HashMap>()
    {
        {
            add(new HashMap<String, String>()
            {{
                put("Kaffeebohnen 1Kg", "18.80");
            }});
            add(new HashMap<String, String>()
            {{
                put("Lipton Icetea 1L", "3.40");
            }});
        }
    };

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

            Controller.userData = (JSONObject) Main.database.get(Main.loggedInAs);
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


        new Thread(() -> {
            while (Main.isNullOrEmpty(Main.mainScene.lookup("#shopContent"))) ;

            final GridPane grid = ((GridPane) Main.mainScene.lookup("#shopContent"));
            Platform.runLater(() -> {
                for (int row = 0; row < Controller.products.size(); row++) {
                    final int finalRow = row;
                    Controller.products.get(finalRow).forEach((key, value) -> {
                        grid.add(new Label(key.toString()), 0, finalRow + 1);
                        grid.add(new Label(value.toString() + " CHF"), 1, finalRow + 1);

                        final TextField numField = new TextField();
                        numField.textProperty().addListener((observable, oldValue, newValue) -> {
                            if (!newValue.matches("\\d*"))
                                numField.setText(newValue.replaceAll("[^\\d]", ""));
                        });

                        grid.add(numField, 2, finalRow + 1);
                    });
                }
            });
        }).start();
    }

    public final void login () throws Exception
    {
        login(null, null);
    }

    public final void loadShop (final ActionEvent event) throws Exception
    {
        loadShop();
    }

    private static void logout () throws Exception
    {
        Main.mainStage.close();
        Main.loginData.delete();
        new Main().start(new Stage(), Main.database);
        new showAlert(Alert.AlertType.INFORMATION, "Information", "Sie wurden vom Konto abgemeldet.");
    }

    public final void logout (final ActionEvent event) throws Exception
    {
        logout();
    }

    public final void order () throws Exception
    {
        final GridPane content = (GridPane) Main.mainScene.lookup("#shopContent");
        final JSONObject order = new JSONObject();

        try {
            for (int i = 1; i < getRowCount(content); i++)
                order.put(((Label) content.getChildren().get(i * 3)).getText(), Integer.parseInt(((TextField) content.getChildren().get(i * 3 + 2)).getText()));
            ((JSONObject) Controller.userData.get("orders")).put(System.currentTimeMillis(), order);
        } catch (final NullPointerException e) {
            Controller.userData.put("orders", new JSONParser().parse("{\"" + System.currentTimeMillis() + "\": " + order + "}"));
        }

        Main.database.replace(Main.loggedInAs, Controller.userData);
        new showAlert(Alert.AlertType.INFORMATION, "Information", "Die Bestellung wurde aufgenommen.");
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

    public final void loadOrders () throws Exception
    {
        Main.content.getChildren().clear();
        Main.mainStage.setTitle("Bestellungen");
        Main.content.getChildren().add(FXMLLoader.load(getClass().getResource("assets/fxml/orderContent.fxml")));

        final JSONObject orders = (JSONObject) Controller.userData.get("orders");

        try {
            final List<String> indexes = new ArrayList<String>(orders.keySet());

            new Thread(() -> {
                while (Main.isNullOrEmpty(Main.mainScene.lookup("#orderContent"))) ;
                final GridPane content = (GridPane) Main.mainScene.lookup("#orderContent");

                Platform.runLater(() -> {
                    int i = 0, j = 0;
                    while (i < orders.size()) {
                        content.add(new Label(new SimpleDateFormat("dd.MM.yy // hh:mm").format(new Date(Long.parseLong(indexes.get(i))))), 0, j + 1);
                        final List<String> articles = new ArrayList<String>(((JSONObject) orders.get(indexes.get(i))).keySet());
                        for (final String article : articles) {
                            content.add(new Label(article), 1, j + 1);
                            content.add(new Label(((JSONObject) orders.get(indexes.get(i))).get(article).toString()), 2, j + 1);
                            j++;
                        }
                        i++;
                    }
                });
            }).start();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public final void loadInfo ()
    {
        new showAlert(Alert.AlertType.INFORMATION, "Information",
                "Ersteller: " + Manifests.read("Creator") + "\n" +
                        "Letzte Änderung: " + Manifests.read("Last-Change") + "\n" +
                        "© 2019 BERNARDO.FM - Alle Rechte vorbehalten.", true);
    }

    public final void accountClick (final ActionEvent event)
    {
        final Node button = (Node) event.getSource();
        final Bounds buttonBounds = button.getBoundsInLocal();
        final Point p = MouseInfo.getPointerInfo().getLocation();
        final double centerX = buttonBounds.getMinX() + buttonBounds.getWidth() / 2, centerY = buttonBounds.getMinY() + buttonBounds.getHeight() / 2, x = p.getX(), y = p.getY();

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
                Controller.userData.replace("password", password);
                Main.database.replace(Main.loggedInAs, Controller.userData);
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

    private int getRowCount (final GridPane pane)
    {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            final Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                final Integer rowIndex = GridPane.getRowIndex(child);
                if (rowIndex != null) {
                    numRows = Math.max(numRows, rowIndex + 1);
                }
            }
        }
        return numRows;
    }

}
