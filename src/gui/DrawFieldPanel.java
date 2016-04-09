package gui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class DrawFieldPanel extends JPanel {
	
	int scale = 1;
	int border = 15;
	
	Rectangle2D field = new Rectangle2D.Double(0 * scale + border, 0 * scale + border, 650 * scale + border, 320 * scale + border);
	Rectangle2D redSpyBox = new Rectangle2D.Double(0 * scale + border, 0 * scale + border, 287 * scale + border, 34 * scale + border);
	Rectangle2D blueSpyBox = new Rectangle2D.Double(650 * scale + border, 320 * scale + border, 363 * scale + border, 266 * scale + border);
	Line2D middleLine = new Line2D.Double(325 * scale + border, 0 * scale + border, 325 * scale + border, 320 * scale + 2 * border);
	Line2D redAutoLine = new Line2D.Double(313 * scale + border, 0 * scale + border, 313 * scale + border, 320 * scale + 2 * border);
	Line2D blueAutoLine = new Line2D.Double(337 * scale + border, 0 * scale + border, 337 * scale + border, 320 * scale + 2 * border);
	
	@Override
	public void paintComponent(Graphics g){
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.draw(field);
		g2.draw(middleLine);
		g2.draw(redAutoLine);
		g2.draw(blueAutoLine);
		g2.draw(redSpyBox);
		g2.draw(blueSpyBox);
		
	}
	
	public DrawFieldPanel(int scale){
		
		this.scale = scale;
		this.setPreferredSize(new Dimension(650 * scale + (3 * border), 320 * scale + (3* border)));
		
		field = new Rectangle2D.Double(0 * scale + border, 0 * scale + border, 650 * scale + border, 320 * scale + border);
		middleLine = new Line2D.Double(325 * scale + border, 0 * scale + border, 325 * scale + border, 320 * scale + 2 * border);
		redAutoLine = new Line2D.Double(313 * scale + border, 0 * scale + border, 313 * scale + border, 320 * scale + 2 * border);
		blueAutoLine = new Line2D.Double(337 * scale + border, 0 * scale + border, 337 * scale + border, 320 * scale + 2 * border);
		
	}
	
}
