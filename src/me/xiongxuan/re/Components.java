package me.xiongxuan.re;

import javafx.animation.ParallelTransition;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Components {
	private Text messages; //分数前的提示文本
	private Text score; //玩家的分数
	private Button pauseBt; //暂停按钮
	
	ParallelTransition pTransition = new ParallelTransition(); //
	
	public Components() {
		messages = new Text("你的分数为：");
		messages.setFill(Color.RED);
		messages.setX(5);
		messages.setY(15);
		
		score = new Text("0");
		score.setX(messages.getLayoutBounds().getWidth() + 10);
		score.setY(15);

		pauseBt = new Button("暂停");
		pauseBt.setLayoutX(250);
	}

	public Text getMessages() {
		return messages;
	}

	public void setMessages(Text messages) {
		this.messages = messages;
	}

	public Button getPauseBt() {
		return pauseBt;
	}

	public void setPauseBt(Button pauseBt) {
		this.pauseBt = pauseBt;
	}

	public Text getScore() {
		return score;
	}
}
