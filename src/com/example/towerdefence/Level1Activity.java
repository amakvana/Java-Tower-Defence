package com.example.towerdefence;

import android.app.Activity;
import android.graphics.Path;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Level1Activity extends Activity implements OnTouchListener {

	// 0=grass = weapon area too
	// 1=path start
	// 2=path
	// 3=path end
	// 4=tree
	// 5=water
	// 6=fire tower placed
	// 7=ice tower placed
	private final int[][] tileMap = new int[][] {
			{ 0, 0, 4, 0, 0, 5, 5, 4, 5, 4 },
			{ 0, 0, 0, 0, 0, 0, 5, 5, 5, 0 },
			{ 0, 0, 2, 2, 2, 0, 0, 0, 0, 0 },
			{ 0, 0, 2, 0, 2, 0, 2, 2, 2, 3 },
			{ 0, 0, 2, 0, 2, 0, 2, 0, 0, 0 },
			{ 0, 0, 2, 0, 2, 0, 2, 0, 0, 0 },
			{ 4, 4, 2, 0, 2, 2, 2, 0, 0, 4 },
			{ 4, 4, 2, 0, 0, 0, 0, 0, 4, 4 },
			{ 1, 2, 2, 0, 0, 0, 0, 0, 4, 4 },
			{ 4, 4, 4, 4, 4, 4, 0, 0, 0, 0 }
	};
	private final Path sPath = new Path();
	private float cellW;
	private float cellH;
	GameView gv;
	GestureDetector gd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get screen dimensions
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// calculate height & widths of screen, grid, cells & cell midpoints
		int height = dm.heightPixels;
		int width = dm.widthPixels;
		this.cellW = width / 10;
		this.cellH = height / 10;
		float chHalf = this.cellH / 2;
		float cwHalf = this.cellW / 2;

		// set up the path for enemy to move on
		// the * 3 e.g. is the position of the current path value, double it & -1 to stop drawing in middle of the end cell before changing direction
		this.sPath.reset();
		this.sPath.moveTo(0, chHalf * 17);
		this.sPath.lineTo(cwHalf * 5, chHalf * 17);
		this.sPath.lineTo(cwHalf * 5, chHalf * 5);
		this.sPath.lineTo(cwHalf * 9, chHalf * 5);
		this.sPath.lineTo(cwHalf * 9, chHalf * 13);
		this.sPath.lineTo(cwHalf * 13, chHalf * 13);
		this.sPath.lineTo(cwHalf * 13, chHalf * 7);
		this.sPath.lineTo(cwHalf * 20, chHalf * 7);

		// create our game view & touch listeners
		gv = new GameView(this, tileMap, width, height, sPath, "level 1");
		gd = new GestureDetector(this, new GestureListener(gv, this.cellW, this.cellH));

		// show the game view
		setContentView(gv);
	}

	@Override
	protected void onPause() {
		super.onPause();
		gv.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		gv.resume();
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		return false;
	}

	// delegate the event to the gesture detector
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return gd.onTouchEvent(e);
	}
}
