package NewItemWindow;

import CustomMessageBox.CustomMessageBox;
import Helpers.INI;
import Helpers.Validations;
import Settings.Settings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewItemWindowController {

    private double xOffset, yOffset = 0;
    @FXML private GridPane topBar;
    @FXML private ImageView ico;
    @FXML private Label ttl;
    @FXML public TextField name;
    @FXML public TextField path;
    @FXML public TextField version;
    @FXML public TextField link;
    @FXML public MenuButton tags;
    @FXML public Button img;
    @FXML public Button addSave;
    private ResourceBundle bundle = ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang));
    private int updateIndex = -1;

    @FXML void initialize() {
        Platform.runLater(()->name.requestFocus());
        img.setBackground(null);
        initListeners();
    }

    private void initListeners() {
        link.textProperty().addListener((observable, oldValue, newValue) -> {

            if (name.getText().isEmpty() && INI.ScriptsFileExist() && INI.scriptsFile.ValueExist("Scripts", "Name")) {
                Matcher _matcher = Pattern.compile(INI.scriptsFile.ReadString("Scripts", "Name", "")).matcher(link.getText());
                if (_matcher.find())
                    name.setText(_matcher.group("name").replace("-", " "));
            }
            if (INI.ScriptsFileExist() && INI.scriptsFile.ValueExist("Scripts", "Version")) {
                Matcher _matcher = Pattern.compile(INI.scriptsFile.ReadString("Scripts", "Version", "")).matcher(link.getText());
                if (_matcher.find())
                    version.setText(_matcher.group(0).replace("-", ".").substring(1, _matcher.group(0).length() -1));
            }
        });
    }

    void initFilters(String[] items) {
        if (INI.FiltersFileExist())
            INI.filtersFile.ReadSections().forEach(item -> {
                CheckBox _cb = new CheckBox(item.toString());
                _cb.setSelected((items != null) && String.join(",",items).contains(item.toString()));
                _cb.setOnAction(this::filterClicked);
                tags.getItems().add(new CustomMenuItem(_cb,false));
                if (_cb.isSelected())
                    tags.setText(tags.getText() + "," + item.toString());
                if (item.toString().equals("Update"))
                    updateIndex = tags.getItems().size() - 1;
            });
        if (tags.getText().length() > bundle.getString("none").length() + 1)
            tags.setText(tags.getText().substring(bundle.getString("none").length() + 1));
    }

    private void filterClicked(ActionEvent actionEvent) {
        if (((CheckBox) actionEvent.getSource()).isSelected()) {
            if (tags.getText().equals(bundle.getString("none")))
                tags.setText(((CheckBox) actionEvent.getSource()).getText());
            else
                tags.setText(tags.getText() + "," + ((CheckBox) actionEvent.getSource()).getText());
        } else {
            tags.setText(tags.getText().replace(((CheckBox) actionEvent.getSource()).getText(), "").replace(",,", ","));
            if (tags.getText().endsWith(","))
                tags.setText(tags.getText().substring(0, tags.getText().length() - 1));
            if (tags.getText().startsWith(","))
                tags.setText(tags.getText().substring(1));
            if (tags.getText().isEmpty())
                tags.setText(bundle.getString("none"));
        }
    }

    @FXML void TitlePressed(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        }
    }

    @FXML void TitleDragged(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            (((Node) mouseEvent.getSource()).getScene().getWindow()).setX(mouseEvent.getScreenX() - xOffset);
            (((Node) mouseEvent.getSource()).getScene().getWindow()).setY(mouseEvent.getScreenY() - yOffset);
        }
    }

    @FXML void Close() {
        NewItemWindow.Close();
    }

    @FXML void img(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("selectImage"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(bundle.getString("imageFile"), "*.bmp; *.png; *.jpg; *.jpeg; *.gif"));
        File selectedFile = fileChooser.showOpenDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            try {
                img.setBackground(new Background(new BackgroundImage(
                        new Image(selectedFile.toURI().toString(), 256, 144, true, true),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.CENTER, new BackgroundSize(
                                        img.getWidth(), img.getHeight(),
                                        true, true,
                                        true, false))));
            }
            catch (Exception ignored){}
        }
    }

    @FXML void browse(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("selectExe"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(bundle.getString("exeFile"), "*.exe"));
        File selectedFile = fileChooser.showOpenDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            path.setText(selectedFile.getPath());
        }
    }

    @FXML void updateLink(ActionEvent actionEvent) {
        if (Validations.isValidURL(link.getText())) {
            WebEngine webEngine = new WebEngine();
            webEngine.load(link.getText());
            link.textProperty().bind(webEngine.locationProperty());
            webEngine.locationProperty().addListener((obs, oldState, newState) -> {
                ((Button)actionEvent.getSource()).setText(bundle.getString("done"));
                tags.setText(tags.getText().replace(",Update", "").replace("Update,", "").replace("Update", bundle.getString("none")));
                if (updateIndex != -1)
                    ((CheckBox)((CustomMenuItem)tags.getItems().get(updateIndex)).getContent()).setSelected(false);
            });
        }
        else
            ((Button)actionEvent.getSource()).setText(bundle.getString("invalidLink") + "...");
    }

    @FXML void add_save() {
        if (!name.getText().isEmpty() && !path.getText().isEmpty() &&
                !version.getText().isEmpty() && !link.getText().isEmpty() &&
                img.getBackground() != null)
            NewItemWindow.Add_Save();
        else
            CustomMessageBox.Show(bundle.getString("addItemFailed"));
    }
}
