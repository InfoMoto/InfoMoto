package infoMOTO;

/* This class is basically the controller of the program.
 * If any classes need to communicate with one-another, they
 * pass messages to each other via this class. This class 
 * also computes real-time stats, marks outliers, and sends
 * data points to be plotted to the graph. */

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.jfree.data.time.Millisecond;

public class InfoMoto
{
	public static Input input;

	public static JFrame frame;
	public static AccelPanel accelPanel;
	public static AnglePanel anglePanel;
	public static DebugPanel debugPanel;

	private static long numData = 0;
	private static long runningTotalX = 0;
	private static long runningTotalY = 0;
	private static long runningTotalZ = 0;
	private static double runningTotalRoll = 0;
	private static double runningTotalPitch = 0;
	private static long runningAverageX = 0;
	private static long runningAverageY = 0;
	private static long runningAverageZ = 0;
	private static double runningAverageRoll = 0;
	private static double runningAveragePitch = 0;

	private static int accelX = 0;
	private static int accelY = 0;
	private static int accelZ = 0;

	private static double roll = 0.0;
	private static double pitch = 0.0;
	private static double vector = 0.0;

	private static String[] data;
	private static String finalOutliersX = "None";
	private static String finalOutliersY = "None";
	private static String finalOutliersZ = "None";
	private static String finalOutliersRoll = "None";
	private static String finalOutliersPitch = "None";

	private static ArrayList<Integer> outliersX = new ArrayList<Integer>();
	private static ArrayList<Integer> outliersY = new ArrayList<Integer>();
	private static ArrayList<Integer> outliersZ = new ArrayList<Integer>();
	private static ArrayList<Double> outliersRoll = new ArrayList<Double>();
	private static ArrayList<Double> outliersPitch = new ArrayList<Double>();

	private static DecimalFormat angleFormatter = new DecimalFormat("00.00\u00B0");


