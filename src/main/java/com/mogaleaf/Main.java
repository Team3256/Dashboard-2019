package com.mogaleaf;

import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.mogaleaf.usbmuxd.api.IUsbMuxd;
import com.mogaleaf.usbmuxd.api.UsbMuxdFactory;
import com.mogaleaf.usbmuxd.api.exception.UsbMuxdException;
import com.mogaleaf.usbmuxd.api.model.Device;
import com.mogaleaf.usbmuxd.api.model.UsbMuxdConnection;
import com.mogaleaf.usbmuxd.protocol.PlistMessageService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;


public class Main {
	static IUsbMuxd usbMuxdDriver = UsbMuxdFactory.getInstance();

	public static void main(String args[]) throws IOException, InterruptedException, UsbMuxdException {
		usbMuxdDriver.registerDeviceConnectionListener(m -> {
			switch (m.type) {
				case Add:
					try {
						UsbMuxdConnection usbMuxdConnection = usbMuxdDriver.connectToDevice(5000, m.device);
						new Thread(() -> {
							byte[] res = new byte[4];
							try {
								usbMuxdConnection.outputStream.write("Test".getBytes());
								usbMuxdConnection.outputStream.flush();
								usbMuxdConnection.inputStream.read(res);
								ByteBuffer readB = ByteBuffer.allocate(res.length);
								readB.order(ByteOrder.BIG_ENDIAN);
								readB.put(res);
								int aInt = readB.getInt(0);
								byte[] body = new byte[aInt];
								usbMuxdConnection.inputStream.read(body);
								NSObject parse = PropertyListParser.parse(body);
								System.out.println(parse.toXMLPropertyList());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}).start();

						byte[] bytes = PlistMessageService.tryLockDown();

						ByteBuffer buffer = ByteBuffer.allocate(
								4);
						buffer.order(ByteOrder.BIG_ENDIAN);
						buffer.putInt(bytes.length);
						//usbMuxdConnection.outputStream.write(buffer.array());

						buffer = ByteBuffer.allocate(
								bytes.length);
						buffer.order(ByteOrder.BIG_ENDIAN);
						buffer.put(bytes);
						//usbMuxdConnection.outputStream.write(bytes);
					} catch (UsbMuxdException e) {
						e.printStackTrace();
					}
					System.out.println("Device connected with ID: " + m.device.deviceId);
					System.out.println("Product ID: " + m.device.productId);
					break;
				case Remove:
					System.out.println("Removed device with ID: " + m.device.deviceId);
					break;
			}
		});
		usbMuxdDriver.startListening();
	}

}
