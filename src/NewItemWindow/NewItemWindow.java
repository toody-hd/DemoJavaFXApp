package NewItemWindow;

import Animations.Animations;
import Settings.Settings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class NewItemWindow extends Stage {

    static NewItemWindow _stage;
    static NewItemWindowController _controller;
    static Item _item;

    private static ResourceBundle bundle = ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang));

    public NewItemWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewItemWindow/NewItemWindow.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang)));
        Parent root = loader.load();
        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);
        setScene(new Scene(root));
        getScene().setFill(Color.TRANSPARENT);
        getScene().getStylesheets().add(getClass().getResource("/NewItemWindow/NewItemWindowStyle.css").toExternalForm());
        _controller = loader.getController();
        ((BorderPane)main.main.getStage().getScene().getRoot()).getCenter().setEffect(new BoxBlur());
        Animations.scaleIn(root);
    }

    public static Item Show() {
        try { _stage = new NewItemWindow(); }
        catch (Exception e) { e.printStackTrace(); }
        _controller.initFilters(null);
        _stage.showAndWait();
        return _item;
    }

    public static Item ShowEdit(Item item) {
        try { _stage = new NewItemWindow(); }
        catch (Exception ignored) { }
        _controller.name.setText(item.name);
        _controller.path.setText(item.path);
        _controller.version.setText(item.version);
        _controller.link.setText(item.link);
        //_controller.tags.setItems(FXCollections.observableArrayList(item.tags));
        _controller.img.setBackground(new Background(new BackgroundImage(item.img,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(0, 0,
                true, true, true, false))));
        _controller.addSave.setText(bundle.getString("save"));
        _controller.initFilters(item.tags);
        _stage.showAndWait();
        return _item;
    }

    public static void Add_Save() {
        ArrayList<String> _list = new ArrayList<>();
        _controller.tags.getItems().forEach(item -> {
            if (((CheckBox)((CustomMenuItem)item).getContent()).isSelected())
                _list.add(((CheckBox)((CustomMenuItem)item).getContent()).getText());
        });
        _item = new Item(_controller.name.getText(), _controller.path.getText(),
                _controller.version.getText(), _controller.link.getText(), _list.toArray(new String[0]),
                _controller.img.getBackground().getImages().get(0).getImage());
        ((BorderPane)main.main.getStage().getScene().getRoot()).getCenter().setEffect(null);
        _stage.close();
        _stage = null;
    }

    public static void Close() {
        ((BorderPane)main.main.getStage().getScene().getRoot()).getCenter().setEffect(null);
        _item = new Item();
        _stage.close();
        _stage = null;
    }

    public static class Item {
        public String name;
        public String path;
        public String version;
        public String link;
        public String[] tags;
        public Image img;
        public Boolean ok;

        private Item(){
            name = path = version = link = "";
            tags = null;
            img = null;
            ok = false;
        }

        public Item(String name, String path, String version, String link, String[] tags, Image img){
            this.name = name;
            this.path = path;
            this.version = version;
            this.link = link;
            this.tags = tags;
            this.img = img;
            ok = true;
        }
    }
}
