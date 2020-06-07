package com.example.towerdefence;

import android.graphics.RectF;

public class Bounding {
	public Bounding(){
		
	}
	
	public boolean overlapRectangles(RectF rectF, RectF rectF2){
		if (rectF.left < rectF2.right && rectF.right > rectF2.left && rectF.top < rectF2.bottom && rectF.bottom > rectF2.top) {
			return true;
		} else {
			return false;
		}
	}

}