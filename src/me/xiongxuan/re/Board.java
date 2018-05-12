package me.xiongxuan.re;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.shape.Rectangle;

public class Board {
	private int width, heigth; //面板的高和宽，也就是随机生成跳板的范围
	private final int recHeight = 5;
	
	
	private Random random = new Random();
	
	private ArrayList<Rectangle> recs = new ArrayList<Rectangle>();
	
	public Board(int width, int heigth) {
		this.width = width;
		this.heigth = heigth;
		
		initRecs();
	}
	
	/**
	 * 初始化五个长方形
	 */
	public void initRecs() {
		for (int i = 1; i < 5; i++)
			recs.add(getARectangle(i));
	}
	
	/**
	 * 返回跳板的集合
	 * @return 返回为一个ArrayList的长方形集合
	 */
	public ArrayList<Rectangle> getRecs() {
		return recs;
	}
	
	/**
	 * 创建一个跳板
	 * @param 输入为木板的位置，1为最下面，4为最上面
	 * @return 返回为一个Rectangle类
	 */
	public Rectangle getARectangle(int i) {
		Rectangle rectangles = new Rectangle();
		rectangles.setFill(javafx.scene.paint.Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		
		rectangles.setWidth(random.nextInt(width / 3 - width / 4 + 1) + width / 4 + 1);
		rectangles.setHeight(recHeight);
		rectangles.setX(random.nextInt((int) (width - rectangles.getWidth())));
		rectangles.setY(heigth - i * heigth / 4);
		
		return rectangles;
	}
	
	/**
	 * 在最顶部创建一个跳板
	 */
	public Rectangle getARectangle() {
		Rectangle rectangles = new Rectangle();
		rectangles.setFill(javafx.scene.paint.Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		
		rectangles.setWidth(random.nextInt(width / 3 - width / 4 + 1) + width / 4 + 1);
		rectangles.setHeight(recHeight);
		rectangles.setX(random.nextInt((int) (width - rectangles.getWidth())));
		rectangles.setY(heigth - 4 * heigth / 4);
		
		return rectangles;
	}
	
	/**
	 * 判断ArrayList里所有的长方形和圆形是否相碰
	 * @param x,y,r 圆形的圆心坐标和半径
	 * @return
	 */
	public boolean isImpactAll(double x, double y, int r) {
		for (int i = 0; i < 4; i++) {
			if (isImpact(x, y, r, recs.get(i)))
				return true;
		}
		return false;
	}
	
	/**
	 * 判断一个长方形和圆形是否相碰
	 * @param x,y,r 圆形的圆心坐标和半径
	 * @param rectangle 长方形类
	 * @return
	 */
	public boolean isImpact(double x, double y, int r, Rectangle rectangle) {
		//把长方形的上下左右都延长r的长度，判断圆心是否在新的长方形内，若不在，则没碰撞
		if (rectangle.getX() - r < x &&
				rectangle.getX() + rectangle.getWidth() + r > x &&
				rectangle.getY() - r < y &&
				rectangle.getY() + rectangle.getHeight() + r > y) {
			//判断圆心是否在新长方形四个角的r * r正方形内，若不在，则碰撞
			if (rectangle.getX() - r < x && rectangle.getX() > x && rectangle.getY() - r < y && rectangle.getY() > y ||
					rectangle.getX() - r < x && rectangle.getX() > x && rectangle.getY() + rectangle.getHeight() < y && rectangle.getY() + rectangle.getHeight() + r > y ||
					rectangle.getX() + rectangle.getWidth() < x && rectangle.getX() + rectangle.getWidth() + r > x && rectangle.getY() - r < y && rectangle.getY() > y ||
					rectangle.getX() + rectangle.getWidth() < x && rectangle.getX() + rectangle.getWidth() + r > x && rectangle.getY() + rectangle.getHeight() < y && rectangle.getY() + rectangle.getHeight() + r > y) {
				//判断圆心和原长方形四个顶点的距离，若有一个距离小于半径r，则碰撞
				if (getDistance(rectangle.getX(), rectangle.getY(), x, y) < r ||
						getDistance(rectangle.getX() + rectangle.getWidth(), rectangle.getY(), x, y) < r ||
						getDistance(rectangle.getX(), rectangle.getY() + rectangle.getHeight(), x, y) < r ||
						getDistance(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight(), x, y) < r) {
					return true;
				}
				else
					return false;
			}
			else
				return true;
		}
		else
			return false;
	}
	
	/**
	 * 计算两点间的距离
	 * @param x 
	 * @param y 
	 * @param x2 
	 * @param y2
	 * @return
	 */
	public double getDistance(double x, double y, double x2, double y2) {
		return Math.sqrt(Math.abs(x-x2)*Math.abs(x-x2)+Math.abs(y-y2)*Math.abs(y-y2));
	}
	
	public void pullDownAll(double v) {
		for (int i = 0; i < 4; i++)
			pullDown(i, v);
	}
	
	public void pullDown(int index, double v) {
		recs.get(index).setY(recs.get(index).getY() + v);
	}
	
	public boolean isBottomOutBorder() {
		if (recs.get(0).getY() > BallGame.HEIGHT)
			return true;
		return false;
	}
	
	public void removeBottomRec() {
		recs.remove(0);
	}
	
	public void removeAllRec() {
		recs.clear();
	}
	
	public Rectangle getTopRectangle() {
		return recs.get(3);
	}
	
	public Rectangle getBottomRectangle() {
		return recs.get(0);
	}
}
