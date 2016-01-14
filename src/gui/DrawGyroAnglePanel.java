package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class DrawGyroAnglePanel extends JPanel implements ComponentListener {
	
	private static final long serialVersionUID = 4609206725470048367L;
	private int width;
	private int height;
	
	private int angleOffset = 0;
	
	private int circleX;
	private int circleY;
	private int circleWidth;
	private Point2D circleCenter;
	
	private int distanceFromCircle;
	private int[] triangleX = new int[3];
	private int[] triangleY = new int[3];
	private int currentDirection = 0;

	private Shape triangle = null;
	
	public DrawGyroAnglePanel(){
		
		this.addComponentListener(this);
		setupVariables();
		
		this.setPreferredSize(new Dimension(200, 500));
		
	}
	
	@Override
	public void paintComponent(Graphics g){
		
		Graphics2D g2 = (Graphics2D) g;
		
		Rectangle2D background = new Rectangle2D.Double(0, 0, width, height);
		Ellipse2D circle = new Ellipse2D.Double(circleX, circleY, circleWidth, circleWidth);
		
		g2.setColor(Color.WHITE);
		g2.fill(background);
		g2.setColor(Color.GRAY);
		g2.fill(circle);
		
		if(triangle != null){
			
			g2.setColor(Color.GREEN);
			g2.fill(triangle);
			
		}
		
		
	}
	
	private Shape getRotatedTriangle(int rotateDegrees){
		
		Polygon upTriangle = new Polygon(triangleX, triangleY, 3);
		AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(rotateDegrees + angleOffset), circleCenter.getX(), circleCenter.getY());
		
		return rotate.createTransformedShape(upTriangle);
		
	}
	
	private void setupVariables(){
		
		width = this.getWidth();
		height = this.getHeight();
		
		int tmp = getSmallerValue(width, height);
		
		circleWidth = ((int) tmp / 5);
		circleX = ((int) ((width / 2) - (.5 * circleWidth)));
		circleY = ((int) ((height / 2) - (.5 * circleWidth)));
		circleCenter = new Point2D.Double((circleX + (circleWidth / 2)), (circleY + (circleWidth / 2)));
		distanceFromCircle = ((int) tmp / 25);
		
		triangleX[0] = (int) circleCenter.getX();
		triangleY[0] = (int) (circleCenter.getY() - (.5 * circleWidth) - distanceFromCircle - (.5 * circleWidth));
		
		triangleX[1] = (int) (circleCenter.getX() - (.5 * (.5 * circleWidth)));
		triangleY[1] = (int) (circleCenter.getY() - (.5 * circleWidth) - distanceFromCircle);
		
		triangleX[2] = (int) (circleCenter.getX() + (.5 * (.5 * circleWidth)));
		triangleY[2] = (int) (circleCenter.getY() - (.5 * circleWidth) - distanceFromCircle);
		
	}
	
	private int getSmallerValue(int one, int two){
		
		if(one < two){
			
			return one;
			
		}else{
			
			return two;
			
		}
		
		//did not add an equal to because it doesn't matter which is returned;
		
	}
	
	public void setAngle(int direction){
		
		currentDirection = direction;
		
		if(direction <= 360){
			
			triangle = getRotatedTriangle(direction);
			repaint();
		
		}else{
			
			triangle = null;
			repaint();
			
		}
		
	}
	
	public int getAngle(){
		
		return currentDirection;
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		
		setupVariables();
		setAngle(currentDirection);
		this.repaint();
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}
	
	public void setOffset(int offset){
		
		angleOffset = Math.abs(offset);
		
	}


}
