package com.rasppi;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

import java.util.Date;

public class PiApplication {

	private final String COMPORT_CARDREADER = "/dev/ttyUSB0";
	private final String BAUD_CARDREADER = "2400";
	
	private final String COMPORT_LCDDISPLAY = "/dev/ttyUSB1";
	private final String BAUD_LCDDISPLAY = "19200";
	
	// - Get an instance of Serial for COM interaction
	private final Serial serialCardReader = SerialFactory.createInstance();
	private final Serial serialLCDDisplay = SerialFactory.createInstance();

	
	private void openCardReader(){
		// - Create and add a SerialDataListener
		serialCardReader.open( COMPORT_CARDREADER, BAUD_CARDREADER );
	}
	
	private void openLCDDisplay(){
		serialLCDDisplay.open( COMPORT_LCDDISPLAY, BAUD_LCDDISPLAY );
	}
	
	
	public PiApplication(String[] args)
	{
		openCardReader();
		openLCDDisplay();
		
		// - Create and add a SerialDataListener
		serialCardReader.addListener(new SerialDataListener()
		{
			@Override
			public void dataReceived(SerialDataEvent event)
			{
				// - Get byte array from SerialDataEvent


				byte[] data = event.getData().getBytes();
				System.out.printf("CURRENT TIME: %s", new Date().toString());

				// - Iterate byte array print a readable representation of each byte
				for ( int i=0; i < data.length; i++ )
				{
					System.out.printf( "0x%02x ", data[i] );
				}

				// - Line break to represent end of data for this event
				System.out.println();
			}
		});




		// - When you are done, ensure you close the port
		// To demonstrate, I am waiting 20 seconds and then closing the port.
		try
		{
			// - Sleep for 20 seconds, (in ms)
			Thread.sleep(20000);

			// - Close port
			serial.close();
			System.out.println("COM port closed.");
		}
		catch ( Exception ex )
		{
			// - I am intentionally ignoring any exception.
		}

		// - And terminate
		System.exit(0);
	}

	public static void main(String[] args) {
		new PiApplication(args);
	}
}