	/************************************************************************************
	 * CREATE GUI
	 * 
	 * This is the method that "initializes" the program. It creates the GUI by instantiating
	 * classes that in turn create their own GUIs. This method is called directly by the
	 * IMStart class.
	 ************************************************************************************/
	public void createGUI() {
		frame = new JFrame("infoMoto");
		frame.setLayout(new BorderLayout());
		
		accelPanel = new AccelPanel();
		anglePanel = new AnglePanel();
		debugPanel = new DebugPanel(this);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Acceleration", accelPanel);
		tabbedPane.add("Angles", anglePanel);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, debugPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(270);
		frame.add(splitPane, BorderLayout.CENTER);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 550);
		frame.setVisible(true);
	}


	/************************************************************************************
	 * INITIALIZE
	 * 
	 * This method is called when the user presses the "Start" button. It basically serves
	 * as the middle-man between the user and the Input class. Before calling the initialize()
	 * method of the Input class, it resets all variables of this class so that any data
	 * from the previous run does not interfere with the data of any future runs.
	 ************************************************************************************/
	public boolean initialize() {
		numData = 0;
		runningTotalX = 0;
		runningTotalY = 0;
		runningTotalZ = 0;
		runningTotalRoll = 0.0;
		runningTotalPitch = 0.0;
		runningAverageX = 0;
		runningAverageY = 0;
		runningAverageZ = 0;
		runningAverageRoll = 0.0;
		runningAveragePitch = 0.0;
		accelX = 0;
		accelY = 0;
		accelZ = 0;
		roll = 0.0;
		pitch = 0.0;
		outliersX = new ArrayList<Integer>();
		outliersY = new ArrayList<Integer>();
		outliersZ = new ArrayList<Integer>();
		outliersRoll = new ArrayList<Double>();
		outliersPitch = new ArrayList<Double>();
		finalOutliersX = "None";
		finalOutliersY = "None";
		finalOutliersZ = "None";
		finalOutliersRoll = "None";
		finalOutliersPitch = "None";

		input = new Input(this);
		println("=================================================");
		print("Starting up ... ");
		boolean success = input.initialize();
		println("=================================================");
		return success;
	}

	/************************************************************************************
	 * TEARDOWN
	 * 
	 * This method is called when the user presses the "Stop" button. It basically serves
	 * as the middle man between the user and the Input class. After calling the close()
	 * method of the Input class, it displays the analysis and outliers. It traverses
	 * through each outlier array list and compares each value to the final average, using
	 * the same formula that it did to mark outliers in the first place.
	 ************************************************************************************/
	public void teardown() {
		println("=================================================");
		print("Tearing down ... ");
		input.close();
		println("=================================================");

		println("Samples   = " + numData);
		println("Total X   = " + runningTotalX);
		println("Total Y   = " + runningTotalY);
		println("Total Z   = " + runningTotalZ);
		println("Average X = " + runningAverageX);
		println("Average Y = " + runningAverageY);
		println("Average Z = " + runningAverageZ);
		println("=================================================");

		// Get final outliers for X.
		for (int i = 0; i < outliersX.size(); i++) {
			int outlier = outliersX.get(i).intValue();
			if (isAccelXOutlier(outlier))
			{
				if (finalOutliersX.equals("None")){
					finalOutliersX = "" + outlier;
				}
				else
				{
					finalOutliersX = finalOutliersX + ", " + outlier;
				}
			}
		}


		// Get final outliers for Y.
		for (int i = 0; i < outliersY.size(); i++) {
			int outlier = outliersY.get(i).intValue();
			if (isAccelYOutlier(outlier))
			{
				if (finalOutliersY.equals("None")){
					finalOutliersY = "" + outlier;
				}
				else
				{
					finalOutliersY = finalOutliersY + ", " + outlier;
				}
			}
		}

		// Get final outliers for Y.
		for (int i = 0; i < outliersZ.size(); i++) {
			int outlier = outliersZ.get(i).intValue();
			if (isAccelZOutlier(outlier))
			{
				if (finalOutliersZ.equals("None")){
					finalOutliersZ = "" + outlier;
				}
				else
				{
					finalOutliersZ = finalOutliersZ + ", " + outlier;
				}
			}
		}

		// Get final outliers for roll.
		for (int i = 0; i < outliersRoll.size(); i++) {
			double outlier = outliersRoll.get(i).doubleValue();
			if (isRollOutlier(outlier))
			{
				if (finalOutliersRoll.equals("None")){
					finalOutliersRoll = "" + angleFormatter.format(outlier);
				}
				else
				{
					finalOutliersRoll = finalOutliersRoll + ", " + angleFormatter.format(outlier);
				}
			}
		}

		// Get final outliers for pitch.
		for (int i = 0; i < outliersPitch.size(); i++) {
			double outlier = outliersPitch.get(i).doubleValue();
			if (isPitchOutlier(outlier))
			{
				if (finalOutliersPitch.equals("None")){
					finalOutliersPitch = "" + angleFormatter.format(outlier);
				}
				else
				{
					finalOutliersPitch = finalOutliersPitch + ", " + angleFormatter.format(outlier);
				}
			}
		}

		println("X Outliers: " + finalOutliersX);
		println("=================================================");
		println("Y Outliers: " + finalOutliersY);
		println("=================================================");
		println("Z Outliers: " + finalOutliersZ);
		println("=================================================");
		println("Roll Outliers: " + finalOutliersRoll);
	}

	/************************************************************************************
	 * PRINT
	 * 
	 * This method is used to print messages to the user. Instead of printing to the
	 * console, we want to simply append the text to the text area in the debug panel.
	 ************************************************************************************/
	public void print(String raw) {
		debugPanel.textArea.append(raw);
	}

	/************************************************************************************
	 * PRINT LINE
	 * 
	 * Same as above, but with an end-of-line character at the end of the statement.
	 ************************************************************************************/
	public void println(String raw) {
		debugPanel.textArea.append(raw + "\n");
	}


	/************************************************************************************
	 * SET DATA
	 * 
	 * This is the method that is used to receive data from the Input class. One-liners
	 * of data are transferred to this class via this method, and then the strings are
	 * torn apart and converted into valid numerical data. Most of the analysis happens
	 * in this method.
	 ************************************************************************************/
	public void setData(String raw) {
		// Make all readings positive.
		//raw = raw.replaceAll("-","");

		// Increment number of samples.
		numData++;

		// Throw away first couple of samples because they are buggy.
		if (numData < 2)
		{
			return;
		}

		/******************************************************************
		 * IMPORTANT: The numerical manipulation of the accel values is to normalize them. We want the axis being acted on by gravity to
		 * display roughly -1000 (milli-Gs) and the other two to display roughly 0. The values returned by the X, Y, and Z axes will differ
		 * based on the voltage sent to the sensor and therefore will probably be different for different configurations of the breadboard.
		 * If the sensors are wired up differently, keep in mind that these values will need to be changed. You will be able to see the raw
		 * numbers returned by the sensor in the Arduino serial monitor.
		 *****************************************************************/

		// Split the data into X, Y, and Z.
		data = raw.split(",");
		accelX = (int)(Integer.parseInt(data[0]) * 1.55) + 2550;
		accelY = (int)(Integer.parseInt(data[1]) * 1.55) + 2550;
		accelZ = (int)(Integer.parseInt(data[2]) * 1.55) + 2550;

		// Calculate the force vector and use it to calculate the roll.
		vector = Math.sqrt(Math.pow((double)accelX, 2) + Math.pow((double)accelY, 2) + Math.pow((double)accelZ, 2));
		roll = Math.toDegrees(Math.asin(((double)accelY / vector)));
		pitch = Math.toDegrees(Math.asin(((double)accelZ / vector)));

		// Print to the debug panel.
		println("X: " + accelX + " | Y: " + accelY + " | Z: " + accelZ + " | Roll: " + angleFormatter.format(roll) + " | Pitch: " + angleFormatter.format(pitch));

		// Let the user know that we have built a foundation for our averages.
		if (numData == 12)
		{
			println("=================================================");
			println("AVERAGE FOUNDATION BUILT");
			println("=================================================");
		}

		// Add a new plot point for each graph.
		// Use the current system time (in milliseconds) as the X coordinate,
		// and the respective value as the Y coordinate.
		accelPanel.xAccel.add(new Millisecond(), accelX);
		accelPanel.yAccel.add(new Millisecond(), accelY);
		accelPanel.zAccel.add(new Millisecond(), accelZ);
		anglePanel.roll.add(new Millisecond(), roll);
		anglePanel.pitch.add(new Millisecond(), pitch);

		// Do not count outliers for less than 10 samples
		if (numData < 10)
		{
			runningTotalX = accelX + runningTotalX;
			runningTotalY = accelY + runningTotalY;
			runningTotalZ = accelZ + runningTotalZ;
			runningTotalRoll = roll + runningTotalRoll;

			runningAverageX = runningTotalX / numData;
			runningAverageY = runningTotalY / numData;
			runningAverageZ = runningTotalZ / numData;
			runningAverageRoll = runningTotalRoll / numData;
		}
		else
		{
			// Check for possible X outliers.
			if(isAccelXOutlier(accelX)) {
				outliersX.add(accelX);
			}
			else
			{
				// Calculate X average.
				// Acceleration values only affect averages if they are NOT outliers.
				runningTotalX = accelX + runningTotalX;
				runningAverageX = runningTotalX / numData;
			}

			// Check for possible Y outliers.
			if(isAccelYOutlier(accelY)) {
				outliersY.add(accelY);
			}
			else
			{
				// Calculate Y average.
				// Acceleration values only affect averages if they are NOT outliers.
				runningTotalY = accelY + runningTotalY;
				runningAverageY = runningTotalY / numData;
			}

			// Check for possible Z outliers.
			if(isAccelZOutlier(accelZ)) {
				outliersZ.add(accelZ);
			}
			else
			{
				// Calculate Z average.
				// Acceleration values only affect averages if they are NOT outliers.
				runningTotalZ = accelZ + runningTotalZ;
				runningAverageZ = runningTotalZ / numData;
			}

			// Check for possible roll outliers.
			if(isRollOutlier(roll)) {
				outliersRoll.add(roll);
			}
			else
			{
				// Calculate roll average.
				// Angle values only affect averages if they are NOT outliers.
				runningTotalRoll = roll + runningTotalRoll;
				runningAverageRoll = runningTotalRoll / numData;
			}

			// Check for possible pitch outliers.
			if(isPitchOutlier(pitch)) {
				outliersPitch.add(pitch);
			}
			else
			{
				// Calculate roll average.
				// Angle values only affect averages if they are NOT outliers.
				runningTotalPitch = pitch + runningTotalPitch;
				runningAveragePitch = runningTotalPitch / numData;
			}
		}
	}

	/************************************************************************************
	 * IS ACCEL X OUTLIER
	 * 
	 * Check the acceleration in the X direction against a custom formula to determine
	 * whether or not it should be marked as an outlier.
	 ************************************************************************************/
	public boolean isAccelXOutlier(int accelX){
		if (accelX > (Math.max(runningAverageX + 20.0, runningAverageX * 1.5))
			|| accelX < (Math.min(runningAverageX - 20.0, runningAverageX * 0.66)))
		{
			return true;
		}
		
		return false;
	}

	/************************************************************************************
	 * IS ACCEL Y OUTLIER
	 * 
	 * Check the acceleration in the Y direction against a custom formula to determine
	 * whether or not it should be marked as an outlier.
	 ************************************************************************************/
	public boolean isAccelYOutlier(int accelY){
		if (accelY > (Math.max(runningAverageY + 200.0, runningAverageY * 1.25))
			|| accelY < (Math.min(runningAverageY - 200.0, runningAverageY * 0.75)))
		{
			return true;
		}
		
		return false;
	}

	/************************************************************************************
	 * IS ACCEL Z OUTLIER
	 * 
	 * Check the acceleration in the Z direction against a custom formula to determine
	 * whether or not it should be marked as an outlier.
	 ************************************************************************************/
	public boolean isAccelZOutlier(int accelZ){
		if (accelZ > (Math.max(runningAverageZ + 200.0, runningAverageZ * 1.25))
			|| accelZ < (Math.min(runningAverageZ - 200.0, runningAverageZ * 0.75)))
		{
			return true;
		}
		
		return false;
	}

	/************************************************************************************
	 * IS ROLL OUTLIER
	 * 
	 * Check the roll against a custom formula to determine whether or not it should be
	 * marked as an outlier.
	 ************************************************************************************/
	 public boolean isRollOutlier(double roll){
		if (roll > (Math.max(runningAverageRoll + 20.0, runningAverageRoll * 1.5))
			|| roll < (Math.min(runningAverageRoll - 20.0, runningAverageRoll * 0.66)))
		{
			return true;
		}
		
		return false;
	}

	/************************************************************************************
	 * IS ROLL OUTLIER
	 * 
	 * Check the pitch against a custom formula to determine whether or not it should be
	 * marked as an outlier.
	 ************************************************************************************/
	 public boolean isPitchOutlier(double pitch){
		if (pitch > (Math.max(runningAveragePitch + 20.0, runningAveragePitch * 1.5))
			|| pitch < (Math.min(runningAveragePitch - 20.0, runningAveragePitch * 0.66)))
		{
			return true;
		}
		
		return false;
	}
}
