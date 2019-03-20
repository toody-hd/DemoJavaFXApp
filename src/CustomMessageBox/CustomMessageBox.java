package CustomMessageBox;

import Animations.Animations;
import Settings.Settings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Locale;
import java.util.ResourceBundle;

public class CustomMessageBox extends Stage {

    static CustomMessageBoxController _controller;

    public CustomMessageBox() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CustomMessageBox/CustomMessageBox.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang)));
        Parent root = loader.load();
        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);
        setScene(new Scene(root));
        getScene().setFill(Color.TRANSPARENT);
        getScene().getStylesheets().add(getClass().getResource("/CustomMessageBox/CustomMessageBoxStyle.css").toExternalForm());
        _controller = loader.getController();
        ((BorderPane)main.main.getStage().getScene().getRoot()).getCenter().setEffect(new BoxBlur());
        Animations.scaleIn(root);
    }

    static CustomMessageBox _messageBox;
    static MessageBoxResult _result = MessageBoxResult.No;

    public static MessageBoxResult Show
            (String message, String title, MessageBoxType type) {
        switch (type)
        {
            case ConfirmationWithYesNo:
                return Show(message, title, MessageBoxButton.YesNo,
                        MessageBoxImage.Question);
            case ConfirmationWithYesNoCancel:
                return Show(message, title, MessageBoxButton.YesNoCancel,
                        MessageBoxImage.Question);
            case Information:
                return Show(message, title, MessageBoxButton.OK,
                        MessageBoxImage.Information);
            case Error:
                return Show(message, title, MessageBoxButton.OK,
                        MessageBoxImage.Error);
            case Warning:
                return Show(message, title, MessageBoxButton.OK,
                        MessageBoxImage.Warning);
            default:
                return MessageBoxResult.No;
        }
    }

    public static MessageBoxResult Show(String message, MessageBoxType type)
    {
        return Show(message, "", type);
    }

    public static MessageBoxResult Show(String message) {
        return Show(message, "",
                MessageBoxButton.OK, MessageBoxImage.None);
    }

    public static MessageBoxResult Show
            (String message, String title) {
        return Show(message, title,
                MessageBoxButton.OK, MessageBoxImage.None);
    }

    public static MessageBoxResult Show
            (String message, String title, MessageBoxButton button) {
        return Show(message, title, button,
                MessageBoxImage.None);
    }

    public static MessageBoxResult Show
            (String message, String title,
             MessageBoxButton button, MessageBoxImage image) {
        try {
            _messageBox = new CustomMessageBox();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _controller.msg.setText(message);
        _controller.ttl.setText(title);
        SetVisibilityOfButtons(button);
        SetImageOfMessageBox(image);
        _messageBox.showAndWait();
        return _result;
    }

    public static void simpleShow
            (String message, String title,
             MessageBoxButton button, MessageBoxImage image) {
        try {
            _messageBox = new CustomMessageBox();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _controller.msg.setText(message);
        _controller.ttl.setText(title);
        SetVisibilityOfButtons(button);
        SetImageOfMessageBox(image);
        _messageBox.show();
    }

    public static MessageBoxResult Show
            (Node obj, MessageBoxButton button, MessageBoxImage image) {
        try {
            _messageBox = new CustomMessageBox();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _controller.messageBox.setManaged(false);
        _controller.messageBox.setVisible(false);
        _controller.objBox.getChildren().add(obj);
        SetVisibilityOfButtons(button);
        SetImageOfMessageBox(image);
        _messageBox.showAndWait();
        return _result;
    }

    public static MessageBoxResult Show
            (Node obj) {
        try {
            _messageBox = new CustomMessageBox();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _controller.messageBox.setManaged(false);
        _controller.messageBox.setVisible(false);
        _controller.objBox.getChildren().add(obj);
        SetVisibilityOfButtons(MessageBoxButton.OK);
        _messageBox.showAndWait();
        return _result;
    }

    public static Control Show
            (String message, Control obj) {
        try { _messageBox = new CustomMessageBox(); }
        catch (Exception e) { e.printStackTrace(); }
        _controller.msg.setText(message);
        _controller.objBox.getChildren().add(obj);
        SetVisibilityOfButtons(MessageBoxButton.OK);
        _messageBox.showAndWait();
        if (_result == MessageBoxResult.OK)
            return obj;
        else
            return null;
    }

    private static void SetVisibilityOfButtons(MessageBoxButton button) {
        switch (button)
        {
            case OK:
                _controller.cancelButton.setManaged(false);
                _controller.noButton.setManaged(false);
                _controller.yesButton.setManaged(false);
                _controller.okButton.requestFocus();
                break;
            case OKCancel:
                _controller.noButton.setManaged(false);
                _controller.yesButton.setManaged(false);
                _controller.cancelButton.requestFocus();
                break;
            case YesNo:
                _controller.okButton.setManaged(false);
                _controller.cancelButton.setManaged(false);
                _controller.noButton.requestFocus();
                break;
            case YesNoCancel:
                _controller.okButton.setManaged(false);
                _controller.cancelButton.requestFocus();
                break;
            default:
                break;
        }
    }

    private static void SetImageOfMessageBox(MessageBoxImage image) {
        switch (image)
        {
            case Warning:
                //_messageBox.SetImage("Warning.png");
                _controller.img.setStyle("-fx-font-family: FontAwesome;" +
                        "-fx-font-size: 30;" +
                        "-fx-text-fill: Orange;");
                _controller.img.setText("\uF071");
                break;
            case Question:
                //_messageBox.SetImage("Question.png");
                _controller.img.setStyle("-fx-font-family: FontAwesome;" +
                        "-fx-font-size: 30;" +
                        "-fx-text-fill: Blue;");
                _controller.img.setText("\uF059");
                break;
            case Information:
                //_messageBox.SetImage("Information.png");
                _controller.img.setStyle("-fx-font-family: FontAwesome;" +
                        "-fx-font-size: 30;" +
                        "-fx-text-fill: Blue;");
                _controller.img.setText("\uF05A");
                break;
            case Error:
                //_messageBox.SetImage("Error.png");
                _controller.img.setStyle("-fx-font-family: FontAwesome;" +
                        "-fx-font-size: 30;" +
                        "-fx-text-fill: Red;");
                _controller.img.setText("\uF2D3");
                break;
            default:
                _controller.img.setManaged(false);
                break;
        }
    }

    public static void Close() {
        ((BorderPane)main.main.getStage().getScene().getRoot()).getCenter().setEffect(null);
        _messageBox.close();
        _messageBox = null;
    }

    public enum MessageBoxType {
        ConfirmationWithYesNo,
        ConfirmationWithYesNoCancel,
        Information,
        Error,
        Warning
    }

    public enum MessageBoxImage {
        Warning,
        Question,
        Information,
        Error,
        None
    }

    public enum MessageBoxResult {
        None,
        OK,
        Cancel,
        Yes,
        No
    }

    public enum MessageBoxButton {
        OK,
        OKCancel,
        YesNoCancel,
        YesNo
    }
}
