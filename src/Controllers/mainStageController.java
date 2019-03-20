package Controllers;

import Animations.Animations;
import Controls.Expander;
import CustomMessageBox.CustomMessageBox;
import CustomMessageBox.CustomMessageBox.MessageBoxButton;
import CustomMessageBox.CustomMessageBox.MessageBoxImage;
import CustomMessageBox.CustomMessageBox.MessageBoxResult;
import Helpers.INI;
import Helpers.Validations;
import NewItemWindow.NewItemWindow;
import NewItemWindow.NewItemWindow.Item;
import Settings.Settings;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class mainStageController {

    @FXML private StackPane mainPane;
    @FXML private TextField searchTextField;
    @FXML private ToggleButton searchTbtn;
    @FXML private FlowPane contentPane;
    @FXML private MenuButton group;
    @FXML private ToggleButton groupBy;
    @FXML private AnchorPane oL;
    private ResourceBundle bundle = ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang));
    private PauseTransition clickTimer = new PauseTransition(Duration.millis(250));
    private int clickCounter;
    private Item clickedItem;
    private ArrayList<String> filters = new ArrayList<>();
    private WebEngine webEngine = new WebEngine();
    private int currentItem;
    private Expander expander;
    private ArrayList _name;
    private ArrayList _currentVersion;
    private ArrayList _newVersion;
    private ArrayList _hide = new ArrayList();
    private ArrayList _exclude = new ArrayList();

    @FXML void initialize() {
        searchTextField.managedProperty().bind(searchTbtn.selectedProperty());
        searchTextField.visibleProperty().bind(searchTbtn.selectedProperty());
        clickTimer.setOnFinished(event -> itemClicked());
        searchTextField.textProperty().addListener((Observable, oldValue, newValue) -> searchItems(newValue));
        initFilters();
        initItems();
        initUpdates();
        Animations.scaleIn(mainPane);
    }

    private void initFilters() {
        if (INI.FiltersFileExist()) {
            INI.filtersFile.ReadSections().forEach(item -> {
                if (!INI.filtersFile.ReadBool(item.toString(), "HideOnStartUp", false)) {
                    CheckBox _cb = new CheckBox(item.toString());
                    _cb.setOnAction(event -> {
                        cbSetText(event);
                        groupItems(((CheckBox) event.getSource()).isSelected(), ((CheckBox) event.getSource()).getText());
                    });
                    group.getItems().add(new CustomMenuItem(_cb, false));
                }
                else
                    _hide.add(item.toString());
                if (INI.filtersFile.ReadBool(item.toString(), "ExcludeUpdate", false))
                    _exclude.add(item.toString());
            });
        }
    }

    private void cbSetText(ActionEvent actionEvent) {
        if (((CheckBox) actionEvent.getSource()).isSelected()) {
            if (group.getText().equals(bundle.getString("none")))
                group.setText(((CheckBox) actionEvent.getSource()).getText());
            else
                group.setText(group.getText() + "," + ((CheckBox) actionEvent.getSource()).getText());
        } else {
            group.setText(group.getText().replace(((CheckBox) actionEvent.getSource()).getText(), "").replace(",,", ","));
            if (group.getText().endsWith(","))
                group.setText(group.getText().substring(0, group.getText().length() - 1));
            if (group.getText().startsWith(","))
                group.setText(group.getText().substring(1));
            if (group.getText().isEmpty())
                group.setText(bundle.getString("none"));
        }
    }

    private void initItems() {
        if (INI.ItemsFileExist())
            INI.itemsFile.ReadSections().forEach(item -> {
                if (Collections.disjoint(Arrays.asList(INI.itemsFile.ReadString(item.toString(), "Tags", "").split(",")), _hide)) {
                    String[] _tags = new String[] {};
                    if (!INI.itemsFile.ReadString(item.toString(), "Tags", "").isEmpty())
                        _tags = INI.itemsFile.ReadString(item.toString(), "Tags", "").split(",");
                    NewItem(new Item(
                            item.toString(),
                            INI.itemsFile.ReadString(item.toString(), "Path", ""),
                            INI.itemsFile.ReadString(item.toString(), "Version", ""),
                            INI.itemsFile.ReadString(item.toString(), "Link", ""),
                            _tags,
                            new Image("file:pictures/" + item.toString().replace(":", "") + ".png")
                    ));
                }
            });
    }

    private void groupItems(boolean selected, String selectedItem) {
        if (selected)
            filters.add(selectedItem);
        else
            filters.remove(selectedItem);
        groupItems();
    }

    private void groupItems() {
        contentPane.getChildren().forEach(item -> {
            if (groupBy.isSelected()) {
                if (filters.size() > 0 && ((ArrayList<String>)item.getUserData()).containsAll(filters)) {
                    item.setVisible(false);
                    item.setManaged(false);
                }
                else {
                    item.setVisible(true);
                    item.setManaged(true);
                }
            }
            else {
                if (((ArrayList<String>)item.getUserData()).containsAll(filters)) {
                    item.setVisible(true);
                    item.setManaged(true);
                }
                else {
                    item.setVisible(false);
                    item.setManaged(false);
                }
            }
        });
    }

    private void initUpdates() {
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                _name.add(((Item) ((VBox) contentPane.getChildren().get(currentItem)).getChildren().get(0).getUserData()).name);
                _currentVersion.add(((Item) ((VBox) contentPane.getChildren().get(currentItem)).getChildren().get(0).getUserData()).version);
                if (INI.ScriptsFileExist() && INI.scriptsFile.ValueExist("Scripts", "Version")) {
                    Matcher _matcher = Pattern.compile(INI.scriptsFile.ReadString("Scripts", "Version", "")).matcher(webEngine.getLocation());
                    if (_matcher.find())
                        _newVersion.add(_matcher.group(0).replace("-", ".").substring(1, _matcher.group(0).length() -1));
                    else
                        _newVersion.add("");
                }
                if (!((ArrayList<String>) contentPane.getChildren().get(currentItem).getUserData()).contains("Update")) {
                    if (((ArrayList<String>) contentPane.getChildren().get(currentItem).getUserData()).size() > 0) {
                        String _tags = String.join(",", ((Item)((VBox)contentPane.getChildren().get(currentItem)).getChildren().get(0).getUserData()).tags);
                        ((Item)((VBox)contentPane.getChildren().get(currentItem)).getChildren().get(0).getUserData()).tags = (_tags += ",Update").split(",");
                        if (INI.ItemsFileExist())
                            INI.itemsFile.WriteString(((Item) ((VBox) contentPane.getChildren().get(currentItem)).getChildren().get(0).getUserData()).name, "Tags", INI.itemsFile.ReadString(((Item) ((VBox) contentPane.getChildren().get(currentItem)).getChildren().get(0).getUserData()).name, "Tags", "") + ",Update");
                    }
                    else {
                        ((Item)((VBox)contentPane.getChildren().get(currentItem)).getChildren().get(0).getUserData()).tags = new String[] {"Update"};
                        INI.itemsFile.WriteString(((Item) ((VBox) contentPane.getChildren().get(currentItem)).getChildren().get(0).getUserData()).name, "Tags", "Update");
                    }
                    ((ArrayList<String>) contentPane.getChildren().get(currentItem).getUserData()).add("Update");
                }
                /////////////////////////////////////////////////////////
                currentItem++;
                boolean _end = false;
                if (contentPane.getChildren().size() > currentItem) {
                    for(int i = currentItem; i < contentPane.getChildren().size(); i++) {
                        if (Collections.disjoint((ArrayList<String>) contentPane.getChildren().get(currentItem).getUserData(), _exclude) && Validations.isValidURL(((Item) ((VBox) contentPane.getChildren().get(currentItem)).getChildren().get(0).getUserData()).link)) {
                            webEngine.load(((Item) ((VBox) contentPane.getChildren().get(currentItem)).getChildren().get(0).getUserData()).link);
                            _end = false;
                            break;
                        }
                        else {
                            currentItem++;
                            _end = true;
                        }
                    }
                }
                if (_end) {
                    INI.itemsFile.UpdateFile();
                    if (_name.size() > 0) {
                        expander = new Expander(bundle.getString("_updatesFound") + _name.size() + bundle.getString("updatesFound"));
                        expander.addItem(creatUpdatesContent(String.join("\n", _name), String.join("\n", _currentVersion), String.join("\n", _newVersion)));
                        CustomMessageBox.Show(expander);
                    }
                    else
                        CustomMessageBox.Show(bundle.getString("noUpdates"));
                    oL.setVisible(false);
                }
            }
        });
    }

    private Node creatUpdatesContent(String name, String currentVersion, String newVersion) {
        Text _name = new Text(bundle.getString("name") + "\n" + name);
        Text _currentVersion = new Text(bundle.getString("version") + "\n" + currentVersion);
        Text _newVersion = new Text(bundle.getString("newVersion") + "\n" + newVersion);
        HBox _content = new HBox(_name, _currentVersion, _newVersion);
        _content.setSpacing(20);
        return _content;
    }

    @FXML void group(ActionEvent actionEvent) {
        if (((ToggleButton)actionEvent.getSource()).isSelected()) {
            ((ToggleButton)actionEvent.getSource()).setText(bundle.getString("excludeBy"));
        }
        else {
            ((ToggleButton)actionEvent.getSource()).setText(bundle.getString("groupBy"));
        }
        groupItems();
    }

    @FXML void search() {
        if (searchTextField.isManaged())
            searchTextField.requestFocus();
        else
            searchTextField.setText("");
    }

    @FXML void searchUpdates() {
        if (contentPane.getChildren().size() > 0) {
            oL.setVisible(true);
            currentItem = 0;
            _name = new ArrayList();
            _currentVersion = new ArrayList();
            _newVersion = new ArrayList();
            if (INI.FiltersFileExist() && !INI.filtersFile.SectionExist("Update")) {
                INI.filtersFile.WriteString("Update", "ExcludeUpdate", "false");
                INI.filtersFile.WriteString("Update", "HideOnStartUp", "false");
                INI.filtersFile.UpdateFile();
            }
            boolean _found = false;
            for (Node item : contentPane.getChildren()) {
                if (Collections.disjoint((ArrayList<String>) item.getUserData(), _exclude) && Validations.isValidURL(((Item) ((VBox) item).getChildren().get(0).getUserData()).link)) {
                    webEngine.load(((Item) ((VBox) item).getChildren().get(0).getUserData()).link);
                    _found = true;
                    break;
                } else {
                    currentItem++;
                    _found = false;
                }
            }
            if (!_found) {
                CustomMessageBox.Show(bundle.getString("noUpdates"));
                oL.setVisible(false);
            }
        }
    }

    @FXML void settings(ActionEvent actionEvent) {
        ScaleTransition _st = Animations.scaleOut(mainPane);
        _st.setOnFinished(e-> {
            try { ((BorderPane)((Node)actionEvent.getSource()).getScene().getRoot()).setCenter(FXMLLoader.load(getClass().getResource("/Stages/settingsStage.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang))));
            } catch (IOException ignored) { }
        });
    }

    @FXML void logout(ActionEvent actionEvent) {
        ScaleTransition _st = Animations.scaleOut(mainPane);
        _st.setOnFinished(e-> {
            try { ((BorderPane)((Node)actionEvent.getSource()).getScene().getRoot()).setCenter(FXMLLoader.load(getClass().getResource("/Stages/loginStage.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang))));
            } catch (IOException ignored) { }
        });
    }

    @FXML void addItem() {
        Item _item = NewItemWindow.Show();
        if (_item.ok) {
            NewItem(_item);
            INI.itemsFile.WriteString(_item.name, "Path", _item.path);
            INI.itemsFile.WriteString(_item.name, "Version", _item.version);
            INI.itemsFile.WriteString(_item.name, "Link", _item.link);
            INI.itemsFile.WriteString(_item.name, "Tags", String.join(",", _item.tags));
            INI.itemsFile.UpdateFile();
            if (!new File("pictures").exists())
                new File("pictures").mkdir();
            try { ImageIO.write(SwingFXUtils.fromFXImage(_item.img, null), "png", new File("pictures/" + _item.name.replace(":", "") + ".png")); }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    @FXML void groupClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
            if (searchTbtn.isSelected()) {
                searchTextField.setText("");
                searchTbtn.setSelected(false);
            }
    }

    private void NewItem(Item item) {
        Button _btn = new Button();
        _btn.setPrefSize(170, 120);
        _btn.setBackground(new Background(new BackgroundImage(item.img,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(0,0,
                true, true, true, false))));
        _btn.setOnMouseClicked(this::itemClick);
        _btn.setUserData(item);
        Label _lbl = new Label(item.name);
        VBox _vbox = new VBox(_btn, _lbl);
        _vbox.setPrefSize(190, 140);
        _vbox.setAlignment(Pos.TOP_CENTER);
        _vbox.setPadding(new Insets(5,10,5,10));
        _vbox.getStylesheets().add("/Styles/mainStageStyle.css");
        _vbox.getStyleClass().add("item");
        _vbox.setUserData(new ArrayList<String>(Arrays.asList(item.tags)));
        contentPane.getChildren().add(_vbox);
        if (groupBy.isSelected()) {
            if (filters.size() > 0 && ((ArrayList<String>) _vbox.getUserData()).containsAll(filters)) {
                _vbox.setVisible(false);
                _vbox.setManaged(false);
            }
        }
        else {
            if (!((ArrayList<String>) _vbox.getUserData()).containsAll(filters)) {
                _vbox.setVisible(false);
                _vbox.setManaged(false);
            }
        }
    }

    private void EditItem(Button sender, Item _item) {
        sender.getParent().setUserData(new ArrayList<String>(Arrays.asList(_item.tags)));
        ((Label)((VBox)sender.getParent()).getChildren().get(1)).setText(_item.name);
        sender.setBackground(new Background(new BackgroundImage(_item.img,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(0, 0,
                true, true, true, false))));
        if (INI.ItemsFileExist())
            if (INI.itemsFile.SectionExist(((Item)sender.getUserData()).name)) {
                INI.itemsFile.WriteString(((Item)sender.getUserData()).name, "Path", _item.path);
                INI.itemsFile.WriteString(((Item)sender.getUserData()).name, "Version", _item.version);
                INI.itemsFile.WriteString(((Item)sender.getUserData()).name, "Link", _item.link);
                INI.itemsFile.WriteString(((Item)sender.getUserData()).name, "Tags", String.join(",", _item.tags));
                INI.itemsFile.RenameSection(((Item)sender.getUserData()).name, _item.name);
                INI.itemsFile.UpdateFile();
            }
        if (new File("pictures/" + ((Item)sender.getUserData()).name + ".png").exists())
            new File("pictures/" + ((Item)sender.getUserData()).name + ".png").delete();
        if (!new File("pictures").exists())
            new File("pictures").mkdir();
        try { ImageIO.write(SwingFXUtils.fromFXImage(_item.img, null), "png", new File("pictures/" + _item.name.replace(":", "") + ".png")); }
        catch (IOException e) { e.printStackTrace(); }
        sender.setUserData(_item);
        if (groupBy.isSelected()) {
            if (filters.size() > 0 && ((ArrayList<String>) sender.getParent().getUserData()).containsAll(filters)) {
                sender.getParent().setVisible(false);
                sender.getParent().setManaged(false);
            }
        }
        else {
            if (!((ArrayList<String>) sender.getParent().getUserData()).containsAll(filters)) {
                sender.getParent().setVisible(false);
                sender.getParent().setManaged(false);
            }
        }
    }

    private void itemClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.MIDDLE)) {
            MessageBoxResult _result = CustomMessageBox.Show(bundle.getString("_deleteItem") + ((Item)((Button)mouseEvent.getSource()).getUserData()).name + bundle.getString("deleteItem"), "", MessageBoxButton.YesNo);
            if (_result == MessageBoxResult.Yes) {
                if (INI.ItemsFileExist())
                    if (INI.itemsFile.SectionExist(((Item)((Button)mouseEvent.getSource()).getUserData()).name)) {
                        INI.itemsFile.EraseSection(((Item) ((Button) mouseEvent.getSource()).getUserData()).name);
                        INI.itemsFile.UpdateFile();
                    }
                if (new File("pictures/" + ((Item)((Button)mouseEvent.getSource()).getUserData()).name.replace(":", "") + ".png").exists())
                    new File("pictures/" + ((Item)((Button)mouseEvent.getSource()).getUserData()).name.replace(":", "") + ".png").delete();
                contentPane.getChildren().remove(((Button) mouseEvent.getSource()).getParent());
            }
        }
        else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            Item _item = NewItemWindow.ShowEdit(((Item)((Button)mouseEvent.getSource()).getUserData()));
            if (_item.ok)
                EditItem(((Button)mouseEvent.getSource()), _item);
        }
        else if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            clickCounter++;
            clickTimer.playFromStart();
            clickedItem = ((Item)((Button)mouseEvent.getSource()).getUserData());
        }
    }

    private void itemClicked() {
        if (clickCounter == 1)
            try { Desktop.getDesktop().open(new File(clickedItem.path)); }
            catch (Exception ignored) { CustomMessageBox.simpleShow(bundle.getString("invalidPath") + "!", bundle.getString("error"), MessageBoxButton.OK, MessageBoxImage.Error ); }
        else if (clickCounter == 2) {
            if (Validations.isValidURL(clickedItem.link))
                try { Runtime.getRuntime().exec("cmd /c start chrome -incognito " + clickedItem.link).waitFor(); }
                catch (Exception ignored) { CustomMessageBox.simpleShow("Chrome Browser not found!", bundle.getString("error"), MessageBoxButton.OK, MessageBoxImage.Error); }
            else
                CustomMessageBox.simpleShow(bundle.getString("invalidLink") + "!", bundle.getString("error"), MessageBoxButton.OK, MessageBoxImage.Error);
        }
        clickCounter = 0;
    }

    private void searchItems(String val) {
        contentPane.getChildren().forEach(item -> {
            if (((ArrayList<String>)item.getUserData()).containsAll(filters) && ((Label)((VBox)item).getChildren().get(1)).getText().toLowerCase().contains(val.toLowerCase())) {
                item.setManaged(true);
                item.setVisible(true);
            }
            else {
                item.setManaged(false);
                item.setVisible(false);
            }
        });
    }
}