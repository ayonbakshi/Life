import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Colony { // Colony class
	private boolean grid[][]; // grid

	public Colony(double density) { // Create a colony based on density
		grid = new boolean[100][100];
		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++)
				grid[row][col] = Math.random() < density;
	}

	public Colony(String filename) { // Create a colony based
										// on file
		int living = 0;
		int dead = 0;
		grid = new boolean[100][100];
		FileReader file = null;
		BufferedReader br = null;
		try {
			file = new FileReader( // Read from file
					getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/saves/" + filename);
			br = new BufferedReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < 100; i++) {
			char[] st = new char[100];
			try {
				st = br.readLine().trim().replaceAll(" ", "").toCharArray();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int j = 0; j < 100; j++) {
				if (st[j] == '1') {
					grid[i][j] = true;
					living++;
				} else
					dead++;
			}
		}
		InfoPane.living = living;
		InfoPane.dead = dead;
		if (LifeSimulationGUI.infoEnabled) // Update info panel
			LifeSimulationGUI.info.updateInfo(DrawArea.lastY / 5, DrawArea.lastX / 5);
		else
			LifeSimulationGUI.info.setEnabled(false);
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save(String filename) throws FileNotFoundException, UnsupportedEncodingException { // Save
																									// current
																									// colony
																									// as
																									// "filename"

		PrintWriter writer = new PrintWriter(
				getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "saves" + "/" + filename,
				"UTF-8");

		for (int i = 0; i < grid.length; i++) {
			String s = "";
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j])
					s += "1 ";
				else
					s += "0 ";
			}
			writer.println(s);
		}
		writer.close();
		// Saved
	}

	public void show(Graphics g) { // Show the colony
		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++) {
				if (grid[row][col]) // life
					g.setColor(Color.black);
				else
					g.setColor(Color.white);
				g.fillRect(col * 5, row * 5, 5, 5); // draw life form, 5x5
													// pixels
				if (LifeSimulationGUI.outlineEnabled) {
					g.setColor(Color.white);
					g.drawRect(col * 5, row * 5, 5, 5);
				}
			}
	}

	public boolean[] getToIgnore(int row, int col) { // Cells to ignore when
														// calculating next gen
		boolean toIgnore[] = new boolean[9]; // i/3 i%3 0 1 2
		// 3 4 5
		// 6 7 8
		toIgnore[4] = true;
		if (row == 0) {
			toIgnore[0] = true;
			toIgnore[1] = true;
			toIgnore[2] = true;
		}
		if (row == grid.length - 1) {
			toIgnore[6] = true;
			toIgnore[7] = true;
			toIgnore[8] = true;
		}
		if (col == 0) {
			toIgnore[0] = true;
			toIgnore[3] = true;
			toIgnore[6] = true;
		}
		if (col == grid[0].length - 1) {
			toIgnore[2] = true;
			toIgnore[5] = true;
			toIgnore[8] = true;
		}
		return toIgnore;
	}

	public boolean live(int row, int col) { // Check if cell will live next
											// generation
		int cellCounter = 0;
		boolean toIgnore[] = getToIgnore(row, col);

		// Calculation # of neighbor cells
		for (int i = 0; i < toIgnore.length; i++) { //
			if (!toIgnore[i]) {
				if (grid[row + (i / 3 - 1)][col + (i % 3 - 1)])
					cellCounter++;
			}
		}

		// Return state of cell in next generation
		int[] s = LifeSimulationGUI.rule.s;
		int[] b = LifeSimulationGUI.rule.b;
		if (grid[row][col]) {
			if (s == null)
				return false;
			for (int i = 0; i < s.length; i++)
				if (cellCounter == s[i])
					return true;
			return false;
		} else {
			if (b == null)
				return false;
			for (int i = 0; i < b.length; i++)
				if (cellCounter == b[i])
					return true;
			return false;
		}
	}

	public void populate(Point start, Size size) { // Populate method, accepts a
													// point and size
		// Modifying the rectangle to keep within bounds
		int startModx = 0;
		int startMody = 0;
		if (size.getWidth() < 0)
			startModx = size.getWidth();
		if (size.getHeight() < 0)
			startMody = size.getHeight();
		int row1 = (start.getY() + startMody) / 5;
		int col1 = (start.getX() + startModx) / 5;
		int row2 = row1 + Math.abs(size.getHeight() / 5);
		int col2 = col1 + Math.abs(size.getWidth() / 5);
		for (int y = row1; y <= row2; y++)
			for (int x = col1; x <= col2; x++)
				if (!grid[y][x]) { // All dead cells have a x% of spawning
					boolean status = Math.random() < (double) (LifeSimulationGUI.populateSldr.getValue()) / 100.0;
					grid[y][x] = status; // remove based on slider value
					if (status) {
						InfoPane.dead--;
						InfoPane.living++;
					} // basedonslidervalue
				}
		if (LifeSimulationGUI.infoEnabled) // Update info panel
			LifeSimulationGUI.info.updateInfo(DrawArea.lastY / 5, DrawArea.lastX / 5);
		else
			LifeSimulationGUI.info.setEnabled(false);

	}

	public void eradicate(Point start, Size size) { // Eradicate method, accepts
													// a point and size
		// Modify rectangle to keep in bounds
		int startModx = 0;
		int startMody = 0;
		if (size.getWidth() < 0)
			startModx = size.getWidth();
		if (size.getHeight() < 0)
			startMody = size.getHeight();
		int row1 = (start.getY() + startMody) / 5;
		int col1 = (start.getX() + startModx) / 5;
		int row2 = row1 + Math.abs(size.getHeight() / 5);
		int col2 = col1 + Math.abs(size.getWidth() / 5);
		for (int y = row1; y <= row2; y++)
			for (int x = col1; x <= col2; x++)
				if (grid[y][x]) {
					boolean status = Math.random() > (double) (LifeSimulationGUI.eradicateSldr.getValue()) / 100.0;
					grid[y][x] = status; // remove based on slider value
					if (!status) {
						InfoPane.dead++;
						InfoPane.living--;
					}
				}
		if (LifeSimulationGUI.infoEnabled) // Update info panel
			LifeSimulationGUI.info.updateInfo(DrawArea.lastY / 5, DrawArea.lastX / 5);
		else
			LifeSimulationGUI.info.setEnabled(false);
	}

	public void advance() { // Advance
		LifeSimulationGUI.population.add(InfoPane.living);
		LifeSimulationGUI.generation++;

		LifeSimulationGUI.speed.setText("<html><div style='text-align: center;'>Speed (Generation "
				+ LifeSimulationGUI.generation + ")<br>Current: "
				+ new DecimalFormat("#.##").format(1000.0 / (410 - LifeSimulationGUI.speedSldr.getValue()))
				+ " generations/sec</div></html>");

		LifeSimulationGUI.graph.updateGraph();

		if (LifeSimulationGUI.followEnd.isSelected()) {
			int rangeStart;
			if (LifeSimulationGUI.population.size() > (int) LifeSimulationGUI.range.getValue())
				rangeStart = LifeSimulationGUI.population.size() - (int) LifeSimulationGUI.range.getValue();
			else
				rangeStart = 0;
			LifeSimulationGUI.genRange.setText("Generation range (" + rangeStart + " - "
					+ (rangeStart + (int) LifeSimulationGUI.range.getValue()) + ")");
		}

		boolean nextGen[][] = new boolean[grid.length][grid[0].length]; // create
																		// next
																		// generation
																		// of
																		// life
																		// forms
		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++) {
				boolean status = live(row, col);
				nextGen[row][col] = status; // determine life/death
											// status
				if (status && !grid[row][col]) {
					InfoPane.living++;
					InfoPane.dead--;
				} else if (!status && grid[row][col]) {
					InfoPane.dead++;
					InfoPane.living--;
				}
			}
		grid = nextGen; // update life forms
		if (LifeSimulationGUI.infoEnabled) // Update info panel
			LifeSimulationGUI.info.updateInfo(DrawArea.lastY / 5, DrawArea.lastX / 5);
		else
			LifeSimulationGUI.info.setEnabled(false);
	}

	public void replaceWithPattern(Pattern p, int x, int y) { // Replaces with
																// pattern,
																// accepts
																// pattern, x
																// and y
		try { // Try to replace
			for (int row = y; row < y + p.getHeight() && row < grid.length; row++)
				for (int col = x; col < x + p.getWidth() && col < grid[0].length; col++) {
					if (grid[row][col]) {
						InfoPane.living--;
						InfoPane.dead++;
					}
					grid[row][col] = p.atIndex(row - y, col - x);
				}
			InfoPane.living += p.getLiving();
			InfoPane.dead -= p.getLiving();
			if (LifeSimulationGUI.infoEnabled) // Update info panel
				LifeSimulationGUI.info.updateInfo(DrawArea.lastY / 5, DrawArea.lastX / 5);
			else
				LifeSimulationGUI.info.setEnabled(false);
		} catch (ArrayIndexOutOfBoundsException e) { // If it goes out of
														// bounds, print error
														// message
			System.out.println("Too large!");
		}
	}

	public boolean atIndex(int row, int col) { // Returns a value at an index of
												// grid
		return grid[row][col];
	}
}