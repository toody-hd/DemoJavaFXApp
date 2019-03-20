package Controls;

import Settings.Settings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Expander extends VBox {

    @FXML private ToggleButton header;
    @FXML private ScrollPane container;

    private void initExpander() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Controls/Expander.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang)));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try { fxmlLoader.load(); }
        catch (IOException exception) { throw new RuntimeException(exception); }

        container.visibleProperty().bind(header.selectedProperty());
    }

    public Expander() {
        initExpander();
    }

    public Expander(String header) {
        initExpander();
        setText(header);
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public StringProperty textProperty() {
        return header.textProperty();
    }

    public void addItem(Node item) {
        container.setContent(item);
    }

    public void addItem(String item) {
        container.setContent(new Text(item));
    }

    public Node getItem(int index) {
        return container.getContent();
    }

    @FXML protected void expand() {
        //container.managedProperty().bind(header.selectedProperty());

    }
}
