package com.example.towerdefence;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Enemy {

	private final Paint p = new Paint();
	private int speed;
	private final float radius;
	private final float xPos;
	private final float yPos;
	private final int colour;
	private final RectF bounds = new RectF();
	private int health;
	private final int originalHealth;
	private final int originalSpeed;
	private static final int PRECISION = 6;

	public Enemy(float radiusIn, float xPosIn, float yPosIn, int speed, int colour, int health) {
		// set the enemy up 
		this.radius = radiusIn;
		this.xPos = xPosIn;
		this.yPos = yPosIn;
		this.speed = speed;
		this.colour = colour;
		this.health = health;
		this.originalHealth = health;
		this.originalSpeed = speed;
	}

	public void draw(Canvas c, float x, float y) {
		// draw enemy
		p.setColor(this.colour);
		p.setStrokeWidth(3);
		c.drawCircle(x, y, this.radius, p);

		// draw the bounds around it
		p.setColor(Color.TRANSPARENT);
		this.bounds.set(x - this.radius + PRECISION, y - this.radius + PRECISION, x + this.radius - PRECISION, y + this.radius - PRECISION);
		c.drawRect(this.bounds, p);
	}

	/**
	 * @return the xPos
	 */
	public float getxPos() {
		return xPos;
	}

	/**
	 * @return the yPos
	 */
	public float getyPos() {
		return yPos;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {
		if (speed == 0) {
			this.speed = 0;
		} else {
			this.speed += speed;
		}
	}

	/**
	 * @return the bounds
	 */
	public RectF getBounds() {
		return bounds;
	}

	/**
	 * @return the bounds
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health of enemy
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * @return the originalHealth
	 */
	public int getOriginalHealth() {
		return originalHealth;
	}

	/**
	 * @return the originalSpeed
	 */
	public int getOriginalSpeed() {
		return originalSpeed;
	}
}
