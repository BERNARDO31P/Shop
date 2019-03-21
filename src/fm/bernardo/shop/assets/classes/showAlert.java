package fm.bernardo.shop.assets.classes;

import fm.bernardo.shop.Main;
import javafx.scene.control.Alert;
import org.json.simple.JSONObject;

public final class showAlert extends Alert {

    public showAlert(final Alert.AlertType type, final String title, final String text, final Boolean force) {
        super(type);
        if (force || Boolean.parseBoolean(((JSONObject) Main.settings.get(Main.loggedInAs)).get("showAlerts").toString())) {
            this.setTitle(title);
            this.setHeaderText(null);
            this.setContentText(text);
            this.showAndWait();
        }
    }

    public showAlert(final Alert.AlertType type, final String title, final String text) {
        super(type);
        new showAlert(type, title, text, false);
    }

}
