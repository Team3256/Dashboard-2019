package com.panos;

import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.mogaleaf.usbmuxd.api.IUsbMuxd;
import com.mogaleaf.usbmuxd.api.UsbMuxdFactory;
import com.mogaleaf.usbmuxd.api.exception.UsbMuxdException;
import com.mogaleaf.usbmuxd.api.model.UsbMuxdConnection;
import com.mogaleaf.usbmuxd.protocol.PlistMessageService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import org.freedesktop.gstreamer.Gst;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * The main idea is to create a pipeline that has an appsink to display the images.
 * Connect the AppSink to the rest of the pipeline.
 * Connect the com.panos.AppSinkListener to the AppSink.
 * The AppSink writes frames to the com.panos.ImageContainer.
 * if you want to display the Videoframes simply add a changeListener to the container who will draw the current
 * Image to a Canvas or ImageView.
 */


public class Main extends Application{

    static IUsbMuxd usbMuxdDriver = UsbMuxdFactory.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setScene(new Scene(root, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight() - 240));
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.show();
        usbMuxdDriver.registerDeviceConnectionListener(m -> {
            switch (m.type) {
                case Add:
                    System.out.println("Device connected with ID: " + m.device.deviceId);
                    System.out.println("Product ID: " + m.device.productId);
                    Devices.devices.add(m.device);
                    break;
                case Remove:
                    Devices.devices.remove(m.device);
                    System.out.println("Removed device with ID: " + m.device.deviceId);
                    break;
            }
        });
        new Thread(() -> {
            try {
                usbMuxdDriver.startListening();
            } catch (UsbMuxdException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        Gst.init("WBDashboard", args);
        launch(args);
    }
}

