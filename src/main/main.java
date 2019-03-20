package main;

import Helpers.resizeHelper;
import Settings.Settings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Locale;
import java.util.ResourceBundle;

public class main extends Application {

    private static Stage pStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"), ResourceBundle.getBundle("Languages", Locale.forLanguageTag(Settings.lang)));
        pStage = primaryStage;
        primaryStage.setTitle("Project");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 1200, 750));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
        primaryStage.getScene().setFill(Color.TRANSPARENT);
        primaryStage.getScene().getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        primaryStage.show();

        resizeHelper.addResizeListener(primaryStage);
        Font.loadFont(getClass().getResourceAsStream("/Resources/FontAwesome-WebFont.ttf"), 12);
    }

    public static Stage getStage() {
        return pStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
