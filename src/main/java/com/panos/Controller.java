package com.panos;

import edu.wpi.first.networktables.*;
import edu.wpi.first.networktables.ConnectionInfo;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSink;

import java.nio.ByteOrder;
import java.util.function.Consumer;

import static javafx.scene.paint.Color.RED;

public class Controller {

    @FXML
    private GridPane gridPane;

    @FXML
    private ImageView cameraView;

    @FXML
    private Text cameraStatus;

    private AppSink videosink;
    private Pipeline pipe;
    private Bin bin;
    Bus bus;
    private StringBuilder caps;
    private ImageContainer imageContainer;


    public void fadeBackgroundToColor(final int red, final int green, final int blue, final int prevRed, final int prevGreen, final int prevBlue) {
        Platform.runLater(() -> {
            // Create imaginary rectangle
            Rectangle rect = new Rectangle();
            rect.setFill(Color.rgb(prevRed, prevGreen, prevBlue));

            FillTransition tr = new FillTransition();
            tr.setShape(rect);
            tr.setDuration(Duration.millis(1000));
            tr.setFromValue(Color.rgb(prevRed, prevGreen, prevBlue));
            tr.setToValue(Color.rgb(red, green, blue));

            tr.setInterpolator(new Interpolator() {
                @Override
                protected double curve(double t) {
                    gridPane.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                    return t;
                }
            });

            tr.play();
        });
    }

    public Controller() {
        //NetworkTableInstance.getDefault().startClientTeam(3256);
        NetworkTableInstance.getDefault().startClient("localhost");

        NetworkTableInstance.getDefault().addConnectionListener(connectionInfo -> {
            NetworkTable table = connectionInfo.getInstance().getTable("SmartDashboard");
            System.out.println(table.getKeys());
            System.out.println(table.getEntry("alliance").getString("oof"));
            table.addEntryListener("alliance", (networkTable, s, networkTableEntry, networkTableValue, i) -> {
                if (networkTableValue.getValue().equals("Red")) {
                    fadeBackgroundToColor(153, 0, 0, 0, 0, 102);
                } else {
                    fadeBackgroundToColor(0, 0, 102, 153, 0, 0);
                }
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
            System.out.println(connectionInfo.connected);
            table.addEntryListener("regional", (networkTable, s, networkTableEntry, networkTableValue, i) -> {
                Platform.runLater(() -> cameraStatus.setText(networkTableValue.getString()));
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
        }, true);

        videosink = new AppSink("GstVideoComponent");
        videosink.set("emit-signals", true);
        AppSinkListener GstListener = new AppSinkListener();
        videosink.connect(GstListener);
        caps = new StringBuilder("video/x-raw, ");
        // JNA creates ByteBuffer using native byte order, set masks according to that.
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            caps.append("format=BGRx");
        } else {
            caps.append("format=xRGB");
        }
        videosink.setCaps(new Caps(caps.toString()));
        videosink.set("max-buffers", 5000);
        videosink.set("drop", true);
        bin = Bin.launch("udpsrc port=5808 caps=\"application/x-rtp\" ! rtph264depay ! avdec_h264 ! videoconvert", true);
        pipe = new Pipeline();
        pipe.addMany(bin, videosink);
        Pipeline.linkMany(bin, videosink);

        //fadeBackgroundToColor(0, 0, 102);

        imageContainer = GstListener.getImageContainer();
        imageContainer.addListener(new ChangeListener<Image>() {
            @Override
            public void changed(ObservableValue<? extends Image> observable, Image oldValue, final
            Image newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        cameraView.setImage(newValue);
                    }
                });
            }
        });

        bus = pipe.getBus();
        bus.connect(new Bus.MESSAGE() {
            @Override
            public void busMessage(Bus arg0, Message arg1) {
                cameraStatus.setText(arg1.getType().getName());
            }
        });
        pipe.play();
    }
}
