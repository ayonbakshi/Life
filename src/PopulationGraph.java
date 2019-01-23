import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class PopulationGraph extends BufferedImage {

	private Graphics2D g;
	private int genStart = 0;
	private int genEnd = 100;
	private int yStart = 0;
	private int yEnd = 2000;
	private int range = 100;
	private boolean followEnd = false;
	private AffineTransform affineTransform = new AffineTransform();

	public PopulationGraph() {
		super(240, 240, BufferedImage.TYPE_INT_ARGB);

		g = this.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 240, 240);
		g.setColor(Color.black);
		g.drawLine(19, 20, 19, 221);
		g.drawLine(19, 221, 220, 221);
		affineTransform.rotate(-Math.PI / 2);
		g.setFont(g.getFont().deriveFont(affineTransform));
		g.drawString("Population", 15, 150);
		affineTransform.rotate(Math.PI / 2);
		g.setFont(g.getFont().deriveFont(affineTransform));
		g.drawString("Generation", 90, 235);

	}

	public void updateGraph() {
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.white);
		g.fillRect(0, 0, 240, 240);
		g.setColor(Color.black);
		g.drawLine(19, 20, 19, 221);
		g.drawLine(19, 221, 220, 221);
		affineTransform.rotate(-Math.PI / 2);
		g.setFont(g.getFont().deriveFont(affineTransform));
		g.drawString("Population", 15, 150);
		affineTransform.rotate(Math.PI / 2);
		g.setFont(g.getFont().deriveFont(affineTransform));
		g.drawString("Generation", 90, 235);
		

		double perY = 200.0 / (yEnd - yStart);

		if (!followEnd) {
			if (LifeSimulationGUI.population.size() > 1)
				for (int i = genStart; i < genEnd - 1; i++) {
					if (i < LifeSimulationGUI.population.size() - 1) {
						g.setColor(Color.RED);
						int x1 = (int) (getWidth() / (genEnd - genStart)) * (i - genStart) + 20;
						int y1 = getHeight() - 1 - 20 - (int) ((LifeSimulationGUI.population.get(i) - yStart) * perY);
						int x2 = (int) (getWidth() / (genEnd - genStart) * (i + 1 - genStart)) + 20;
						int y2 = getHeight() - 1 - 20
								- (int) ((LifeSimulationGUI.population.get(i + 1) - yStart) * perY);
						if (x1 <= 220 && x2 <= 220) {
							g.setStroke(new BasicStroke(2));
							g.setColor(Color.RED);
							if (y1 < 20 && y2 >= 20 && y2 <= 220)
								g.drawLine(x1, 20, x2, y2);
							else if (y1 > 220 && y2 <= 220 && y2 >= 20)
								g.drawLine(x1, 220, x2, y2);
							else if (y2 < 20 && y1 >= 20 && y1 <= 220)
								g.drawLine(x1, y1, x2, 20);
							else if (y2 > 220 && y1 <= 220 && y1 >= 20)
								g.drawLine(x1, y1, x2, 220);
							else if(y1 <= 220 && y2 >= 20 && y1 >= 20 && y2<= 220)
								g.drawLine(x1, y1, x2, y2);
						}
					}
				}
		} else {
			int rangeStart;
			if (LifeSimulationGUI.population.size() > range)
				rangeStart = LifeSimulationGUI.population.size() - range;
			else
				rangeStart = 0;
			for (int i = 0; i + rangeStart < LifeSimulationGUI.population.size() - 1; i++) {

				int x1 = (getWidth() / (range)) * i + 20;
				int y1 = getWidth() - 1 - 20
						- (int) ((LifeSimulationGUI.population.get(i + rangeStart) - yStart) * perY);
				int x2 = (getWidth() / (range)) * (i + 1) + 20;
				int y2 = getWidth() - 1 - 20
						- (int) ((LifeSimulationGUI.population.get(i + rangeStart + 1) - yStart) * perY);

				if (x1 <= 220 && x2 <= 220) {
					g.setStroke(new BasicStroke(2));
					g.setColor(Color.RED);
					if (y1 < 20 && y2 >= 20 && y2 <= 220)
						g.drawLine(x1, 20, x2, y2);
					else if (y1 > 220 && y2 <= 220 && y2 >= 20)
						g.drawLine(x1, 220, x2, y2);
					else if (y2 < 20 && y1 >= 20 && y1 <= 220)
						g.drawLine(x1, y1, x2, 20);
					else if (y2 > 220 && y1 <= 220 && y1 >= 20)
						g.drawLine(x1, y1, x2, 220);
					else if(y1 <= 220 && y2 >= 20 && y1 >= 20 && y2<= 220)
						g.drawLine(x1, y1, x2, y2);
				}
			}
		}
	}

	public void setGenStart(int genStart) {
		this.genStart = genStart;
	}

	public void setGenEnd(int genEnd) {
		this.genEnd = genEnd;
	}

	public void setYStart(int yStart) {
		this.yStart = yStart;
	}

	public void setYEnd(int yEnd) {
		this.yEnd = yEnd;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public void setFollow(boolean followEnd) {
		this.followEnd = followEnd;
	}
}
