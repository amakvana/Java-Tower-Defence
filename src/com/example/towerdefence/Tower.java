package com.example.towerdefence;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Tower {

	// variables
	private int cost;
	private Bitmap currentTower;
	private final Paint p = new Paint();
	private final Paint linePaint = new Paint();
	private int strength;
	private int speed;
	private String towerType;
	//private MediaPlayer mediaPlayer;

	// constructor
	public Tower(Context c, Resources resources, String towerType) {
		// initialise the tower that has been passed through
		if (towerType == "fire") {
			this.cost = 30;
			this.strength = 25;
			this.speed = 1500;
			this.towerType = "fire";
			this.currentTower = BitmapFactory.decodeResource(resources, R.drawable.ic_fire);
			//this.mediaPlayer = MediaPlayer.create(c, R.raw.fire_sound);
		}
		if (towerType == "gun") {
			this.cost = 80;
			this.strength = 50;
			this.speed = 1000;
			this.towerType = "gun";
			this.currentTower = BitmapFactory.decodeResource(resources, R.drawable.ic_gun);
			//this.mediaPlayer = MediaPlayer.create(c, R.raw.gun_sound);
		}
		// smoothen the lines
		this.linePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		this.linePaint.setAntiAlias(true);
		this.linePaint.setStrokeWidth(6);
	}

	public void draw(Canvas c, float left, float top, float right, float bottom, RectF towerBound) {
		// calculate measurements
		RectF rec = new RectF(left, top, right, bottom);
		float radius = (right - left) * 1.6f;
		float x = rec.centerX();
		float y = rec.centerY();

		// draw the tower
		c.drawBitmap(this.currentTower, null, rec, new Paint());

		// draw the circle around it
		p.setFlags(Paint.ANTI_ALIAS_FLAG);
		p.setAntiAlias(true);
		p.setARGB(50, 0, 0, 0);  // rgba(0,0,0,0.2)
		c.drawCircle(x, y, radius, p);

		// draw bounds onto the circle
		p.setColor(Color.TRANSPARENT);
		c.drawRect(towerBound, p);
	}

	public void attack(Canvas c, RectF towerBound, RectF enemyBound) {
		// draw a different coloured line for each tower
		if (this.towerType == "fire") {
			linePaint.setColor(Color.rgb(189, 20, 25));  // red
			c.drawLine(towerBound.centerX(), towerBound.centerY(), enemyBound.centerX(), enemyBound.centerY(), linePaint);
			//this.mediaPlayer.start();
		}
		if (this.towerType == "gun") {
			linePaint.setColor(Color.rgb(99, 101, 99));  // gray
			c.drawLine(towerBound.centerX(), towerBound.centerY(), enemyBound.centerX(), enemyBound.centerY(), linePaint);
			//this.mediaPlayer.start();
		}
	}

	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * @return the strength
	 */
	public int getStrength() {
		return strength;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}
}
