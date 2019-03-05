import java.nio.ByteOrder;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Message;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.elements.AppSink;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/*
 * The main idea is to create a pipeline that has an appsink to display the images.
 * Connect the AppSink to the rest of the pipeline.
 * Connect the AppSinkListener to the AppSink.
 * The AppSink writes frames to the ImageContainer.
 * if you want to display the Videoframes simply add a changeListener to the container who will draw the current
 * Image to a Canvas or ImageView.
 */


public class Main extends Application{
    private ImageView imageView;

    public Main() {


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight() - 240));
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.show();
//        primaryStage.setTitle("probably dashboard?");
//        BorderPane grid = new BorderPane();
//        grid.setCenter(imageView);
//        imageView.fitWidthProperty().bind(grid.widthProperty());
//        imageView.fitHeightProperty().bind(grid.heightProperty());
//        imageView.setPreserveRatio(true);
//        primaryStage.setScene(new Scene(grid, 460, 460));
//        primaryStage.show();
    }

    public static void main(String[] args) {
        Gst.init("WBDashboard", args);
        launch(args);
    }
}

