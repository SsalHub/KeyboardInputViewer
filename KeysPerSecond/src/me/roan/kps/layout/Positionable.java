package me.roan.kps.layout;

import me.roan.kps.RenderingMode;

public abstract interface Positionable{

	public abstract void setX(int x);
	
	public abstract void setY(int y);
	
	public abstract void setWidth(int w);
	
	public abstract void setHeight(int h);
	
	public abstract String getName();
	
	public abstract int getX();
	
	public abstract int getY();
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public abstract RenderingMode getRenderingMode();
	
	public abstract void setRenderingMode(RenderingMode mode);
}
