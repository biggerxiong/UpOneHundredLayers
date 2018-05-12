package me.xiongxuan.re;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Animations {
	private static double initWidth = 0; //小球的初始宽度
	
	private Text promptText = new Text();
	private ImageView welcomeImage = new ImageView("100ceng.png");
	
	private Timeline welcomeImageDisappearAnimation; //欢迎动画消失的动画
	private Timeline ballRotateAnimation; //小球旋转的动画
	private Timeline promptTextAnimation; //提示文本的动画
	private Timeline ballRotateInitAnimation; //使小球快速转正的动画
	private Timeline scoreMoveToCenter; //展示分数的最后一个动画
	private ParallelTransition parallelTransition; //使小球变小并回到底部的合并动画
	private ParallelTransition displayScoreTransition; //展示分数的第一个动画
	private ParallelTransition stopDisplayTransition; //收起分数的第一个动画
	
	private double temp = -0.01; //提示文本动画透明度所需
	
	public Animations() {
		//初始化提示文本
		promptText.setText("按任意键开始游戏");
		promptText.setFill(Color.RED);
		promptText.setFont(Font.font(14));
		promptText.setX(100);
		promptText.setY(580);
		
		//提示文本的动画
		promptTextAnimation = new Timeline(new KeyFrame(Duration.millis(20), e -> {
			promptText.setOpacity(promptText.getOpacity() + temp);
			if (promptText.getOpacity() > 0.99 || promptText.getOpacity() < 0.3)
				temp *= -1;
		}));
		
	}
	
	public ImageView getWelcomeImage() {
		return welcomeImage;
	}
	
	/**
	 * 返回欢迎图片消失的动画事件
	 */
	public Timeline getWelcomeImageDisappearAnimation() {
		return welcomeImageDisappearAnimation;
	}
	
	/**
	 * 返回小球旋转的动画事件
	 * @return
	 */
	public Timeline getBallRotateAnimation() {
		return ballRotateAnimation;
	}

	/**
	 * 返回小球快速转正的动画事件
	 * @return
	 */
	public Timeline getBallRotateInitAnimation() {
		return ballRotateInitAnimation;
	}

	/**
	 * 返回小球变小并回到底部的合并的动画事件
	 * @return
	 */
	public ParallelTransition getParallelTransition() {
		return parallelTransition;
	}
	
	/**
	 * 返回展示分数第一个动画的事件
	 */
	public ParallelTransition getpTransition() {
		return displayScoreTransition;
	}

	/**
	 * 获得面板下方的提示文本
	 */
	public Text getPromptText() {
		promptTextAnimation.setCycleCount(Timeline.INDEFINITE);
		promptTextAnimation.play();
		
		return promptText;
	}
	
	/**
	 * 设置提示文本的内容
	 */
	public void setPromptText(String text) {
		promptText.setText(text);
	}
	
	/**
	 * 初始化小球，使小球更适合欢迎界面（把小球放大、旋转）
	 * @param ball 传入小球的imageView
	 */
	public void initBall(ImageView ball) {
		initWidth = ball.getFitWidth();
		
		ball.setX(225);
		ball.setY(300);
		
		ball.setFitHeight(initWidth * 2);
		ball.setFitWidth(initWidth * 2);
		
		ballRotateAnimation = new Timeline(new KeyFrame(Duration.millis(10), 
				e -> ball.setRotate(((int)ball.getRotate() + 1) % 360)));
		ballRotateAnimation.setCycleCount(Timeline.INDEFINITE);
		ballRotateAnimation.play();
	}
	
	/**
	 * 在1s内将小球转正
	 * @param ball
	 */
	public void correctBallRotate(ImageView ball) {
		ballRotateAnimation.stop();
		
		if (ball.getRotate() != 0) { //判断小球是否是正的，如果不是正的，就快速转正
			double circleTimes = 360 - ball.getRotate();
			ballRotateInitAnimation = new Timeline(new KeyFrame(Duration.millis(1000 / circleTimes), 
					e -> ball.setRotate(ball.getRotate() + 1)));
			ballRotateInitAnimation.setCycleCount((int)circleTimes);
			ballRotateInitAnimation.setOnFinished(e -> {
				ball.setRotate(0);
			});
			ballRotateInitAnimation.play();
		}
	}
	
	/**
	 * 在1.5s内把小球变回初始大小并移到某点
	 * @param ball
	 * @param x
	 * @param y
	 */
	public void transBallToPoint(ImageView ball, double x, double y) {
		Point point1 = new Point(ball.getX(), ball.getY());
		Point point2 = new Point(x, y);
		double distance = point1.getDistance(point2);
		
		if (distance == 0)
			return;
		
		Timeline transition = new Timeline(new KeyFrame(Duration.millis(1500 / distance), e -> {
			ball.setX(ball.getX() + Math.cos(point1.getACos(point2)));
			ball.setY(ball.getY() + Math.sin(point1.getASin(point2)));
		}));
		transition.setCycleCount((int)distance);
		transition.setOnFinished(e -> {
			ball.setX(x);
			ball.setY(y);
		});
		
		//使小球变回正常的动画
		double rate = (ball.getFitHeight() - initWidth) / distance;
		Timeline scale = new Timeline(new KeyFrame(Duration.millis(1500 / distance), e -> {
			ball.setFitWidth(ball.getFitWidth() - rate);
			ball.setFitHeight(ball.getFitHeight() - rate);
		}));
		scale.setCycleCount((int)distance);
		scale.setOnFinished(e -> {
			ball.setFitWidth(initWidth);
			ball.setFitHeight(initWidth);
		});
		
		//将两个动画合起来播放
		parallelTransition = new ParallelTransition(ball, transition, scale);
		parallelTransition.play();
	}

	Timeline messageMoveAnimation, scoreMoveAnimation;
	double scoreInitFontSize;
	double messageInitFontSize;
	boolean key = true;
	/**
	 * 游戏结束时显示分数
	 */
	public void displayScore(Text score, Text message) {
		scoreInitFontSize = score.getFont().getSize();
		messageInitFontSize = message.getFont().getSize();
		
		Point point1 = new Point(message.getX(), message.getY());
		Point point2 = new Point(50, 250);
		double distance = point1.getDistance(point2);

		ballRotateAnimation.play(); //使小球旋转
		
		//使分数的字体缓慢变大
		Timeline scoreSizeAnimation = new Timeline(new KeyFrame(Duration.millis(1000 / ((scoreInitFontSize * 2) * 10)), e -> {
			score.setFont(Font.font(score.getFont().getSize() + 0.1));
		}));
		scoreSizeAnimation.setCycleCount((int)((scoreInitFontSize * 2) * 10));
		//使分数前面的文本缓慢变大
		Timeline messageSizeAnimation = new Timeline(new KeyFrame(Duration.millis(1000 / ((messageInitFontSize * 2) * 10)), e -> {
			message.setFont(Font.font(message.getFont().getSize() + 0.1));
		}));
		messageSizeAnimation.setCycleCount((int)((messageInitFontSize * 2) * 10));

		//使分数前面的文本移动到屏幕中间
		messageMoveAnimation = new Timeline(new KeyFrame(Duration.millis(1000 / distance), e -> {
			message.setX(message.getX() + Math.cos(point1.getACos(point2)));
			message.setY(message.getY() + Math.sin(point1.getASin(point2)));
			if (score.getX() + score.getLayoutBounds().getWidth() > 300 && key) {
				key = false;
				
				displayScoreTransition.pause();
				displayScoreTransition.getChildren().removeAll(scoreMoveAnimation, scoreSizeAnimation, messageSizeAnimation);
				scoreMoveAnimation = null;
				scoreSizeAnimation.pause();
				displayScoreTransition.play();
				
				scoreMoveToCenter(score);
			}
		}));
		messageMoveAnimation.setOnFinished(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				if (key) {
					scoreMoveToCenter(score);
				}
				else {
					key = true;
				}
			}
		});
		messageMoveAnimation.setCycleCount((int)distance);
		//使分数跟随它前面的文本移动，分数在撞上边缘或者文本动画停止时开始第二段动画
		scoreMoveAnimation = new Timeline(new KeyFrame(Duration.millis(1000 / distance), e -> {
			score.setX(message.getX() + message.getLayoutBounds().getWidth() + 10);
			score.setY(message.getY());
		}));
		scoreMoveAnimation.setCycleCount((int)distance);

		//使上面出现的所有动画一起播放
		displayScoreTransition = new ParallelTransition(scoreSizeAnimation, messageSizeAnimation,
				messageMoveAnimation, scoreMoveAnimation);
		displayScoreTransition.setCycleCount(1);
		displayScoreTransition.play();
	}
	
	/**
	 * 展示分数的第二段动画
	 * @param score
	 */
	public void scoreMoveToCenter(Text score) {
		double distanceForBallToCenter;
		Point point1 = new Point(score.getX(), score.getY());
		Point point2 = new Point((300 - score.getLayoutBounds().getWidth()) / 2, 300);
		distanceForBallToCenter = point1.getDistance(point2);
		
//		System.out.println(point1);
//		System.out.println(point2);

		scoreMoveToCenter = new Timeline(new KeyFrame(Duration.millis(500 / distanceForBallToCenter), e -> {
			score.setX(score.getX() + Math.cos(point1.getACos(point2)));
			score.setY(score.getY() + Math.sin(point1.getASin(point2)));
		}));
		scoreMoveToCenter.setCycleCount((int)distanceForBallToCenter);
//		scoreMoveToCenter.setOnFinished(e -> {
////			score.setX(point4.getX());
////			score.setY(point4.getY());
////			System.out.println(score.getX());
////			System.out.println(score.getY());
//		});
		scoreMoveToCenter.play();
	}
	
	/**
	 * 收起展示分数的动画
	 */
	public void stopDisplayScore(Text score, Text message) {
		double scoreInitFontSizeNew = score.getFont().getSize() - scoreInitFontSize;
		double messageInitFontSizeNew = message.getFont().getSize() - messageInitFontSize;
		
		//使分数缓慢变小
		Timeline scoreSizeAnimation = new Timeline(new KeyFrame(Duration.millis(1000 / ((scoreInitFontSizeNew) * 10)), e -> {
			score.setFont(Font.font(score.getFont().getSize() - 0.1));
		}));
		scoreSizeAnimation.setOnFinished(e -> score.setFont(Font.font(scoreInitFontSize)));
		scoreSizeAnimation.setCycleCount((int)((scoreInitFontSizeNew) * 10));
		//使分数前面的文本缓慢变小
		Timeline messageSizeAnimation = new Timeline(new KeyFrame(Duration.millis(1000 / ((messageInitFontSizeNew) * 10)), e -> {
			message.setFont(Font.font(message.getFont().getSize() - 0.1));
		}));
		messageSizeAnimation.setOnFinished(e -> message.setFont(Font.font(messageInitFontSize)));
		messageSizeAnimation.setCycleCount((int)((messageInitFontSizeNew) * 10));
		
		Point pointMessageInit = new Point(5, 15);
		Point pointMessage = new Point(message.getX(), message.getY());
		double distanceMessage = pointMessage.getDistance(pointMessageInit);
		Timeline moveMessage = new Timeline(new KeyFrame(Duration.millis(1000 / distanceMessage), e -> {
			message.setX(message.getX() + Math.cos(pointMessage.getACos(pointMessageInit)));
			message.setY(message.getY() + Math.sin(pointMessage.getASin(pointMessageInit)));
		}));
		moveMessage.setCycleCount((int)distanceMessage);
		
		Point pointScoreInit = new Point(82, 15);
		Point pointScore = new Point(score.getX(), score.getY());
		double distanceScore = pointScore.getDistance(pointScoreInit);
		Timeline moveScore = new Timeline(new KeyFrame(Duration.millis(1000 / distanceScore), e -> {
			score.setX(score.getX() + Math.cos(pointScore.getACos(pointScoreInit)));
			score.setY(score.getY() + Math.sin(pointScore.getASin(pointScoreInit)));
		}));
		moveScore.setOnFinished(e -> score.setY(message.getY()));
		moveScore.setCycleCount((int)distanceScore);
		
		stopDisplayTransition = new ParallelTransition(scoreSizeAnimation, messageSizeAnimation,
				moveMessage, moveScore);
		stopDisplayTransition.setCycleCount(1);
		stopDisplayTransition.setOnFinished(e -> score.setY(message.getY()));
		stopDisplayTransition.play();
	}
	
	/**
	 * 清除所有动画的setOnFinished
	 */
	public void clearAllAnimationFinished() {
		ballRotateAnimation.setOnFinished(null);
		promptTextAnimation.setOnFinished(null);
		ballRotateInitAnimation.setOnFinished(null);
		parallelTransition.setOnFinished(null);
	}
	
	/**
	 * 使欢迎图片消失
	 */
	public void welcomeImageDisappear() {
		welcomeImageDisappearAnimation = new Timeline(new KeyFrame(Duration.millis(10), e -> {
			welcomeImage.setOpacity(welcomeImage.getOpacity() - 0.01);
		}));
		welcomeImageDisappearAnimation.setCycleCount(100);
		welcomeImageDisappearAnimation.play();
	}
}
