
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

class DrawArea extends JPanel implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int lastX;
	static int lastY;
	private int type = 0;
	private int x1, y1, x2, y2, width, height;
	private int patternx, patterny;
	private boolean isDrag = false, noPaint = true; // isDrag to check if
													// dragging, noPaint to
													// check if outside bounds
	private Color red = new Color(255, 0, 0, 100);
	private Color green = new Color(0, 255, 0, 100);
	private Pattern p = PatternPane.currentPattern();
	private Rectangle r;

	public DrawArea(int width, int height) { // Initialize
		this.setPreferredSize(new Dimension(width, height)); // size
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.width = width;
		this.height = height;
	}

	public void paintComponent(Graphics g) { // Paint component method
		LifeSimulationGUI.colony.show(g); // Always start by painting grid

		if (PatternPane.addBtn.getText().equals("<html><font size=1>Cancel</font></html>")) { // If
																								// user
																								// is
			// currently
			// adding
			if (!noPaint) { // if inside bounds
				// Paint green outline of pattern size
				Graphics2D g2 = (Graphics2D) g;
				int x = patternx / 5 * 5;
				int y = patterny / 5 * 5;
				r = new Rectangle(x, y, p.getWidth() * 5, p.getHeight() * 5);
				g2.drawImage(p.preview(), x, y, this);
				g2.setColor(green);
				g2.fill(r);
				g2.setColor(Color.green);
				g2.draw(r);
			}
		} else {
			if (isDrag) { // If currently being dragged
				Graphics2D g2 = (Graphics2D) g;
				// Calculate rectangle to be drawn
				fixPoints();
				int xstart = x1, ystart = y1, xend = x2 - x1, yend = y2 - y1;
				if (xend < 0)
					xstart = x1 + xend;
				if (yend < 0)
					ystart = y1 + yend;
				if (y2 == 500)
					yend--;
				if (x2 == 500)
					xend--;
				r = new Rectangle(xstart, ystart, Math.abs(xend), Math.abs(yend)); // Create
																					// rectangle
				// Depending on left or right click (populate or eradicate),
				// draw either red or green rectangle
				if (type == 0) {
					g2.setColor(green);
					g2.fill(r);
					g2.setColor(Color.green);
					g2.draw(r);
				}
				if (type == 1) {
					g2.setColor(red);
					g2.fill(r);
					g2.setColor(Color.red);
					g2.draw(r);
				}
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) { // On mouse click
		if (PatternPane.addBtn.getText().equals("<html><font size=1>Cancel</font></html>")) { // If
																								// user
																								// is
																								// currently
																								// adding
			LifeSimulationGUI.colony.replaceWithPattern(p, patternx / 5, patterny / 5); // Add
																						// pattern
			if (!PatternPane.multiple.isSelected()) // check if user wants
													// multiple
				PatternPane.addBtn.setText("Add");
		} else {
			if (e.getButton() == MouseEvent.BUTTON1) {
				LifeSimulationGUI.colony.populate(new Point(e.getX() / 5 * 5, e.getY() / 5 * 5), new Size(1, 1));
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				LifeSimulationGUI.colony.eradicate(new Point(e.getX() / 5 * 5, e.getY() / 5 * 5), new Size(1, 1));
			}
		}
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		noPaint = false; // If mouse enters the component, painting is allowed
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		noPaint = true; // If mouse exits component, stop painting square
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) { // If mouse is pressed
		if (!PatternPane.addBtn.getText().equals("<html><font size=1>Cancel</font></html>")) { // Gets
																								// type
																								// and
																								// starting
																								// coordinates
			if (e.getButton() == MouseEvent.BUTTON1)
				type = 0;
			else if (e.getButton() == MouseEvent.BUTTON3)
				type = 1;
			x1 = e.getX() / 5 * 5;
			y1 = e.getY() / 5 * 5;
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) { // When mouse is released
		if (!PatternPane.addBtn.getText().equals("<html><font size=1>Cancel</font></html>")) { // Gets
																								// last
																								// coordinates,
																								// fixes
																								// them,
																								// calls
																								// eradicate/populate
																								// method
			x2 = (e.getX()) / 5 * 5;
			y2 = (e.getY()) / 5 * 5;
			fixPoints();
			if (r != null) {
				Point point = new Point((int) r.getX(), (int) r.getY());
				int width, height;
				if (x2 == 500)
					width = (int) r.getWidth() - 5 + 1;
				else
					width = (int) r.getWidth() - 5;
				if (y2 == 500)
					height = (int) r.getHeight() - 5 + 1;
				else
					height = (int) r.getHeight() - 5;
				Size size = new Size(width, height);
				if (x1 != x2 && y1 != y2) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						LifeSimulationGUI.colony.populate(point, size);
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						LifeSimulationGUI.colony.eradicate(point, size);
					}
				}
			}
			repaint();
		}
		isDrag = false;
	}

	private void fixPoints() { // Makes sure points are within bounds
		if (x1 < 0)
			x1 = 0;
		else if (x1 >= width)
			x1 = width;
		if (y1 < 0)
			y1 = 0;
		else if (y1 >= height)
			y1 = height;
		if (x2 < 0)
			x2 = 0;
		else if (x2 >= width)
			x2 = width;
		if (y2 < 0)
			y2 = 0;
		else if (y2 >= height)
			y2 = height;
	}

	@Override
	public void mouseDragged(MouseEvent e) { // When mouse is dragged
		if (PatternPane.addBtn.getText().equals("<html><font size=1>Cancel</font></html>")) {
			mouseMoved(e); // If currently adding, calls the mousemoved event
							// which handles getting coordinates for the pattern
		}
		// Updates second coordinates
		x2 = (e.getX()) / 5 * 5;
		y2 = (e.getY()) / 5 * 5;
		isDrag = true;

		lastX = e.getX(); // Last x and y for info panel
		lastY = e.getY();
		if (lastX < 0)
			lastX = 0;
		else if (lastX >= width)
			lastX = width - 1;
		if (lastY < 0)
			lastY = 0;
		else if (lastY >= height)
			lastY = height - 1;
		if (LifeSimulationGUI.infoEnabled) // Updates info
			LifeSimulationGUI.info.updateInfo(lastY / 5, lastX / 5);
		else
			LifeSimulationGUI.info.setEnabled(false);
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) { // When mouse is moved
		p = PatternPane.currentPattern();
		if (PatternPane.addBtn.getText().equals("<html><font size=1>Cancel</font></html>")) { // If
																								// currently
																								// adding
			// Get values for drawing shape
			int x = e.getX();
			int y = e.getY();
			Pattern p = PatternPane.currentPattern();
			patternx = x - (p.getWidth() * 5) / 2;
			patterny = y - (p.getHeight() * 5) / 2;
			if (x - (p.getWidth() * 5) / 2 < 0)
				patternx = 0;
			else if (x + (p.getWidth() * 5) / 2 >= width)
				patternx = width - p.getWidth() * 5;
			if (y - (p.getHeight() * 5) / 2 < 0)
				patterny = 0;
			else if (y + (p.getHeight() * 5) / 2 >= height)
				patterny = height - p.getHeight() * 5;
		}
		lastX = e.getX();
		lastY = e.getY();
		if (LifeSimulationGUI.infoEnabled) // Update info
			LifeSimulationGUI.info.updateInfo(lastY / 5, lastX / 5);
		else
			LifeSimulationGUI.info.setEnabled(false);
		isDrag = false;
		repaint();
	}
}