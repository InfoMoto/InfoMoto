package infoMOTO;

/* This class creates the panel used for debugging and displaying raw data. */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class DebugPanel extends JPanel implements ActionListener
{
	public InfoMoto main;

	public static JTextArea textArea;
	public static JButton startButton;
	public static JButton stopButton;

	public DebugPanel(InfoMoto im){
		super();

		main = im;

		// Create the text area.
		textArea = new JTextArea(10, 40);
		textArea.setEditable(false);

		// This makes the text area automatically scroll to the bottom whenever something is added.
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// Create the scroll pane.
		// This pane will have a scrollbar on the side, but not at the bottom.
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Create the buttons.
		startButton = new JButton("Start");
		stopButton = new JButton("Stop");

		startButton.addActionListener(this);
		stopButton.addActionListener(this);

		// Stop button initializes in disabled state.
		stopButton.setEnabled(false);

		// Create the horizontal box that houses the buttons.
		Box HBox = Box.createHorizontalBox();
		HBox.add(startButton);
		HBox.add(Box.createHorizontalStrut(15));
		HBox.add(stopButton);

		// Create the vertical box that houses the text area and buttons.
		Box VBox = Box.createVerticalBox();
		VBox.add(Box.createVerticalStrut(10));
		VBox.add(scrollPane);
		VBox.add(Box.createVerticalStrut(15));
		VBox.add(HBox);

		// Add all components to the panel.
		add(VBox);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startButton)
		{
			textArea.setText("");
			if (main.initialize())
			{
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
			}
		}
		else if (e.getSource() == stopButton)
		{
			main.teardown();
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
		}
	}
}

