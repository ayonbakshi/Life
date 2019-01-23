import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class PatternPane extends JPanel implements ItemListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int row = 0;
	private static int col = 0;
	static Pattern allPatterns[][] = new Pattern[5][];

	// Buttons for adding
	static JButton addBtn = new JButton("Add");
	static JToggleButton multiple = new JToggleButton("<html><font size=1>Multiple</font></html>");

	// Panel for going thru patterns
	private JPanel displayImage = new JPanel(new CardLayout());
	// Buttons for going thru patterns
	private JButton last = new JButton("<");
	private JButton next = new JButton(">");
	// Combobox to go thru patternt types
	private JComboBox<String> type = new JComboBox<String>(
			new String[] { "Guns", "Miscellaneous", "Oscillators", "Spaceships", "Still Life" });

	// Toggle info
	private JButton toggleInfo = new JButton("Show Info");
	// Information labels
	private JLabel displayName = new JLabel();
	private JLabel displaySize = new JLabel();
	private JLabel displayLiving = new JLabel();

	public PatternPane() { // Constructor

		// Add item listener to type
		type.addItemListener(this);

		// Add actionlisteners to buttons
		last.addActionListener(this);
		next.addActionListener(this);
		addBtn.addActionListener(this);
		toggleInfo.addActionListener(this);

		// Get file of patterns
		File dirs[] = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/patterns")
				.listFiles();

		// List files in each folder
		for (int i = 0; i < 5; i++) {
			String patternFiles[] = dirs[i].list();
			allPatterns[i] = new Pattern[patternFiles.length];
			for (int j = 0; j < allPatterns[i].length; j++)
				allPatterns[i][j] = new Pattern(patternFiles[j], dirs[i].getName());
		}

		// Default index to 0
		type.setSelectedIndex(0);
		row = type.getSelectedIndex(); // Set row

		// Create images from patterns to display the images
		JLabel[] patternImages = new JLabel[allPatterns[row].length];
		for (int i = 0; i < allPatterns[row].length; i++) {
			patternImages[i] = new JLabel(new ImageIcon(allPatterns[row][i].toImage(120)), JLabel.CENTER);
			displayImage.add(patternImages[i]);
		}

		// Set allignment of info labels
		Font infoFont = new Font("Courier", Font.PLAIN, 12);
		Font infoBoldFont = new Font("Courier", Font.BOLD, 14);

		displayName.setHorizontalAlignment(JLabel.CENTER);
		displayName.setFont(infoBoldFont);
		displaySize.setHorizontalAlignment(JLabel.CENTER);
		displaySize.setFont(infoFont);
		displayLiving.setHorizontalAlignment(JLabel.CENTER);
		displayLiving.setFont(infoFont);

		// Set text of info labels based on current pattern
		if (currentPattern() != null) {
			displayName.setText(currentPattern().getName());
			displaySize.setText("Dimensions: " + currentPattern().getWidth() + " x " + currentPattern().getHeight());
			displayLiving.setText("Live Cells: " + currentPattern().getLiving());
		} else {
			addBtn.setText("Add");
			LifeSimulationGUI.board.repaint();
			displayName.setText("");
			displaySize.setText("");
			displayLiving.setText("");
		}

		// To make sure these components dont resize in the program
		addBtn.setPreferredSize(addBtn.getPreferredSize());
		displayName.setPreferredSize(displayName.getPreferredSize());
		displaySize.setPreferredSize(displaySize.getPreferredSize());

		// Set layout to gridbag
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(150, 300));

		// Put everything on the panel
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 1, 2, 1);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.weightx = .5;
		c.gridx = 0;
		c.gridy = 0;
		add(addBtn, c);
		c.gridx = 1;
		add(multiple, c);
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
		add(type, c);
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		add(displayImage, c);
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 3;
		add(toggleInfo, c);
		c.gridwidth = 1;
		c.weightx = .5;
		c.gridy = 7;
		c.gridx = 0;
		add(last, c);
		c.gridx = 1;
		add(next, c);

	}

	@Override
	public void itemStateChanged(ItemEvent e) { // This is called twice due to
												// one item being deselected and
												// another being selected.
												// Called to change image and
												// row/col
		displayImage.removeAll();
		row = type.getSelectedIndex();
		col = 0;
		JLabel[] images = new JLabel[allPatterns[row].length];
		for (int i = 0; i < allPatterns[row].length; i++) {
			images[i] = new JLabel(new ImageIcon(allPatterns[row][i].toImage(120)), JLabel.CENTER);
			displayImage.add(images[i]); // Get new image array to display
		}
		repaint();
		revalidate();
		if (currentPattern() != null) { // Changes text in info labels
			displayName.setText(currentPattern().getName());
			displaySize.setText("Dimensions: " + currentPattern().getWidth() + " x " + currentPattern().getHeight());
			displayLiving.setText("Live Cells:" + currentPattern().getLiving());
		} else {
			addBtn.setText("Add");
			LifeSimulationGUI.board.repaint();
			displayName.setText("");
			displaySize.setText("");
			displayLiving.setText("");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) { // Action performed method
		CardLayout cl = (CardLayout) displayImage.getLayout(); // Card layout

		// Swaps back and forth
		if (e.getSource() == next) {
			cl.next(displayImage);
			if (col == allPatterns[row].length)
				col = 0;
			else
				col = (col + 1) % allPatterns[row].length;
		} else if (e.getSource() == last) {
			cl.previous(displayImage);
			if (col == 0)
				col = allPatterns[row].length - 1;
			else
				col = (col - 1) % allPatterns[row].length;
		}

		else if (e.getSource() == addBtn) { // If add button is selected
			if (addBtn.getText().equals("Add"))
				addBtn.setText("<html><font size=1>Cancel</font></html>"); // Set
																			// up
																			// text
																			// for
																			// adding
																			// from
																			// drawarea
			else {
				addBtn.setText("Add");
				LifeSimulationGUI.board.repaint();
			}
		}

		// Toggle info button toggles info
		else if (e.getSource() == toggleInfo && toggleInfo.getText().equals("Show Info")) {
			toggleInfo.setText("Hide Info");
			GridBagConstraints c = new GridBagConstraints();
			c.gridwidth = 2;
			c.weightx = 1;
			c.fill = GridBagConstraints.BOTH;
			c.gridy = 4;
			add(displayName, c);
			c.gridy = 5;
			add(displaySize, c);
			c.gridy = 6;
			add(displayLiving, c);
			revalidate();
		} else if (e.getSource() == toggleInfo && toggleInfo.getText().equals("Hide Info")) {
			toggleInfo.setText("Show Info");
			remove(displayName);
			remove(displaySize);
			remove(displayLiving);
			revalidate();
		}

		// At the end of each action performed, set the text in the info panels
		if (currentPattern() != null) {
			displayName.setText(currentPattern().getName());
			displaySize.setText("Dimensions: " + currentPattern().getWidth() + " x " + currentPattern().getHeight());
			displayLiving.setText("Live Cells: " + currentPattern().getLiving());
		} else {
			addBtn.setText("Add");
			LifeSimulationGUI.board.repaint();
			displayName.setText("");
			displaySize.setText("");
			displayLiving.setText("");
		}
	}

	public static Pattern currentPattern() { // Returns current pattern or null
		try {
			return allPatterns[row][col];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}
}
