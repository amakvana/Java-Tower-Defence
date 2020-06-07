package com.example.towerdefence;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class LevelSelectActivity extends Activity {

	MediaPlayer mediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level_select);
		
		// music
		mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
		mediaPlayer.start();
	}

	public void level1(View v) {
		mediaPlayer.stop();
		startActivity(new Intent(LevelSelectActivity.this, Level1Activity.class));
	}

	public void level2(View v) {
		mediaPlayer.stop();
		startActivity(new Intent(LevelSelectActivity.this, Level2Activity.class));
	}

	public void level3(View v) {
		mediaPlayer.stop();
		startActivity(new Intent(LevelSelectActivity.this, Level3Activity.class));
	}
}
