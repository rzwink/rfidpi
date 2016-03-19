package com;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

import java.util.Date;
import java.io.*;
import java.util.*;

public class PiApplication {

	private final String COMPORT_CARDREADER = "/dev/ttyUSB1";
	private final int BAUD_CARDREADER = 2400;

	private final String COMPORT_LCDDISPLAY = "/dev/ttyUSB0";
	private final int BAUD_LCDDISPLAY = 19200;

	// - Get an instance of Serial for COM interaction
	private final Serial serialCardReader = SerialFactory.createInstance();
	private final Serial serialLCDDisplay = SerialFactory.createInstance();
	private static String currentToken = "";

	private void openCardReader() {
		// - Create and add a SerialDataListener
		serialCardReader.open(COMPORT_CARDREADER, BAUD_CARDREADER);

		// - Create and add a SerialDataListener
		serialCardReader.addListener(new SerialDataListener() {
			public void dataReceived(SerialDataEvent event) {
				// - Get byte array from SerialDataEvent

				byte[] data = event.getData().getBytes();
				// if data is incomplete, try again
				if (data.length != 12) {
					return;
				}
				// System.out.printf(data.length + " CURRENT TIME: %s -> ", new
				// Date().toString());

				// - Iterate byte array print a readable representation of each
				// byte
				for (int i = 0; i < data.length; i++) {
					currentToken += (data[i]);
				}

				System.out.println(currentToken);

				// reset screen
				serialLCDDisplay.write((char) 0x0C);
				// trun on backlight
				serialLCDDisplay.write((char) 0x11);

				serialLCDDisplay.write(people.get(currentToken).getName());
				serialLCDDisplay.write(" Log " + people.get(currentToken).toggleStatus().getStatus());
				
				serialLCDDisplay.write((char) 0x0D);
				serialLCDDisplay.write((new Date().toString()).substring(0, 16));
				
				currentToken = "";
				try {
					Thread.sleep(2000);
					// reset screen
					serialLCDDisplay.write((char) 0x0C);
					// turn off backlight
					serialLCDDisplay.write((char) 0x12);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		System.out.println("Ready");

	}

	private void openLCDDisplay() {
		serialLCDDisplay.open(COMPORT_LCDDISPLAY, BAUD_LCDDISPLAY);
	}

	public PiApplication(String[] args) {
		people.put("104867484851545049525513", new Person("Emma"));
		people.put("105169484853575149506813", new Person("John"));
		people.put("105351484850485567695313", new Person("Lili"));
		people.put("105351484850486956486613", new Person("Logan"));
		people.put("104849486969707067676813", new Person("Wade"));
		
		TimeZone.setDefault(TimeZone.getTimeZone("EST"));
		
		
		openLCDDisplay();

		openCardReader();

		// - When you are done, ensure you close the port
		// To demonstrate, I am waiting 20 seconds and then closing the port.
		try {
			// - Sleep for 20 seconds, (in ms)
			while(true){continue;}

		} catch (Exception ex) {
			// - I am intentionally ignoring any exception.
			// - Close port
			serialCardReader.close();
			System.out.println("COM port closed.");

		}

		// - And terminate
		System.exit(0);
	}

	public static void main(String[] args) {

		new PiApplication(args);

	}

	public static HashMap<String, Person> people = new HashMap<String, Person>();

	public class Person {
		String name;
		String status;

		String in = "in";
		String out = "out";

		public Person(String oname) {
			name = oname;
			status = "";
		}

		public String getName() {
			return name;
		}

		public String getStatus() {
			return status;
		}

		public Person toggleStatus() {
			if (status.equals(in)) {
				status = out;
			} else {
				status = in;
			}
			return this;

		}

	}
}
