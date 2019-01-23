import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*; // Needed for ActionListener
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.event.*; // Needed for ActionListener

class LifeSimulationGUI extends JFrame implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static ArrayList<Integer> population = new ArrayList<Integer>(10000);
	static int generation = 0;
	static PopulationGraph graph = new PopulationGraph();
	private JLabel graphImage = new JLabel(new ImageIcon(graph));

	private JSpinner startY = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 100));
	private JSpinner endY = new JSpinner(new SpinnerNumberModel(2000, 1, 10000, 100));
	private JSpinner startGen = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 10));
	private JSpinner endGen = new JSpinner(new SpinnerNumberModel(100, 1, 1000000, 10));
	static JSpinner range = new JSpinner(new SpinnerNumberModel(100, 1, 200, 10));
	static JToggleButton followEnd = new JToggleButton("Follow");

	private JLabel yRange = new JLabel("Y-Axis range (" + startY.getValue() + " - " + endY.getValue() + ")",
			JLabel.CENTER);
	static JLabel genRange = new JLabel("Generation range (" + 0 + " - " + 100 + ")", JLabel.CENTER);

	static Colony colony = new Colony(0.1); // Default colony

	// Display Panels
	private JPanel control = new JPanel(new GridBagLayout());
	private PatternPane patterns = new PatternPane();
	static DrawArea board = new DrawArea(500, 500);

	static boolean outlineEnabled = true;
	static Rule rule = new Rule("B3/S23");
	private JButton ruleBtn = new JButton("Change Rule");
	private JComboBox<Rule> ruleList;
	private JTextField enterRule = new JTextField(10);

	static Image selected = null;
	static InfoPane info; // Default info pane

	// JSliders
	static JSlider speedSldr = new JSlider(10, 400, 210);
	static JSlider populateSldr = new JSlider(1, 100, 35);
	static JSlider eradicateSldr = new JSlider(1, 100, 50);

	// Labels
	static JLabel kill, populate, speed;

	// Timer
	Movement moveColony = new Movement(colony); // ActionListener
	private Timer t = new Timer(410 - speedSldr.getValue(), moveColony); // set
																			// up
	// timer
	// Simulate button
	private JButton simulateBtn = new JButton("Simulate");
	// Instructions button
	JButton instructions = new JButton("Instructions");
	// Enable/Disable outline on colony show
	JButton toggleOutline = new JButton("Disable Outline");
	// Enable/Disable infopane
	JButton toggleInfo = new JButton("^Disable^");
	static boolean infoEnabled = true;

	// Saving components
	private JButton saveBtn = new JButton("Save");
	private JTextField saveName = new JTextField("", 13);

	// Loading components
	private JButton loadBtn = new JButton("Load");
	private JComboBox<String> loadName = new JComboBox<String>(
			new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/saves").list());

	// Delete file
	private JButton delBtn = new JButton("Delete");

	// ======================================================== constructor
	public LifeSimulationGUI() {
		// Adding actionlisteners to buttons
		simulateBtn.addActionListener(this);
		saveBtn.addActionListener(this);
		loadBtn.addActionListener(this);
		instructions.addActionListener(this);
		toggleInfo.addActionListener(this);
		delBtn.addActionListener(this);
		toggleOutline.addActionListener(this);
		ruleBtn.addActionListener(this);

		toggleInfo.setToolTipText("Disable this to reduce lag");

		// Adding change listeners to sliders
		speedSldr.addChangeListener(this);
		populateSldr.addChangeListener(this);
		eradicateSldr.addChangeListener(this);
		startY.addChangeListener(this);
		endY.addChangeListener(this);
		startGen.addChangeListener(this);
		endGen.addChangeListener(this);
		range.addChangeListener(this);
		followEnd.addChangeListener(this);

		// Create content pant
		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());

		// Create info panel
		try {
			selected = ImageIO.read(this.getClass().getResourceAsStream("/resources/selected.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		info = new InfoPane(0, 0);

		// Set default rule
		FileReader file = null;
		try {
			file = new FileReader( // Read from file
					getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
							+ "/rules/conways_life.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rule = new Rule(file);

		// Scan for other rule files, add to combobox
		File[] ruleFiles = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/rules")
				.listFiles();
		Rule[] rules = new Rule[ruleFiles.length];
		int defaultIndex = 0;
		for (int i = 0; i < rules.length; i++) {
			try {
				rules[i] = new Rule(new FileReader(ruleFiles[i]));
				if (rules[i].toRuleString().equalsIgnoreCase("B3/S23"))
					defaultIndex = i;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		ruleList = new JComboBox<Rule>(rules);
		ruleList.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
		dlcr.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
		ruleList.setRenderer(dlcr);
		ruleList.setSelectedIndex(defaultIndex);

		ruleBtn.setToolTipText("Changing rule from Conway's Life will make most patterns ineffective");

		// Formatting sliders, labels, adding colour, uses html
		simulateBtn.setPreferredSize(simulateBtn.getPreferredSize());

		populateSldr.setPreferredSize(new Dimension((int) (speedSldr.getPreferredSize().getWidth() / 2),
				(int) (speedSldr.getPreferredSize().getHeight())));
		eradicateSldr.setPreferredSize(new Dimension((int) (speedSldr.getPreferredSize().getWidth() / 2),
				(int) (speedSldr.getPreferredSize().getHeight())));

		int sv = speedSldr.getValue();
		speed = new JLabel(
				"<html><div style='text-align: center;'>Speed (Generation " + generation + ")<br>Current: "
						+ new DecimalFormat("#.##").format(1000.0 / (410 - sv)) + " generations/sec</div></html>",
				JLabel.CENTER);
		speed.setFont(speed.getFont().deriveFont(11f));
		speed.setPreferredSize(new Dimension((int) speedSldr.getPreferredSize().getWidth(), 35));

		int pv = populateSldr.getValue();
		populate = new JLabel("<html><div style='text-align: center;'>Populate Density<br>Current: "
				+ "<font color=rgb(" + colour(pv) + ")>" + pv + "%</font></div></html>", JLabel.CENTER);
		populate.setFont(populate.getFont().deriveFont(11f));
		populate.setPreferredSize(new Dimension((int) populateSldr.getPreferredSize().getWidth(), 35));

		int ev = eradicateSldr.getValue();
		kill = new JLabel("<html><div style='text-align: center;'>Eradicate Power<br>Current: " + "<font color=rgb("
				+ colour(ev) + ")>" + ev + "%" + "</font></div></html>", JLabel.CENTER);
		kill.setFont(kill.getFont().deriveFont(11f));
		kill.setPreferredSize(new Dimension((int) eradicateSldr.getPreferredSize().getWidth(), 35));

		speed.setToolTipText("Change speed of simulation");
		speedSldr.setToolTipText(speed.getToolTipText());
		populate.setToolTipText("Change populate density. Current: " + populateSldr.getValue() + "%"
				+ " To populate, left-click then drag");
		populateSldr.setToolTipText(populate.getToolTipText());
		kill.setToolTipText("Change eradicate effectiveness. Current: " + eradicateSldr.getValue() + "%"
				+ " To eradicate, right-click then drag");
		eradicateSldr.setToolTipText(kill.getToolTipText());

		startY.setPreferredSize(followEnd.getPreferredSize());
		endY.setPreferredSize(followEnd.getPreferredSize());
		startGen.setPreferredSize(followEnd.getPreferredSize());
		endGen.setPreferredSize(followEnd.getPreferredSize());
		range.setPreferredSize(followEnd.getPreferredSize());

		range.setEnabled(false);

		// End of formatting

		// Add components to control panel
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 10, 2);
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 2;
		c.gridx = 1;
		c.gridy = 0;
		control.add(simulateBtn, c);

		c.insets = new Insets(2, 2, 2, 2);
		c.gridheight = 1;
		c.gridy = 0;

		c.gridx = 2;
		control.add(speed, c);
		c.gridx = 3;
		control.add(populate, c);
		c.gridx = 4;
		control.add(kill, c);

		c.gridy = 1;
		c.insets = new Insets(2, 2, 10, 2);

		c.gridx = 2;
		control.add(speedSldr, c);
		c.gridx = 3;
		control.add(populateSldr, c);
		c.gridx = 4;
		control.add(eradicateSldr, c);

		// End of adding components to control

		// Constructing the sidebar
		JPanel sidebar = new JPanel(new GridBagLayout());
		GridBagConstraints c2 = new GridBagConstraints(); // To be used for all
															// inner panels
		c2.fill = GridBagConstraints.BOTH;
		c2.insets = new Insets(2, 2, 2, 2);
		c2.gridy = 0;
		sidebar.add(instructions, c2);
		c2.gridy = 1;
		sidebar.add(toggleOutline, c2);
		c2.gridy = 2;
		sidebar.add(ruleBtn, c2);
		c2.insets = new Insets(0, 0, 0, 0);
		c2.gridy = 3;
		sidebar.add(patterns, c2);
		c2.gridy = 4;
		sidebar.add(info, c2);
		c2.gridy = 5;
		sidebar.add(toggleInfo, c2);
		// End

		// Add Everything to content panel
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 4;
		c.gridx = 1;
		c.gridy = 0;
		content.add(control, c);
		c.gridy = 1;
		content.add(board, c); // Output area
		c.gridwidth = 2;
		c.gridy = 2;

		c.insets = new Insets(10, 2, 2, 2);
		JPanel savePnl = new JPanel(new GridBagLayout());
		c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		savePnl.add(saveBtn, c2);
		c2.gridx = 1;
		savePnl.add(saveName, c2);

		JPanel loadDel = new JPanel(new GridBagLayout());
		c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		loadDel.add(loadBtn, c2);
		c2.gridx = 1;
		loadDel.add(loadName, c2);
		c2.gridx = 2;
		loadDel.add(delBtn, c2);

		content.add(savePnl, c);
		c.gridx = 3;
		content.add(loadDel, c);
		c.gridx = 5;
		c.gridy = 0;
		c.gridheight = 3;
		c.insets = new Insets(0, 10, 0, 0);
		c.gridwidth = 1;
		content.add(sidebar, c);

		JPanel graphPanel = new JPanel(new GridBagLayout());
		JPanel graphControl = new JPanel(new GridBagLayout());
		graphControl.setPreferredSize(new Dimension(250, 120));

		GridBagConstraints c3 = new GridBagConstraints();

		c2 = new GridBagConstraints();
		c2.insets = new Insets(2, 2, 2, 2);
		c2.fill = GridBagConstraints.BOTH;

		c2.gridx = 0;
		c2.gridy = 0;
		graphPanel.add(graphImage, c2);

		c2.gridy = 1;
		c2.gridx = 0;
		graphPanel.add(graphControl, c2);

		c3.fill = GridBagConstraints.BOTH;
		c3.insets = new Insets(2, 2, 2, 2);
		c3.gridwidth = 2;
		graphControl.add(yRange, c3);

		c3.gridwidth = 1;
		c3.gridy = 1;
		graphControl.add(startY, c3);
		c3.gridx = 1;
		graphControl.add(endY, c3);

		c3.insets = new Insets(10, 2, 2, 2);
		c3.gridwidth = 2;
		c3.gridy = 2;
		c3.gridx = 0;
		graphControl.add(genRange, c3);

		c3.insets = new Insets(2, 2, 2, 2);
		c3.gridwidth = 1;
		c3.gridy = 3;
		c3.gridx = 0;
		graphControl.add(startGen, c3);
		c3.gridx = 1;
		graphControl.add(endGen, c3);

		c3.gridy = 4;
		c3.gridx = 0;
		graphControl.add(range, c3);
		c3.gridx = 1;
		graphControl.add(followEnd, c3);

		c.insets = new Insets(2, 2, 2, 15);
		c.gridx = 0;
		c.gridy = 0;
		content.add(graphPanel, c);

		// Set this window's attributes.
		setContentPane(content);
		pack();
		setTitle("Conway's Game of Life");
		setSize(1000, 665);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Center window.

	}

	private static String colour(int x) { // colour method, accepts an integer
											// (used in program as a num from
											// 1-100) and finds an appropriate
											// rgb string ranging from green to
											// red. decreases exponentially
		Color c = Color.getHSBColor((float) ((120 - (30 * Math.pow(x / 50.0, 2))) / 360.0), 1f, .7f); // Algorithm
																										// for
																										// finding
																										// colour
																										// based
																										// on
																										// value,
																										// quadradic
		return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
	}

	public void stateChanged(ChangeEvent e) { // If there is as change in a
												// slider

		int rangeStart;
		if (population.size() > (int) range.getValue())
			rangeStart = population.size() - (int) range.getValue();
		else
			rangeStart = 0;

		if (e.getSource() == speedSldr) { // If event is from sleed slider,
											// change text
			int sv = speedSldr.getValue();
			if (t != null)
				t.setDelay(410 - sv); // 10 to 500 ms
			speedSldr.setToolTipText(speed.getToolTipText());
			speed.setText("<html><div style='text-align: center;'>Speed (Generation " + generation + ")<br>Current: "
					+ new DecimalFormat("#.##").format(1000.0 / (410 - sv)) + " generations/sec</div></html>");
		} else if (e.getSource() == populateSldr) { // Likewise for populate
													// slider and eradicate
													// slider
			int pv = populateSldr.getValue();
			populate.setToolTipText(
					"Change populate density. Current: " + pv + "%" + " To populate, left-click then drag");
			populateSldr.setToolTipText(populate.getToolTipText());
			populate.setText("<html><div style='text-align: center;'>Populate Density<br>Current: " + "<font color=rgb("
					+ colour(pv) + ")>" + pv + "%</font></div></html>");
		} else if (e.getSource() == eradicateSldr) {
			int ev = eradicateSldr.getValue();
			kill.setToolTipText(
					"Change eradicate effectiveness. Current: " + ev + "%" + " To eradicate, right-click then drag");
			eradicateSldr.setToolTipText(kill.getToolTipText());
			kill.setText("<html><div style='text-align: center;'>Eradicate Power<br>Current: " + "<font color=rgb("
					+ colour(ev) + ")>" + ev + "%</font></div></html>");

		} else if (e.getSource() == followEnd) {
			graph.setFollow(followEnd.isSelected());
			if (followEnd.isSelected()) {
				startGen.setEnabled(false);
				endGen.setEnabled(false);
				range.setEnabled(true);
			} else {
				startGen.setEnabled(true);
				endGen.setEnabled(true);
				range.setEnabled(false);

				startGen.setValue(rangeStart);
				endGen.setValue(rangeStart + (int) range.getValue());
				genRange.setText("Generation range (" + startGen.getValue() + " - " + endGen.getValue() + ")");
			}
		} else if (e.getSource() == startY) {
			if ((int) startY.getValue() >= (int) endY.getValue())
				startY.setValue((int) endY.getValue() - 1);
			graph.setYStart((int) startY.getValue());
			graph.updateGraph();
		} else if (e.getSource() == endY) {
			if ((int) endY.getValue() <= (int) startY.getValue())
				endY.setValue((int) startY.getValue() + 1);
			graph.setYEnd((int) endY.getValue());
			graph.updateGraph();
		} else if (e.getSource() == startGen) {
			if ((int) startGen.getValue() >= (int) endGen.getValue())
				startGen.setValue((int) endGen.getValue() - 1);
			if ((int) startGen.getValue() < (int) endGen.getValue() - 200)
				startGen.setValue((int) endGen.getValue() - 200);
			graph.setGenStart((int) startGen.getValue());
			graph.updateGraph();
		} else if (e.getSource() == endGen) {
			if ((int) endGen.getValue() <= (int) startGen.getValue())
				endGen.setValue((int) startGen.getValue() + 1);
			if ((int) endGen.getValue() > (int) startGen.getValue() + 200)
				endGen.setValue((int) startGen.getValue() + 200);
			graph.setGenEnd((int) endGen.getValue());
			graph.updateGraph();
		} else if (e.getSource() == range) {
			graph.setRange((int) range.getValue());
			graph.updateGraph();
		}
		yRange.setText("Y-Axis range (" + startY.getValue() + " - " + endY.getValue() + ")");
		if (followEnd.isSelected()) {
			genRange.setText("Generation range (" + rangeStart + " - " + (rangeStart + (int) range.getValue()) + ")");
		} else
			genRange.setText("Generation range (" + startGen.getValue() + " - " + endGen.getValue() + ")");
		
		graph.updateGraph();
		graphImage.repaint();
	}

	public void actionPerformed(ActionEvent e) { // Action performed method
		JButton source = (JButton) e.getSource();
		if (source.getText().equals("Simulate")) {
			moveColony = new Movement(colony);
			t = new Timer(410 - speedSldr.getValue(), moveColony); // set up
			// timer
			t.start(); // start simulation
			source.setText("Pause");
		} else if (source.getText().equals("Pause")) { // Simulation commands
			t.stop();
			source.setText("Simulate");
		} else if (source == instructions) {
			JOptionPane.showMessageDialog(this,
					"Left-click and drag to populate." + "\nRight-click and drag to eradicate."
							+ "\n\nPressing \"Add\" attempts to add" + "\nthe current pattern the the grid and"
							+ "\ndisables eradicating and populating." + "\n\nMultiple button allows user to add"
							+ "\nmultiple patterns without having to" + "\nkeep pressing \"Add\". User can"
							+ "\ndisable/enable multiple by pressing it" + "\n\nChange the game rule by"
							+ "\nclicking the change rule button and" + "\npicking/entering a rule."
							+ "\nFor entering a rule, the format is" + "\nB___/S___. B and S are explained"
							+ "\nin the Rule class." + "\nNote that changing the rule"
							+ "\nwill make most patterns ineffective",
					"Instructions", JOptionPane.INFORMATION_MESSAGE);
		} else if (source == ruleBtn) {
			t.stop();
			simulateBtn.setText("Simulate");
			JPanel options = new JPanel(new GridLayout(2, 1));
			options.add(ruleList);
			options.add(enterRule);
			JOptionPane.showMessageDialog(this, options, "Choose or enter a rule", JOptionPane.QUESTION_MESSAGE);
			rule = new Rule(enterRule.getText());
			enterRule.setText("");
			if (rule.toString().equals("N/A")) {
				rule = (Rule) ruleList.getSelectedItem();
			}
		} else if (source == toggleOutline) {
			outlineEnabled = !outlineEnabled;
			if (source.getText().equals("Disable Outline"))
				source.setText("Enable Outline");
			else
				source.setText("Disable Outline");
			board.repaint();
		} else if (source == toggleInfo) {
			infoEnabled = !infoEnabled;
			if (source.getText().equals("^Disable^"))
				source.setText("^Enable^");
			else
				source.setText("^Disable^");
		} else if (source == delBtn) {
			int choice = 1;
			choice = JOptionPane.showConfirmDialog(this,
					"Are you sure you want to\ndelete \"" + loadName.getSelectedItem()
							+ "\"\n\nOnce deleted, there is no\nway of recovering the file",
					"Delete Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (choice == 0) {
				File toDelete = null;
				try {
					toDelete = new File(getClass().getResource("/saves/" + loadName.getSelectedItem()).toURI());
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
				toDelete.delete();
				loadName.removeItemAt(loadName.getSelectedIndex());
			}
		} else if (source == saveBtn) { // If save
			try {
				if (saveName.getText().length() != 0) { // Make sure there is a
														// string
					String filename = saveName.getText().replaceAll(".txt", "").replaceAll("[^a-zA-Z1-9_]", "")
							+ ".txt"; // Replace all non alphanumeric/_
										// characters

					boolean replace = false; // If a replace is required

					String files[] = new File(
							getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/saves").list();
					for (int i = 0; i < files.length; i++) // If another file of
															// the same name is
															// found
						if (files[i].equals(filename))
							replace = true; // Replace is true

					int choice = 0;
					if (replace) // If replace
						choice = JOptionPane.showConfirmDialog(this, // Ask user
																		// if
																		// they
																		// want
																		// to
																		// replace
								"A file named \"" + filename + "\" already exists\nReplace file?", "Replace file",
								JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (choice == 0) { // If they answer yes
						colony.save(filename); // Replace file
						if (!replace) // If no file is being replaced
							loadName.addItem(filename); // Add filename to load
						saveName.setText("");
						JOptionPane.showMessageDialog(this, "\"" + filename + "\" has successfully been saved",
								"Instructions", JOptionPane.INFORMATION_MESSAGE);
					}
				} else
					JOptionPane.showMessageDialog(this, "Must enter name of save", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (source == loadBtn) { // If load button
			simulateBtn.setText("Simulate"); // Make sure display is paused
			if (t.isRunning())
				t.stop();
			if ((String) loadName.getSelectedItem() != null)
				colony = new Colony((String) loadName.getSelectedItem());
			population = new ArrayList<Integer>(10000);
			generation = 0;
			graph.updateGraph();
			graphImage.repaint();
			LifeSimulationGUI.speed.setText("<html><div style='text-align: center;'>Speed (Generation "
					+ LifeSimulationGUI.generation + ")<br>Current: "
					+ new DecimalFormat("#.##").format(1000.0 / (410 - LifeSimulationGUI.speedSldr.getValue()))
					+ " generations/sec</div></html>");
		}
		board.repaint(); // refresh colony display
	}

	class Movement implements ActionListener { // Movement class for timer
		private Colony colony;

		public Movement(Colony col) {
			colony = col;
		}

		public void actionPerformed(ActionEvent event) {
			colony.advance();
			repaint();
		}
	}

	// ======================================================== method main
	public static void main(String[] args) throws Exception {
		LifeSimulationGUI window = new LifeSimulationGUI();
		window.setVisible(true);
		// window.setResizable(false);
	}
}
