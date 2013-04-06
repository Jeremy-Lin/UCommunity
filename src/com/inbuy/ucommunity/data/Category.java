package com.inbuy.ucommunity.data;

import android.graphics.drawable.Drawable;

public class Category {
	public Drawable mLogo;
	public String mName; 
	
	public Category(Drawable logo, String name) {
		this.mLogo = logo;
		this.mName = name;
	}
}
