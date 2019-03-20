package CustomMessageBox;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class CustomMessageBoxController {

    private double xOffset, yOffset = 0;
    @FXML GridPane topBar;
    @FXML public ImageView ico;
    @FXML public Button okButton;
    @FXML public Button yesButton;
    @FXML public Button noButton;
    @FXML public Button cancelButton;
    @FXML public Label img;
    @FXML public HBox messageBox;
    @FXML public Label msg;
    @FXML public Label ttl;
    @FXML public HBox objBox;

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

    @FXML void Close(MouseEvent mouseEvent){
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY))
            CustomMessageBox.Close();
    }

    @FXML void buttonClick(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == okButton)
            CustomMessageBox._result = CustomMessageBox.MessageBoxResult.OK;
        else if (mouseEvent.getSource() == yesButton)
            CustomMessageBox._result = CustomMessageBox.MessageBoxResult.Yes;
        else if (mouseEvent.getSource() == noButton)
            CustomMessageBox._result = CustomMessageBox.MessageBoxResult.No;
        else if (mouseEvent.getSource() == cancelButton)
            CustomMessageBox._result = CustomMessageBox.MessageBoxResult.Cancel;
        else
            CustomMessageBox._result = CustomMessageBox.MessageBoxResult.None;
        CustomMessageBox.Close();
    }

}
