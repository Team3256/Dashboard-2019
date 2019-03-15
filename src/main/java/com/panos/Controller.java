package com.panos;

import com.mogaleaf.usbmuxd.api.IUsbMuxd;
import com.mogaleaf.usbmuxd.api.UsbMuxdFactory;
import com.mogaleaf.usbmuxd.api.exception.UsbMuxdException;
import com.mogaleaf.usbmuxd.api.model.UsbMuxdConnection;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSink;

import java.io.IOException;
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

    @FXML
    private HBox cameraViewContainer;

    private AppSink videosink;
    private Pipeline pipe;
    private Bin bin;
    Bus bus;
    private StringBuilder caps;
    private ImageContainer imageContainer;

    private double regional;
    private double match;

    static IUsbMuxd usbMuxdDriver = UsbMuxdFactory.getInstance();

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

    public void updateTitle() {
        Platform.runLater(() -> {
            cameraStatus.setText(regional + " - " + (int) match);
        });
    }

    public void sendToDevices(String input) {
        Devices.devices.forEach(device -> {
            System.out.println("Sending to device: " + device.deviceId);
            try {
                UsbMuxdConnection connection = usbMuxdDriver.connectToDevice(5000, device);
                try {
                    connection.outputStream.write(input.getBytes());
                    connection.outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UsbMuxdException e) {
                e.printStackTrace();
            }
        });
    }

    public Controller() {
        //NetworkTableInstance.getDefault().startClientTeam(3256);

        NetworkTableInstance.getDefault().startClient("127.0.0.1");

        NetworkTableInstance.getDefault().addConnectionListener(connectionInfo -> {
            NetworkTable fms = connectionInfo.getInstance().getTable("FMSInfo");
            NetworkTable table = connectionInfo.getInstance().getTable("SmartDashboard");

            System.out.println(fms.getKeys());

            fms.addEntryListener("IsRedAlliance", (networkTable, s, networkTableEntry, networkTableValue, i) -> {
                Platform.runLater(() -> {
                    System.out.println(networkTableValue.getBoolean());
                });
                if (networkTableValue.getBoolean()) {
                    fadeBackgroundToColor(153, 0, 0, 0, 0, 102);
                } else {
                    fadeBackgroundToColor(0, 0, 102, 153, 0, 0);
                }
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kImmediate);

            fms.addEntryListener("StationNumber", (networkTable, s, networkTableEntry, networkTableValue, i) -> {
                this.regional = networkTableValue.getDouble();
                updateTitle();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kImmediate);

            table.addEntryListener("MatchNumber", (networkTable, s, networkTableEntry, networkTableValue, i) -> {
                this.match = networkTableValue.getDouble();
                updateTitle();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kImmediate);

            table.addEntryListener("alliance", (networkTable, s, networkTableEntry, networkTableValue, i) -> {
                Platform.runLater(() -> {
                    System.out.println(networkTableValue.getString());
                });
                sendToDevices("alliance:" + networkTableValue.getString());
                if (networkTableValue.getString().equals("Red")) {
                    fadeBackgroundToColor(153, 0, 0, 0, 0, 102);
                } else {
                    fadeBackgroundToColor(0, 0, 102, 153, 0, 0);
                }
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kImmediate);

            table.addEntryListener("displayAutoPath", (networkTable, s, networkTableEntry, networkTableValue, i) -> {
                Platform.runLater(() -> {
                    System.out.println(networkTableValue.getBoolean());
                });
                sendToDevices("displayAutoPath:" + networkTableValue.getBoolean());
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kImmediate);
        }, true);
        fadeBackgroundToColor(153, 0, 0, 0, 0, 102);
        Platform.runLater(() -> {
            cameraStatus.setText("Monterey Regional - Match 10");
        });

        videosink = new AppSink("GstVideoComponent");
        videosink.set("emit-signals", true);
        AppSinkListener GstListener = new AppSinkListener();
        videosink.connect(GstListener);
        caps = new StringBuilder("video/x-raw, ");
        // JNA creates ByteBuffer using native byte order, set masks according to that.
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            System.out.println("LITTLE");
            caps.append("format=BGRx");
        } else {
            System.out.println("BIG");
            caps.append("format=xRGB");
        }
        videosink.setCaps(new Caps(caps.toString()));
        videosink.set("max-buffers", 5000);
        videosink.set("drop", true);
        bin = Gst.parseBinFromDescription("udpsrc port=5808 caps=\"application/x-rtp\" ! rtph264depay ! avdec_h264 ! videoconvert", true);
        pipe = new Pipeline();
        pipe.addMany(bin, videosink);
        Pipeline.linkMany(bin, videosink);

        imageContainer = GstListener.getImageContainer();
        imageContainer.addListener((observable, oldValue, newValue) -> Platform.runLater(() -> cameraView.setImage(newValue)));

        bus = pipe.getBus();
        bus.connect((Bus.MESSAGE) (arg0, arg1) -> {});
        pipe.play();

        Platform.runLater(() -> cameraView.fitHeightProperty().bind(cameraViewContainer.heightProperty()));
    }
}
