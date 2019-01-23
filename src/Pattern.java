import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class Pattern {

	// Instance variables
	private int height;
	private int width;
	private boolean pattern[][];
	private String name;
	private int livingCells = 0;

	public Pattern(String name, String type) { // Constructor, accepts a name
												// (.txt extension) and a type

		this.name = name;

		LineNumberReader lnr = null; // Gets width and height in terms of cells
		lnr = new LineNumberReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/patterns/" + type + "/" + name)));
		try {
			width = lnr.readLine().replaceAll(" ", "").length();
			lnr.skip(Long.MAX_VALUE);
			height = lnr.getLineNumber() + 1;
			lnr.close();

			pattern = new boolean[height][width];
			BufferedReader br = new BufferedReader(
					new InputStreamReader(this.getClass().getResourceAsStream("/patterns/" + type + "/" + name)));
			// Puts pattern file into boolean array and sets living cells
			for (int i = 0; i < height; i++) {
				char[] st = new char[width];
				st = br.readLine().trim().replaceAll(" ", "").toCharArray();
				for (int j = 0; j < width; j++) {
					pattern[i][j] = st[j] == '1';
					if (pattern[i][j])
						livingCells++;
				}
			}
			br.close();
		} catch (NullPointerException e) {
			// I dont know why this exception occurs but the patterns are read
			// in corretcly
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (width == 0 || height == 0) { // If null pattern, set to a 1x1
			pattern = new boolean[1][1];
			pattern[0][0] = false;
			width = 1;
			height = 1;
		}
	}

	// Accessors
	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public String getName() {
		return name;
	}

	public int getLiving() {
		return livingCells;
	}

	public boolean atIndex(int row, int col) {
		return pattern[row][col];
	}

	public Image toImage(int size) { // Converts pattern to an image displayed a
										// square of size "size"
		// Calculate cell size, width and height of image
		int w, h, cellSize;
		if (width > height) {
			cellSize = size / width;
			w = cellSize * width;
			h = cellSize * height;
		} else {
			cellSize = size / height;
			w = cellSize * width;
			h = cellSize * height;
		}

		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB); // Create
																						// new
																						// square
																						// image
		Graphics2D g2 = img.createGraphics(); // Get graphics object
		// If cellsize is 0, display red to show that image is too large to be
		// displayed
		if (cellSize == 0) {
			g2.setColor(Color.red);
			g2.fillRect(0, 0, size, size);
			g2.drawString("Pattern is too large", 40, 40);
			return img;
		} // If cell size is greater than 1
			// Draw the image and return it
		int x = (size - w) / 2;
		int y = (size - h) / 2;
		g2.setColor(Color.white);
		g2.fillRect(0, 0, size, size);
		for (int row = 0; row < pattern.length; row++) {
			for (int col = 0; col < pattern[0].length; col++) {
				if (pattern[row][col]) {
					g2.setColor(Color.black);
					g2.fillRect(col * cellSize + x, row * cellSize + y, cellSize, cellSize);
					if (cellSize > 3) {
						g2.setColor(Color.white);
						g2.drawRect(col * cellSize + x, row * cellSize + y, cellSize, cellSize);
					}
				}
			}
		}
		return img;
	}
	
	public Image preview(){
		BufferedImage img = new BufferedImage(width*5, height*5, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		for (int row = 0; row < pattern.length; row++) {
			for (int col = 0; col < pattern[0].length; col++) {
				if (pattern[row][col]) // life
					g2.setColor(Color.black);
				else
					g2.setColor(Color.white);
				g2.fillRect(col * 5, row * 5, 5, 5); // draw life form, 5x5
													// pixels
				if (LifeSimulationGUI.outlineEnabled) {
					g2.setColor(Color.white);
					g2.drawRect(col * 5, row * 5, 5, 5);
				}
			}
		}
		return img;
	}
}
