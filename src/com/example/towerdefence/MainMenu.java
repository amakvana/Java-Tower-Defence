package com.example.towerdefence;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class MainMenu extends Activity {

	private static final int PADDING_TOP = 20;
	private static final int PADDING_BOTTOM = 20;
	private static final int BUTTON_MARGIN_BOTTOM = 5;
	private static final int[] BUTTONS = {
		R.id.button1,
		R.id.button2,
		R.id.button3,
		R.id.button4
	};
	Button[] btns = new Button[BUTTONS.length];
	MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);

		// screen detection here
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// get heights and calculate new button height
		int screenHeight = dm.heightPixels;
		int btnNewHeight = (((screenHeight - (PADDING_TOP + PADDING_BOTTOM)) / 5) - (4 * (BUTTON_MARGIN_BOTTOM)));

		// set height for each button dynamically
		int totalButtons = BUTTONS.length;
		Button btn;
		for (int i = 0; i < totalButtons; i++) {
			btn = (Button) findViewById(BUTTONS[i]);
			btn.setHeight(btnNewHeight);
		}
		
		// music
		mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
		mediaPlayer.start();
	}
	
	public void newGame(View v) {
		mediaPlayer.stop();
		startActivity(new Intent(MainMenu.this, LevelSelectActivity.class));
	}

	public void loadGame(View v) {
		mediaPlayer.stop();
		startActivity(new Intent(MainMenu.this, MainMenu.class));
	}

	public void scores(View v) {
		mediaPlayer.stop();
		startActivity(new Intent(MainMenu.this, MainMenu.class));
	}

	public void exit(View v) {
		mediaPlayer.stop();
		mediaPlayer = null;
		finish();
	}
}
