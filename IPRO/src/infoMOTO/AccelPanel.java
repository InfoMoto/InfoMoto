package infoMOTO;

/* This class creates the panel that holds the acceleration graph. */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class AccelPanel extends JPanel
{
	public TimeSeries xAccel;
	public TimeSeries yAccel;
	public TimeSeries zAccel;

	public AccelPanel() {
		super(new BorderLayout());

		// Set up time series for X and Y.
		this.xAccel = new TimeSeries("Acceleration (X Axis)", Millisecond.class);
		this.yAccel = new TimeSeries("Acceleration (Y Axis)", Millisecond.class);
		this.zAccel = new TimeSeries("Acceleration (Z Axis)", Millisecond.class);

		// Create a data set of the X and Y series.
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(this.xAccel);
		dataset.addSeries(this.yAccel);
		dataset.addSeries(this.zAccel);

		// Set up the domain and range of the graph.
		DateAxis domain = new DateAxis();
		NumberAxis range = new NumberAxis();

		// Set up the renderer for the lines and their colors.
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);

		// Create the plot for the points.
		XYPlot plot = new XYPlot(dataset, domain, range, renderer);
		domain.setFixedAutoRange(30000.0);
		domain.setLowerMargin(0.0);
		domain.setUpperMargin(0.0);
		domain.setTickLabelsVisible(true);
		range.setTickLabelsVisible(true);
		range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		renderer.setBaseItemLabelsVisible(false);

		// Create the actual chart.
		JFreeChart chart = new JFreeChart("Acceleration", new Font("SansSerif", Font.BOLD, 18), plot, true);
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

		// Add the chart to the panel.
		ChartPanel chartPanel = new ChartPanel(chart, true);
        	chartPanel.setBorder(null);

		// Create a chart scroll bar.
		ChartScrollBar chartScrollBar = new ChartScrollBar(JScrollBar.HORIZONTAL, chart);


		// Place everything in a vertical box to avoid display issues.
		Box VBox = Box.createVerticalBox();
		VBox.add(chartPanel);
		VBox.add(Box.createVerticalStrut(5));
		VBox.add(chartScrollBar);
        	add(VBox);
	}
}