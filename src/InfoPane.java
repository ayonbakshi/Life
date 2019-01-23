import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Instance variables
	static int living = 0, dead = 0;
	private int focusedGrid[][];
	private Image img;
	private JLabel liveLabel, deadLabel, currLabel, nextGen, image;

	public InfoPane(int row, int col) { // Constructor, accepts a row and column
		focusedGrid = new int[3][3]; // Creates the focuessed grid
		boolean toIgnore[] = LifeSimulationGUI.colony.getToIgnore(row, col); // Instead
																				// of
																				// boolean
																				// array,
																				// uses
																				// int
																				// array
		// Initialize the focused array
		for (int i = 0; i < toIgnore.length; i++) {
			if (toIgnore[i] && i != 4)
				focusedGrid[1 + (i / 3 - 1)][1 + (i % 3 - 1)] = 2;

			else {
				if (LifeSimulationGUI.colony.atIndex(row + (i / 3 - 1), col + (i % 3 - 1)))
					focusedGrid[1 + (i / 3 - 1)][1 + (i % 3 - 1)] = 1;
				else
					focusedGrid[1 + (i / 3 - 1)][1 + (i % 3 - 1)] = 0;
			}
		}
		generateImage(); // Creates the image for the current cell

		for (int i = 0; i < 100; i++)
			for (int j = 0; j < 100; j++) {
				if (LifeSimulationGUI.colony.atIndex(i, j))
					living++;
				else
					dead++;
			} // Caluculates living and dead
		liveLabel = new JLabel("Living: " + living, JLabel.CENTER); // Label for
																	// living
																	// and dead
		deadLabel = new JLabel("Dead: " + dead, JLabel.CENTER);

		String status; // find status of current cell
		if (LifeSimulationGUI.colony.atIndex(row, col))
			status = "alive";
		else
			status = "dead";
		currLabel = new JLabel("Current(" + row + "," + col + "): " + status, JLabel.CENTER);

		// Find status of cell in next generation
		if (LifeSimulationGUI.colony.live(row, col))
			status = "alive";
		else
			status = "dead";
		nextGen = new JLabel("Next Gen(" + row + "," + col + "): " + status, JLabel.CENTER);
		image = new JLabel(new ImageIcon(img), JLabel.CENTER);

		// Set layout and add to it
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(150, 200));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		add(liveLabel, c);
		c.gridy = 1;
		add(deadLabel, c);
		c.gridy = 2;
		add(currLabel, c);
		c.gridy = 3;
		add(image, c);
		c.gridy = 4;
		add(nextGen, c);
	}

	public void updateInfo(int row, int col) { // Goes thru constructor again
												// but just updates the fields
		focusedGrid = new int[3][3];
		boolean toIgnore[] = LifeSimulationGUI.colony.getToIgnore(row, col);
		for (int i = 0; i < toIgnore.length; i++) {
			if (toIgnore[i] && i != 4)
				focusedGrid[1 + (i / 3 - 1)][1 + (i % 3 - 1)] = 2;
			else if (LifeSimulationGUI.colony.atIndex(row + (i / 3 - 1), col + (i % 3 - 1)))
				focusedGrid[1 + (i / 3 - 1)][1 + (i % 3 - 1)] = 1;
			else
				focusedGrid[1 + (i / 3 - 1)][1 + (i % 3 - 1)] = 0;
		}
		generateImage();

		liveLabel.setText("Living: " + living);
		deadLabel.setText("Dead: " + dead);
		String status;
		if (LifeSimulationGUI.colony.atIndex(row, col))
			status = "alive";
		else
			status = "dead";
		currLabel.setText("Current(" + row + "," + col + "): " + status);
		if (LifeSimulationGUI.colony.live(row, col))
			status = "alive";
		else
			status = "dead";
		nextGen.setText("Next Gen(" + row + "," + col + "): " + status);
		image.setIcon(new ImageIcon(img));
	}

	public void generateImage() { // Generates an image of the current cell
		boolean light = true;
		BufferedImage img = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		g2.setColor(Color.white);
		g2.fillRect(0, 0, 120, 120);
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (row == 1 && col == 1)
					light = false;
				if (focusedGrid[row][col] == 0) {
					g2.setColor(Color.white);
					g2.fillRect(col * 32 + 12, row * 32 + 12, 32, 32);
				} else if (focusedGrid[row][col] == 1) {
					if (light)
						g2.setColor(Color.gray);
					else
						g2.setColor(Color.black);
					g2.fillRect(col * 32 + 12, row * 32 + 12, 32, 32);
					g2.setColor(Color.white);
					g2.drawRect(col * 32 + 12, row * 32 + 12, 32, 32);
				} else if (focusedGrid[row][col] == 2) {
					g2.setColor(new Color(1f, 0, 0, .5f));
					g2.fillRect(col * 32 + 12, row * 32 + 12, 32, 32);
					g2.setColor(Color.white);
					g2.drawRect(col * 32 + 12, row * 32 + 12, 32, 32);
				}
				light = true;
			}
		}
		g2.drawImage(LifeSimulationGUI.selected, (120 - 36) / 2, (120 - 36) / 2, this);
		this.img = img;
		g2.dispose();
	}

}
