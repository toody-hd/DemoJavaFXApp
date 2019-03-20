package Controllers;

import CustomMessageBox.CustomMessageBox;
import Settings.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.ResourceBundle;

public class generalSettingsStageController {

    @FXML private ScrollPane expanderScrollPane;
    @FXML private VBox expanderPane;
    @FXML private ToggleButton expander;
    @FXML private ComboBox lang;
    @FXML private CheckBox mini;
    @FXML private CheckBox top;
    @FXML private CheckBox sys;
    @FXML private ComboBox filter;
    @FXML private MenuButton exclude;
    @FXML private MenuButton hide;
    @FXML private Button delFil;

    private ResourceBundle bundle = ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang));
    private int selectedLang = 0;

    @FXML void initialize() {
        bindings();
        lang.getSelectionModel().select(Settings._getLang());
        mini.setSelected(Settings._miniMode);
        top.setSelected(Settings._topmostMode);
        sys.setSelected(Settings._messageMode);
        populateFilters();
        populateScripts();

    }

    private void bindings() {
        expanderScrollPane.managedProperty().bind(expander.selectedProperty());
        expanderScrollPane.visibleProperty().bind(expander.selectedProperty());
    }

    private void populateFilters() {
        Settings._filters.forEach(item -> {
            creatFilter(item);
            if (Boolean.parseBoolean(item[1]))
                exclude.setText(exclude.getText() + "," + item[0]);
            if (Boolean.parseBoolean(item[2]))
                hide.setText(hide.getText() + "," + item[0]);
        });
        if (Settings._filters.size() > 0) {
            if (exclude.getText().length() > bundle.getString("none").length() + 1)
                exclude.setText(exclude.getText().substring(bundle.getString("none").length() + 1));
            if (hide.getText().length() > bundle.getString("none").length() + 1)
                hide.setText(hide.getText().substring(bundle.getString("none").length() + 1));
        }
        if (filter.getItems().size() > 0) {
            filter.getSelectionModel().select(0);
        }
    }

    private void populateScripts() {
        Settings._scripts.forEach(item -> createScript(item));
    }

    @FXML void langOpen() {
        selectedLang = lang.getSelectionModel().getSelectedIndex();
    }

    @FXML void langClose() {
        if (lang.getSelectionModel().getSelectedIndex() != selectedLang)
            Settings._setLang(lang.getSelectionModel().getSelectedIndex());

    }

    @FXML void miniClick(){
        Settings._miniMode = mini.isSelected();
    }

    @FXML void topClick(){
        Settings._topmostMode = top.isSelected();
    }

    @FXML void sysClick(){
        Settings._messageMode = sys.isSelected();
    }

    @FXML void addScript() {
        createScript(new String[] {bundle.getString("name"), bundle.getString("data")});
    }

    private void createScript(String[] item) {
        if (!expander.isSelected())
            expander.setSelected(true);
        Label _name = new Label(item[0]);
        TextField _nameTF = new TextField();
        Label _data = new Label(item[1]);
        TextField _dataTF = new TextField();
        ToggleButton _edit_save = new ToggleButton("\uF0C7");
        if (!item[0].equals(bundle.getString("name")) || !item[1].equals(bundle.getString("data"))) {
            _edit_save.setSelected(true);
            _edit_save.setText("\uF044");
            _edit_save.setUserData(item);
        }
        _edit_save.getStylesheets().add("/Styles/generalSettingsStageStyle.css");
        _edit_save.getStyleClass().add("expander-btn");
        _edit_save.setOnAction(e -> {
            if (_edit_save.isSelected()) {
                String[] _item = new String[] {_nameTF.getText(), _dataTF.getText()};
                _edit_save.setText("\uF044");
                if (!_nameTF.getText().isEmpty())
                    _name.setText(_nameTF.getText());
                if (!_dataTF.getText().isEmpty())
                    _data.setText(_dataTF.getText());
                if (_edit_save.getUserData() != null)
                    Settings._scripts.set(Settings._scripts.indexOf((String[]) _edit_save.getUserData()), _item);
                else
                    Settings._scripts.add(_item);
                _edit_save.setUserData(_item);
            }
            else {
                _edit_save.setText("\uF0C7");
                if (!_name.getText().equals(bundle.getString("name")))
                    _nameTF.setText(_name.getText());
                if (!_data.getText().equals(bundle.getString("data")))
                    _dataTF.setText(_data.getText());
                _name.setText(bundle.getString("name"));
                _data.setText(bundle.getString("data"));
            }
        });
        _nameTF.managedProperty().bind(_edit_save.selectedProperty().not());
        _nameTF.visibleProperty().bind(_edit_save.selectedProperty().not());
        _dataTF.managedProperty().bind(_edit_save.selectedProperty().not());
        _dataTF.visibleProperty().bind(_edit_save.selectedProperty().not());
        Button _delBtn = new Button("-");
        _delBtn.setUserData(expanderPane.getChildren().size());
        _delBtn.getStylesheets().add("/Styles/generalSettingsStageStyle.css");
        _delBtn.getStyleClass().add("expander-btn");
        _delBtn.setOnAction(e -> {
            Settings._scripts.remove((String[]) _edit_save.getUserData());
            expanderPane.getChildren().remove(_delBtn.getParent());
            if (expanderPane.getChildren().size() == 0)
                expander.setSelected(false);
        });
        HBox _hBox = new HBox(_name, _nameTF, _data, _dataTF, _edit_save, _delBtn);
        _hBox.setAlignment(Pos.CENTER_LEFT);
        _hBox.setSpacing(5);
        expanderPane.getChildren().add(_hBox);
    }

    @FXML void addFilter() {
        Control _obj = CustomMessageBox.Show(bundle.getString("filterName"), new TextField());
        if (_obj != null && !((TextField)_obj).getText().isEmpty() && !filter.getItems().contains(((TextField)_obj).getText())) {
            creatFilter(new String[] {((TextField) _obj).getText(), "false", "false"});
            filter.getSelectionModel().select(((TextField) _obj).getText());
            Settings._filters.add(new String[] {((TextField) _obj).getText(), "false", "false"});
        }
    }

    private void creatFilter(String[] item) {
        filter.getItems().add(item[0]);
        CheckBox _exclude = new CheckBox(item[0]);
        CheckBox _hide = new CheckBox(item[0]);
        _exclude.setSelected(Boolean.parseBoolean(item[1]));
        _hide.setSelected(Boolean.parseBoolean(item[2]));
        _exclude.setOnAction(event -> itemCheck(event, exclude));
        _hide.setOnAction(event -> itemCheck(event, hide));
        exclude.getItems().add(new CustomMenuItem(_exclude, false));
        hide.getItems().add(new CustomMenuItem(_hide, false));
        delFil.setVisible(true);
    }

    private void itemCheck(ActionEvent actionEvent, MenuButton menuButton) {
        if (((CheckBox)actionEvent.getSource()).isSelected()) {
            if (menuButton.getText().equals(bundle.getString("none")))
                menuButton.setText(((CheckBox) actionEvent.getSource()).getText());
            else
                menuButton.setText(menuButton.getText() + "," + ((CheckBox) actionEvent.getSource()).getText());
        }
        else {
            menuButton.setText(menuButton.getText().replace(((CheckBox) actionEvent.getSource()).getText(), "").replace(",,", ","));
            trimPromptItem(menuButton);
        }
        if (menuButton == exclude)
            Settings._filters.get(filter.getItems().indexOf(((CheckBox) actionEvent.getSource()).getText()))[1] = String.valueOf(((CheckBox)actionEvent.getSource()).isSelected());
        else
            Settings._filters.get(filter.getItems().indexOf(((CheckBox) actionEvent.getSource()).getText()))[2] = String.valueOf(((CheckBox)actionEvent.getSource()).isSelected());
    }

    @FXML void delFilter() {
        exclude.getItems().remove(filter.getSelectionModel().getSelectedIndex());
        hide.getItems().remove(filter.getSelectionModel().getSelectedIndex());
        removePromptItem(exclude);
        removePromptItem(hide);
        Settings._filters.remove(filter.getSelectionModel().getSelectedIndex());
        filter.getItems().remove(filter.getSelectionModel().getSelectedIndex());
        if (filter.getItems().size() > 0)
            filter.getSelectionModel().select(0);
        if (filter.getItems().size() == 0)
            delFil.setVisible(false);
    }

    private void removePromptItem(MenuButton menuButton) {
        menuButton.setText(menuButton.getText().replace(filter.getSelectionModel().getSelectedItem().toString(), "").replace(",,", ","));
        trimPromptItem(menuButton);
    }

    private void trimPromptItem(MenuButton menuButton) {
        if (menuButton.getText().endsWith(","))
            menuButton.setText(menuButton.getText().substring(0, menuButton.getText().length() - 1));
        if (menuButton.getText().startsWith(","))
            menuButton.setText(menuButton.getText().substring(1));
        if (menuButton.getText().isEmpty())
            menuButton.setText(bundle.getString("none"));
    }

    void ResetSettings() {
        lang.getSelectionModel().select(0);
        mini.setSelected(false);
        top.setSelected(false);
        sys.setSelected(true);
    }
}