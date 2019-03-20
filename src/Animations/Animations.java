package Animations;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;


public class Animations {

    public static ScaleTransition scaleIn(Node node) {
        node.setScaleX(0);
        node.setScaleY(0);
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setDuration(Duration.millis(500));
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.setNode(node);
        scaleTransition.play();
        return scaleTransition;
    }

    public static ScaleTransition scaleOut(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setDuration(Duration.millis(500));
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(0);
        scaleTransition.setToY(0);
        scaleTransition.setNode(node);
        scaleTransition.play();
        return scaleTransition;
    }
}
