package main;

import Animations.Animations;
import Helpers.INI;
import Settings.Settings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class mainController {

    @FXML private Label title;
    @FXML private BorderPane mainPane;
    @FXML private ImageView ico;
    @FXML private HBox buttons;
    @FXML private GridPane topBar;
    private double xOffset, yOffset = 0;
    private ResourceBundle bundle;

    @FXML void initialize() throws Exception {
        initializeSettings();
        mainPane.setCenter(FXMLLoader.load(getClass().getResource("/Stages/loginStage.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang))));
        Platform.runLater(() -> {
            title.setText(((Stage) mainPane.getScene().getWindow()).getTitle());
            applySettings();
            Animations.scaleIn(mainPane.getCenter());
        });
    }

    private void initializeSettings() {
        if (INI.SettingsFileExist()) {
            Settings.lang = Settings._lang = INI.settingsFile.ReadString("Settings", "Language", "en-US");
            Settings.miniMode = Settings._miniMode = INI.settingsFile.ReadBool("Settings", "MiniMode", false);
            Settings.topmostMode = Settings._topmostMode = INI.settingsFile.ReadBool("Settings", "TopMode", false);
            Settings.messageMode = Settings._messageMode = INI.settingsFile.ReadBool("Settings", "MessageMode", true);
        }
    }

    private void applySettings() {
        ((Stage)mainPane.getScene().getWindow()).setAlwaysOnTop(Settings.topmostMode);
    }

    @FXML void TitlePressed(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        }
    }

    @FXML void TitleDragged(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).isMaximized()) {
                double percent = mouseEvent.getScreenX() / ((topBar.getWidth() - ico.getFitWidth() - buttons.getWidth()) / 100);
                Maximize(mouseEvent);
                xOffset = (((topBar.getWidth() - ico.getFitWidth() - buttons.getWidth()) * percent) / 100) + ico.getFitWidth() + 10;
            }
            (((Node) mouseEvent.getSource()).getScene().getWindow()).setX(mouseEvent.getScreenX() - xOffset);
            (((Node) mouseEvent.getSource()).getScene().getWindow()).setY(mouseEvent.getScreenY() - yOffset);
        }
    }

    @FXML void TitleClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
            if(mouseEvent.getClickCount() == 2){
                Maximize(mouseEvent);
            }
        }
    }

    @FXML void Close() {
        System.exit(0);
    }

    @FXML void Maximize(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).isMaximized()) {
                mainPane.getStyleClass().remove("root-expanded");
                mainPane.getStyleClass().add("root-normal");
                ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).setMaximized(false);
            } else {
                mainPane.getStyleClass().remove("root-normal");
                mainPane.getStyleClass().add("root-expanded");
                ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).setMaximized(true);
            }
        }
    }

    @FXML void Minimize(ActionEvent actionEvent) {
        if (!Settings.miniMode)
            ((Stage) ((Node)actionEvent.getSource()).getScene().getWindow()).setIconified(true);
        else {
            toTray(((Stage) ((Node)actionEvent.getSource()).getScene().getWindow()));
            ((Stage) ((Node)actionEvent.getSource()).getScene().getWindow()).close();
        }
    }

    private void toTray(final Stage stage) {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
        }
        else {
            bundle = ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang));
            Platform.setImplicitExit(false);
            BufferedImage trayIconImage = null;
            try { trayIconImage = ImageIO.read(getClass().getResource("/Resources/img.png"));
            } catch (IOException ignored) {}
            assert trayIconImage != null;
            int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
            final PopupMenu popup = new PopupMenu();
            final TrayIcon trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
            final SystemTray tray = SystemTray.getSystemTray();
            trayIcon.addActionListener(e -> {
                Platform.runLater(stage::show);
                tray.remove(trayIcon);
            });
            if (Settings.messageMode)
                Platform.runLater(() -> trayIcon.displayMessage(stage.getTitle(), stage.getTitle() + bundle.getString("trayMessage"), TrayIcon.MessageType.NONE));
            MenuItem showItem = new MenuItem(bundle.getString("show"));
            MenuItem exitItem = new MenuItem(bundle.getString("exit"));
            stage.setOnCloseRequest(t -> Platform.runLater(stage::hide));
            showItem.addActionListener(e -> {
                        Platform.runLater(stage::show);
                        tray.remove(trayIcon);
            });
            exitItem.addActionListener(e -> System.exit(0));
            popup.add(showItem);
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            try { tray.add(trayIcon); }
            catch (AWTException e) { System.out.println("TrayIcon could not be added."); }
        }
    }
}