package com.example.towerdefence;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
	
	private final float cellW;
	private final float cellH;
	private final GameView gameView;
	
	public GestureListener(GameView gv, float w, float h) {
		this.cellW = w;
		this.cellH = h;
		this.gameView = gv;
	}

	// event when single tap occurs
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		placeTower(e, 1);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	// event when double tap occurs
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		placeTower(e, 2);
		return true;
	}

	public void placeTower(MotionEvent e, int weaponType) {
		int x = (int) (e.getX() / cellW);
		int y = (int) (e.getY() / cellH);
		gameView.placeTower(x, y, weaponType);
	}
}

