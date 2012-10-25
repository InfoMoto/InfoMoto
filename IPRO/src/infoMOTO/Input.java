package infoMOTO;

/* This class gathers data from the board and sends it to be analyzed by InfoMoto */

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class Input implements SerialPortEventListener
{
	public InfoMoto main;

	SerialPort serialPort;
	private InputStream input;
	private OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 4800;

	/******************************************************************
	 * IMPORTANT: Change port strings based on the COM ports that your Arduino board is connected to.
	 *****************************************************************/

	// Ports to use based on OS:
	private static final String PORT_NAMES[] = {
		// Mac OSX
		"/dev/tty.usbserial-A9007UX1",
		// Linux
		"/dev/ttyUSB0",
		// Windows
		"COM13",
	};

	public Input(InfoMoto im) {
		main = im;
	}


	/************************************************************************************
	 * INITIALIZE
	 * 
	 * This method is called when the user presses the "Start" button. It connects to
	 * the Arduino board via the serial port specified above. This is why we need the
	 * SerialRXTX library. Once we begin streaming data via Bluetooth rather than via
	 * serial port, this method will have changed and the SerialRXTX library will no
	 * longer be necessary.
	 ************************************************************************************/
	public boolean initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		if (portId == null) {
			main.println("Could not find COM port.");
			return false;
		}
		else{
			main.println("Started successfully!");
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}

		return true;
	}

	/************************************************************************************
	 * CLOSE
	 * 
	 * This method is called when the user presses the "Stop" button. It closes the
	 * serial port on the software level to prevent port locking on some operating
	 * systems. Again, once we begin streaming data via Bluetooth, this method will
	 * have changed accordingly.
	 ************************************************************************************/
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}

		main.println("Tore down successfully!");
	}


	/************************************************************************************
	 * SERIAL EVENT
	 * 
	 * This method is called when the event listener detects that data from the Arduino
	 * board is available in the stream. This is where concatenate the data to a string
	 * and send it to the InfoMoto class.
	 ************************************************************************************/
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				// This is done to prevent the program from grabbing incomplete
				// sets of data from the stream.

				Thread.sleep(100);

				int available = input.available();
				byte chunk[] = new byte[available];
				input.read(chunk, 0, available);

				String raw = new String(chunk);
				
				main.setData(raw);
			} 
			catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}
}
