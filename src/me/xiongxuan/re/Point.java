package me.xiongxuan.re;

public class Point {
	private double x;
	private double y;
	
	public Point() {}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getDistance(Point point) {
		return Math.abs(Math.sqrt((x - point.getX()) * (x - point.getX()) + (y - point.getY()) * (y - point.getY())));
	}

	public double getASin(Point point) {
		return Math.asin((point.getY() - y) / getDistance(point));
	}
	
	public double getACos(Point point) {
		return Math.acos((point.getX() - x) / getDistance(point));
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public String toString() {
		return "x:" + x + "\n" + "y:" + y;
	}
}
