package com.panos;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import com.mogaleaf.usbmuxd.api.IUsbMuxd;
import com.mogaleaf.usbmuxd.api.UsbMuxdFactory;
import com.mogaleaf.usbmuxd.api.exception.UsbMuxdException;
import com.mogaleaf.usbmuxd.api.model.Device;
import com.mogaleaf.usbmuxd.api.model.UsbMuxdConnection;
import org.freedesktop.gstreamer.Buffer;
import org.freedesktop.gstreamer.FlowReturn;
import org.freedesktop.gstreamer.Sample;
import org.freedesktop.gstreamer.Structure;
import org.freedesktop.gstreamer.elements.AppSink;


import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;


public class AppSinkListener implements AppSink.NEW_SAMPLE {

    private Image actualFrame;
    private int lastWidth = 0;
    private int lastHeigth = 0;
    private byte[] byteArray;
    private ImageContainer imageContainer = new ImageContainer();

    public ImageContainer getImageContainer(){
        return imageContainer;
    }

    static IUsbMuxd usbMuxdDriver = UsbMuxdFactory.getInstance();

    @Override
    public FlowReturn newSample(AppSink appSink) {
        // Try to get a sample
        Sample sample = appSink.pullSample();
        Buffer buffer = sample.getBuffer();
        ByteBuffer byteBuffer = buffer.map(false);
        if (byteBuffer != null) {
            byteArray = new byte[byteBuffer.remaining()];
//            Structure capsStruct = sample.getCaps().getStructure(0);
//            int width = capsStruct.getInteger("width");
//            int height = capsStruct.getInteger("height");
//            if (width != lastWidth || height != lastHeigth) {
//                lastWidth = width;
//                lastHeigth = height;
//                byteArray = new byte[width * height * 4];
//            }
//            // Writes the buffer to the byteArray
            byteBuffer.get(byteArray);
            String length = String.format("%08x", byteArray.length);
            System.out.println(length);
            System.out.println(length.substring(0, 2));
            System.out.println(length.substring(2, 4));
            System.out.println(length.substring(4, 6));
            System.out.println(length.substring(6, 8));
            byteArray[0] = (byte) Integer.parseInt(length.substring(0, 2), 16);
            byteArray[1] = (byte) Integer.parseInt(length.substring(2, 4), 16);
            byteArray[2] = (byte) Integer.parseInt(length.substring(4, 6), 16);
            byteArray[3] = (byte) Integer.parseInt(length.substring(6, 8), 16);
            for (int i = 0; i < 4; i++) {
                System.out.print((byteArray[i]) + ", ");
            }
            System.out.println("");
            Devices.devices.forEach(device -> {
                System.out.println("Sending to device: " + device.deviceId);
                try {
                    UsbMuxdConnection connection = usbMuxdDriver.connectToDevice(5000, device);
                    try {
                        connection.outputStream.write(byteArray);
                        connection.outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (UsbMuxdException e) {
                    e.printStackTrace();
                }
            });
//            actualFrame = convertBytesToImage(byteArray, width, height);
//            // Writes the new Image to the com.panos.ImageContainer. If an other part of the program wants to do something like displaying or storing
//            //with the frames it can set up a changeListener to get a chance to do something with the newest frame.
//            imageContainer.setImage(actualFrame);
            buffer.unmap();
        }
        sample.dispose();
        return FlowReturn.OK;
    }

    private Image convertBytesToImage(byte[] pixels, int width, int height){
        //Writes a bytearray to a WritableImage.
        WritableImage img = new WritableImage(width, height);
        PixelWriter pw = img.getPixelWriter();
        pw.setPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(), pixels, 0, width *4);
        return img;
    }
}