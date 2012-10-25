package Grapher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class DataGrapher2
{
	public static void main(String[] args) 
	{
		ArrayList<String> lines = new ArrayList<String>();

		// Accelerometer-related data
		ArrayList<String> accelX = new ArrayList<String>();
		ArrayList<String> accelY = new ArrayList<String>();
		ArrayList<String> accelZ = new ArrayList<String>();

		// GPS-related data
		ArrayList<String> latitude = new ArrayList<String>();
		ArrayList<String> longitude = new ArrayList<String>();
		ArrayList<String> altitude = new ArrayList<String>();
		ArrayList<String> speed = new ArrayList<String>();

		// Date- and time-related data
		ArrayList<String> second = new ArrayList<String>();
		ArrayList<String> minute = new ArrayList<String>();
		ArrayList<String> hour = new ArrayList<String>();
		ArrayList<String> day = new ArrayList<String>();
		ArrayList<String> month = new ArrayList<String>();
		ArrayList<String> year = new ArrayList<String>();

		// Create a basic GUI.
		JFrame frame = new JFrame("infoMOTO");
		frame.setLayout(new BorderLayout());

		JPanel anglePanel, accelPanel, altPanel, speedPanel;

		String line;
		Scanner input = new Scanner(System.in);
		System.out.print("Enter file name:");
		String filename = input.nextLine();
		input.close();
		String [] tokens;

		// Read the file line by line.
		try{
			Scanner scanner = new Scanner(new File(filename));

			while(scanner.hasNextLine())
			{
				line = scanner.nextLine();
				lines.add(line);
			}
			scanner.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		// Parse lines into data and insert it into corresponding data structures.
		for(int i = 0; i < lines.size() - 1; i++)
		{
			tokens = lines.get(i).split(",");

			accelX.add(tokens[0]);
			accelY.add(tokens[1]);
			accelZ.add(tokens[2]);
			latitude.add(tokens[3]);
			longitude.add(tokens[4]);
			altitude.add(tokens[5]);
			speed.add(tokens[6]);

			// Date and time are split into their smaller components (second, minute, hour, day, month, year).
			day.add(tokens[7].substring(0,2));
			month.add(tokens[7].substring(2,4));
			year.add(tokens[7].substring(4,6));
			hour.add(tokens[8].substring(0,2));
			minute.add(tokens[8].substring(2,4));
			second.add(tokens[8].substring(4,6));
		}

		TimeSeries accelXSeries = new TimeSeries("Accel X");
		TimeSeries accelYSeries = new TimeSeries("Accel Y");
		TimeSeries accelZSeries = new TimeSeries("Accel Z");
		TimeSeries pitchSeries = new TimeSeries("Pitch");
		TimeSeries rollSeries = new TimeSeries("Roll");
		TimeSeries altSeries = new TimeSeries("Altitude");
		TimeSeries speedSeries = new TimeSeries("Speed");

		try
		{
			// Create acceleration chart.
			TimeSeriesCollection dataset = new TimeSeriesCollection();
			dataset.addSeries(accelXSeries);
			dataset.addSeries(accelYSeries);
			dataset.addSeries(accelZSeries);
			accelPanel = createChart(dataset);

			// Create angle chart.
			dataset = new TimeSeriesCollection();
			dataset.addSeries(pitchSeries);
			dataset.addSeries(rollSeries);
			anglePanel = createChart(dataset);

			// Create altitude chart.
			dataset = new TimeSeriesCollection();
			dataset.addSeries(altSeries);
			altPanel = createChart(dataset);

			// Create speed chart.
			dataset = new TimeSeriesCollection();
			dataset.addSeries(speedSeries);
			speedPanel = createChart(dataset);

			// Place the charts in the GUI.
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, speedPanel, anglePanel);
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerLocation(300);
			frame.add(splitPane, BorderLayout.CENTER);

			// Show the GUI.
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800, 640);
			frame.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// Calculation-based variables.
		double accX, accY, accZ, pitch, roll, vector;
		String lat, lon;
		String url = "https://maps.googleapis.com/maps/api/staticmap?zoom=18&size=640x640&sensor=true&path=color:0x0000ff|weight:5";

		for(int i = 0; i < second.size(); i++)
		{
			// Skip to the next iteration if the current second is the same as the previous.
			if (i != 0 && second.get(i).equals(second.get(i-1)))
			{
				continue;
			}

			// Parse latitude.
			if (latitude.get(i).contains("-"))
			{
				// Must account for the minus at the beginning of the string.
				lat = latitude.get(i).substring(0, 3) + "." + latitude.get(i).substring(3);
			}
			else
			{
				// No minus sign at beginning of string.
				lat = latitude.get(i).substring(0, 2) + "." + latitude.get(i).substring(2);
			}

			// Parse longitude.
			if (longitude.get(i).contains("-"))
			{
				// Must account for the minus at the beginning of the string.
				lon = longitude.get(i).substring(0, 3) + "." + longitude.get(i).substring(3);
			}
			else
			{
				// No minus sign at beginning of string.
				lon = longitude.get(i).substring(0, 2) + "." + longitude.get(i).substring(2);
			}

			// Use only intermittent GPS coordinates, scaled (not well) to the total length of the ride.
			if(i % ((second.size() / 50) + 1) == 0)
			{
				url = url + "|" + lat + "," + lon;
			}

			// Calculate the force vector, then use it to calculate the pitch and roll.
			accX = (new Integer(accelX.get(i)).intValue() + 9) * 10;
			accY = (new Integer(accelY.get(i)).intValue() + 9) * 10;
			accZ = (new Integer(accelZ.get(i)).intValue() + 9) * 10;
			vector = Math.sqrt(Math.pow(accX, 2) + Math.pow(accY, 2) + Math.pow(accZ, 2));
			roll = Math.toDegrees(Math.asin((accY / vector)));
			pitch = Math.toDegrees(Math.asin((accZ / vector)));

			Second sec = new Second(Integer.parseInt(second.get(i)), Integer.parseInt(minute.get(i)), Integer.parseInt(hour.get(i)), 
				Integer.parseInt(day.get(i)), Integer.parseInt(month.get(i)), (Integer.parseInt(year.get(i)) + 2000));

			// Populate the acceleration series.
			accelXSeries.add(sec, accX);
			accelYSeries.add(sec, accY);
			accelZSeries.add(sec, accZ);

			// Populate the angle series.
			pitchSeries.add(sec, pitch);
			rollSeries.add(sec, roll);

			// Populate the altitude series.
			altSeries.add(sec, Double.parseDouble(altitude.get(i)));

			// Populate speed series.
			speedSeries.add(sec, Double.parseDouble(speed.get(i)));

			try
			{
				Thread.sleep(1000);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static ChartPanel createChart(TimeSeriesCollection dataset)
	{
		// Create the domain and range of the graph.
		DateAxis domain = new DateAxis();
		NumberAxis range = new NumberAxis();

		// Create the renderer for the lines.
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);

		// Create the plot for the points.
		XYPlot plot = new XYPlot(dataset, domain, range, renderer);
		domain.setFixedAutoRange(60000.0);
		domain.setLowerMargin(0.0);
		domain.setUpperMargin(0.0);
		domain.setTickLabelsVisible(true);
		range.setTickLabelsVisible(true);
		range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		renderer.setBaseItemLabelsVisible(false);

		// Create the actual chart.
		JFreeChart chart = new JFreeChart("Speed", new Font("SansSerif", Font.BOLD, 18), plot, true);
		String t = null;
		chart.setTitle(t);
		chart.setBackgroundPaint(Color.blue);

		ChartUtilities.applyCurrentTheme(chart);
		chart.setBorderVisible(false);
		chart.setBorderPaint(null);
		domain.setTickLabelInsets(RectangleInsets.ZERO_INSETS);
		range.setTickLabelInsets(RectangleInsets.ZERO_INSETS);
		domain.setTickMarksVisible(true);
		range.setTickMarksVisible(true);
        chart.setPadding(RectangleInsets.ZERO_INSETS);

		ChartPanel chartPanel = new ChartPanel(chart, true);
        chartPanel.setBorder(null);

		return chartPanel;
	}
}