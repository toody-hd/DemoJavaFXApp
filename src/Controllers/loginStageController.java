package Controllers;

import Animations.Animations;
import CustomMessageBox.CustomMessageBox;
import CustomMessageBox.CustomMessageBox.*;
import Settings.Settings;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class loginStageController {

    @FXML private StackPane mainPane;
    @FXML private TextField user;
    @FXML private PasswordField pass;
    private ResourceBundle bundle = ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang));

    @FXML void initialize() {
        Animations.scaleIn(mainPane);
        Platform.runLater(()->user.requestFocus());
    }

    @FXML void login(ActionEvent actionEvent) {
            if (user.getText().equals("admin") && pass.getText().equals("passx")) {
                ScaleTransition _st = Animations.scaleOut(((BorderPane)((Node)actionEvent.getSource()).getScene().getRoot()).getCenter());
                _st.setOnFinished(e-> {
                    try { ((BorderPane)((Node)actionEvent.getSource()).getScene().getRoot()).setCenter(FXMLLoader.load(getClass().getResource("/Stages/mainStage.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang))));
                    } catch (IOException ignored) {}
                });
            } else
                CustomMessageBox.Show(bundle.getString("invalidUserOrPass"), bundle.getString("warning"), MessageBoxButton.OK, MessageBoxImage.Warning);
    }
}
