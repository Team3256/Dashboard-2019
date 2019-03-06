package com.panos;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import org.freedesktop.gstreamer.Gst;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/*
 * The main idea is to create a pipeline that has an appsink to display the images.
 * Connect the AppSink to the rest of the pipeline.
 * Connect the com.panos.AppSinkListener to the AppSink.
 * The AppSink writes frames to the com.panos.ImageContainer.
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
        primaryStage.setScene(new Scene(root, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight() - 240));
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Gst.init("WBDashboard", args);
        launch(args);
    }
}

