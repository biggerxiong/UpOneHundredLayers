package me.xiongxuan.re;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BallGame extends Application {
	public static final int WIDTH = 300;
	public static final int HEIGHT = 600;
	
	private int delayMax = 20, delay = delayMax; //小球下降时，球没有对准跳板，若此时突然左右移动，碰撞到了跳板，会导致多次的碰撞判定，使游戏提前结束。
	 									//这个变量设置了一个延迟判断的时间，判断碰撞成功后延迟delayMax * Ball.upTime ms的时间进行下一次判断

	private double boardPullDownVelocity = 0.7; //跳板下降的速度，越大下降得越快
	private double ballPullDownVelocity = 0.1; //小球跟随跳板下降的速度，越大下降得越快
	
	Timeline animationBall;
	Timeline animationBoard;
	
	Ball ball = new Ball(WIDTH, HEIGHT);
	Pane pane = new Pane();
	Board board = new Board(WIDTH, HEIGHT);
	Scene scene = new Scene(pane);
	Components components = new Components();
	Animations animations = new Animations();
	ParallelTransition pTransition = new ParallelTransition();
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	@Override
	public void start(Stage rootStage) throws Exception {
		welcomeAnimation();
		
		rootStage.setTitle("上不了一百层的都是滑稽");
		rootStage.setScene(scene);
		rootStage.show();
	}
	
	/**
	 * 游戏开始时展示欢迎界面
	 */
	public void welcomeAnimation() {
		scene.setOnKeyPressed(e -> {
			scene.setOnKeyPressed(null); //使按键只能按一下
			
			pane.getChildren().remove(animations.getPromptText()); //移除闪动的提示文本
			animations.correctBallRotate(ball.getImageView()); //在1s内将小球转正
			
			animations.getBallRotateInitAnimation().setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					animations.transBallToPoint(ball.getImageView(), 137, 574); //将球移动到某个点
					//设置移动动画结束的事件
					animations.getParallelTransition().setOnFinished(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							pane.getChildren().addAll(board.getRecs());
							animations.welcomeImageDisappear(); //用一个渐隐的动画使欢迎界面的图片消失
							////设置图片消失后的事件
							animations.getWelcomeImageDisappearAnimation().setOnFinished(e -> {
								pane.getChildren().remove(animations.getWelcomeImage()); //从pane上移除welcomeImage
								startGame();
								animations.clearAllAnimationFinished(); //清除所有动画的setOnFinished
							});
						}
					});
				}
			});
		});
		pane.getChildren().addAll(animations.getWelcomeImage(), animations.getPromptText());
		
		animations.initBall(ball.getImageView()); //初始化小球（把小球放大并旋转）
		pane.getChildren().add(ball.getImageView());
	}
	
	/**
	 * 开始游戏
	 */
	public void startGame() {
		components.getPauseBt().setOnMouseClicked(new PauseBtClicked()); //设置暂停按钮的事件
		pane.getChildren().addAll(components.getMessages(), components.getScore(), components.getPauseBt());
		
		
		pane.setOnKeyPressed(new LeftRightMoveBall()); //按住左右键时一直播放小球左右移动的动画
		pane.setOnKeyReleased(new StopLeftRightMoveBall()); //松开左右键时停止小球左右移动的动画
		
		animationBall = new Timeline(new KeyFrame(Duration.millis(0.1), new UpDownBall())); //小球上下移动的动画
		animationBoard  = new Timeline(new KeyFrame(Duration.millis(8), new AllDown())); //小球和跳板一起下移的动画
		
		pTransition.getChildren().addAll(animationBall, animationBoard);
		pTransition.setCycleCount(Timeline.INDEFINITE);
		pTransition.play();
		
	}
	
	/**
	 * 游戏结束
	 */
	public void gameOver() {
		System.out.println("Game over");
		
		//停止界面下移和小球上升下降的动画
		pTransition.stop();
		pane.setOnKeyPressed(null);
		pane.setOnKeyReleased(null);
		//停止左右键控制小球
		ball.stopXMoveBall(1);
		ball.stopXMoveBall(-1);
		
		//展示分数的动画，并展示提示文本
		pane.getChildren().add(animations.getPromptText());
		
		animations.displayScore(components.getScore(), components.getMessages());
		animations.getpTransition().setOnFinished(e -> {
			//显示分数后的事件，按任意键重开游戏
			pane.setOnKeyPressed(ec -> {
				//按下一个键以后，立即去除所有的按键事件（防止意外的bug）
				pane.setOnKeyPressed(null);
				pane.setOnKeyReleased(null);
				
				//移除提示文本
				pane.getChildren().remove(animations.getPromptText());
				animations.stopDisplayScore(components.getScore(), components.getMessages());
				animations.correctBallRotate(ball.getImageView());
				
				//小球快速转正后做的事情，即重开游戏
				animations.getBallRotateInitAnimation().setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						restartGame();
//						animations.clearAllAnimationFinished();
					}
				});
			});
		});
		
	}
	
	/**
	 * 重新开始游戏
	 */
	public void restartGame() {
		//移除面板上的所有跳板，并清空跳板的ArrayList
		pane.getChildren().removeAll(board.getRecs());
		board.removeAllRec();
		
		//重新创建五个新的跳板，并在面板上显示
		board.initRecs();
		pane.getChildren().addAll(board.getRecs());
		
		//重置计分板
		components.getScore().setText("0");
		
		//把小球移到最底部
		animations.transBallToPoint(ball.getImageView(), 137, 574);
		
		//小球移到底部后的事件
		animations.getParallelTransition().setOnFinished(e -> {
			ball.initBall(); //初始化小球 把小球的所有参数设为初始
			
			//给面板添加左右移动的事件
			pane.setOnKeyPressed(new LeftRightMoveBall());
			pane.setOnKeyReleased(new StopLeftRightMoveBall());

			pTransition.play();
		});
		
	}
	
	/**
	 * 往左或往右移动小球的事件
	 */
	class LeftRightMoveBall implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent e) {
			if (e.getCode() == KeyCode.LEFT) {
				ball.XMoveBall(-1);
			}
			else if (e.getCode() == KeyCode.RIGHT) {
				ball.XMoveBall(1);
			}
		}
	}
	
	/**
	 * 松开按键后停止移动小球的事件
	 */
	class StopLeftRightMoveBall implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent e) {
			if (e.getCode() == KeyCode.LEFT)
				ball.stopXMoveBall(-1);
			else if (e.getCode() == KeyCode.RIGHT)
				ball.stopXMoveBall(1);
		}
	}
	
	/**
	 * 上下移动小球的事件
	 */
	class UpDownBall implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			ball.moveBall();
			if (ball.isCrossBorder()) {
				gameOver();
			}
			if (delay == 0 &&
					board.isImpactAll(ball.getX(), ball.getY(), ball.getR())) {
				if (ball.getDirection() == 1) {
					gameOver();
				}
				else {
					components.getScore().setText(Integer.toString(Integer.valueOf(components.getScore().getText()) + 1));
					ball.resetCount();
					delay = delayMax;
					ball.reversalYDirection();
				}
			}
			else 
				delay = delay > 0 ? delay - 1 : delay;
		}
	}
	
	/**
	 * 使整个界面下滑的事件
	 */
	class AllDown implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			board.pullDownAll(boardPullDownVelocity);
			if (board.isBottomOutBorder()) { //判断最下方的跳板是否出界
				//出界就移除最下方的跳板，再创建一个新跳板
				pane.getChildren().remove(board.getBottomRectangle());
				board.removeBottomRec();
				
				//将跳板加入到ArrayList里，并在面板上显示
				board.getRecs().add(board.getARectangle());
				pane.getChildren().add(board.getTopRectangle());
			}
			ball.pullDown(ballPullDownVelocity);
		}
	}
	
	/**
	 * 点击暂停按钮的事件
	 */
	class PauseBtClicked implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			if (pTransition.getStatus() != Status.STOPPED) {
				if (components.getPauseBt().getText().equals("暂停")) {
					//点击暂停以后，使所有的按键无效（为了防止暂停后小球还能左右移动）
					pane.setOnKeyPressed(null);
					pane.setOnKeyReleased(null);
					pTransition.pause();
					components.getPauseBt().setText("继续");
				}
				else {
					pane.setOnKeyPressed(new LeftRightMoveBall());
					pane.setOnKeyReleased(new StopLeftRightMoveBall());
					pTransition.play();
					components.getPauseBt().setText("暂停");
				}
			}
		}
	}
}
