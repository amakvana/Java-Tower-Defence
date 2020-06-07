package com.example.towerdefence;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
//		// sound 
//		MediaPlayer sound = MediaPlayer.create(getBaseContext(), R.raw.intro);
//		sound.start();
		
		final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
		mediaPlayer.start();
		
		// display splash screen, with timer
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mediaPlayer.stop();
				startActivity(new Intent(SplashScreen.this, MainMenu.class));
				
				// close this activity
				finish();
			}
		}, 5000);
	}
}
