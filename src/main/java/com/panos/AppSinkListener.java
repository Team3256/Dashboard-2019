package com.panos;

import java.io.*;
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

    private String[] types = new String[] {
            "Unspecified",
            "Coded slice of a non-IDR picture",
            "Coded slice data partition A",
            "Coded slice data partition B",
            "Coded slice data partition C",
            "Coded slice of an IDR picture",
            "Supplemental enhancement information (SEI)",
            "Sequence parameter set",
            "Picture parameter set",
            "Access unit delimiter",
            "End of sequence",
            "End of stream",
            "Filler data",
            "Sequence parameter set extension",
            "Prefix NAL unit",
            "Subset sequence parameter set",
            "Depth parameter set",
            "Reserved",
            "Coded slice of an auxiliary coded picture without partitioning",
            "Coded slice extension",
            "Coded slice extension for a depth view component or a 3D-AVC texture view component"
    };

    DataOutputStream os = null;

    public AppSinkListener() {
        File file = new File("/Users/john/Desktop/dump.h264");
        try {
            if (file.createNewFile()) {
                System.out.println("File doesn't exist, so one was created");
            } else {
                System.out.println("File exists, will overwrite existing data");
            }
            os = new DataOutputStream(new FileOutputStream(file, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int count = 0;

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
            System.out.println("Length: 0x" + length + " (" + byteArray.length + ")");

            for (int i = 4; i < byteArray.length; i++) {
                if (
                        byteArray[i-4] == 0 &&
                        byteArray[i-3] == 0 &&
                        byteArray[i-2] == 0 &&
                        byteArray[i-1] == 1
                ) {
                    System.out.println("Header Type: " + types[byteArray[i]&0x1F] + " (" + (byteArray[i]&0x1F) + ")");
                    System.out.println();
                }
            }

//            if (count > 50) {
//                System.out.println(count);
//                System.exit(0);
//            }
//
//            try {
//                os.write(byteArray);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            //count++;

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