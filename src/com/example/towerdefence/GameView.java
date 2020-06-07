package com.example.towerdefence;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements Runnable {

	// variables
	private final SurfaceHolder mHolder; // Surface mHolder for the canvas
	private Thread mThread = null; // Thread for the game logic
	private boolean mRunning = false; // Boolean to control pause and resume
	private boolean mapDrawn = false;
	private final Path sPath;
	private Paint pathPaint = new Paint();
	private final PathMeasure pm;
	private final Paint p = new Paint();
	private final int screenHeight;
	private final int screenWidth;
	private final float cellWidth;
	private final float cellHeight;
	private final int[][] map;
	private final Context mContext;
	private int lives = 20;
	private int money = 100;
	private int score = 0;
	private final ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private final int totalEnemies;
	private final Bounding b = new Bounding();
	private final int[] enemyCurStep = {
			0, 0, 0, 0, 0, 0, 0, 0
	};
	private static final int TOWER_PRECISION = 17;  // shrinks range of towerBound for towers, larger number = more shrinkage
	private static final int MONEY_INCREMENT = 3;  // amount of money is added when enemy killed
	private static final int SCORE_INCREMENT = 1;  // score added when enemy killed
	private final MediaPlayer fireMediaPlayer;
	
	private final Bitmap grass = BitmapFactory.decodeResource(getResources(), R.drawable.ic_grass);
	private final Bitmap path = BitmapFactory.decodeResource(getResources(), R.drawable.ic_path);
	private final Bitmap tree = BitmapFactory.decodeResource(getResources(), R.drawable.ic_tree);
	private final Bitmap water = BitmapFactory.decodeResource(getResources(), R.drawable.ic_water);

	// set up towers
	private final Tower fireTower;
	private final Tower gunTower;
	private final String currLevel;

	// constructor
	public GameView(Context context, int[][] m, int w, int h, Path path, String currLevel) {
		super(context);

		// set the view up & initialise member variables
		setLayoutParams(new LayoutParams(w , h));
		this.mHolder = getHolder();
		this.mContext = context;
		this.map = m;
		this.screenHeight = h;
		this.screenWidth = w;
		this.cellWidth = this.screenWidth / 10;
		this.cellHeight = this.screenHeight / 10;
		//this.chHalf = this.cellHeight / 2;
		//this.cwHalf = this.cellWidth / 2;
		this.p.setFlags(Paint.ANTI_ALIAS_FLAG);  // smoothness :D
		this.p.setAntiAlias(true);  // smoothness :D
		this.currLevel = currLevel;
		this.fireTower = new Tower(context, getResources(), "fire");
		this.gunTower = new Tower(context, getResources(), "gun");
		this.fireMediaPlayer = MediaPlayer.create(this.mContext, R.raw.fire_explosion);
		
		// generate enemies
		Random rdm = new Random();
		int minSpeed = 300;
		int maxSpeed = 600;
		int minHealth = 3000;
		int maxHealth = 5000;
		int numEnemies = 8;
		for (int i=0; i<numEnemies; i++) {
			this.enemies.add(new Enemy((this.cellHeight/2)-5, 0f, 0f, ((rdm.nextInt(maxSpeed-minSpeed)+1)+minSpeed), Color.rgb(rdm.nextInt(255), rdm.nextInt(255), rdm.nextInt(255)), ((rdm.nextInt(maxHealth-minHealth)+1)+minHealth)));
		}
		this.totalEnemies = this.enemies.size();

		// create the enemy path
		this.sPath = path;
		this.pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.pathPaint.setStyle(Paint.Style.STROKE);
		this.pathPaint.setStrokeWidth(2);
		//this.pathPaint.setColor(Color.WHITE);  // debug only
		this.pathPaint.setColor(Color.TRANSPARENT);
		this.pm = new PathMeasure(sPath, false);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas){
		int mapXLength = this.map.length;
		int mapYLength = this.map[0].length;

		// if the map hasn't been drawn
		if (!this.mapDrawn) {
			// draw it (not tower & bounds)
			for (int x = 0; x < mapXLength; x++) {
				for (int y = 0; y < mapYLength; y++) {
					int currMapPos = this.map[y][x];
					float left = x * this.cellWidth;
					float top = y * this.cellHeight;
					float right = left + this.cellWidth;
					float bottom = top + this.cellHeight;

					switch (currMapPos) {
					case 0:  // draw grass (aka weapon placement area)
						canvas.drawBitmap(grass, null, new RectF(left, top, right, bottom), p);
						break;

					case 1: case 2: case 3:  // draw path
						canvas.drawBitmap(path, null, new RectF(left, top, right, bottom), p);
						break;

					case 4:  // draw tree
						canvas.drawBitmap(tree, null, new RectF(left, top, right, bottom), p);
						break;

					case 5:  // draw water
						canvas.drawBitmap(water, null, new RectF(left, top, right, bottom), p);
						break;
					}
				}
			}
			this.mapDrawn = true;
		}
		
		// lets draw the towers & bounds ontop of the rest of the map (this way prevents tile overlapping)
		for (int x = 0; x < mapXLength; x++) {
			for (int y = 0; y < mapYLength; y++) {
				int currMapPos = this.map[y][x];  // cache current tower position 
				
				// get current positions for current tower 
				float left = x * this.cellWidth;
				float top = y * this.cellHeight;
				float right = left + this.cellWidth;
				float bottom = top + this.cellHeight;

				// decide which tower to draw 
				switch (currMapPos) {
				case 6:  // drawing the fire tower
					// set the co-ordinates up for bounds
					final RectF fireTowerBound = new RectF();
					RectF rec = new RectF(left, top, right, bottom);
					float radius = (right - left) * 1.5f;
					float centerRecX = rec.centerX();
					float centerRecY = rec.centerY();
					
					// set the bounds and draw the fire tower
					fireTowerBound.set(centerRecX - radius + TOWER_PRECISION, centerRecY - radius + TOWER_PRECISION, centerRecX + radius - TOWER_PRECISION, centerRecY + radius - TOWER_PRECISION);
					fireTower.draw(canvas, left, top, right, bottom, fireTowerBound);

					// allow each fire tower to check if all the enemies are within the bounds
					for (int i=0; i<totalEnemies; i++) {
						// if the enemy is inside the bounds of the tower
						if (b.overlapRectangles(this.enemies.get(i).getBounds(), fireTowerBound)) {
							// attack it
							fireTower.attack(canvas, fireTowerBound, this.enemies.get(i).getBounds());

							// if enemy is killed
							if (enemies.get(i).getHealth() <= 0) {
								this.fireMediaPlayer.start();
								score += SCORE_INCREMENT;  // increase score
								money += MONEY_INCREMENT;  // increase money
								enemyCurStep[i] = 0;  // reset enemy position
								enemies.get(i).setHealth(enemies.get(i).getOriginalHealth()+50);  // reset then increase enemy health
								enemies.get(i).setSpeed(enemies.get(i).getOriginalSpeed()-50);  // reset enemy speed
								if (enemies.get(i).getSpeed() <= 50) {
									enemies.get(i).setSpeed(50);
								}
							} else {
								// deduct firetower damage from enemy health
								enemies.get(i).setHealth(enemies.get(i).getHealth() - fireTower.getStrength());
							}
						}
					}
					break;

				case 7:  // drawing gun tower
					// set the co-ordinates up for bounds
					final RectF gunTowerBound = new RectF();
					RectF rec1 = new RectF(left, top, right, bottom);
					float radius1 = (right - left) * 1.5f;
					float centerRecX1 = rec1.centerX();
					float centerRecY1 = rec1.centerY();
					
					// set the bounds and draw the gun tower
					gunTowerBound.set(centerRecX1 - radius1 + TOWER_PRECISION, centerRecY1 - radius1 + TOWER_PRECISION, centerRecX1 + radius1 - TOWER_PRECISION, centerRecY1 + radius1 - TOWER_PRECISION);
					gunTower.draw(canvas, left, top, right, bottom, gunTowerBound);

					// allow each tower to check if all the enemies are within the bounds
					for (int i=0; i<totalEnemies; i++) {
						// if the enemy is inside the bounds of the tower
						if (b.overlapRectangles(this.enemies.get(i).getBounds(), gunTowerBound)) {
							// attack it
							gunTower.attack(canvas, gunTowerBound, this.enemies.get(i).getBounds());

							// if enemy is killed
							if (enemies.get(i).getHealth() <= 0) {
								score += SCORE_INCREMENT;  // increase score
								money += MONEY_INCREMENT;  // increase money
								enemyCurStep[i] = 0;  // reset enemy position
								enemies.get(i).setHealth(enemies.get(i).getOriginalHealth()+50);  // reset then increase enemy health
								enemies.get(i).setSpeed(enemies.get(i).getOriginalSpeed()-50);  // reset enemy speed
								if (enemies.get(i).getSpeed() <= 50) {
									enemies.get(i).setSpeed(50);
								}
							} else {
								// deduct gun tower damage from enemy health
								enemies.get(i).setHealth(enemies.get(i).getHealth() - gunTower.getStrength());
							}
						}
					}
					break;
				}
			}
		}

		// move the enemies along the path that is defined in each of the level activities 
		for (int i=0; i<totalEnemies; i++) {
			float fSegmentLen = pm.getLength() / this.enemies.get(i).getSpeed();  // we'll get the speed of each enemy chopped up points from path
			float afP[] = { 0f, 0f };

			// if the enemy has not reached the end of the path 
			if (this.enemyCurStep[i] <= this.enemies.get(i).getSpeed()) {
				// get the points & move enemy along them
				pm.getPosTan(fSegmentLen * this.enemyCurStep[i], afP, null);
				canvas.drawPath(sPath, pathPaint);
				this.enemies.get(i).draw(canvas, afP[0], afP[1]);
				this.enemyCurStep[i]++;
				postInvalidate();
			} else {
				// if enemy has reached the end of the path
				this.enemyCurStep[i] = 0; // reset enemy path
				this.lives--;  // deduct a life
			}
		}

		// display infomation panel
		p.setColor(Color.BLACK);
		p.setTextSize(20);
		p.setTypeface(Typeface.SANS_SERIF);
		canvas.drawText("LIVES: " + this.lives, 10, 20, p);
		canvas.drawText("MONEY: £" + this.money, 10, 40, p);
		canvas.drawText("SCORE: "+ this.score, 10, 60, p);
		canvas.drawText("FIRE TOWER: £" + fireTower.getCost(), 10, 100, p);
		canvas.drawText("GUN TOWER: £" + gunTower.getCost(), 10, 120, p);
	}

	@Override
	@SuppressLint("WrongCall")
	public void run() {
		while (mRunning == true){
			//perform canvas drawing
			if(!mHolder.getSurface().isValid()){//if surface is not valid
				continue;//skip anything below it
			}
			Canvas c = mHolder.lockCanvas(); //Lock canvas, paint canvas, unlock canvas
			this.onDraw(c);
			mHolder.unlockCanvasAndPost(c);
			
			// if the player runs out of lives, game over
			if (this.lives == 0) {
				// game over screen 
				// check if current score beats high score replace file.
				// then close the current level 
				if (this.currLevel == "level 1") {
					((Level1Activity) getContext()).finish();
				}
				else if (this.currLevel == "level 2") {
					((Level2Activity) getContext()).finish();
				}
				else if (this.currLevel == "level 3") {
					((Level3Activity) getContext()).finish();
				}
			}
		}
	}

	public void pause(){
		mRunning = false;
		while(true){
			try{
				mThread.join();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			break;
		}
		mThread = null;
	}

	public void resume(){
		mRunning = true;
		mThread = new Thread(this);
		mThread.start();
	}

	public void placeTower(int x, int y, int weaponType) {
		// check if map area is grass
		if (this.map[y][x] == 0) {
			// decide which tower has been chosen
			switch (weaponType) {
			case 1:  // tower 1
				// check if we have enough money to use item
				if ((this.money - fireTower.getCost()) >= 0) {
					// if so place it, deduct money & tell user
					this.map[y][x] = 6;
					this.money -= fireTower.getCost();
					Toast.makeText(this.mContext, "Fire Tower placed", Toast.LENGTH_SHORT).show();
				} else {
					// tell user, not enough money
					Toast.makeText(this.mContext, "Not enough money!", Toast.LENGTH_SHORT).show();
				}
				break;

			case 2:  // tower 2
				// check if we have enough money to use item
				if ((this.money - gunTower.getCost()) >= 0) {
					// if so place it, deduct money & tell user
					this.map[y][x] = 7;
					this.money -= gunTower.getCost();
					Toast.makeText(this.mContext, "Gun Tower placed", Toast.LENGTH_SHORT).show();
				} else {
					// tell user, not enough money
					Toast.makeText(this.mContext, "Not enough money!", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		} else {
			// tell user, incorrect location to place tower 
			Toast.makeText(this.mContext, "You cannot place a tower here", Toast.LENGTH_SHORT).show();
		}
	}
}
