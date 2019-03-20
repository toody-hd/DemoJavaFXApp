package Controllers;

import Animations.Animations;
import CustomMessageBox.CustomMessageBox;
import CustomMessageBox.CustomMessageBox.MessageBoxButton;
import CustomMessageBox.CustomMessageBox.MessageBoxImage;
import CustomMessageBox.CustomMessageBox.MessageBoxResult;
import Helpers.INI;
import Settings.Settings;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class settingsStageController {

    @FXML private BorderPane mainPane;
    private int page;
    private FXMLLoader loader;
    private ResourceBundle bundle = ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang));

    @FXML void initialize() throws Exception {
        Animations.scaleIn(mainPane);
        readSettings();
        Settings._reset();
        generalSettings();
    }

    private void readSettings() {
        Settings.ResetData();
        if (INI.FiltersFileExist())
            INI.filtersFile.ReadSections().forEach(item -> Settings.filters.add(new String[] {item.toString(), INI.filtersFile.ReadString(item.toString(), "ExcludeUpdate", ""), INI.filtersFile.ReadString(item.toString(), "HideOnStartUp", "")}));
        if (INI.ScriptsFileExist())
            INI.scriptsFile.ReadSection("Scripts").forEach(item -> Settings.scripts.add(new String[] {item.toString(), INI.scriptsFile.ReadString("Scripts", item.toString(), "")}));
    }

    @FXML void generalSettings() throws Exception {
        loader = new FXMLLoader(getClass().getResource("/Stages/generalSettingsStage.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang)));
        mainPane.setCenter(loader.load());
        page = 1;
    }

    @FXML void visualSettings() throws Exception {
        loader = new FXMLLoader(getClass().getResource("/Stages/visualSettingsStage.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang)));
        mainPane.setCenter(loader.load());
        page = 2;
    }

    @FXML void goBack(ActionEvent actionEvent) {
        Settings._reset();
        ScaleTransition _st = Animations.scaleOut(mainPane);
        _st.setOnFinished(e-> {
            try { ((BorderPane) ((Node) actionEvent.getSource()).getScene().getRoot()).setCenter(FXMLLoader.load(getClass().getResource("/Stages/mainStage.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang))));
            } catch (IOException ignored) { }
        });
    }

    @FXML void save(ActionEvent actionEvent) {
        Settings.Save();
        INI.settingsFile.WriteString("Settings","Language", Settings.lang);
        INI.settingsFile.WriteBool("Settings","MiniMode", Settings.miniMode);
        INI.settingsFile.WriteBool("Settings","TopMode", Settings.topmostMode);
        INI.settingsFile.WriteBool("Settings","MessageMode", Settings.messageMode);
        INI.settingsFile.UpdateFile();
        INI.filtersFile.Clear();
        Settings.filters.forEach(item -> {
            INI.filtersFile.WriteString(item[0], "ExcludeUpdate", item[1]);
            INI.filtersFile.WriteString(item[0], "HideOnStartUp", item[2]);
        });
        INI.filtersFile.UpdateFile();
        INI.scriptsFile.Clear();
        Settings.scripts.forEach(item -> INI.scriptsFile.WriteString("Scripts", item[0], item[1]));
        INI.scriptsFile.UpdateFile();
        applySettings();
        ScaleTransition _st = Animations.scaleOut(mainPane);
        _st.setOnFinished(e-> {
            try { ((BorderPane) ((Node) actionEvent.getSource()).getScene().getRoot()).setCenter(FXMLLoader.load(getClass().getResource("/Stages/mainStage.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang))));
            } catch (IOException ignored) { }
        });
    }

    private void applySettings() {
        ((Stage)mainPane.getScene().getWindow()).setAlwaysOnTop(Settings.topmostMode);
    }

    @FXML void resetSettings(){
        MessageBoxResult result = CustomMessageBox.Show(bundle.getString("resetSettings?"), bundle.getString("resetSettings"), MessageBoxButton.YesNo, MessageBoxImage.Question);
        if (result.equals(MessageBoxResult.Yes)) {
            if (page == 1) {
                ((generalSettingsStageController)loader.getController()).ResetSettings();
                Settings.Reset();
            }
            else if (page == 2) {
                //((visualSettingsStageController)loader.getController()).ResetSettings();
                Settings.Reset();
            }
        }
    }

    @FXML void resetData(ActionEvent actionEvent) {
        MessageBoxResult result = CustomMessageBox.Show(bundle.getString("resetData?"), bundle.getString("resetData"), MessageBoxButton.YesNo, MessageBoxImage.Question);
        if (result.equals(MessageBoxResult.Yes)) {
            Settings.Reset();
            Settings.ResetData();
            INI.settingsFile.WriteString("Settings","Language", "en-US");
            INI.settingsFile.WriteBool("Settings","MiniMode", false);
            INI.settingsFile.WriteBool("Settings","TopMode", false);
            INI.settingsFile.WriteBool("Settings","MessageMode", true);
            if (new File("filters.ini").exists())
                new File("filters.ini").delete();
            if (new File("scripts.ini").exists())
                new File("scripts.ini").delete();
            if (new File("items.ini").exists())
                new File("items.ini").delete();
            if (new File("pictures").exists())
                if (new File("pictures").isDirectory()) {
                    String[] children = new File("pictures").list();
                    for (int i = 0; i < children.length; i++) {
                        (new File(new File("pictures"), children[i])).delete();
                    }
                    new File("pictures").delete();
                }
            ScaleTransition _st = Animations.scaleOut(mainPane);
            _st.setOnFinished(e-> {
                try { ((BorderPane) ((Node) actionEvent.getSource()).getScene().getRoot()).setCenter(FXMLLoader.load(getClass().getResource("/Stages/mainStage.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang))));
                } catch (IOException ignored) { }
            });
        }
    }
}
