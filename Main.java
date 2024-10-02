package test;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.*;
import java.util.*;

public class Main {
	public static double camX = 250;
	public static double camY = 250;
	public static double camZ = 0;
	public static double zSensitivity = 0.01;
	
	public static void main(String[] args) {
		Frame frame = new Frame();
		Panel panel = new Panel();
		frame.add(panel);
		
		Point a = new Point(130, 130, 100);
		Point b = new Point(250, 100, 50);
		Point c = new Point(130, 220, 100);
		
		Point d = new Point(100, 100, 0, Color.red);
		Point e = new Point(200, 100, 0, Color.red);
		Point f = new Point(100, 200, 0, Color.red);
		
		ArrayList<Point> tri1 = triArray(a, b, c);
		ArrayList<Point> tri2 = triArray(d, e, f);
		
		Object3D obj1 = new Object3D(tri1);
		Object3D obj2 = new Object3D(tri2);
		
		panel.objects.add(obj1);
		panel.objects.add(obj2);
		
		
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				a.z += -0.1;
				b.z += -0.1;
				c.z += -0.1;
			
				panel.objects.get(0).points = triArray(a, b, c);
				panel.objects.get(1).points = triArray(d, e, f);
				frame.repaint();
			}
		}, 0, 16);
	}
	
	public static ArrayList<Point> lineArray(Point a, Point b) {
		ArrayList<Point> result = new ArrayList<Point>();
		
		Point a2D = a.xy();
		Point b2D = b.xy();
		
		int numPoints = (int) (a2D.dist(b2D) + 0.5); //using a2D and b2D in order to not find more points than can be drawn
		
		
		for (int i = 0; i < numPoints; i++) {
			//The percent, as a decimal, to lerp from the first point to the last point
			double p = (double) i / numPoints; //Either i or numPoints (or both) must be casted to a double so that i / numPoints will not be evaluated as 0
			
			result.add(new Point(a.x + (b.x - a.x) * p, a.y + (b.y - a.y) * p, a.z + (b.z - a.z) * p, a.color));
		}
		
		result.add(b);
		
		return result;
	}
	
	
	public static ArrayList<Point> triArray(Point a, Point b, Point c) {
		ArrayList<Point> result = new ArrayList<Point>();
		
		Point a2D = a.xy();
		Point b2D = b.xy();
		Point c2D = c.xy();
		
		int numLines = 0;
		Point hyp = a;
		
		
		
		if (a2D.dist(b2D) > numLines) {
			numLines = (int) (a2D.dist(b2D) + 0.5);
			hyp = a;
		}
		
		if (b2D.dist(c2D) > numLines) {
			numLines = (int) (a2D.dist(b2D) + 0.5);
			hyp = b;
		}
		
		if (c2D.dist(a2D) > numLines) {
			numLines = (int) (a2D.dist(b2D) + 0.5);
			hyp = c;
		}
		
		
		ArrayList<Point> opp = new ArrayList<Point>();
		if (hyp == a) opp = lineArray(b, c);
		if (hyp == b) opp = lineArray(a, c);
		if (hyp == c) opp = lineArray(a, b);
		
		for (Point p : opp) {
			ArrayList<Point> fillLine = lineArray(hyp, p);
			for (Point fillPoint : fillLine) {
				result.add(fillPoint);
			}
		}
		
		
		return result;
	}
	
}

class Point {
	public double x;
	public double y;
	public double z;
	public Color color;
	
	public static double camX = 250;
	public static double camY = 250;
	public static double camZ = 0;
	public static double zSensitivity = 0.01;
	
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = Color.black;
	}
	
	public Point(double x, double y, double z, Color color) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;
	}
	
	public double dist(Point p) {
		return Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
	}
	
	public Point xy() {
		double rx = camX;
		double ry = camY;
		
		
		double p = Math.exp(-z * zSensitivity);
		
		rx += (x - camX) * p;
		ry += (y - camY) * p;
		
		Point result = new Point(rx, ry, 0);
		
		return result;
	}
	
	public Point xyInt() {
		double rx = camX;
		double ry = camY;
		
		
		double p = Math.exp(-z * zSensitivity);
		
		rx += (x - camX) * p;
		ry += (y - camY) * p;
		
		Point result = new Point((int) (rx + 0.5), (int) (ry + 0.5), 0);
		
		return result;
	}
	
	public String toString() {
		return "" + x + ", " + y + ", " + z;
	}
}


class PointComparator implements Comparator<Point> {
    public int compare(Point a, Point b) {
		if (a.z < b.z) return 1;
		if (a.z > b.z) return -1;
        return 0;
    }
}







class Frame extends JFrame {
	public Frame() {
		initializeWindow();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paintComponents(g);
	}
	
	public void initializeWindow() {
		this.setTitle("Window");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(new Dimension(500, 500));
		this.setLocationRelativeTo(null);
		
	}
}

class Panel extends JPanel {
	public ArrayList<Object3D> objects;
	
	public Panel() {
		objects = new ArrayList<Object3D>();
		initializePanel();
	}
	
	public Panel(ArrayList<Object3D> objects) {
		this.objects = objects;
		initializePanel();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		
		ArrayList<Point> allPoints = new ArrayList<Point>();
		
		for (Object3D obj : objects) {
			for (Point p : obj.points) {
				allPoints.add(p);
			}
		}
		
		//Sorting points by z-value in descending order so objects further away are drawn first, then drawn over by closer objects
		Collections.sort(allPoints, new PointComparator());
		
		for (Point point : allPoints) {
			Point p = point.xy();
			g2D.setColor(point.color);
			g2D.drawOval((int) (p.x + 0.5), (int) (p.y + 0.5), 1, 1); //Adding 0.5 to round the values to the nearest whole number, in order to prevent holes due to integer conversion
		}
		
	}
	
	
	public void initializePanel() {
		this.setVisible(true);
		this.setOpaque(false);
		this.setBounds(new Rectangle(1920, 1080));
	}
}

class Object3D {
	public ArrayList<Point> points;
	
	public Object3D() {
		points = new ArrayList<Point>();
	}
	
	public Object3D(ArrayList<Point> points) {
		this.points = points;
	}
}
