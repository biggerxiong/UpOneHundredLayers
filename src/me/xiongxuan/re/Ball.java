package me.xiongxuan.re;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Ball {
	private ImageView imageView = new ImageView("balls.png"); //小球的图片
	private Timeline moveRight, moveLeft; //控制小球左右移动的timeline

	private final int maxCount = 110; //用来控制上升的高度
	private int count = maxCount; //用来记录小球此时上升的高度	
	private int direction = 1; //小球的方向，-1为向下，1为向上
	private final int RADIOUS = 13; //小球的半径
	private double YMoveStep = 1.1, XMoveStep = 0.75; //小球上下或左右移动一步的距离
	private double xMoveSpeed = 6; //小球左右移动的速度，越小越快
	
	public Ball(int paneWidth, int paneHeight) {
		imageView.setSmooth(true);
		
		imageView.setFitHeight(RADIOUS * 2);
		imageView.setFitWidth(RADIOUS * 2);
		
		//向左移动的动画
		moveRight = new Timeline(new KeyFrame(Duration.millis(xMoveSpeed), e -> {
			if (imageView.getX() + imageView.getFitWidth() + XMoveStep > paneWidth) {
				imageView.setX(paneWidth - imageView.getFitWidth());
			}
			else {
				imageView.setX(imageView.getX() + XMoveStep);;
			}
		}));
		moveRight.setCycleCount(Timeline.INDEFINITE);
		//向右移动的动画
		moveLeft =  new Timeline(new KeyFrame(Duration.millis(xMoveSpeed), e -> {
			if (imageView.getX() - XMoveStep < 0) {
				imageView.setX(0);
			}
			else {
				imageView.setX(imageView.getX() - XMoveStep);
			}
		}));
		moveLeft.setCycleCount(Timeline.INDEFINITE);
	}
	
	public ImageView getImageView() {
		return imageView;
	}
	
	/**
	 * 初始化小球
	 * 把小球的所有参数设为初始
	 */
	public void initBall() {
		count = maxCount;
		direction = 1;
	}
	
	/**
	 * 使小球左右移动
	 * @param direction 为1时向右，为-1时向左
	 */
	public void XMoveBall(int direction) { //1为向右，-1为向左
		if (direction == 1) {
			moveRight.play();
		}
		else if (direction == -1) {
			moveLeft.play();
		}
	}
	
	/**
	 * 控制小球上下移动的高度
	 */
	public void moveBall() {
		if (count!= 0) {
			YMoveBall();
			count--;
		}
		else if (count == 0 && getDirection() == 1) {
			reversalYDirection();
			count = maxCount;
		}
		else 
			count--;
	}
	
	/**
	 * 使小球上下移动
	 */
	public void YMoveBall() {
		if (direction == -1) {
			imageView.setY(imageView.getY() + YMoveStep);
		}
		else {
			if (imageView.getY() > YMoveStep)
				imageView.setY(imageView.getY() - YMoveStep);
			else
				imageView.setY(0);
		}
	}
	
	/**
	 * 使小球往下移动v个单位，这个方法是由整个界面下滑调用的
	 */
	public void pullDown(double v) {
		imageView.setY(imageView.getY() + v);
	}
	
	/**
	 * 停止小球左右移动的动画
	 * @param direction 为1时向右，为-1时向左
	 */
	public void stopXMoveBall(int direction) {//1为向右，-1为向左
		if (direction == 1)
			moveRight.stop();
		else
			moveLeft.stop();
	}
	
	/**
	 * 使小球上下的方向反转
	 */
	public void reversalYDirection() {
		direction = direction == -1 ? 1 : -1; 
	}
	
	/**
	 * 重置count，使count = maxCount
	 */
	public void resetCount() {
		count = maxCount;
	}
	
	/**
	 * 判断是否越界
	 */
	public boolean isCrossBorder() {
		if (imageView.getY() + imageView.getFitHeight() > 600 || imageView.getY() < 0)
			return true;
		else {
			return false;
		}
	}
	
	/**
	 * 返回小球此时的方向
	 * @return
	 */
	public int getDirection() {
		return direction;
	}
	
	/**
	 * 返回小球当前x的坐标
	 * @return
	 */
	public double getX() {
		return imageView.getX() + RADIOUS;
	}
	
	/**
	 * 返回小球当前y的坐标
	 * @return
	 */
	public double getY() {
		return imageView.getY() + RADIOUS;
	}
	
	/**
	 * 返回小球的半径
	 */
	public int getR() {
		return RADIOUS;
	}
}
